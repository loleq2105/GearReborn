package dev.loleq21.gearreborn.mixin;

import dev.loleq21.gearreborn.GRContent;
import dev.loleq21.gearreborn.items.NightvisionGoggles;
import dev.loleq21.gearreborn.items.hazmat.HazmatChestPiece;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public boolean gearreborn$hadHazmatChestpiece;
    public boolean gearreborn$hadNVGoggles;

    private void gearreborn$updateGear() {
        if (this.getEquippedStack(EquipmentSlot.HEAD).isOf(GRContent.NV_GOGGLES)) {
            gearreborn$hadNVGoggles = true;
        } else if (gearreborn$hadNVGoggles) {
            NightvisionGoggles.onRemoved((PlayerEntity) ((Object) this));
        }
        if (this.getEquippedStack(EquipmentSlot.CHEST).isOf(GRContent.HAZMAT_CHESTPIECE)) {
            gearreborn$hadHazmatChestpiece = true;
        } else if (gearreborn$hadHazmatChestpiece) {
            HazmatChestPiece.onRemoved((PlayerEntity) ((Object) this));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick()V", cancellable = false)
    public void tick(CallbackInfo ci) {
        this.gearreborn$updateGear();
    }

}
