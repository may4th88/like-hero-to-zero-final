package controller;

import org.primefaces.PrimeFaces;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

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
            System.out.println("latestEmissions null");
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

    private Long selectedCountryId;

    public Long getSelectedCountryId() {
        return selectedCountryId;
    }

    public void setSelectedCountryId(Long selectedCountryId) {
        this.selectedCountryId = selectedCountryId;
    }

    // ==============================
    // DETAILANSICHT LAND
    // ==============================

    private Emission latestSelectedEmission;
    private List<Emission> emissionsOfCountry;
    private String lineModel;



    public Emission getLatestSelectedEmission() {
        return latestSelectedEmission;
    }

    public String getLineModel() {
        return lineModel;
    }


    /**
     * Wird vom Submit-Button aufgerufen
     */
    public void loadCountryData() {

        if (selectedCountryId == null) {
            latestSelectedEmission = null;
            emissionsOfCountry = null;
            lineModel = null;
            return;
        }

        latestSelectedEmission = emissionDAO.findLatestEmissionByCountry(selectedCountryId);
        emissionsOfCountry = emissionDAO.findEmissionHistoryByCountry(selectedCountryId);

        createLineModel();
    }


    /**
     * Chart für PrimeFaces 13 erzeugen
     */
    private void createLineModel() {

        if (emissionsOfCountry == null || emissionsOfCountry.isEmpty()) {
            lineModel = null;
            return;
        }

        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();

        labels.append("[");
        values.append("[");

        for (int i = 0; i < emissionsOfCountry.size(); i++) {

            Emission e = emissionsOfCountry.get(i);

            labels.append("\"").append(e.getYear()).append("\"");
            values.append(e.getCo2Kt());

            if (i < emissionsOfCountry.size() - 1) {
                labels.append(",");
                values.append(",");
            }
        }

        labels.append("]");
        values.append("]");

        lineModel =
            "{"
            + "\"type\":\"line\","
            + "\"data\":{"
            + "\"labels\":" + labels + ","
            + "\"datasets\":[{"
            + "\"label\":\"CO₂ Emissionen (kt)\","
            + "\"data\":" + values + ","
            + "\"borderColor\":\"rgb(75, 192, 192)\","
            + "\"backgroundColor\":\"rgba(75, 192, 192,0.2)\","
            + "\"tension\":0.1,"
            + "\"fill\":false"
            + "}]"
            + "},"
            + "\"options\":{"
            + "\"responsive\":true,"
            + "\"maintainAspectRatio\":false,"
            + "\"plugins\":{"
            + "\"title\":{"
            + "\"display\":true,"
            + "\"text\":\"CO₂-Entwicklung\""
            + "}"
            + "}"
            + "}"
            + "}";
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

    public void selectEmission(Emission e) {
        this.selectedEmission = e;
        this.newMode = false;
    }

    public void prepareNewEmission() {
        this.selectedEmission = new Emission();
        this.newMode = true;
    }

    // ==============================
    // WISSENSCHAFTLER – BESTEHENDEN DATENSATZ ÄNDERN
    // ==============================

    public void saveEmission() {

        FacesContext context = FacesContext.getCurrentInstance();

        // Validierung: Pending-Wert muss eingegeben sein
        if (selectedEmission.getCo2KtPending() == null) {

            context.addMessage("backendForm:pendingInput",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler", "Bitte einen neuen CO₂-Wert eingeben."));

            context.validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);

            return;
        }

        // Status auf PENDING setzen
        selectedEmission.setStatus("PENDING");

        // Änderung speichern
        emissionDAO.update(selectedEmission);

        // Globale Erfolgsmeldung
        context.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Änderung gespeichert",
                "Datensatz wurde als PENDING markiert."));

        // Tabelle neu laden
        latestEmissions = null;
    }

    // ==============================
    // WISSENSCHAFTLER – NEUEN DATENSATZ ANLEGEN
    // ==============================

    public void saveNewEmission() {

        if (selectedCountryId != null) {
            Country country = countryDAO.findCountry(selectedCountryId);
            selectedEmission.setCountry(country);
        }

        if (selectedEmission.getCountry() == null ||
            selectedEmission.getYear() == null ||
            selectedEmission.getCo2KtPending() == null) {

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Bitte alle Felder ausfüllen."));
            return;
        }

        boolean exists = emissionDAO.existsByCountryAndYear(
                selectedEmission.getCountry().getId(),
                selectedEmission.getYear());

        if (exists) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Für dieses Land existiert bereits ein Datensatz für dieses Jahr."));
            return;
        }

        selectedEmission.setStatus("PENDING");
        selectedEmission.setCo2Kt(null);
        selectedEmission.setUnit("kt");

        emissionDAO.create(selectedEmission);

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

        latestEmissions = null;
    }

    // ==============================
    // NAVIGATION
    // ==============================

    public String returnEmissionsPage() {
        return "emissions.xhtml";
    }

    public void save() {

        if (newMode) {
            saveNewEmission();
        } else {
            saveEmission();
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("EmissionsController wurde erzeugt");
    }
}
