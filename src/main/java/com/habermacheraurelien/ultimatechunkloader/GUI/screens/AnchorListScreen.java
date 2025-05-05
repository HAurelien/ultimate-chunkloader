package com.habermacheraurelien.ultimatechunkloader.GUI.screens;

import com.habermacheraurelien.ultimatechunkloader.GUI.ModUpdatableScreen;
import com.habermacheraurelien.ultimatechunkloader.GUI.actionHandlers.AnchorListActionsHandler;
import com.habermacheraurelien.ultimatechunkloader.GUI.dataHandlers.AnchorListDataHolder;
import com.habermacheraurelien.ultimatechunkloader.GUI.model.ScreenAnchorTrackerModel;
import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.model.ChunkAnchorBlockModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
/**
 * GUI screen for displaying and interacting with the player's known chunk anchors.
 * <p>
 * This screen supports paginated display, editable anchor names, anchor state toggling,
 * and forgetting anchors. It dynamically adjusts its height to 70% of the screen for responsive layout.
 * </p>
 */
public class AnchorListScreen extends ModUpdatableScreen {

    /** Number of items (rows) displayed per page, calculated dynamically based on screen height. */
    private int itemsPerPage;

    /** Current index of the page being displayed. */
    private int currentPage = 0;

    /** Flag indicating whether the screen should refresh due to model data change. */
    private boolean dataChanged = false;

    /** Model containing anchor data available to this screen. */
    private ScreenAnchorTrackerModel anchors;

    /** The root layout of the UI components on this screen. */
    private LinearLayout layout;

    /**
     * Constructs a new AnchorListScreen with a translatable title.
     */
    public AnchorListScreen() {
        super(Component.translatable("gui.ultimatechunkloader.title.anchor_discovery_list"));
    }

    /**
     * Initializes or reinitializes all screen elements, layout, and widgets.
     * Handles pagination logic, button callbacks, and anchor interactivity.
     */
    @Override
    protected void init() {
        this.clearWidgets();
        AnchorListDataHolder.addListener(this);
        anchors = AnchorListDataHolder.screenAnchorTrackerModel();
        if (anchors == null) return;

        layout = LinearLayout.vertical().spacing(5);
        Font font = Minecraft.getInstance().font;

        int availableHeight = (int) (this.height * 0.7);
        int fontLineHeight = font.lineHeight;
        int rowHeight = (fontLineHeight + 10) * 2;
        this.itemsPerPage = Math.max(1, availableHeight / rowHeight);

        List<ChunkAnchorBlockModel> list = anchors.chunkAnchorBlockModelList();
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, list.size());

        ResourceLocation ACTIVE_ICON = ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, "textures/gui/anchor_on.png");
        ResourceLocation INACTIVE_ICON = ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, "textures/gui/anchor_off.png");
        ResourceLocation REMOVE_NORMAL = ResourceLocation.fromNamespaceAndPath(UltimateChunkLoaderMod.MOD_ID, "anchor_remove_button");

        for (int i = startIndex; i < endIndex; i++) {
            ChunkAnchorBlockModel anchor = list.get(i);
            LinearLayout row = LinearLayout.horizontal().spacing(10);
            LinearLayout leftColumn = LinearLayout.vertical().spacing(2);

            EditBox nameBox = new EditBox(font, 150, 20, Component.literal("Anchor name"));
            nameBox.setValue(anchor.getName());
            nameBox.setResponder(name -> {
                anchor.setName(name);
                AnchorListActionsHandler.onAnchorChangeProperty(anchor);
            });
            leftColumn.addChild(nameBox);

            leftColumn.addChild(new StringWidget(
                    Component.translatable("gui.ultimatechunkloader.dimension", anchor.getDimension().location()
                            .toString()), font));
            row.addChild(leftColumn);

            Component buttonText = anchor.isActive() ?
                    Component.translatable("gui.ultimatechunkloader.anchor_state.on") :
                    Component.translatable("gui.ultimatechunkloader.anchor_state.off");

            Button toggleButton = Button.builder(buttonText,
                    b -> AnchorListActionsHandler.onAnchorUpdateState(anchor)
            ).width(100).build();
            toggleButton.setTooltip(Tooltip.create(Component.translatable("tooltip.ultimatechunkloader.anchor_state")));
            row.addChild(toggleButton);

            ImageButton removeButton = new ImageButton(
                    0, 0, 16, 16,
                    new WidgetSprites(REMOVE_NORMAL, REMOVE_NORMAL),
                    b -> AnchorListActionsHandler.onAnchorRemove(anchor)
            );
            removeButton.setTooltip(Tooltip.create(Component.translatable("gui.ultimatechunkloader.forget_anchor")));
            row.addChild(removeButton);

            ResourceLocation icon = anchor.isActive() ? ACTIVE_ICON : INACTIVE_ICON;
            ImageWidget statusIcon = ImageWidget.texture(10, 10, icon, 10, 10);
            row.addChild(statusIcon);

            layout.addChild(row);
        }

        // Fill empty rows for consistent layout height
        int actualDisplayed = endIndex - startIndex;
        for (int i = actualDisplayed; i < itemsPerPage; i++) {
            layout.addChild(new SpacerElement(0, rowHeight - 5));
        }

        int totalPages = (int) Math.ceil((double) list.size() / itemsPerPage);
        int displayPage = currentPage + 1;

        Component pageText = Component.translatable("gui.ultimatechunkloader.page", displayPage, totalPages);
        StringWidget pageIndicator = new StringWidget(pageText, font);
        pageIndicator.setColor(0xFFFFFF);

        FrameLayout centeredPageIndicator = new FrameLayout();
        centeredPageIndicator.addChild(pageIndicator);
        FrameLayout.centerInRectangle(centeredPageIndicator, this.getRectangle());
        layout.addChild(centeredPageIndicator);

        LinearLayout navButtons = LinearLayout.horizontal().spacing(10);
        navButtons.addChild(Button.builder(Component.translatable("gui.ultimatechunkloader.previous"),
                b -> previousPage()).width(80).build());
        navButtons.addChild(Button.builder(Component.translatable("gui.ultimatechunkloader.close"),
                b -> onClose()).width(80).build());
        navButtons.addChild(Button.builder(Component.translatable("gui.ultimatechunkloader.next"),
                b -> nextPage()).width(80).build());

        layout.addChild(new SpacerElement(0, 5));
        layout.addChild(navButtons);

        layout.arrangeElements();
        FrameLayout.centerInRectangle(layout, this.getRectangle());
        layout.visitWidgets(this::addRenderableWidget);
    }

    /**
     * Switches to the next page in the anchor list. Wraps around to the first page.
     */
    private void nextPage() {
        int totalPages = (int) Math.ceil((double) anchors.chunkAnchorBlockModelList().size() / itemsPerPage);
        currentPage = (currentPage + 1) % totalPages;
        init();
    }

    /**
     * Switches to the previous page in the anchor list. Wraps around to the last page.
     */
    private void previousPage() {
        int totalPages = (int) Math.ceil((double) anchors.chunkAnchorBlockModelList().size() / itemsPerPage);
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        init();
    }

    /**
     * Renders the GUI screen, including the layered background and centered title.
     *
     * @param guiGraphics The rendering context.
     * @param mouseX      Mouse X position.
     * @param mouseY      Mouse Y position.
     * @param partialTick Tick interpolation.
     */
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (dataChanged) {
            init();
            dataChanged = false;
        }

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        if (layout != null) {
            ScreenRectangle rect = layout.getRectangle();
            int paddingBackground = 12;
            int paddingMiddle = 10;
            int paddingInner = 4;

            guiGraphics.fill(
                    rect.left() - paddingBackground, rect.top() - paddingBackground,
                    rect.right() + paddingBackground, rect.bottom() + paddingBackground,
                    0xFF555555
            );
            guiGraphics.fill(
                    rect.left() - paddingMiddle, rect.top() - paddingMiddle,
                    rect.right() + paddingMiddle, rect.bottom() + paddingMiddle,
                    0xFFC6C6C6
            );
            guiGraphics.fill(
                    rect.left() - paddingInner, rect.top() - paddingInner,
                    rect.right() + paddingInner, rect.bottom() + paddingInner,
                    0xFF8B8B8B
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    /**
     * Called when the user closes the screen. Unregisters data listeners.
     */
    @Override
    public void onClose() {
        AnchorListDataHolder.removeListener(this);
        super.onClose();
        Minecraft.getInstance().setScreen(null);
    }

    /**
     * Called when anchor tracking data changes and screen needs to refresh.
     *
     * @param dataId The identifier of the changed data.
     */
    @Override
    public void onDataChange(String dataId) {
        if (dataId.equals(AnchorListDataHolder.ID_PLAYER_ANCHOR_TRACKER_MODEL)) {
            dataChanged = true;
        }
    }

    /**
     * Defines the initial input focus behavior (none in this case).
     */
    @Override
    protected void setInitialFocus() {}

    /**
     * Renders the screen background (dark translucent overlay).
     */
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xA0000000);
    }
}
