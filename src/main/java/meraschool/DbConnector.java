package meraschool;

import java.nio.file.Path;
import java.util.List;

import meraschool.models.Location;

public interface DbConnector {

    public int addView(Location location, Path image, int viewIdAfter);

    // Retrive list of all locations
    public List<Location> getLocationList();

    // Get Location by view id
    public Location getLocationByViewId(int viewId);

    // Get first View in location
    public int getFirstViewByLocation(Location location);

    public Path getImagePathByViewId(int viewId);
    // public void setImage(Embedded image, int viewId);

    public void removeLocation(Location location);

    public void removeView(int viewId);
}