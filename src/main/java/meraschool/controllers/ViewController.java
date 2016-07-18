package meraschool.controllers;

import java.util.concurrent.Callable;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Window;

import meraschool.App;
//import meraschool.listeners.ModelCloseListener;
import meraschool.models.LocationModel;
import meraschool.models.ViewModel;
import meraschool.views.PreviewWindow;

public class ViewController {

    public App app;
    public ViewModel viewModel;
    protected Window window; // ???

    public ViewController(App app) {
        this(app, new ViewModel());
    }

    public ViewController(App app, ViewModel model) {
        this.app = app;
        this.viewModel = model;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public Resource getImage() { // move entrails to DbConnectorImp ?
                                 // Cant move embedded without initialization
                                 // with Application
        if (viewModel.getViewId() == 0) {
            return new ClassResource("/1.jpg", app);
        } else {
            // get image and draw location links using AlphaComposite ac;
            return new FileResource(app.dbConnector.getImagePathByViewId(viewModel.getViewId()).toFile(), app);
            // return new FileResource(new File(dbInstance.getBaseDir() + " " +
            // dbInstance.getImageByViewId(viewModel.getViewId())), app);
        }
    }

    // TODO: Safe current view and call
    // currentParentLinkHandler.call() -> to safe link data
    // safe tmpPicture to persistent folder with viewId name
    // move to Controller
    // public void saveView() {
    // }

    public void loadView(int viewId) {

    }

    @SuppressWarnings("serial")
    public ClickListener getImageClickListener() {
        return new ClickListener() {
            public void click(ClickEvent event) {
                if (viewModel.getViewId() == 0) {
                    int viewLevel = getThis() instanceof PreviewController ? ((PreviewController) getThis()).viewLevel
                            : 0;
                    final PreviewController c = new PreviewController(app, viewLevel + 1,
                            new LocationModel(null, app.dbConnector.getLocationList()),
                            new ViewModel());
                    c.setStrangeHandler(new Callable<Void>() {
                        public Void call() throws Exception {
                            loadView(c.viewModel.getViewId());
                            ((Window) c.window.getParent()).removeWindow(c.window);
                            window.focus();
                            return null;
                        }
                    });
                    Window w = new PreviewWindow(c, window);
                    w.setModal(true);
                    app.getMainWindow().addWindow(w);
                    w.setCloseShortcut(KeyCode.ESCAPE, null);
                    w.focus();
                } else {
                    System.out
                            .println(event.getButton() + ": (" + event.getClientX() + ", " + event.getClientY() + ")");
                    System.out.println(
                            event.getButtonName() + ": (" + event.getRelativeX() + ", " + event.getRelativeY() + ")");
                    System.out.println(event.getComponent());
                    System.out.println(event.getSource());
                }
            }
        };
    }

    public ViewController getThis() {
        return this;
    }
}