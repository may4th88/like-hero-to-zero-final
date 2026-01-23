package controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

import dao.EmissionDAO;
import model.Emission;

@Named
@ViewScoped
public class EmissionsController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmissionDAO emissionDAO;

    private List<Emission> latestEmissions;

    
    public List<Emission> getLatestEmissions() {
        if (latestEmissions == null) {
            latestEmissions = emissionDAO.findLatestEmissionsPerCountry();
        }
        return latestEmissions;
    }

    public String returnEmissionsPage() {
        return "emissions.xhtml";
    }
    
    @PostConstruct
    public void init() {
        System.out.println("EmissionsController wurde erzeugt");
    }
}


