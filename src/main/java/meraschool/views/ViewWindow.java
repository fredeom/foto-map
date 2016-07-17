package meraschool.views;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import meraschool.App;
import meraschool.controllers.ViewController;

@SuppressWarnings("serial")
public class ViewWindow extends Window {

    public ViewWindow(App app, Window parent) {
        super("Use left and right mouse buttons to navigate");
        ViewController c = new ViewController(app);
        c.setWindow(this);
        setContent(new ViewPanel(c));
        setSizeUndefined();
        addListener(new CloseListener1(parent));
    }
}

class CloseListener1 implements CloseListener {
    private Window parent;

    public CloseListener1(Window parent) {
        this.parent = parent;
    }
    public void windowClose(CloseEvent e) {
        parent.focus();
    }
}