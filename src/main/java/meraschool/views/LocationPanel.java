package meraschool.views;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import meraschool.controllers.PreviewController;

@SuppressWarnings("serial")
public class LocationPanel extends Panel {

    public LocationPanel(PreviewController c) {
        VerticalLayout vl = new VerticalLayout();
        // fill with location buttons(caption: locationId)
        // add click listeners(c.getClickListener(locationId))
        vl.setSizeUndefined();
        setContent(vl);
        setSizeUndefined();
    }
}