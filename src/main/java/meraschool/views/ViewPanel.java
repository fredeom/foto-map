package meraschool.views;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.Paintable.RepaintRequestEvent;
import com.vaadin.terminal.Paintable.RepaintRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

import meraschool.controllers.ViewController;

@SuppressWarnings("serial")
public class ViewPanel extends Panel {

    public ViewPanel(ViewController controller) {
        Button btnLeft = new Button("<");
        btnLeft.addShortcutListener(new MyDebugListener1("<<arrow=left<<", KeyCode.ARROW_LEFT, null));
        btnLeft.setSizeUndefined();
        Button btnRight = new Button(">");
        btnRight.setSizeUndefined();
        Embedded image = new Embedded(null, controller.getImage());
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
    }

    public void reloadView() {
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

// image.addListener(new MyDebugListener2()); // controller.action2()
@SuppressWarnings("serial")
class MyDebugListener2 implements RepaintRequestListener {
    public void repaintRequested(RepaintRequestEvent event) {
        System.out.println(event.getSource());
    }
}