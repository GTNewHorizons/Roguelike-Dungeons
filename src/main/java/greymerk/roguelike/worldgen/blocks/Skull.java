package greymerk.roguelike.worldgen.blocks;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;

import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;

public enum Skull {

    SKELETON,
    WITHER,
    ZOMBIE,
    STEVE,
    CREEPER;

    public static void set(IWorldEditor editor, Random rand, Coord pos, Cardinal dir, Skull type) {

        MetaBlock skullBlock = new MetaBlock(Blocks.skull, 1);

        if (!skullBlock.set(editor, pos)) return;

        TileEntity skullEntity = editor.getTileEntity(pos);

        if (skullEntity == null) return;
        if (!(skullEntity instanceof TileEntitySkull)) return;

        TileEntitySkull skull = (TileEntitySkull) skullEntity;

        setType(skull, type);
        setRotation(rand, skull, dir);
    }

    public static void setType(TileEntitySkull skull, Skull type) {
        skull.func_152107_a(getSkullId(type));
    }

    public static void setRotation(Random rand, TileEntitySkull skull, Cardinal dir) {
        int directionValue = getDirectionValue(dir);

        directionValue += -1 + rand.nextInt(3);
        directionValue = directionValue % 16;

        skull.func_145903_a(directionValue);
    }

    public static int getSkullId(Skull type) {
        switch (type) {
            case SKELETON:
                return 0;
            case WITHER:
                return 1;
            case ZOMBIE:
                return 2;
            case STEVE:
                return 3;
            case CREEPER:
                return 4;
            default:
                return 0;
        }
    }

    public static int getDirectionValue(Cardinal dir) {
        switch (dir) {
            case NORTH:
                return 0;
            case EAST:
                return 4;
            case SOUTH:
                return 8;
            case WEST:
                return 12;
            default:
                return 0;
        }
    }
}
