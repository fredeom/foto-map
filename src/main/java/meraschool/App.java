package meraschool;

import com.vaadin.Application;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class App extends Application
{
    @Override
    public void init()
    {
        setMainWindow(new Window("My Vaadin Application"));
        Button button = new Button("Click Me");
        button.addListener(new CustomClickListener(this));
        button.setClickShortcut(KeyCode.SPACEBAR);
        getMainWindow().addComponent(button);
    }
}

@SuppressWarnings("serial")
class CustomClickListener implements Button.ClickListener {
    Application app;

    public CustomClickListener(Application app) {
        this.app = app;
    }

    public void buttonClick(ClickEvent event) {
        app.getMainWindow().addComponent(new Label("Thank you for clicking"));
        Window w = new ViewWindow(new Controller(app));
        w.setModal(true);
        app.getMainWindow().addWindow(w);
        w.setCloseShortcut(KeyCode.ESCAPE, null);
        w.focus();
    }
}