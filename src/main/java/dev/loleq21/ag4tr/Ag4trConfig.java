package dev.loleq21.ag4tr;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "ag4tr")
public class Ag4trConfig implements ConfigData {

    int hazmatChestpieceEnergyCapacity = 120000;
    int hazmatChestpieceAirTicksCapacity = 524;
    int hazmatChestpieceInLavaCoolingEnergyPerTickCost = 32;
    int hazmatChestpieceCompressedAirCellSwapEnergyCost = 128;
    int nvgActiveEnergyPerTickCost = 16;
    int nvgEnergyCapacity = 80000;
    int taserOneClickEnergyCost = 4;
    int taserEnergyCapacity = 20000;
    int taserHowManyClicksItTakesForTheCapacitorsToFullyCharge = 64;
    int taserHowManyTicksOfSlownessAreInflictedOnChargedHit = 100;
    int taserHowManyTicksOfWeaknessAreInflictedOnChargedHit = 100;
    int taserDamageDealtToArthropodsOnChargedHit = 16;
    boolean taserShouldChargedHitsIgniteCreepers = true;
    boolean taserShouldStunBossMobs = false;

}
