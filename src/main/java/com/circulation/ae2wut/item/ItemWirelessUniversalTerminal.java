package com.circulation.ae2wut.item;

import appeng.core.sync.GuiBridge;
import appeng.items.tools.powered.ToolWirelessTerminal;
import appeng.util.Platform;
import baubles.api.BaublesApi;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.model.ItemWUTBakedModel;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class ItemWirelessUniversalTerminal extends ToolWirelessTerminal {

    public static final String NAME = "wireless_universal_terminal";
    public static final ItemWirelessUniversalTerminal INSTANCE = new ItemWirelessUniversalTerminal();

    private final ByteSet secure = new ByteOpenHashSet() {{
        add((byte) 0);
        add((byte) 2);
        add((byte) 5);
        add((byte) 10);
    }};

    private ItemWirelessUniversalTerminal() {
        this.setMaxStackSize(1);
        this.setCreativeTab(new CreativeTabs(AE2UELWirelessUniversalTerminal.MOD_ID) {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ItemWirelessUniversalTerminal.INSTANCE);
            }
        });
        this.setRegistryName(new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, NAME));
        this.setTranslationKey(AE2UELWirelessUniversalTerminal.MOD_ID + '.' + NAME);
        this.addPropertyOverride(new ResourceLocation("mode"), (stack, worldIn, entityIn) -> {
            if (stack.hasTagCompound()) {
                if (stack.getTagCompound().hasKey("Nova")) {
                    return 114514;
                } else {
                    return stack.getTagCompound().getByte("mode");
                }
            }
            return 0;
        });
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer player, EnumHand hand) {
        if (w.isRemote) return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
        ItemStack item = player.getHeldItem(hand);
        if (item.hasTagCompound()) {
            final var tag = item.getTagCompound();
            byte mode = tag.getByte("mode");
            int slot = hand == EnumHand.MAIN_HAND ? player.inventory.currentItem : 40;
            if (mode == 0) {
                if (tag.hasKey("modes", 11)) {
                    var modes = tag.getIntArray("modes");
                    if (modes.length == 0)
                        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
                    mode = (byte) modes[0];
                    if (mode == 0) {
                        if (modes.length == 1)
                            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
                        mode = (byte) modes[1];
                    }
                } else
                    return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
            if (hasMode(item, mode)) {
                AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(item, player, mode, slot, false);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean canHandle(ItemStack is) {
        return is.getItem() == this;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (stack.hasTagCompound()) {
                return (super.getItemStackDisplayName(stack).trim() + getWirelessName(stack.getTagCompound().getByte("mode"))).trim();
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    public void nbtChange(ItemStack item, byte mode) {
        final var tag = Platform.openNbtData(item);
        tag.setByte("mode", mode);
        if (secure.contains(mode)) return;
        tag.setInteger("craft", 1);
        if (hasMode(item, mode)) {
            NBTTagList cache = tag.getCompoundTag("cache").getTagList(String.valueOf(mode), Constants.NBT.TAG_COMPOUND);
            if (cache.tagCount() != 0) {
                switch (mode) {
                    case 1, 3, 4 -> tag.getCompoundTag("craftingGrid").setTag("Items", cache);
                    case 6, 7, 8, 9 -> tag.setTag("crafting", cache);
                }
                tag.getCompoundTag("cache").removeTag(String.valueOf(mode));
            }
        }
    }

    public void nbtChangeB(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item.hasTagCompound() && item.getItem() == this) {
                nbtChangeB(item);
            }
        }
        if (Loader.isModLoaded("baubles")) {
            baublesNBTB(player);
        }
    }

    @Optional.Method(modid = "baubles")
    public void baublesNBTB(EntityPlayer player) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (item.hasTagCompound() && item.getItem() == this) {
                nbtChangeB(item);
            }
        }
    }

    public void nbtChangeB(ItemStack item) {
        final var tag = item.getTagCompound();
        byte mode = tag.getByte("mode");
        if (secure.contains(mode)) return;
        tag.setInteger("craft", 0);
        if (hasMode(item, mode)) {
            tag.setByte("mode", mode);
            NBTTagList items = tag.getCompoundTag("craftingGrid").getTagList("Items", Constants.NBT.TAG_COMPOUND);
            if (Loader.isModLoaded("ae2exttable") && tag.hasKey("crafting")) {
                int ii = 0;
                int iii = switch (mode) {
                    case 6 -> 9;
                    case 7 -> 25;
                    case 8 -> 49;
                    case 9 -> 81;
                    default -> 0;
                };
                int iiii = 0;
                NBTTagList nbtList = new NBTTagList();
                Iterator<NBTBase> iterator = tag.getTagList("crafting", Constants.NBT.TAG_COMPOUND).iterator();
                while (iterator.hasNext() && ii < iii) {
                    NBTBase nbt = iterator.next();
                    if (nbt instanceof NBTTagCompound n) {
                        if (!n.getString("id").endsWith("air") || !n.getString("id").startsWith("minecraft")) {
                            nbtList.appendTag(nbt);
                            iiii++;
                        } else {
                            nbtList.appendTag(new NBTTagCompound());
                        }
                    }
                    ii++;
                }
                if (iiii > 0) {
                    items = nbtList;
                }
            }
            if (items.tagCount() != 0) {
                if (!tag.hasKey("cache")) {
                    tag.setTag("cache", new NBTTagCompound());
                }
                tag.getCompoundTag("cache").setTag(String.valueOf(mode), items);
            }
            tag.getCompoundTag("craftingGrid").removeTag("Items");
            tag.removeTag("crafting");
        }
    }

    @SideOnly(Side.CLIENT)
    public static String getWirelessName(int value) {
        return "§6(" + ItemWUTBakedModel.getIconItem((byte) value).getDisplayName() + ")";
    }

    @Override
    public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addCheckedInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag advancedTooltips) {
        super.addCheckedInformation(stack, world, lines, advancedTooltips);
        if (stack.hasTagCompound()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                lines.add(I18n.format("item.wut.shift_tooltip"));
                int[] modes = stack.getTagCompound().getIntArray("modes");
                if (modes.length != 0) {
                    for (int number : modes) {
                        if (number != 0) {
                            lines.add("  - " + getWirelessName(number));
                        }
                    }
                } else {
                    lines.add("   " + I18n.format("item.wut.shift_tooltip1"));
                }
            } else {
                lines.add(I18n.format("item.wut.tooltip"));
                lines.add(I18n.format("item.wut.tooltip1"));
                lines.add(I18n.format("item.wut.tooltip2"));
            }
        }
    }

    @Override
    public IGuiHandler getGuiHandler(ItemStack is) {
        if (is.hasTagCompound()) {
            byte mode = is.getTagCompound().getByte("mode");
            return switch (mode) {
                case 1 -> GuiBridge.GUI_WIRELESS_CRAFTING_TERMINAL;
                case 2 -> GuiBridge.GUI_WIRELESS_FLUID_TERMINAL;
                case 3 -> GuiBridge.GUI_WIRELESS_PATTERN_TERMINAL;
                case 10 -> GuiBridge.GUI_WIRELESS_INTERFACE_TERMINAL;
                default -> GuiBridge.GUI_WIRELESS_TERM;
            };
        }
        return null;
    }

    @Override
    protected void getCheckedSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> itemStacks) {
        ItemStack charged = new ItemStack(this, 1);
        NBTTagCompound tag = Platform.openNbtData(charged);
        tag.setDouble("internalCurrentPower", this.getAEMaxPower(charged));
        tag.setDouble("internalMaxPower", this.getAEMaxPower(charged));
        tag.setIntArray("modes", AE2UELWirelessUniversalTerminal.proxy.getAllMode());
        itemStacks.add(charged);
    }

    @Optional.Method(modid = "ae2exttable")
    public static AE2ExtendedGUIs getGuiType(ItemStack item) {
        if (item.hasTagCompound()) {
            byte mode = item.getTagCompound().getByte("mode");
            return getGui(mode);
        }
        return null;
    }

    @Optional.Method(modid = "ae2exttable")
    public static AE2ExtendedGUIs getGui(byte value) {
        return switch (value) {
            case 6 -> AE2ExtendedGUIs.WIRELESS_BASIC_CRAFTING_TERMINAL;
            case 7 -> AE2ExtendedGUIs.WIRELESS_ADVANCED_CRAFTING_TERMINAL;
            case 8 -> AE2ExtendedGUIs.WIRELESS_ELITE_CRAFTING_TERMINAL;
            case 9 -> AE2ExtendedGUIs.WIRELESS_ULTIMATE_CRAFTING_TERMINAL;
            default -> null;
        };
    }

    @Optional.Method(modid = "ae2exttable")
    public static byte getAE2EMode(AE2ExtendedGUIs value) {
        return switch (value) {
            case WIRELESS_BASIC_CRAFTING_TERMINAL -> 6;
            case WIRELESS_ADVANCED_CRAFTING_TERMINAL -> 7;
            case WIRELESS_ELITE_CRAFTING_TERMINAL -> 8;
            case WIRELESS_ULTIMATE_CRAFTING_TERMINAL -> 9;
            default -> 0;
        };
    }

    public boolean hasMode(ItemStack t, byte mode) {
        if (!AE2UELWirelessUniversalTerminal.proxy.getAllModeSet().contains(mode) || !t.hasTagCompound()) return false;
        for (int m : t.getTagCompound().getIntArray("modes")) {
            if (m == mode) return true;
        }
        return false;
    }
}