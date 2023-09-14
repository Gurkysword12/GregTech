package muramasa.gregtech.integration.forge.tfc;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.providers.AntimatterBlockTagProvider;
import muramasa.antimatter.datagen.providers.AntimatterFluidTagProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemTagProvider;
import muramasa.antimatter.event.forge.AntimatterLoaderEvent;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.TagUtils;
import muramasa.gregtech.GTIRef;
import muramasa.gregtech.data.Materials;
import muramasa.gregtech.integration.forge.tfc.datagen.TFCBlockTagProvider;
import muramasa.gregtech.integration.forge.tfc.datagen.TFCItemTagProvider;
import muramasa.gregtech.integration.forge.tfc.datagen.TFCLangProvider;
import muramasa.gregtech.integration.forge.tfc.ore.GTTFCOreBlock;
import muramasa.gregtech.integration.forge.tfc.ore.GTTFCOreItem;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiConsumer;

import static muramasa.antimatter.data.AntimatterDefaultTools.SAW;
import static muramasa.gregtech.data.Materials.*;

public class TFCRegistrar implements IAntimatterRegistrar {

    public static Material[] array;
    public TFCRegistrar(){
        onRegistrarInit();
    }
    @Override
    public String getId() {
        return "tfc";
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Side side) {
        if (event == RegistrationEvent.DATA_INIT){
            array = new Material[]{Bauxite, Cobaltite, Galena, Uraninite, VanadiumMagnetite, BrownLimonite, BandedIron, Cooperite, Palladium};
            /*for (Material material : array) {
                Helpers.mapOfKeys(Ore.Grade.class, (grade) -> {
                    new GTTFCOreItem(GTIRef.ID, grade.name().toLowerCase() + "_" + material.getId());
                    return Helpers.mapOfKeys(Rock.class, (rock) -> {
                        new GTTFCOreBlock(GTIRef.ID, material, rock, grade);
                        return true;
                    });
                });
            }*/
            Helpers.mapOfKeys(Rock.class, (rock) -> {
                AntimatterAPI.register(StoneType.class, new StoneType(GTIRef.ID, "raw_" + rock.name().toLowerCase(), Material.NULL, new Texture("tfc", "block/rock/raw/" + rock.name().toLowerCase()), SoundType.STONE, false).setStateSupplier(() -> rock.getBlock(Rock.BlockType.RAW).get().defaultBlockState()).setHardnessAndResistance(rock.category().hardness(6.5F), 10.0F).setHarvestLevel(1));
                AntimatterAPI.register(StoneType.class, new StoneType(GTIRef.ID, rock.name().toLowerCase() + "_gravel", Material.NULL, new Texture("tfc", "block/rock/gravel/" + rock.name().toLowerCase()), SoundType.GRAVEL, false).setSandLike(true).setHardnessAndResistance(rock.category().hardness(2.0F)).setStateSupplier(() -> rock.getBlock(Rock.BlockType.GRAVEL).get().defaultBlockState()).setHarvestLevel(1).setRequiresTool(true));
                return true;
            });
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onRegistrarInit() {
        AntimatterAPI.addRegistrar(this);
        if (isEnabled()) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            MinecraftForge.EVENT_BUS.addListener(this::registerRecipeLoaders);
            AntimatterDynamics.clientProvider(GTIRef.ID, () -> new TFCLangProvider(GTIRef.ID, "TFC en_us Lang", "en_us"));
        }
    }

    public void registerRecipeLoaders(AntimatterLoaderEvent event){
        BiConsumer<String, IRecipeRegistrate.IRecipeLoader> loader = (a, b) -> event.registrat.add(GTIRef.ID, a, b);
        loader.accept("tfc_machine_recipes", MachineRecipes::init);
    }

    @SubscribeEvent
    public void onProviders(AntimatterProvidersEvent ev) {
        ev.event.addProvider("tfc", () -> new AntimatterFluidTagProvider("tfc", "TFC Fluid Tags", false){
            @Override
            protected void processTags(String domain) {
                super.processTags(domain);
                this.tag(TagUtils.getForgelikeFluidTag("salt_water")).add(TFCFluids.SALT_WATER.getSource());
            }
        });
        AntimatterBlockTagProvider[] blockTagProviders = new AntimatterBlockTagProvider[1];
        blockTagProviders[0] = new TFCBlockTagProvider( "tfc", "TFC Block Tags", false);
        ev.event.addProvider("tfc", () -> new TFCItemTagProvider("tfc", "TFC Item Tags", false,  blockTagProviders[0]));
        ev.event.addProvider("tfc", () -> blockTagProviders[0]);

    }

    @Override
    public boolean isEnabled() {
        return AntimatterAPI.isModLoaded("tfc");
    }
}
