package com.habermacheraurelien.ultimatechunkloader.GUI;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ModUpdatableScreen extends Screen {
    protected ModUpdatableScreen(Component title) {
        super(title);
    }

    public abstract void onDataChange(String dataId);
}
