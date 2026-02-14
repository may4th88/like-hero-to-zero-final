package controller;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class BackendController implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private boolean showEmissions = false;

    public void showEmissionsTable() {
        showEmissions = true;
    }

    public boolean isShowEmissions() {
        return showEmissions;
    }
}

