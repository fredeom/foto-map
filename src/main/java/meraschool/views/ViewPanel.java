package meraschool.views;

import java.awt.Point;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

import meraschool.DbConnectorImpl.Neighbor;
import meraschool.controllers.ViewController;
import meraschool.models.Location;
import meraschool.views.NavigationPanel.Side;

@SuppressWarnings("serial")
public class ViewPanel extends Panel implements ViewModelListener {

    private ViewController c;
    private Embedded image;

    public ViewPanel(ViewController controller) {
        this.c = controller;
        c.viewModel.addListener(this);

        Upload btnLeft = new Upload();
        btnLeft.setImmediate(true);
        btnLeft.setButtonCaption("Add(<)");
        Serializable myReceiver = new MyReceiver2(c, MyReceiver2.Side.LEFT);
        btnLeft.setReceiver((Receiver) myReceiver);
        btnLeft.addListener((SucceededListener) myReceiver);
        btnLeft.setSizeUndefined();

        Upload btnRight = new Upload();
        btnRight.setImmediate(true);
        btnRight.setButtonCaption("Add(>)");
        Serializable myReceiver2 = new MyReceiver2(c, MyReceiver2.Side.RIGHT);
        btnRight.setReceiver((Receiver) myReceiver2);
        btnRight.addListener((SucceededListener) myReceiver2);
        btnRight.setSizeUndefined();

        image = new Embedded(null, c.getImage());
        image.setDebugId("imageDebugId" + this.hashCode());
        image.setSizeUndefined();
        image.addListener(controller.getImageClickListener());

        NavigationPanel npl = new NavigationPanel(c, Side.LEFT);
        NavigationPanel npr = new NavigationPanel(c, Side.RIGHT);

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(btnLeft);
        hl.setComponentAlignment(btnLeft, Alignment.MIDDLE_CENTER);
        hl.addComponent(npl);
        hl.addComponent(image);
        hl.setComponentAlignment(image, Alignment.BOTTOM_CENTER);
        hl.addComponent(npr);
        hl.addComponent(btnRight);
        hl.setComponentAlignment(btnRight, Alignment.MIDDLE_CENTER);

        hl.setSizeUndefined();
        setContent(hl);
        setSizeUndefined();

        viewChanged();
    }

    public void viewChanged() {
        Point p = c.getImageSize();
        if (p == null) {
            c.viewModel.selectViewId(c.app.dbConnector.getNeighborViewTo(c.viewModel.getViewId(), Neighbor.RIGHT));
        } else {
            image.setSource(c.getImage());
            String h = Float.toString(600 * p.y / p.x);
            image.setWidth("600px");
            image.setHeight(h + "px");
        }

        if (getWindow() != null) {
            getWindow().focus();
        }
    }

    public void viewClosing() {
        Window w = getWindow();
        ((Window) w.getParent()).removeWindow(w);
    }
}

@SuppressWarnings("serial")
class NavigationPanel extends CustomComponent implements /* Handler, */ ClickListener {

    Panel p = new Panel();

    // Action left = new ShortcutAction("left",
    // ShortcutAction.KeyCode.ARROW_LEFT, null);
    // Action right = new ShortcutAction("right",
    // ShortcutAction.KeyCode.ARROW_RIGHT, null);

    private ViewController c;
    private Side s;

    public enum Side {
        LEFT, RIGHT
    }

    public NavigationPanel(ViewController c, Side s) {
        setCompositionRoot(p);
        p.setSizeFull();
        setSizeFull();
        this.c = c;
        this.s = s;
        // p.addAction(new LeftShortcutListener(c));
        // p.addActionHandler(this);
        p.addListener(this);
    }

    // public Action[] getActions(Object target, Object sender) {
    // System.out.println("getActions()");
    // return new Action[] { left, right };
    // }

    // public void handleAction(Action action, Object sender, Object target) {
    // if (action == left) {
    // leftHandler();
    // }
    // if (action == right) {
    // rightHandler();
    // }
    // }
    //
    // private void leftHandler() {
    // System.out.println("left");
    // }
    //
    // private void rightHandler() {
    // System.out.println("right");
    // }

    public void click(ClickEvent event) {
        int viewId = c.app.dbConnector.getNeighborViewTo(c.viewModel.getViewId(),
                s == Side.LEFT ? Neighbor.LEFT : Neighbor.RIGHT);
        if (viewId == 0) {
            c.app.getMainWindow().showNotification("No view selected");
        }
        c.viewModel.selectViewId(viewId); // ???
    }
}

@SuppressWarnings("serial")
class MyReceiver2 implements Upload.Receiver, SucceededListener {
    private Path img;
    private ViewController c;
    private Side d;

    public enum Side {
        LEFT, RIGHT
    }

    public MyReceiver2(ViewController c, Side d) {
        this.c = c;
        this.d = d;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {
        OutputStream os = null;
        try {
            String ext = filename.substring(filename.lastIndexOf("."));
            String base = filename.substring(0, filename.lastIndexOf("."));
            img = Files.createTempFile(base, ext);
            os = new FileOutputStream(img.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return os;
    }

    public void uploadSucceeded(SucceededEvent event) {
        int viewId = c.viewModel.getViewId();
        Location loc = c.app.dbConnector.getLocationByViewId(viewId);
        viewId = d == Side.RIGHT ? viewId : c.app.dbConnector.getNeighborViewTo(viewId, Neighbor.LEFT);
        c.viewModel.selectViewId(c.app.dbConnector.addView(loc, img, viewId));
    }
}

// @SuppressWarnings("serial")
// class LeftShortcutListener extends ShortcutListener {
// private ViewController c;
//
// public LeftShortcutListener(ViewController c) {
// this("", KeyCode.ARROW_LEFT, null);
// this.c = c;
// }
//
// public LeftShortcutListener(String caption, int keyCode, int[] modifierKeys)
// {
// super(caption, keyCode, modifierKeys);
// }
//
// @Override
// public void handleAction(Object sender, Object target) {
// System.out.println(c.getImageSize());
// }
// }