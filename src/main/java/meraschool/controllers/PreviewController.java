package meraschool.controllers;

import java.util.concurrent.Callable;

import meraschool.App;
import meraschool.models.LocationModel;
import meraschool.models.ViewModel;

public class PreviewController extends ViewController {

    public final int viewLevel;
    private Callable<Void> strangeHandler; // needed for left button clicks on
                                           // images but not on links
                                           // use of Runnable instead ?

    public PreviewController(App app, int viewLevel, ViewModel model) {
        super(app, model);
        this.viewLevel = viewLevel;
    }

    public PreviewController(App app, int viewLevel, LocationModel locationModel, ViewModel viewModel) {
        this(app, viewLevel, viewModel);
        // load all locations // db call
        // setSelectedLocationByViewId(viewModel.getViewId()) // db call
    }

    public void setStrangeHandler(Callable<Void> strangeHandler) {
        this.strangeHandler = strangeHandler;
    }
}