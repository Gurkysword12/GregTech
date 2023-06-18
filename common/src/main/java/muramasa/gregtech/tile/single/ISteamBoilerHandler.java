package muramasa.gregtech.tile.single;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.TesseractGraphWrappers;

import static muramasa.gregtech.data.Materials.Steam;

public interface ISteamBoilerHandler {
    int getProcessDelay();

    int getLossTimerMax();
    int getHeat();
    void setHeat(int heat);

    int getLossTimer();
    void setLossTimer(int lossTimer);

    boolean hadNoWater();

    void setHadNoWater(boolean hadNoWater);

    TileEntityMachine<?> getTile();

    default void tick(){
        if (this.getHeat() <= 20) {
            this.setHeat(20);
            this.setLossTimer(0);
        }
        setLossTimer(getLossTimer() + 1);
        if (getLossTimer() > getLossTimerMax()) {
            this.setHeat(getHeat() - 1);
            this.setLossTimer(0);
        }
        this.exportFluid();
        if (getTile().getLevel().getGameTime() % getProcessDelay() == 0) {
            getTile().fluidHandler.ifPresent(f -> {
                FluidStack[] inputs = f.getInputs();
                if (this.getHeat() > 100) {
                    if (inputs[0].getRealAmount() == 0) {
                        setHadNoWater(true);
                    } else {
                        if (hadNoWater()) {
                            getTile().getLevel().explode(null, getTile().getBlockPos().getX(), getTile().getBlockPos().getY(), getTile().getBlockPos().getZ(), 4.0F, Explosion.BlockInteraction.DESTROY);
                            getTile().getLevel().setBlockAndUpdate(getTile().getBlockPos(), Blocks.AIR.defaultBlockState());
                            return;
                        }
                        f.drainInput(new FluidStack(Fluids.WATER, 1), IFluidHandler.FluidAction.EXECUTE);
                        long room = (16000 * TesseractGraphWrappers.dropletMultiplier) - f.getOutputs()[0].getRealAmount();
                        long fill = Math.min(room, (150 * TesseractGraphWrappers.dropletMultiplier));
                        if (room > 0){
                            f.fillOutput(Steam.getGas(fill), IFluidHandler.FluidAction.EXECUTE);
                        }
                        if (fill < (150 * TesseractGraphWrappers.dropletMultiplier)) {
                            //TODO:steam sounds
                            getTile().getLevel().playSound(null, getTile().getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                            if (getTile().getLevel() instanceof ServerLevel)
                                ((ServerLevel) getTile().getLevel()).sendParticles(ParticleTypes.SMOKE, getTile().getBlockPos().getX(), getTile().getBlockPos().getY(), getTile().getBlockPos().getZ(), getTile().getLevel().getRandom().nextInt(8) + 1, 0.0D, 0.2D, 0.0D, 0.0D);
                            f.drain(4000, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                } else {
                    this.setHadNoWater(false);
                }
            });
        }
    }

    void exportFluid();
}
