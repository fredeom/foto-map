package meraschool.views;

import java.awt.Point;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

import meraschool.DbConnectorImpl.Neighbor;
import meraschool.controllers.ViewController;

@SuppressWarnings("serial")
public class ViewPanel extends Panel implements ViewModelListener {

    private ViewController c;
    private Embedded image;

    public ViewPanel(ViewController controller) {
        this.c = controller;
        c.viewModel.addListener(this);
        Button btnLeft = new Button("<");
        btnLeft.addShortcutListener(new MyDebugListener1("<<arrow=left<<", KeyCode.ARROW_LEFT, null));
        btnLeft.setSizeUndefined();
        Button btnRight = new Button(">");
        btnRight.setSizeUndefined();
        image = new Embedded(null, c.getImage());
        image.setDebugId("imageDebugId" + this.hashCode());
        image.setSizeUndefined();
        image.addListener(controller.getImageClickListener());
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(btnLeft);
        hl.setComponentAlignment(btnLeft, Alignment.MIDDLE_CENTER);
        hl.addComponent(image);
        hl.setComponentAlignment(image, Alignment.BOTTOM_CENTER);
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
class MyDebugListener1 extends ShortcutListener {
    public MyDebugListener1(String caption, int keyCode, int[] modifierKeys) {
        super(caption, keyCode, modifierKeys);
        keyCode = KeyCode.ARROW_LEFT;
    }

    @Override
    public void handleAction(Object sender, Object target) {
        System.out.println("LEFT: " + sender + " | " + target + " | " + getCaption());
    }
}