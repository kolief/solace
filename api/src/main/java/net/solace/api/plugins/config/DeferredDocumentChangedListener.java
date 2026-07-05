package net.solace.api.plugins.config;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DeferredDocumentChangedListener
implements DocumentListener {
    private final Timer timer;
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener>(25);

    public DeferredDocumentChangedListener() {
        this.timer = new Timer(200, e -> this.fireStateChanged());
        this.timer.setRepeats(false);
    }

    public void addChangeListener(ChangeListener listener) {
        this.listeners.add(listener);
    }

    private void fireStateChanged() {
        if (!this.listeners.isEmpty()) {
            ChangeEvent evt = new ChangeEvent(this);
            for (ChangeListener listener : this.listeners) {
                listener.stateChanged(evt);
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.timer.restart();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.timer.restart();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.timer.restart();
    }
}

