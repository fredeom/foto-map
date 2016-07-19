package meraschool.models;

import java.util.ArrayList;
import java.util.List;

import meraschool.views.ViewModelListener;

public class ViewModel {
    private int viewId;
    public final LinkModel editLink;
    public final ViewModel parentViewModel;
    private List<ViewModelListener> listeners = new ArrayList<ViewModelListener>();

    public ViewModel(int viewId, LinkModel editLink, ViewModel parentViewModel) {
        this.viewId = viewId;
        this.editLink = editLink;
        this.parentViewModel = parentViewModel;
    }

    public ViewModel() {
        this(0, null, null);
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

    public void fireViewClosing() {
        for (ViewModelListener l : listeners) {
            l.viewClosing();
        }
    }
}