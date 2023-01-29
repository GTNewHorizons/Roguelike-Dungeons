package greymerk.roguelike.worldgen.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;

public class Furnace {

    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    public static void generate(IWorldEditor editor, Cardinal dir, Coord pos) {
        generate(editor, null, false, dir, pos);
    }

    public static void generate(IWorldEditor editor, boolean lit, Cardinal dir, Coord pos) {
        generate(editor, null, lit, dir, pos);
    }

    public static void generate(IWorldEditor editor, ItemStack fuel, boolean lit, Cardinal dir, Coord pos) {

        MetaBlock furnace;

        if (lit) {
            furnace = new MetaBlock(Blocks.lit_furnace);
        } else {
            furnace = new MetaBlock(Blocks.furnace);
        }

        furnace.setMeta(dirNum(dir));

        furnace.set(editor, pos);

        if (fuel == null) return;

        TileEntity te = editor.getTileEntity(pos);
        if (te == null) return;
        if (!(te instanceof TileEntityFurnace)) return;
        TileEntityFurnace teFurnace = (TileEntityFurnace) te;
        teFurnace.setInventorySlotContents(FUEL_SLOT, fuel);
    }

    public static int dirNum(Cardinal dir) {
        switch (dir) {
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                return 2;
        }
    }
}
