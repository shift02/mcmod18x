package ruby.bamboo.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruby.bamboo.api.BambooItems;
import ruby.bamboo.api.Constants;

@SideOnly(Side.CLIENT)
public class GuiSack extends GuiContainer {
    private static final ResourceLocation RESORCE = new ResourceLocation(Constants.RESOURCED_DOMAIN + "textures/guis/guisack.png");

    public GuiSack(InventoryPlayer par1InventoryPlayer, ItemStack par2ItemStack) {
        super(new ContainerSack(par1InventoryPlayer, par2ItemStack));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String s = I18n.format(BambooItems.SACK.getUnlocalizedName() + ".name");
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(RESORCE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}
