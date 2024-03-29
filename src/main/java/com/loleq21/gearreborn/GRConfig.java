package com.loleq21.gearreborn;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "gearreborn")
public class GRConfig implements ConfigData {

    public static GRConfig CONFIG;

    public static void init(){
        AutoConfig.register(GRConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(GRConfig.class).getConfig();
    }

    @Comment("Amount of ticks of Water Breathing that one compressed air cell provides")
    @ConfigEntry.Category("hazmat")
    @ConfigEntry.Gui.RequiresRestart
    public int hazmatChestpieceAirTicksCapacity = 524;

    @Comment("Should the Hazmat suit slowly degrade in durability when the user is in lava")
    @ConfigEntry.Category("hazmat")
    @ConfigEntry.Gui.RequiresRestart
    public boolean hazmatDegradesInLava = true;

    @Comment("Effective amount of ticks between the hazmat suit's pieces taking damage from being in lava")
    @ConfigEntry.Category("hazmat")
    @ConfigEntry.Gui.RequiresRestart
    public int hazmatLavaDegradeRate = 60;

    @Comment("How much energy per tick the NVGoggles consume when they're active and on the player's head")
    @ConfigEntry.Category("nvg")
    @ConfigEntry.Gui.RequiresRestart
    public int nvgActiveEnergyPerTickCost = 16;

    @Comment("The energy capacity of the NVG")
    @ConfigEntry.Category("nvg")
    @ConfigEntry.Gui.RequiresRestart
    public int nvgEnergyCapacity = 100000;

    @Comment("The energy capacity of the Stun Gun")
    @ConfigEntry.Category("stungun")
    @ConfigEntry.Gui.RequiresRestart
    public int stungunEnergyCapacity = 20000;

    @Comment("How many ticks it takes for the Stun Gun's capacitors to fully charge")
    @ConfigEntry.Category("stungun")
    @ConfigEntry.Gui.RequiresRestart
    public int stungunChargeTicks = 64;

    @Comment("How much bonus damage a charged Stun Gun deals to arthropods")
    @ConfigEntry.Category("stungun")
    @ConfigEntry.Gui.RequiresRestart
    public int stungunDamageDealtToArthropodsOnChargedHit = 16;

    @Comment("Should the Stun Gun ignite Creepers")
    @ConfigEntry.Category("stungun")
    public boolean stungunShouldChargedHitsIgniteCreepers = false;

    @Comment("Should the Stun Gun stun vanilla boss mobs")
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("stungun")
    public boolean stungunShouldStunBossMobs = false;

    @Comment("How many ticks of Slowness should the Stun Gun inflict on mobs")
    @ConfigEntry.Category("stungun")
    @ConfigEntry.Gui.RequiresRestart
    public int stungunSlownessTicks = 100;

    @Comment("How many ticks of Weakness should the Stun Gun inflict on mobs")
    @ConfigEntry.Category("stungun")
    @ConfigEntry.Gui.RequiresRestart
    public int stungunWeaknessTicks = 100;

}
