package dev.loleq21.ag4tr;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import reborncore.common.util.ItemDurabilityExtensions;
import reborncore.common.util.ItemUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;

public class HandheldSolarPanel extends Item implements EnergyHolder, ItemDurabilityExtensions {

    public HandheldSolarPanel(Settings settings) {
        super(settings);
    }

    public static final int ZEROGEN = 0;
    public static final int DAYGEN = 2;

    private int state = ZEROGEN;
    private int prevState = ZEROGEN;
    private int lightLevel;

    private boolean canGenerate = false;
    private boolean held;

    private void distributePowerToInventory(World world, PlayerEntity player, ItemStack itemStack, int maxOutput) {
        if (world.isClient || !Energy.valid(itemStack)) {
            return;
        }
        for (int i = 0; i < player.inventory.size(); i++) {
            if (Energy.valid(player.inventory.getStack(i))) {
                Energy.of(itemStack)
                        .into(Energy.of(player.inventory.getStack(i)))
                        .move(maxOutput);
            }
        }
    }

    private void setSunState(int state) {
        this.state = state;
    }

    public int getSunState() {
        return state;
    }

    private void updateState(ServerWorld world, BlockPos position, PlayerEntity user, boolean held) {

        if (held) {
            if (world == null) {
                return;
            }
            if (!world.getDimension().hasSkyLight()) {
                this.setSunState(ZEROGEN);
            }
            if (world.isSkyVisible(position.up())) {
                this.setSunState(ZEROGEN);
                lightLevel = world.getLightLevel(LightType.SKY, position) - world.getAmbientDarkness();
                user.sendMessage(new LiteralText(Integer.toString(lightLevel)), true);

                if (!world.isRaining() && !world.isThundering() && lightLevel >= 9) {
                    this.setSunState(DAYGEN);
                }
            } else {
                this.setSunState(ZEROGEN);
            }
            if (prevState != this.getSunState()) {
                canGenerate = getSunState() == DAYGEN;
                prevState = this.getSunState();
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (world instanceof ServerWorld) {
            ServerWorld serverworld = (ServerWorld) world;
            if (entity instanceof PlayerEntity) {
                PlayerEntity user = (PlayerEntity) entity;
                held = user.getMainHandStack() == stack || user.getOffHandStack() == stack;
                updateState(serverworld, user.getBlockPos(), user, held);

                if (held) {
                    distributePowerToInventory(world, (PlayerEntity) entity, stack, getTier().getMaxOutput());
                    if(canGenerate) {
                        Energy.of(stack).insert(1.0);
                    }
                }

            }     }
    }

    @Override
    public double getMaxStoredPower() {
        return 20000;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.LOW;
    }
}
