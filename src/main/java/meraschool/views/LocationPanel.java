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
        selectedLocationChanged();
        setSizeUndefined();
    }

    public void selectedLocationChanged() {
        Location loc = c.locationModel.getSelectedLocation();
        for (Button b : btnList) {
            if (loc != null) {
                if (b.getCaption().equals(loc.toString())) {
                    b.setStyleName(BaseTheme.BUTTON_LINK);
                } else {
                    b.removeStyleName(BaseTheme.BUTTON_LINK);
                }
            }
        }
    }

    public void locationListChanged() {
        removeAllComponents();
        VerticalLayout vl = new VerticalLayout();
        btnList.clear();
        for (Location l : c.locationModel.getLocations()) {
            Button b = new Button(l.toString());
            b.addListener(c.getLocationButtonClickListener(l));
            vl.addComponent(b);
            btnList.add(b);
        }
        vl.setSizeUndefined();
        setContent(vl);
    }
}