package meraschool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ViewWindow extends Window {

	private Controller controller;

	private Button btnLeft;
	private Button btnRight;
	private Embedded image;

	public ViewWindow(Controller controller) {
		super("Use left and right mouse buttons to navigate");
		this.controller = controller;
		createAndShowGUI();
	}

	private void createAndShowGUI() {
		btnLeft = new Button("<");
		btnLeft.setSizeUndefined();
		btnRight = new Button(">");
		btnRight.setSizeUndefined();
		image = new Embedded("", new ClassResource("/1.jpg", controller.getApplication()));
		image.setSizeUndefined();
		image.addListener(new ClickListener() {
            public void click(ClickEvent event) {
                System.out.println("Click on image " + event.getRelativeX() + " " + event.getRelativeY() + " " + event.getButton());

                // TODO: add window with upload button
                Upload upload = new Upload(null, null);
                upload.setImmediate(true);
                upload.setButtonCaption("Upload me");
                upload.setReceiver(new MyReceiver());
                upload.setVisible(true);

                addComponent(upload);
            }
        });
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

	public static class MyReceiver implements com.vaadin.ui.Upload.Receiver {
        public OutputStream receiveUpload(String filename, String mimeType) {
            OutputStream os = null;
            try {
                os = new FileOutputStream(new File(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return System.out;
        }
	}
}
