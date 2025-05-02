package com.habermacheraurelien.ultimatechunkloader.GUI.screens;

import com.habermacheraurelien.ultimatechunkloader.GUI.ModUpdatableScreen;
import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.model.PlayerAnchorTrackerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AnchorListScreen extends ModUpdatableScreen {

    private PlayerAnchorTrackerModel anchors; // All the anchors
    private final int itemsPerPage = 6; // The number of items per page
    private int currentPage = 0; // The current page the user is on
    private boolean dataChanged = false;
    private boolean canRender = false;

    public AnchorListScreen() {
        super(Component.literal("Anchor Discovery List"));
    }

    @Override
    protected void init() {
        AnchorListDataHolder.addListener(this);
        anchors = AnchorListDataHolder.playerAnchorTrackerModel();
        canRender = (anchors != null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (dataChanged) {
            init();
            dataChanged = false;
        }

        // Render the background similar to inventory
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderPage(guiGraphics); // Render the current page of anchors
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Render the list of anchors for the current page
    private void renderPage(GuiGraphics guiGraphics) {
        if (!canRender) {
            return;
        }
        Font font = Minecraft.getInstance().font;

        this.clearWidgets();

        // Calculate the start and end index based on current page
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, anchors.getIdList().size());

        int listTop = this.height / 10;
        int listBottom = this.height / 10 * 9;
        int listLeft = this.width / 10;
        int listRight = this.width / 10 * 9;

        int listHeight = listBottom - listTop;
        int listWidth = listRight - listLeft;

        int listBorderWidth = listHeight / 10;

        int textMarginLeft = listWidth / 20;
        int textStartLeft = textMarginLeft + listLeft + listBorderWidth;

        int marginBetweenTextEntries = listHeight / 20;
        int textHeight = font.lineHeight;
        int textStartHeight = listTop + listBorderWidth + listHeight / 20;

        // Draw box around the list
        guiGraphics.fill(listLeft, listTop, listRight, listBottom, 0xFF000000); // Box for the list area
        guiGraphics.fill(listLeft + listBorderWidth, listTop + listBorderWidth, listRight - listBorderWidth, listBottom - listBorderWidth, 0xFF1F1F1F); // Box for the list area

        // Loop over the items to display
        for (int i = startIndex; i < endIndex; i++) {
            int itemY = textStartHeight + (i - startIndex) * (marginBetweenTextEntries + textHeight); // Space out items vertically

            // Get the anchor ID (remove the player ID display)
            int anchorId = anchors.getIdList().get(i);
            String chunkText = "Anchor ID: " + anchorId;


            // Draw a background box behind the text (a little padding)
            //guiGraphics.fill(textX - 5, itemY - 5, textWidth + 10, 30, 0x80000000); // Semi-transparent black box
            guiGraphics.drawString(font, chunkText, textStartLeft, itemY, 0xFFFFFF); // White text on listTop of the box

            // Add an interactable button for each anchor
            this.createAnchorButton(guiGraphics, anchorId, itemY - marginBetweenTextEntries / 2,
                    textStartLeft + font.width(chunkText),
                    font.lineHeight + marginBetweenTextEntries, listWidth, false);
        }

        // Draw the separator line between the list and the buttons
        // guiGraphics.fill(this.width / 2 - 100, listTop + (endIndex - startIndex) * 40 + 40, this.width / 2 + 100, listTop + (endIndex - startIndex) * 40 + 42, 0xFF888888); // Separator

        int buttonHeight = 25;
        int buttonStartY = listBottom - listBorderWidth - listHeight / 20 - buttonHeight;
        int buttonStartX = listLeft + listBorderWidth;

        // Add navigation buttons (Previous and Next)
        this.renderNavigationButtons(guiGraphics, buttonStartY, buttonStartX, buttonHeight, listWidth - 2 * listBorderWidth);
    }

    private void renderNavigationButtons(GuiGraphics guiGraphics, int startY, int startX, int buttonHeight, int availableWidth) {
        int buttonWidth = availableWidth / 5;
        int marginX = availableWidth / 5;
        int sideMargin = 5;

        // Previous page button
        this.addRenderableWidget(Button.builder(Component.literal("Previous"), button -> previousPage())
                .bounds(startX + sideMargin, startY, buttonWidth, buttonHeight)
                .build());

        // Close button
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds(startX + buttonWidth + marginX, startY, buttonWidth, buttonHeight)
                .build());

        // Next page button
        this.addRenderableWidget(Button.builder(Component.literal("Next"), button -> nextPage())
                .bounds(availableWidth + startX - buttonWidth - sideMargin, startY, buttonWidth, buttonHeight)
                .build());

    }

    // Create a button for each anchor
    private void createAnchorButton(GuiGraphics guiGraphics, int anchorId, int startY, int startX,
                                    int maxAvailableHeight, int listWidth, boolean activated) {
        int marginX = 10;

        int finalStartX = startX + marginX;
        int finalWidth = listWidth - startX - marginX;

        int finalStartY = startY;
        int finalHeight = maxAvailableHeight;

        if(activated){
            this.addRenderableWidget(Button.builder(Component.literal("Turn off"), button ->
                    {tmpSendMessage("Button clicked for id turned off : " + anchorId);})
                    .bounds(finalStartX, finalStartY, finalWidth, finalHeight)
                    .build());
        }
        else{
            this.addRenderableWidget(Button.builder(Component.literal("Turn on"), button ->
                    {tmpSendMessage("Button clicked for id turned on : " + anchorId);})
                    .bounds(finalStartX, finalStartY, finalWidth, finalHeight)
                    .build());
        }
    }

    // Go to the previous page
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    private void tmpSendMessage(String message){
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(message));
    }

    // Go to the next page
    private void nextPage() {
        if ((currentPage + 1) * itemsPerPage < anchors.getIdList().size()) {
            currentPage++;
        }
    }

    @Override
    public void onClose() {
        AnchorListDataHolder.removeListener(this);
        super.onClose();
        Minecraft.getInstance().setScreen(null); // Close the screen
    }

    @Override
    public void onDataChange(String dataId) {
        if (dataId.equals(AnchorListDataHolder.ID_PLAYER_ANCHOR_TRACKER_MODEL)) {
            dataChanged = true;
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }
}
