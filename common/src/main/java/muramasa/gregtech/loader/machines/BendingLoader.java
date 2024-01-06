package muramasa.gregtech.loader.machines;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.gregtech.data.GregTechItems;
import muramasa.gregtech.data.GregTechMaterialTags;
import muramasa.gregtech.data.Materials;

import java.util.function.ToLongFunction;

import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static muramasa.gregtech.data.TierMaps.INT_CIRCUITS;
import static muramasa.gregtech.data.RecipeMaps.BENDER;

public class BendingLoader {
    public static void init() {
        ToLongFunction<Material> baseDuration = m -> {
            if (m.has(GregTechMaterialTags.RECIPE_MASS)) return GregTechMaterialTags.RECIPE_MASS.get(m);
            return m.getMass();
        };
        PLATE.all().forEach(t -> {
            if (!t.has(AntimatterMaterialTypes.INGOT) || t.has(MaterialTags.RUBBERTOOLS)) return;
            BENDER.RB().ii(AntimatterMaterialTypes.INGOT.getMaterialIngredient(t,1),INT_CIRCUITS.get(1)).io(PLATE.get(t,1)).add("plate_" + t.getId(), baseDuration.applyAsLong(t), 24);
        });
        AntimatterMaterialTypes.FOIL.all().forEach(foil -> {
            if (!foil.has(PLATE) || foil.has(MaterialTags.RUBBERTOOLS)) return;
            BENDER.RB().ii(PLATE.getMaterialIngredient(foil,1), INT_CIRCUITS.get(1)).io(AntimatterMaterialTypes.FOIL.get(foil,4)).add("foil_" + foil.getId(), baseDuration.applyAsLong(foil),24);
        });
        AntimatterMaterialTypes.SPRING.all().stream().filter(m -> !m.has(MaterialTags.RUBBERTOOLS)).forEach(spring -> {
            BENDER.RB().ii(ROD_LONG.getMaterialIngredient(spring, 1), INT_CIRCUITS.get(1)).io(SPRING.get(spring)).add(spring.getId() + "_spring", 200, 16);
        });
        RING.all().stream().filter(m -> !m.has(MaterialTags.RUBBERTOOLS)).forEach(ring -> {
            BENDER.RB().ii(ROD.getMaterialIngredient(ring, 1), INT_CIRCUITS.get(1)).io(RING.get(ring, 2)).add(ring.getId() + "_ring", 200, 16);
        });
        BENDER.RB().ii(PLATE.getMaterialIngredient(Materials.Tin,2), INT_CIRCUITS.get(4)).io(GregTechItems.CellTin.getDefaultInstance()).add("tin_cell",80,24);
        BENDER.RB().ii(PLATE.getMaterialIngredient(Materials.Steel,2), INT_CIRCUITS.get(4)).io(GregTechItems.CellSteel.getDefaultInstance()).add("steel_cell",80,96);
        BENDER.RB().ii(PLATE.getMaterialIngredient(Materials.TungstenSteel,2), INT_CIRCUITS.get(4)).io(GregTechItems.CellTungstensteel.getDefaultInstance()).add("tungstensteel_cell",80,384);
        BENDER.RB().ii(PLATE.getMaterialIngredient(Materials.Zirconium,1), INT_CIRCUITS.get(1)).io(GregTechItems.EmptyNuclearFuelRod).add("empty_fuel_rod",80,96);
    }
}
