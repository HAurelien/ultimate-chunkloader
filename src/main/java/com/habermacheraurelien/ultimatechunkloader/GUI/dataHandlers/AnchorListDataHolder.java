package com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers;

import com.habermacheraurelien.ultimatechunkloader.GUI.ModUpdatableScreen;
import com.habermacheraurelien.ultimatechunkloader.GUI.model.ScreenAnchorTrackerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the screen-level data model for anchor discovery and manages listeners
 * that are interested in receiving updates when the model changes.
 *
 * <p>This class is typically used to share the anchor tracking data across GUI screens
 * and trigger re-renders when the data is modified.</p>
 */
public class AnchorListDataHolder {

    /**
     * Identifier for listeners to know the type of data that has changed.
     */
    public static final String ID_PLAYER_ANCHOR_TRACKER_MODEL = "player_anchor_tracker_model";

    private static ScreenAnchorTrackerModel screenAnchorTrackerModel;
    private static final List<ModUpdatableScreen> listeners = new ArrayList<>();

    /**
     * Gets the current {@link ScreenAnchorTrackerModel} shared with screens.
     *
     * @return the current anchor tracker model
     */
    public static ScreenAnchorTrackerModel screenAnchorTrackerModel() {
        return screenAnchorTrackerModel;
    }

    /**
     * Updates the current {@link ScreenAnchorTrackerModel} and notifies all listeners.
     *
     * @param newModel the new model to use
     */
    public static void setScreenAnchorTrackerModel(ScreenAnchorTrackerModel newModel) {
        screenAnchorTrackerModel = newModel;
        onChange();
    }

    /**
     * Notifies all registered listeners that the anchor model has changed.
     */
    private static void onChange() {
        for (ModUpdatableScreen listener : listeners) {
            listener.onDataChange(ID_PLAYER_ANCHOR_TRACKER_MODEL);
        }
    }

    /**
     * Registers a screen listener that will be notified when the model changes.
     *
     * @param listener the screen to register
     */
    public static void addListener(ModUpdatableScreen listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a screen listener from update notifications.
     *
     * @param listener the screen to remove
     */
    public static void removeListener(ModUpdatableScreen listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the list of all registered listeners.
     * Mostly for debugging or utility purposes.
     *
     * @return the list of listeners
     */
    public static List<ModUpdatableScreen> getListeners() {
        return listeners;
    }
}