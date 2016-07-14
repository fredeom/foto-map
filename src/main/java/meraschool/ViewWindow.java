package meraschool;

import java.io.OutputStream;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

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
		btnRight = new Button(">");
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(btnLeft);
		hl.setComponentAlignment(btnLeft, Alignment.MIDDLE_CENTER);
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
		hl.addComponent(image);
		hl.setComponentAlignment(image, Alignment.BOTTOM_CENTER);
		hl.addComponent(btnRight);
		hl.setComponentAlignment(btnRight, Alignment.MIDDLE_CENTER);
		hl.setSizeUndefined();
		addComponent(hl);
	}

	public static class MyReceiver implements com.vaadin.ui.Upload.Receiver {
        public OutputStream receiveUpload(String filename, String mimeType) {
            return System.out;
        }
	}
}
