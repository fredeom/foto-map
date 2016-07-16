package meraschool.models;

import java.util.List;
import java.util.concurrent.Callable;

public class ViewModel {
    private LinkModel currentEditableLink;
    private String currentViewId;
    private String currentPictureSource;
    private List<LinkModel> links;
    private Callable<Void> currentParentLinkHandler;

    // TODO: what parameters to pass
    public ViewModel() {
        
    }

    // TODO: Safe current view and call
    // currentParentLinkHandler.call() -> to safe link data
    public void saveView() {
    }

    public void loadView(String viewId) {
    }
}
