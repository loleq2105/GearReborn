package dev.loleq21.gearreborn;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "gearreborn")
public class GRConfig implements ConfigData {

    @Comment("The energy capacity of the hazmat chestpiece, expressed in E")
    @ConfigEntry.Gui.RequiresRestart
    int hazmatChestpieceEnergyCapacity = 80000;

    @Comment("How many ticks of water breathing you can get with the hazmat chestpiece with one compressed air cell")
    @ConfigEntry.Gui.RequiresRestart
    int hazmatChestpieceAirTicksCapacity = 524;

    @Comment("How much energy (expressed in E) per tick the hazmat chestpiece consumes when shielding the player from lava damage")
    @ConfigEntry.Gui.RequiresRestart
    int hazmatChestpieceLavaCoolingEnergyCost = 32;

    @Comment("How much energy (expressed in E) per tick the NVG consume when they're active and on the player's head")
    @ConfigEntry.Gui.RequiresRestart
    int nvgActiveEnergyPerTickCost = 16;

    @Comment("The energy capacity of the NVG, expressed in E")
    @ConfigEntry.Gui.RequiresRestart
    int nvgEnergyCapacity = 80000;

    @Comment("How much energy (expressed in E) per tick the stun gun consumes when it's charging its capacitors")
    @ConfigEntry.Gui.RequiresRestart
    int stungunOneClickEnergyCost = 8;

    @Comment("The energy capacity of the stun gun, expressed in E")
    @ConfigEntry.Gui.RequiresRestart
    int stungunEnergyCapacity = 10000;

    @Comment("How many ticks it takes for the stun gun's capacitors to fully charge")
    @ConfigEntry.Gui.RequiresRestart
    int stungunChargeTicks = 64;

    @Comment("How much damage a charged stun gun deals specifically to arthropods. Set to 0 to disable this whole spider zapping feature")
    @ConfigEntry.Gui.RequiresRestart
    int stungunDamageDealtToArthropodsOnChargedHit = 16;

    @Comment("Should the stun gun ignite Creepers")
    @ConfigEntry.Gui.RequiresRestart
    boolean stungunShouldChargedHitsIgniteCreepers = true;

    @Comment("Should the stun gun stun vanilla boss mobs")
    @ConfigEntry.Gui.RequiresRestart
    boolean stungunShouldStunBossMobs = false;

    @Comment("How many ticks of Slowness should the Stun Gun inflict on mobs")
    @ConfigEntry.Gui.RequiresRestart
    int stungunSlownessTicks = 100;

    @Comment("How many ticks of Weakness should the Stun Gun inflict on mobs")
    @ConfigEntry.Gui.RequiresRestart
    int stungunWeaknessTicks = 100;

}
