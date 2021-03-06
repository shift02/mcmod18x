package ruby.bamboo.item.itemblock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruby.bamboo.block.ILeave;
import ruby.bamboo.core.init.EnumCreateTab.ICreativeSoatName;

public class ItemSakuraLeave extends ItemBlock implements IItemColorWrapper, ICreativeSoatName {

    private final ILeave leave;

    public ItemSakuraLeave(Block block) {
        super(block);
        this.leave = (ILeave) block;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(this.leave.getLeaveName(stack.getMetadata()));
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        return this.leave.getLeaveRenderColor(this.leave.getLeaveStateFromMeta(stack.getMetadata()));
    }

    @Override
    public String getSortName(ItemStack is) {
        return "sakura" + getUnlocalizedName(is);
    }
}
