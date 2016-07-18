package meraschool.models;

import java.util.ArrayList;
import java.util.List;

import meraschool.views.ViewModelListener;

public class ViewModel {
    public int viewId;
    private List<ViewModelListener> listeners = new ArrayList<ViewModelListener>();

    public ViewModel(int viewId) {
        this.viewId = viewId;
    }

    public ViewModel() {
        this(0);
    }

    public int getViewId() {
        return viewId;
    }

    public void addListener(ViewModelListener listener) {
        listeners.add(listener);
    }

    public void selectViewId(int viewId) {
        this.viewId = viewId;
        fireViewChanged();
    }

    public void fireViewChanged() {
        for (ViewModelListener l : listeners) {
            l.viewChanged();
        }
    }

    // Bring interfaces home!!! (ViewModelListener)
}