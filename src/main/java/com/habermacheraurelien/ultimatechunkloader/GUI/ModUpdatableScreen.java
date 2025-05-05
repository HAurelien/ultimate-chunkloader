package com.habermacheraurelien.ultimatechunkloader.GUI;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * An abstract screen class that supports external data-driven updates.
 * <p>
 * This serves as a base for GUI screens that need to respond dynamically to changes
 * in model data or networked information. Subclasses must implement {@link #onDataChange(String)}.
 * </p>
 *
 * @author
 */
public abstract class ModUpdatableScreen extends Screen {

    /**
     * Constructs a screen with the specified title component.
     *
     * @param title The title to be displayed on the screen.
     */
    protected ModUpdatableScreen(Component title) {
        super(title);
    }

    /**
     * Callback invoked when relevant tracked data has changed.
     * <p>
     * This method should be implemented by subclasses to define behavior
     * when specific data IDs trigger a change.
     * </p>
     *
     * @param dataId The identifier for the data that has changed.
     */
    public abstract void onDataChange(String dataId);
}
