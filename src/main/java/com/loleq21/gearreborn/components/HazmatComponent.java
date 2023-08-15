package com.loleq21.gearreborn.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.loleq21.gearreborn.components.GRComponents.HAZMAT_COMPONENT_KEY;

public class HazmatComponent implements ComponentV3, AutoSyncedComponent {

    private final Object provider;

    public static final String BITS_KEY = "bits";
    public static final String DIVE_KEY = "dive";
    public static final String FULL_KEY = "full";
    public static final String LAST_CELLS_KEY = "lastCells";
    public static final String WAS_DIVING_KEY = "wasDiving";

    private byte bits;
    private boolean dive;
    private boolean full;
    private boolean wasDiving;
    private int lastCells;

    public HazmatComponent(Object provider) {
        this.provider = provider;
    }

    /*
     Code partially adapted from Create
     Available at: https://github.com/Fabricators-of-Create/Create/blob/caac54ea1f6ab7dcd22aa810a027e73eae34233d/src/main/java/com/simibubi/create/content/equipment/armor/NetheriteDivingHandler.java#L48
    */

    public void setBit(EquipmentSlot equipmentSlot){
        if ((this.bits & 0b1111) == 0b1111) {
            return;
        }

        this.bits |= 1 << equipmentSlot.getEntitySlotId();

        if ((bits & 0b1100) == 0b1100) {
            setDive(true);
        }

        if ((bits & 0b1111) == 0b1111) {
            setFull(true);
        }
    }

    public void clearBit(EquipmentSlot equipmentSlot){
        boolean prevFullSet = (this.bits & 0b1111) == 0b1111;
        bits &= ~(1 << equipmentSlot.getEntitySlotId());

        if (prevFullSet) {
            setFull(false);
        }

        if ((bits & 0b1100) != 0b1100) {
            setDive(false);
        }

    }

    public void setDive(boolean dive) {
        this.dive = dive;
        HAZMAT_COMPONENT_KEY.sync(this.provider);
    }

    public void setFull(boolean full) {
        this.full = full;
        HAZMAT_COMPONENT_KEY.sync(this.provider);
    }

    public boolean canDive() {
        return dive;
    }

    public boolean isWearingFullSet() {
        return full;
    }

    public byte getBits() {return bits;}

    public int getLastCells() {
        return lastCells;
    }

    public void setLastCells(int lastCells) {
        this.lastCells = lastCells;
    }

    public boolean wasDiving() {
        return this.wasDiving;
    }

    public void setWasDiving(boolean wasDiving) {
        this.wasDiving = wasDiving;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.bits = tag.getByte(BITS_KEY);
        this.dive = tag.getBoolean(DIVE_KEY);
        this.full = tag.getBoolean(FULL_KEY);
        this.lastCells = tag.getInt(LAST_CELLS_KEY);
        this.wasDiving = tag.getBoolean(WAS_DIVING_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putByte(BITS_KEY, this.bits);
        tag.putBoolean(DIVE_KEY, this.dive);
        tag.putBoolean(FULL_KEY, this.full);
        tag.putInt(LAST_CELLS_KEY, this.lastCells);
        tag.putBoolean(WAS_DIVING_KEY, this.wasDiving);
    }

    // Only sync the info that's useful for the client

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean(DIVE_KEY, this.dive);
        tag.putBoolean(FULL_KEY, this.full);
        buf.writeNbt(tag);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        NbtCompound tag = buf.readNbt();
        if (tag != null) {
            this.dive = tag.getBoolean(DIVE_KEY);
            this.full = tag.getBoolean(FULL_KEY);
        }
    }
}
