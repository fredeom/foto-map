package meraschool.controllers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import meraschool.App;
import meraschool.models.Location;
import meraschool.models.LocationModel;
import meraschool.models.ViewModel;
import meraschool.views.ViewModelListener;

public class PreviewController extends ViewController {

    public final int viewLevel;
    public final LocationModel locationModel;

    public PreviewController(App app, int viewLevel, ViewModel viewModel) {
        this(app, viewLevel, new LocationModel(), viewModel);
    }

    public PreviewController(App app, int viewLevel, LocationModel locationModel, ViewModel viewModel) {
        super(app, viewModel);
        this.viewLevel = viewLevel;
        this.locationModel = locationModel;
        viewModel.addListener(new MyPreViewModelListener());
    }

    @SuppressWarnings("serial")
    class MyReceiver implements Upload.Receiver, SucceededListener {
        private TextField tf;
        private Path img;
        private String ext;

        public MyReceiver(TextField tf) {
            this.tf = tf;
        }

        public OutputStream receiveUpload(String filename, String mimeType) {
            OutputStream os = null;
            try {
                ext = filename.substring(filename.lastIndexOf("."));
                String base = filename.substring(0, filename.lastIndexOf("."));
                img = Files.createTempFile(base, ext);
                os = new FileOutputStream(img.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return os;
        }

        public void uploadSucceeded(SucceededEvent event) {
            viewModel.selectViewId(app.dbConnector.addView(new Location(0, (String) tf.getValue()), img, 0));
        }
    }

    @SuppressWarnings("serial")
    public Upload.Receiver getAddLocationClickListener(final TextField tf) {
        return new MyReceiver(tf);
    }

    @SuppressWarnings("serial")
    public ClickListener getRemoveSelectedViewClickListener() {
        return new ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (viewModel.getViewId() == 0) {
                    app.getMainWindow().showNotification("No view selected");
                    return;
                } else {
                    app.dbConnector.removeView(viewModel.getViewId());
                }
                viewModel.selectViewId(app.dbConnector.getFirstViewByLocation(locationModel.getSelectedLocation()));
            }
        };
    }

    @SuppressWarnings("serial")
    public ClickListener getRemoveSelectedLocationClickListener() {
        return new ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (locationModel.getSelectedLocation() == null) {
                    app.getMainWindow().showNotification("No location selected");
                } else {
                    app.dbConnector.removeLocation(locationModel.getSelectedLocation());
                }
                viewModel.selectViewId(0);
            }
        };
    }

    @SuppressWarnings("serial")
    public ClickListener getLocationButtonClickListener(final Location l) {
        return new ClickListener() {
            public void buttonClick(ClickEvent event) {
                viewModel.selectViewId(app.dbConnector.getFirstViewByLocation(l));
            }
        };
    }

    class MyPreViewModelListener implements ViewModelListener {
        public void viewChanged() {
            locationModel.setLocationList(app.dbConnector.getLocationList());
            locationModel.selectLocation(app.dbConnector.getLocationByViewId(viewModel.getViewId()));
        }

        public void viewClosing() {
            System.out.println("View panel and Location panel have common window, no need to remove it twice");
        }
    }
}