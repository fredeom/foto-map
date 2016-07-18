package meraschool.views;

import java.io.Serializable;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import meraschool.controllers.PreviewController;

@SuppressWarnings("serial")
public class PreviewWindow extends Window {

    public PreviewWindow(PreviewController c, Window parent) {
        super("Down the Rabbit Hole: " + c.viewLevel);
        c.setWindow(this);
        LocationPanel locationPanel = new LocationPanel(c);
        ViewPanel viewPanel = new ViewPanel(c);

        Upload btnAddLocation = new Upload();
        TextField tfNewLocationName = new TextField();
        Button btnRemoveSelectedLocation = new Button("Remove Selected Location");
        Button btnRemoveSelectedView = new Button("Remove Selected View");

        btnAddLocation.setImmediate(true);
        btnAddLocation.setButtonCaption("Add Location");
        Serializable myReceiver = c.getAddLocationClickListener(tfNewLocationName);
        btnAddLocation.setReceiver((Receiver) myReceiver);
        btnAddLocation.addListener((SucceededListener) myReceiver);
        tfNewLocationName.setValue("location");
        btnRemoveSelectedLocation.addListener(c.getRemoveSelectedLocationClickListener());
        btnRemoveSelectedView.addListener(c.getRemoveSelectedViewClickListener());

        HorizontalLayout hl_ = new HorizontalLayout();
        hl_.addComponent(btnAddLocation);
        hl_.addComponent(tfNewLocationName);

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(hl_);
        vl.addComponent(btnRemoveSelectedLocation);
        vl.addComponent(btnRemoveSelectedView);
        vl.addComponent(locationPanel);
        vl.setSizeUndefined();

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(vl);
        hl.addComponent(viewPanel);
        hl.setSizeUndefined();

        setContent(hl);
        setSizeUndefined();
        addListener(new CloseListener1(parent));
    }
}