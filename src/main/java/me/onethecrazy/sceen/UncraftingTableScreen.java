package me.onethecrazy.sceen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.onethecrazy.TableOfUncrafting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UncraftingTableScreen extends HandledScreen<UncraftingTableScreenHandler> {
    private static final Identifier BG_TEXTURE =
            Identifier.of(TableOfUncrafting.MOD_ID, "textures/gui/uncrafting_table.png");

    public UncraftingTableScreen(UncraftingTableScreenHandler handler,
                         PlayerInventory inv,
                         Text title) {
        super(handler, inv, title);
        backgroundWidth = 176;   // size of your texture
        backgroundHeight = 166;
    }

    /** centre the title */
    @Override protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);                    // darkens the world behind
        super.render(ctx, mouseX, mouseY, delta); // draws slots & widgets
        drawMouseoverTooltip(ctx, mouseX, mouseY);
    }

    /** draw the texture; called by super.render */
    @Override
    protected void drawBackground(DrawContext ctx,
                                  float delta,
                                  int mouseX,
                                  int mouseY) {
        int x = (width  - backgroundWidth)  / 2;
        int y = (height - backgroundHeight) / 2;

        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        ctx.drawTexture(BG_TEXTURE, x, y, 0, 0,
                backgroundWidth, backgroundHeight);
    }
}
