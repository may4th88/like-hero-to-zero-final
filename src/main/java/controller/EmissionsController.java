package controller;

import java.io.Serializable;
import java.util.List;

import org.primefaces.PrimeFaces;

import dao.CountryDAO;
import dao.EmissionDAO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Country;
import model.Emission;

@Named
@ViewScoped
public class EmissionsController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmissionDAO emissionDAO;

    @Inject
    private CountryDAO countryDAO;

    private List<Emission> latestEmissions;
    private List<Country> countries;

    private Long selectedCountryId;

    private Emission latestSelectedEmission;
    private List<Emission> emissionsOfCountry;
    private String lineModel;

    private Emission selectedEmission;
    private boolean newMode;

    public List<Emission> getLatestEmissions() {
        if (latestEmissions == null) {
            latestEmissions = emissionDAO.findLatestEmissionsPerCountry();
        }

        return latestEmissions;
    }

    public List<Country> getCountries() {
        if (countries == null) {
            countries = countryDAO.findCountries();
        }

        return countries;
    }

    public Long getSelectedCountryId() {
        return selectedCountryId;
    }

    public void setSelectedCountryId(Long selectedCountryId) {
        this.selectedCountryId = selectedCountryId;
    }

    public Emission getLatestSelectedEmission() {
        return latestSelectedEmission;
    }

    public String getLineModel() {
        return lineModel;
    }

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
     * Lädt die aktuellen Emissionsdaten und die Historie des ausgewählten Landes.
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
     * Erstellt das JSON-Modell für das PrimeFaces-Liniendiagramm.
     */
    private void createLineModel() {
        if (emissionsOfCountry == null || emissionsOfCountry.isEmpty()) {
            lineModel = null;
            return;
        }

        StringBuilder labels = new StringBuilder("[");
        StringBuilder values = new StringBuilder("[");

        for (int i = 0; i < emissionsOfCountry.size(); i++) {
            Emission emission = emissionsOfCountry.get(i);

            labels.append("\"").append(emission.getYear()).append("\"");
            values.append(emission.getCo2Value());

            if (i < emissionsOfCountry.size() - 1) {
                labels.append(",");
                values.append(",");
            }
        }

        labels.append("]");
        values.append("]");

        lineModel = "{"
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

    public void selectEmission(Emission emission) {
        selectedEmission = emission;
        newMode = false;
    }

    public void prepareNewEmission() {
        selectedEmission = new Emission();
        newMode = true;
    }

    /**
     * Speichert die Änderung eines bestehenden Datensatzes als ausstehende Änderung.
     */
    public void saveEmission() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (selectedEmission.getCo2ValuePending() == null) {
            context.addMessage("backendForm:pendingInput",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Bitte einen neuen CO₂-Wert eingeben."
                ));

            context.validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);

            return;
        }

        selectedEmission.setStatus("PENDING");
        emissionDAO.update(selectedEmission);

        String countryName = selectedEmission.getCountry().getName();

        context.addMessage(null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Änderung gespeichert",
                "Datensatz mit Land " + countryName + " wurde als PENDING markiert."
            ));

        latestEmissions = null;
    }

    /**
     * Legt einen neuen Emissionsdatensatz mit dem Status PENDING an.
     */
    public void saveNewEmission() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (selectedCountryId != null) {
            Country country = countryDAO.findCountry(selectedCountryId);
            selectedEmission.setCountry(country);
        }

        if (selectedEmission.getCountry() == null
                || selectedEmission.getYear() == null
                || selectedEmission.getCo2ValuePending() == null) {

            context.addMessage("backendForm:dlgMsgs",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Bitte alle Felder ausfüllen."
                ));

            context.validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);

            return;
        }

        boolean exists = emissionDAO.existsByCountryAndYear(
            selectedEmission.getCountry().getId(),
            selectedEmission.getYear()
        );

        if (exists) {
            context.addMessage("backendForm:dlgMsgs",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Fehler",
                    "Für dieses Land existiert bereits ein Datensatz für dieses Jahr."
                ));

            context.validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);

            return;
        }

        selectedEmission.setStatus("PENDING");
        selectedEmission.setCo2Value(null);
        selectedEmission.setUnit("kt");

        emissionDAO.create(selectedEmission);

        String countryName = selectedEmission.getCountry().getName();

        context.addMessage(null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Erfolgreich",
                "Neuer Datensatz mit Land " + countryName
                    + " wurde angelegt und als PENDING markiert."
            ));

        newMode = false;
        latestEmissions = null;
    }

    /**
     * Übernimmt einen ausstehenden CO₂-Wert und gibt den Datensatz frei.
     */
    public void validateEmission() {
        if (selectedEmission.getCo2ValuePending() == null) {
            return;
        }

        selectedEmission.setCo2Value(selectedEmission.getCo2ValuePending());
        selectedEmission.setCo2ValuePending(null);
        selectedEmission.setStatus("APPROVED");

        emissionDAO.update(selectedEmission);

        String countryName = selectedEmission.getCountry().getName();

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Validiert",
                "Datensatz mit Land " + countryName + " wurde freigegeben."
            ));

        latestEmissions = null;
    }

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
}