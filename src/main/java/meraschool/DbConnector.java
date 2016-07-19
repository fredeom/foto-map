package meraschool;

import java.nio.file.Path;
import java.util.List;

import meraschool.DbConnectorImpl.Neighbor;
import meraschool.models.LinkModel;
import meraschool.models.Location;

public interface DbConnector {

    public int addView(Location location, Path image, int viewIdAfter);

    public int addLink(int viewId, double x, double y, int refViewId);

    public void editLink(LinkModel lm);

    public List<Location> getLocationList();

    public Location getLocationByViewId(int viewId);

    public int getFirstViewByLocation(Location location);

    public int getNeighborViewTo(int viewId, Neighbor n);

    public Path getImagePathByViewId(int viewId);

    public LinkModel getLinkById(int linkId);

    public List<LinkModel> getLinksByViewId(int viewId);

    public void removeLocation(Location location);

    public void removeView(int viewId);

    public void removeLink(LinkModel link);
}