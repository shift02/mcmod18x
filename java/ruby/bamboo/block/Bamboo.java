package ruby.bamboo.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruby.bamboo.api.BambooBlocks;
import ruby.bamboo.api.Constants;
import ruby.bamboo.core.init.BambooData.BambooBlock;
import ruby.bamboo.core.init.BambooData.BambooBlock.StateIgnore;
import ruby.bamboo.core.init.EnumCreateTab;
import ruby.bamboo.core.init.EnumMaterial;
import ruby.bamboo.item.itemblock.ItemBamboo;

/**
 * ばんぼー
 *
 * @author Ruby
 *
 */
@BambooBlock(itemBlock = ItemBamboo.class, createiveTabs = EnumCreateTab.TAB_BAMBOO, material = EnumMaterial.PLANTS)
public class Bamboo extends BlockBush implements IGrowable {
    // 最大の長さ
    public static final PropertyInteger LENGTH = PropertyInteger.create(Constants.META, 0, 15);
    public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);

    public Bamboo(Material material) {
        this.setDefaultState(this.blockState.getBaseState().withProperty(LENGTH, 10));
        this.setLightOpacity(0);
        this.setTickRandomly(true);
        this.setHardness(0.75F);
        this.setResistance(1F);
        this.setHarvestLevel("axe", 0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BLOCK_AABB;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LENGTH, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LENGTH).intValue();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LENGTH);
    }

    /**
     * 中心軸からずらすアレ
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XYZ;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {

        return this.canSustainBush(worldIn.getBlockState(pos.down())) || worldIn.getBlockState(pos.down()).getBlock() == this;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        BlockPos p = pos.down();
        if (worldIn.getBlockState(p).getBlock() == this) {
            this.dropBlockAsItem(worldIn, p, state, 0);
            worldIn.setBlockState(p, Blocks.AIR.getDefaultState(), 3);
        }
    }

    private void tryBambooGrowth(World world, BlockPos pos, IBlockState state, float probability) {
        if (!world.isRemote) {
            if (world.isAirBlock(pos.up())) {
                if (world.rand.nextFloat() < probability) {
                    int meta = this.getMetaFromState(state);
                    if (meta > 0) {
                        this.growBamboo(world, pos, meta);
                    } else {
                        if (world.isRaining() || world.rand.nextFloat() < probability) {
                            this.tryChildSpawn(world, pos, state);
                        }
                    }
                }
            }
        }
    }

    int getLength(IBlockState state) {
        return state.getValue(LENGTH);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        tryBambooGrowth(world, pos, state, world.isRaining() ? 0.25F : 0.125F);
    }

    private void growBamboo(World world, BlockPos pos, int meta) {
        world.setBlockState(pos.up(), this.getStateFromMeta(--meta));
    }

    private boolean canChildSpawn(World world, BlockPos pos, IBlockState state) {
        if (world.isAirBlock(pos)) {
            BlockPos pd = pos.down();
            if (BambooBlocks.BAMBOOSHOOT.canBlockStay(world, pos, state)) {
                // 天候・耕地確変
                if (world.rand.nextFloat() < (world.isRaining() ? 0.4F : world.getBlockState(pd).getBlock() == Blocks.FARMLAND ? 0.25F : 0.1F)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void tryChildSpawn(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            BlockPos p = pos.down();

            for (; !this.canSustainBush(world.getBlockState(p)); p = p.down())
                ;
            for (BlockPos target : BlockPos.getAllInBox(p.add(-1, -1, -1), p.add(1, 1, 1))) {
                if (this.canChildSpawn(world, target, state)) {
                    world.setBlockState(target.down(), Blocks.DIRT.getDefaultState());
                    world.setBlockState(target, BambooBlocks.BAMBOOSHOOT.getDefaultState());
                }
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) || worldIn.getBlockState(pos.down()).getBlock() == this;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (this.canSustainBush(worldIn.getBlockState(pos.down()))) {
            worldIn.setBlockState(pos, state.withProperty(LENGTH, 8 + worldIn.rand.nextInt(5)));
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        BlockPos tmp = pos;
        while (!world.isAirBlock(tmp = tmp.up())) {
            pos = tmp;
        }
        this.tryBambooGrowth(world, pos, world.getBlockState(pos), 0.65F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @StateIgnore
    public IProperty[] getIgnoreState() {
        return new IProperty[] { LENGTH };
    }

}
