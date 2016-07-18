package meraschool.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import meraschool.controllers.PreviewController;
import meraschool.models.Location;

@SuppressWarnings("serial")
public class LocationPanel extends Panel implements LocationModelListener {

    private PreviewController c;
    private List<Button> btnList = new ArrayList<Button>();

    public LocationPanel(PreviewController c) {
        this.c = c;
        c.locationModel.addListener(this);
        locationListChanged();
        setSizeUndefined();
    }

    public void selectedLocationChanged() {
        for (Button b : btnList) {
            String currentCaption = c.locationModel.selectedLocation.id + ": " + c.locationModel.selectedLocation.name;
            if (b.getCaption().equals(currentCaption)) {
                b.setStyleName(BaseTheme.BUTTON_LINK);
            } else {
                b.removeStyleName(BaseTheme.BUTTON_LINK);
            }
        }
    }

    public void locationListChanged() {
        removeAllComponents();
        VerticalLayout vl = new VerticalLayout();
        btnList.clear();
        for (Location l : c.locationModel.locations) {
            Button b = new Button(l.id + ": " + l.name);
            b.addListener(c.getLocationButtonClickListener(l));
            vl.addComponent(b);
            btnList.add(b);
        }
        vl.setSizeUndefined();
        setContent(vl);
    }
}