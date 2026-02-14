package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;

import dao.EmissionDAO;
import dao.CountryDAO;
import model.Emission;
import model.Country;

@Named
@ViewScoped
public class EmissionsController implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==============================
    // DAO INJECTIONS
    // ==============================

    @Inject
    private EmissionDAO emissionDAO;

    @Inject
    private CountryDAO countryDAO;

    // ==============================
    // TABELLEN-DATEN (Frontend)
    // ==============================

    private List<Emission> latestEmissions;

    public List<Emission> getLatestEmissions() {
        if (latestEmissions == null) {
            latestEmissions = emissionDAO.findLatestEmissionsPerCountry();
        }
        return latestEmissions;
    }

    // ==============================
    // LÄNDER (Dropdown)
    // ==============================

    private List<Country> countries;

    public List<Country> getCountries() {
        if (countries == null) {
            countries = countryDAO.findCountries();
        }
        return countries;
    }

    // ==============================
    // BACKEND – SELEKTIERTER DATENSATZ
    // ==============================

    private Emission selectedEmission;

    private boolean newMode = false;

    public Emission getSelectedEmission() {
        if (selectedEmission == null) {
            selectedEmission = new Emission();
        }
        return selectedEmission;
    }


    public boolean isNewMode() {
        return newMode;
    }

    /**
     * Bestehenden Datensatz auswählen (Bearbeiten / Validieren)
     */
    public void selectEmission(Emission e) {
        this.selectedEmission = e;
        this.newMode = false;
    }

    /**
     * Vorbereitung für neuen Datensatz (Anforderung 2)
     */
    public void prepareNewEmission() {
        this.selectedEmission = new Emission();
        this.newMode = true;
    }

    // ==============================
    // WISSENSCHAFTLER – BESTEHENDEN DATENSATZ ÄNDERN
    // ==============================

    public void saveEmission() {

        if (selectedEmission == null) return;

        selectedEmission.setStatus("PENDING");

        emissionDAO.update(selectedEmission);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Änderung gespeichert",
                "Datensatz wurde als PENDING markiert."));

        latestEmissions = null; // Tabelle neu laden
    }

    // ==============================
    // WISSENSCHAFTLER – NEUEN DATENSATZ ANLEGEN (Anforderung 2)
    // ==============================

    public void saveNewEmission() {

        System.out.println("=== saveNewEmission() START ===");

        if (selectedEmission == null) {
            System.out.println("selectedEmission ist NULL!");
            return;
        }

        System.out.println("selectedCountryId = " + selectedCountryId);
        System.out.println("Year = " + selectedEmission.getYear());
        System.out.println("Pending = " + selectedEmission.getCo2KtPending());

        // 1️ Country setzen (muss vor Validation erfolgen)
        if (selectedCountryId != null) {
            Country country = countryDAO.findCountry(selectedCountryId);
            selectedEmission.setCountry(country);
            System.out.println("Country gesetzt: " + country);
        }

        // 2️ Validierung (Integer prüfen!)
        if (selectedEmission.getCountry() == null ||
            selectedEmission.getYear() == null ||
            selectedEmission.getCo2KtPending() == null) {

            System.out.println("!!! VALIDIERUNG BLOCKIERT !!!");

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Bitte alle Felder ausfüllen."));
            return;
        }

        // 3️ Prüfen ob Datensatz existiert
        boolean exists = emissionDAO.existsByCountryAndYear(
                selectedEmission.getCountry().getId(),
                selectedEmission.getYear());

        System.out.println("exists = " + exists);

        if (exists) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Für dieses Land existiert bereits ein Datensatz für dieses Jahr."));
            return;
        }

        // 4️ Pflichtfelder setzen
        selectedEmission.setStatus("PENDING");
        selectedEmission.setCo2Kt(null);
        selectedEmission.setUnit("kt"); // WICHTIG wegen NOT NULL

        System.out.println("Speichere neuen Datensatz...");

        emissionDAO.create(selectedEmission);

        System.out.println("Datensatz erfolgreich gespeichert.");

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Erfolgreich",
                "Neuer Datensatz wurde angelegt (Status: PENDING)."));

        newMode = false;
        latestEmissions = null;
    }


    // ==============================
    // HERAUSGEBER – VALIDIEREN
    // ==============================

    public void validateEmission() {

        if (selectedEmission == null) return;

        if (selectedEmission.getCo2KtPending() != null) {

            selectedEmission.setCo2Kt(selectedEmission.getCo2KtPending());
            selectedEmission.setCo2KtPending(null);
            selectedEmission.setStatus("APPROVED");

            emissionDAO.update(selectedEmission);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Validiert",
                    "Datensatz wurde freigegeben."));
        }

        latestEmissions = null; // Tabelle neu laden
    }
    
    
    private Long selectedCountryId;

    public Long getSelectedCountryId() {
        return selectedCountryId;
    }

    public void setSelectedCountryId(Long selectedCountryId) {
        this.selectedCountryId = selectedCountryId;
    }
    
    
    // ==============================
    // Für emission.xhtml
    // ==============================
    
    public Country getSingleSelectedCountry() {

        if (selectedCountryId == null) {
            return null;
        }

        return countryDAO.findCountry(selectedCountryId);
    }


    

    // ==============================
    // NAVIGATION
    // ==============================

    public String returnEmissionsPage() {
        return "emissions.xhtml";
    }
    
    public void save() {

        System.out.println("=== SAVE() ===");
        System.out.println("newMode = " + newMode);

        if (newMode) {
            System.out.println("-> gehe zu saveNewEmission()");
            saveNewEmission();
        } else {
            System.out.println("-> gehe zu saveEmission()");
            saveEmission();
        }
    }

    

    @PostConstruct
    public void init() {
        System.out.println("EmissionsController wurde erzeugt");
    }
}