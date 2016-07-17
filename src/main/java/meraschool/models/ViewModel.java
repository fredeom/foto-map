package meraschool.models;

import java.util.List;

public class ViewModel {
    private LinkModel currentEditableLink; // TODO: mirror cPrntLnkHndlr func?
    private String viewId;
    private List<LinkModel> links;

    public ViewModel(LinkModel currentEditableLink, String viewId,
            List<LinkModel> links) {
        this.currentEditableLink = currentEditableLink;
        this.viewId = viewId;
        this.links = links;
    }

    public ViewModel() {
        this(null, null, null);
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public List<LinkModel> getLinks() {
        return links;
    }

    public void setLinks(List<LinkModel> links) {
        this.links = links;
    }
}