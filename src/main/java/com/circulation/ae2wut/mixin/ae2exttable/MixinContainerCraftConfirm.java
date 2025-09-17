package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.interfaces.IInventorySlotAware;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= ContainerCraftConfirm.class, remap=false)
public abstract class MixinContainerCraftConfirm extends AEBaseContainer {

    public MixinContainerCraftConfirm(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Inject(method = "startJob", at = @At(value = "INVOKE", target = "Lappeng/container/ContainerOpenContext;getTile()Lnet/minecraft/tileentity/TileEntity;", shift = At.Shift.AFTER), cancellable = true)
    public void startJobMixin(CallbackInfo ci) {
        TileEntity te = this.getOpenContext().getTile();
        if (te == null) {
            if (this.obj != null && this.obj.getItemStack().getItem() instanceof ItemWirelessUniversalTerminal t) {
                var mode = this.obj.getItemStack().getTagCompound().getByte("mode");
                switch (mode) {
                    case 6, 7, 8, 9: {
                        IInventorySlotAware i = ((IInventorySlotAware) this.obj);
                        EntityPlayer player = this.getInventoryPlayer().player;

                        AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(this.obj.getItemStack(),player,mode,((IInventorySlotAware) this.obj).getInventorySlot(),((IInventorySlotAware) this.obj).isBaubleSlot());

                        ci.cancel();
                    }
                }
            }
        }
    }
}