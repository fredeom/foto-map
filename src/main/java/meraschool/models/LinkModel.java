package meraschool.models;

public class LinkModel {
    public double x;
    public double y;
    public String viewId;

    public LinkModel() {
        this(0, 0, null);
    }

    public LinkModel(double x, double y, String viewId) {
        this.x = x;
        this.y = y;
        this.viewId = viewId;
    }
}
