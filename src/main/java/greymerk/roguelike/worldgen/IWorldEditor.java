package greymerk.roguelike.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;

import greymerk.roguelike.treasure.ITreasureChest;
import greymerk.roguelike.treasure.TreasureManager;

public interface IWorldEditor {

    boolean setBlock(Coord pos, MetaBlock metaBlock, boolean fillAir, boolean replaceSolid);

    void setBlockMetadata(Coord pos, int meta);

    MetaBlock getBlock(Coord pos);

    boolean isAirBlock(Coord pos);

    TileEntity getTileEntity(Coord pos);

    BiomeGenBase getBiome(Coord pos);

    int getDimension();

    long getSeed();

    Random getSeededRandom(int m, int n, int i);

    void fillDown(Random rand, Coord pos, IBlockFactory pillar);

    boolean canPlace(MetaBlock block, Coord pos, Cardinal dir);

    boolean validGroundBlock(Coord pos);

    void spiralStairStep(Random rand, Coord pos, IStair stair, IBlockFactory pillar);

    int getStat(Block block);

    TreasureManager getTreasure();

    void addChest(ITreasureChest chest);

}
