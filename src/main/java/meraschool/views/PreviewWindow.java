package meraschool.views;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import meraschool.controllers.PreviewController;

@SuppressWarnings("serial")
public class PreviewWindow extends Window {

    public PreviewWindow(PreviewController c, Window parent) {
        super("Down the Rabbit Hole: " + c.viewLevel);
        c.setWindow(this);
        LocationPanel locationPanel = new LocationPanel(c);
        ViewPanel viewPanel = new ViewPanel(c);
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(new Button("Awesome button"));
        hl.addComponent(locationPanel);
        hl.addComponent(viewPanel);
        hl.setSizeUndefined();
        setContent(hl);
        setSizeUndefined();
        addListener(new CloseListener1(parent));
    }
}