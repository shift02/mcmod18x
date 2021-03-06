package ruby.bamboo.item.arrow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import ruby.bamboo.core.init.EnumCreateTab.ICreativeSoatName;
import ruby.bamboo.entity.arrow.BaseArrow;
import ruby.bamboo.util.ItemStackHelper;

public abstract class ArrowBase extends Item implements IBambooArrow, ICreativeSoatName {
    public abstract Class<? extends BaseArrow> getArrowClass();

    // 竹槍直接投げ
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        Class<? extends BaseArrow> arrow = getArrowClass();
        if (arrow == null) {
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }
        float power = 0.5F;
        try {

            BaseArrow entityArrow;
            try {
                entityArrow = arrow.getConstructor(World.class, EntityLivingBase.class, float.class, ItemStack.class).newInstance(worldIn, playerIn, power, itemStackIn);
            } catch (NoSuchMethodException e) {
                entityArrow = arrow.getConstructor(World.class, EntityLivingBase.class, float.class).newInstance(worldIn, playerIn, power);
            }

            entityArrow.setAim(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, power * 2.0F, 1.0F);
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entityArrow);
            }
            if (!playerIn.capabilities.isCreativeMode) {
                ItemStackHelper.decrStackSize(playerIn.inventory, itemStackIn, 1);
            } else {
                entityArrow.setMaxAge(60);
                entityArrow.setNoPick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public String getSortName(ItemStack is) {
        return "bambooArrow" + this.getUnlocalizedName(is);
    }
}
