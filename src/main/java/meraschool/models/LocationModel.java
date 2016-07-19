package meraschool.models;

import java.util.ArrayList;
import java.util.List;

import meraschool.views.LocationModelListener;

public class LocationModel {

    private Location selectedLocation;
    private List<Location> locations;
    private List<LocationModelListener> listeners = new ArrayList<LocationModelListener>();

    public LocationModel() {
        this(null, new ArrayList<Location>());
    }

    public LocationModel(Location selectedLocation, List<Location> locations) {
        this.selectedLocation = selectedLocation;
        this.locations = locations;
    }

    public void addListener(LocationModelListener listener) {
        listeners.add(listener);
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void selectLocation(Location location) {
        selectedLocation = location;
        fireSelectedLocationChanged();
    }

    public void setLocationList(List<Location> locationList) {
        this.locations = locationList;
        fireLocationListChanged();
    }

    private void fireSelectedLocationChanged() {
        for (LocationModelListener l : listeners) {
            l.selectedLocationChanged();
        }
    }

    private void fireLocationListChanged() {
        for (LocationModelListener l : listeners) {
            l.locationListChanged();
        }
    }

    @Override
    public String toString() {
        return "LocationModel [selectedLocation=" + selectedLocation + ", locations=" + locations + ", listeners="
                + listeners + "]";
    }
}