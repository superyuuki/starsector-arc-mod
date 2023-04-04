package urs.world;


import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import urs.Tokens;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Insert genshin reference here
 */
public class PrimoGen {

    static void kalideus(StarSystemAPI system, PlanetAPI primo) {
        PlanetAPI kalideus = system.addPlanet(Tokens.KALIDEUS,
                primo,
                "Kalideus",
                Planets.PLANET_TERRAN_ECCENTRIC,
                360f*(float)Math.random(),
                180,
                2800,
                170);

        PlanetConditionGenerator.generateConditionsForPlanet(kalideus, StarAge.AVERAGE);
        kalideus.setCustomDescriptionId(Tokens.KALIDEUS_DESC);

        addMarketplace(Tokens.FACTION, kalideus, null,
                "Kalideus",
                6,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_6,
                                Conditions.HABITABLE,
                                Conditions.HIGH_GRAVITY,
                                Conditions.POLLUTION,
                                Conditions.FARMLAND_POOR,
                                Conditions.ORE_MODERATE,
                                Conditions.RARE_ORE_SPARSE,
                                Conditions.ORBITAL_BURNS
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.LIGHTINDUSTRY,
                                Industries.FARMING,
                                Industries.WAYSTATION,
                                Industries.STARFORTRESS_MID,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND
                        )
                ),
                0.3f,
                false,
                true
        );


    }

    static void gate(StarSystemAPI system, PlanetAPI primo) {
        SectorEntityToken lethiaGate = system.addCustomEntity(Tokens.PRIMO_GATE,
                "Primo Ilios Gate",
                "inactive_gate",
                null);
        lethiaGate.setCircularOrbit(primo, 257,100+1000, 520);
        lethiaGate.setCustomDescriptionId(Tokens.PRIMO_GATE_DESC);
    }

    static void asteroids(StarSystemAPI system, PlanetAPI primo) {
        system.addAsteroidBelt(primo, 1000, 3800, 1000, 120, 500, Terrain.ASTEROID_BELT, "Yuuki's Mantle");
        system.addRingBand(primo, "misc", "rings_asteroids0", 256f, 3, Color.gray, 256f, 3650, 220f);
        system.addRingBand(primo, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 3800, 370f);
        system.addRingBand(primo, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, 4050, 235f);
    }

    static void tulm(StarSystemAPI system, PlanetAPI primo, float radiusAfter) {
        PlanetAPI tulm = system.addPlanet(Tokens.TULM,
                primo,
                "Kalideus",
                "toxic_cold",
                360f*(float)Math.random(),
                180,
                5000,
                170);

        PlanetConditionGenerator.generateConditionsForPlanet(tulm, StarAge.AVERAGE);
        tulm.setCustomDescriptionId(Tokens.TULM_DESC);

        SectorEntityToken tulmStation = system.addCustomEntity(Tokens.TULM_STATION, "Catalyst Station", "station_side06", Tokens.FACTION);
        tulmStation.setCircularOrbitPointingDown(tulm, 360f * (float)Math.random(), radiusAfter + 700f, 600f);
        tulmStation.setCustomDescriptionId(Tokens.TULM_STATION_DESC);
        tulmStation.setInteractionImage("illustrations", "orbital");

        addMarketplace(Tokens.FACTION, tulm, null,
                "Tulm",
                4,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.EXTREME_WEATHER,
                                Conditions.HIGH_GRAVITY,
                                Conditions.POLLUTION,
                                Conditions.VOLATILES_PLENTIFUL,
                                Conditions.ORGANICS_PLENTIFUL
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.LIGHTINDUSTRY,
                                Industries.BATTLESTATION_MID,
                                Industries.HEAVYBATTERIES,
                                Industries.PATROLHQ
                        )
                ),
                0.3f,
                false,
                true
        );

        addMarketplace(Tokens.FACTION, tulmStation, null,
                "Catalyst Station",
                3,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_3,
                                Conditions.VOLATILES_TRACE,
                                Conditions.ORGANICS_TRACE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.WAYSTATION,
                                Industries.MINING,
                                Industries.ORBITALSTATION_MID,
                                Industries.GROUNDDEFENSES,
                                Industries.PATROLHQ
                        )
                ),
                0.3f,
                false,
                true
        );
    }

    static void emprum(StarSystemAPI system, PlanetAPI primo) {
        PlanetAPI emprum = system.addPlanet(Tokens.EMPRUM,
                primo,
                "Emprum",
                Planets.TUNDRA,
                360f*(float)Math.random(),
                180,
                6000,
                170);

        PlanetConditionGenerator.generateConditionsForPlanet(emprum, StarAge.AVERAGE);
        emprum.setCustomDescriptionId(Tokens.EMPRUM_DESC);

        addMarketplace(Factions.INDEPENDENT, emprum, null,
                "Emprum",
                5,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_5,
                                Conditions.HABITABLE,
                                Conditions.VERY_COLD,
                                Conditions.FARMLAND_POOR,
                                Conditions.ORE_MODERATE,
                                Conditions.ORGANICS_TRACE,
                                Conditions.ORBITAL_BURNS
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.MINING,
                                Industries.FARMING,
                                Industries.TECHMINING,
                                Industries.HEAVYBATTERIES,
                                Industries.PATROLHQ
                        )
                ),
                0.3f,
                true,
                true
        );
    }

    static void fortFairway(StarSystemAPI system, PlanetAPI primo, float radiusAfter) {
        SectorEntityToken fort = system.addCustomEntity(Tokens.FORT, "Fort Fairway", "station_side06", Factions.TRITACHYON);
        fort.setCircularOrbitPointingDown(primo, 360f * (float)Math.random(), radiusAfter + 2000f, 600f);
        fort.setCustomDescriptionId(Tokens.FORT_DESC);
        fort.setInteractionImage("illustrations", "orbital");

        addMarketplace(Factions.TRITACHYON, fort, null,
                "Fort Fairway",
                3,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_3,
                                Conditions.HABITABLE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.WAYSTATION,
                                Industries.BATTLESTATION_MID,
                                Industries.HEAVYBATTERIES,
                                Industries.MILITARYBASE
                        )
                ),
                0.3f,
                false,
                true
        );
    }


    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Primo Ilios");
        system.addTag(Tags.THEME_CORE_POPULATED);
        system.getLocation().set(4500,3000);
        PlanetAPI primo = system.initStar("urs_primo_ilios",
                StarTypes.YELLOW,
                350f,
                600f);
        system.setLightColor(new Color(255,255,255));

        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, primo, StarAge.AVERAGE,
                1, 2, // min/max entities to add
                8000, // radius to start adding at
                5, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true
        ); // whether to use custom or system-name based names



        kalideus(system, primo);
        gate(system, primo);
        asteroids(system, primo);
        tulm(system, primo, radiusAfter);
        emprum(system, primo);
        fortFairway(system, primo, radiusAfter);

        SectorEntityToken relay = system.addCustomEntity(Tokens.PRIMO_RELAY, // unique id
                "Primo Ilios Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay", // type of object, defined in custom_entities.json
                Tokens.FACTION); // faction
        relay.setCircularOrbitPointingDown( primo, 360f*(float)Math.random(), 4000, 315);

        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);

        //Finally cleans up hyperspace
        cleanup(system);
    }


    //Shorthand function for cleaning up hyperspace
    private void cleanup(StarSystemAPI system){
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0f, 360f, 0.25f);
    }


    //Shorthand function for adding a market
    public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
                                           int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries, float tarrif,
                                           boolean freePort, boolean withJunkAndChatter) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", tarrif);

        //Adds submarkets
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(freePort);

        //Adds our connected entities, if any
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        //Finally, return the newly-generated market
        return newMarket;
    }

    //Shorthand for adding derelicts, thanks Tart
    protected void addDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId,
                               ShipRecoverySpecial.ShipCondition condition, float orbitRadius, boolean recoverable) {
        DerelictShipEntityPlugin.DerelictShipData params = new DerelictShipEntityPlugin.DerelictShipData(new ShipRecoverySpecial.PerShipData(variantId, condition), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);

        float orbitDays = orbitRadius / (10f + (float) Math.random() * 5f);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, orbitDays);

        if (recoverable) {
            SalvageSpecialAssigner.ShipRecoverySpecialCreator creator = new SalvageSpecialAssigner.ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
    }

}
