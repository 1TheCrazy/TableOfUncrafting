// UncraftingTableScreenHandler.java
package me.onethecrazy.sceen;

import me.onethecrazy.TableOfUncrafting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class UncraftingTableScreenHandler extends ScreenHandler {

    /** 3×3 input grid + 1 result slot (same shape as CraftingTable) */
    private final CraftingInventory input = new CraftingInventory(this, 1, 1);
    private final CraftingInventory result = new CraftingInventory(this, 3, 3);
    private final World world;
    private final ScreenHandlerContext ctx;

    public UncraftingTableScreenHandler(int syncId, PlayerInventory playerInv, ScreenHandlerContext context) {
        super(ModScreenHandlers.UNCRAFTING_SCREEN, syncId);
        this.world = playerInv.player.getWorld();
        this.ctx = context;

        /* ----- input slot ----- */
        // slot‑index 0
        this.addSlot(new Slot(input, 0, 48, 35));



        /* ----- output grid ----- */
        // slot‑indexes 1‑9
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new CraftingResultSlot(playerInv.player,
                        input,
                        result,
                        col + row * 3,
                        94 + col * 18,
                        17 + row * 18));
            }
        }

        // Player Inv
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }

        /* ----- hot‑bar (9) ----- */
        // slot‑indexes 37‑45
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv,
                    col,
                    8 + col * 18,
                    142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasStack()) return ItemStack.EMPTY;

        ItemStack original = slot.getStack();
        ItemStack copy     = original.copy();

        // slot index map
        final int INPUT          = 0;// 1 slot
        final int INV_START      = 10;// player inventory 9×3
        final int INV_END        = 37; // exclusive
        final int HOTBAR_START   = 37;// hot‑bar 9
        final int HOTBAR_END     = 46;// exclusive

        if (index == INPUT) {
            // input -> player
            if (!this.insertItem(original, INV_START, INV_END, false) && // inventory first
                    !this.insertItem(original, HOTBAR_START, HOTBAR_END, false))// then hot‑bar
                return ItemStack.EMPTY;
            slot.onQuickTransfer(original, copy);
        } else if (index >= INV_START) {
            // inventory or hot‑bar -> input
            if (!this.insertItem(original, INPUT, INPUT + 1, false))
                return ItemStack.EMPTY;

        } else {
            // any output slot -> player
            if (!this.insertItem(original, INV_START, INV_END, false) &&   // inventory first
                    !this.insertItem(original, HOTBAR_START, HOTBAR_END, false))
                return ItemStack.EMPTY;
            slot.onQuickTransfer(original, copy);
        }

        if (original.isEmpty()) slot.setStack(ItemStack.EMPTY); else slot.markDirty();
        return copy;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // Default Behaviour
        this.ctx.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public void onContentChanged(Inventory inv) {
        super.onContentChanged(inv);
        if (inv == input)
            this.ctx.run((world, pos) -> updateResult());
    }


    protected void updateResult(){
        Item source = this.input.getStack(0).getItem();
        TableOfUncrafting.LOGGER.info(source.toString());

        List<RecipeEntry<CraftingRecipe>> recipes =
                world.getRecipeManager()
                        .listAllOfType(RecipeType.CRAFTING)
                        .stream()
                        .filter(r -> r.value().getResult(world.getRegistryManager()).isOf(source))
                .toList();

        if(recipes.isEmpty())
            return;

        CraftingRecipe recipe = recipes.getFirst().value();

        // Handle recipe
    }
}
