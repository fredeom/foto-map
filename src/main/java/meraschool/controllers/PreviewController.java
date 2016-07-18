package meraschool.controllers;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

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
import meraschool.views.LocationModelListener;

public class PreviewController extends ViewController {

    public final int viewLevel;
    public final LocationModel locationModel;
    private Callable<Void> strangeHandler; // needed for left button clicks on
                                           // images but not on links
                                           // use of Runnable instead ?

    public PreviewController(App app, int viewLevel, ViewModel viewModel) {
        this(app, viewLevel, new LocationModel(), viewModel);
    }

    public PreviewController(App app, int viewLevel, LocationModel locationModel, ViewModel viewModel) {
        super(app, viewModel);
        this.viewLevel = viewLevel;
        this.locationModel = locationModel;
        locationModel.addListener(new MyLocationModelListener());
        // load all locations // db call
        // setSelectedLocationByViewId(viewModel.getViewId()) // db call
    }

    public void setStrangeHandler(Callable<Void> strangeHandler) {
        this.strangeHandler = strangeHandler;
    }

    @SuppressWarnings("serial")
    class MyReceiver implements Upload.Receiver, SucceededListener {
        private TextField tf;
        private Path img;

        public MyReceiver(TextField tf) {
            this.tf = tf;
        }

        public OutputStream receiveUpload(String filename, String mimeType) {
            OutputStream os = null;
            try {
                String ext = filename.substring(filename.indexOf("."));
                String base = filename.substring(0, filename.indexOf("."));
                img = Files.createTempFile(base, ext);
                os = new FileOutputStream(img.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return os;
        }

        public void uploadSucceeded(SucceededEvent event) {
            String newLocationName = (String) tf.getValue();
            int newViewId = app.dbConnector.addView(new Location(0, newLocationName), img, 0);
            locationModel.setLocationList(app.dbConnector.getLocationList());
            locationModel.selectLocation(app.dbConnector.getLocationByViewId(newViewId));
            viewModel.selectViewId(newViewId);
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
                app.dbConnector.removeView(viewModel.viewId);
                viewModel.selectViewId(app.dbConnector.getFirstViewByLocation(locationModel.selectedLocation));
                locationModel.setLocationList(app.dbConnector.getLocationList());
                locationModel.selectLocation(app.dbConnector.getLocationByViewId(viewModel.getViewId()));
            }
        };
    }

    @SuppressWarnings("serial")
    public ClickListener getRemoveSelectedLocationClickListener() {
        return new ClickListener() {
            public void buttonClick(ClickEvent event) {
                app.dbConnector.removeLocation(locationModel.selectedLocation);
                locationModel.setLocationList(app.dbConnector.getLocationList());
            }
        };
    }

    @SuppressWarnings("serial")
    public ClickListener getLocationButtonClickListener(final Location l) {
        return new ClickListener() {
            public void buttonClick(ClickEvent event) {
                locationModel.selectLocation(l);
            }
        };
    }

    class MyLocationModelListener implements LocationModelListener {
        public void selectedLocationChanged() {
            viewModel.selectViewId(app.dbConnector.getFirstViewByLocation(locationModel.selectedLocation));
        }

        public void locationListChanged() { // ???
        }
    }
}