package meraschool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.Application;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import meraschool.views.ViewWindow;

@SuppressWarnings("serial")
public class App extends Application
{
    public String dbname;

    @Override
    public void init()
    {
        setMainWindow(new Window("My Vaadin Application"));
        getMainWindow().addComponent(new Label("Press spacebar or escape to navigate"));
        Button startButton = new Button("Start Application");
        startButton.addListener(new ClickListener1(this));
        startButton.setClickShortcut(KeyCode.SPACEBAR);
        getMainWindow().addComponent(startButton);
        Button exitButton = new Button("Restart");
        exitButton.addListener(new ClickListener2(this));
        exitButton.setClickShortcut(KeyCode.ESCAPE);
        getMainWindow().addComponent(exitButton);

        getContext().addTransactionListener(new MyTransactionListener(this));
    }
}

@SuppressWarnings("serial")
class ClickListener1 implements Button.ClickListener {
    App app;

    public ClickListener1(App app) {
        this.app = app;
    }

    public void buttonClick(ClickEvent event) {
        app.getMainWindow().addComponent(new Label("new Application().start()"));
        Window w = new ViewWindow(app, app.getMainWindow());
        w.setModal(true);
        app.getMainWindow().addWindow(w);
        w.setCloseShortcut(KeyCode.ESCAPE, null);
        w.focus();
    }
}

@SuppressWarnings("serial")
class ClickListener2 implements Button.ClickListener {
    App app;

    public ClickListener2(App app) {
        this.app = app;
    }

    public void buttonClick(ClickEvent event) {
        this.app.close();
    }
}

@SuppressWarnings("serial")
class MyTransactionListener implements TransactionListener {
    App app;

    public MyTransactionListener(App app) {
        this.app = app;
    }
    public void transactionStart(Application application, Object transactionData) {
        Matcher m = Pattern.compile("\\(GET /(.*)\\).*").matcher(transactionData.toString());
        if (m.find()) {
            String db = m.group(1).isEmpty() ? "db" : m.group(1);
            db = db.replaceAll("/$", "");
            System.out.println("data base name: " + db);
            app.dbname = db;
            app.getContext().removeTransactionListener(this);
        }
    }
    public void transactionEnd(Application application, Object transactionData) {
    }
}