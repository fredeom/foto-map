package meraschool.models;

public class LinkModel {
    private int id;
    private int viewId;
    private double x;
    private double y;
    public int viewRefId;

    public final double RADIUS = 10;

    public LinkModel() {
        this(0, 0, 0, 0, 0);
    }

    public LinkModel(int id, int viewId, double x, double y, int viewRefId) {
        this.id = id;
        this.viewId = viewId;
        this.x = x;
        this.y = y;
        this.viewRefId = viewRefId;
    }

    public int getId() {
        return id;
    }

    public int getViewId() {
        return viewId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean catches(int x, int y, int width, int height) {
        double x1 = this.x * width;
        double y1 = this.y * height;
        return Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)) < RADIUS;
    }

    @Override
    public String toString() {
        return "LinkModel [id=" + id + ", viewId=" + viewId + ", x=" + x + ", y=" + y + ", viewRefId=" + viewRefId
                + ", RADIUS=" + RADIUS + "]";
    }
}