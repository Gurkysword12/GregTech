package muramasa.gregtech.items;

import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import muramasa.gregtech.GTIRef;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemBreederRod extends ItemBasic<ItemBreederRod> implements IColorHandler {
    private final Material material;
    private final Supplier<Item> enrichedRod;
    private final int loss;
    private final long needed;

    public ItemBreederRod(String domain, Material material, Supplier<Item> enrichedRod, int loss, long needed) {
        super(domain, material.getId() + "_breeder_rod");
        this.material = material;
        this.enrichedRod = enrichedRod;
        this.loss = loss;
        this.needed = needed;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.0", Utils.translatable("tooltip.gti.breeder_rod.enriched").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.1").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.2", Utils.translatable("tooltip.gti.nuclear_rod.moderated.1").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.3", Utils.translatable("tooltip.gti.breeder_rod.loss").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.4").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.5").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.6", enrichedRod.get().getDefaultInstance().getHoverName().copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GREEN));
        long needed = stack.getTagElement("neededNeutrons") != null ? stack.getTag().getLong("neededNeutrons") : this.needed;
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.7", Utils.literal("" + needed).withStyle(ChatFormatting.WHITE), Utils.translatable("tooltip.gti.breeder_rod.neutrons").withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Utils.translatable("tooltip.gti.breeder_rod.8", Utils.literal("" + loss).withStyle(ChatFormatting.WHITE), Utils.translatable("tooltip.gti.breeder_rod.neutrons").withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(GTIRef.ID, "item/basic/nuclear_fuel_rod"), new Texture(GTIRef.ID, "item/basic/empty_nuclear_fuel_rod")};
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (i == 0 && material != Material.NULL){
            return material.getRGB();
        }
        return IColorHandler.super.getItemColor(stack, block, i);
    }

    public Material getMaterial() {
        return material;
    }
}