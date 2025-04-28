package com.habermacheraurelien.ultimatechunkloader.GUI.screens;

import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class AnchorListScreen extends Screen {

    private final PlayerAnchorTrackerModel anchors; // All the anchors
    private final int itemsPerPage = 5; // The number of items per page
    private int currentPage = 0; // The current page the user is on

    public AnchorListScreen(PlayerAnchorTrackerModel anchors) {
        super(Component.literal("Anchor Discovery List"));
        this.anchors = anchors;
    }

    @Override
    protected void init() {
        // Previous page button
        this.addRenderableWidget(Button.builder(Component.literal("Previous"), button -> previousPage())
                .bounds(this.width / 2 - 100, this.height - 40, 90, 20)
                .build());

        // Next page button
        this.addRenderableWidget(Button.builder(Component.literal("Next"), button -> nextPage())
                .bounds(this.width / 2 + 10, this.height - 40, 90, 20)
                .build());

        // Close button
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 70, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick); // Render background
        this.renderPage(guiGraphics); // Render the current page of anchors
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Render the list of anchors for the current page
    private void renderPage(GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;

        // Calculate start and end index based on current page
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, anchors.getIdList().size());

        int top = this.height + 32; // Start rendering below the header

        for (int i = startIndex; i < endIndex; i++) {
            int itemY = top + (i - startIndex) * 20; // Adjust item spacing
            guiGraphics.drawString(font, "Player " + anchors.getPlayerId() + "'s Anchors", this.width + 10, itemY, 0); // Render player ID

            // Display each anchor ID from the player's discovered anchors
            List<Integer> anchorIds = anchors.getIdList();
            for (int j = 0; j < anchorIds.size(); j++) {
                int anchorId = anchorIds.get(j);
                guiGraphics.drawString(font, "Anchor ID: " + anchorId, this.width + 20, itemY + (j + 1) * 20, 0);
            }

            // Add interactable button for each anchor
            this.createAnchorButton(guiGraphics, anchors, itemY);
        }
    }

    // Create a button for each anchor
    private void createAnchorButton(GuiGraphics guiGraphics, PlayerAnchorTrackerModel anchor, int itemY) {
        Button button = Button.builder(Component.literal("Interact"), (btn) -> {
                    // Handle interaction with anchor (you could open a new screen or perform an action)
                    System.out.println("Interacting with " + anchor.getPlayerId() + "'s anchor");
                })
                .bounds(this.width + 150, itemY, 100, 20)
                .build();

        button.render(guiGraphics, 0, 0, 0); // Render the button
    }

    // Go to the previous page
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    // Go to the next page
    private void nextPage() {
        if ((currentPage + 1) * itemsPerPage < anchors.getIdList().size()) {
            currentPage++;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().setScreen(null); // Close the screen
    }
}