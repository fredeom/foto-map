package meraschool.views;

import com.vaadin.ui.Window;

import meraschool.App;
import meraschool.controllers.ViewController;

@SuppressWarnings("serial")
public class ViewWindow extends Window {

    public ViewWindow(App app) {
        super("Use left and right mouse buttons to navigate");
        ViewController c = new ViewController(app);
        setContent(new ViewPanel(c));
        setSizeUndefined();
    }
}