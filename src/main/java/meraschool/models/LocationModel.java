package meraschool.models;

import java.util.ArrayList;
import java.util.List;

import meraschool.views.LocationModelListener;

public class LocationModel {
    public Location selectedLocation;
    public List<Location> locations;
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
}