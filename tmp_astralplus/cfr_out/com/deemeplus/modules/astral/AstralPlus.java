/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.deemeplus.modules.astral.AstralPlus$State
 *  com.deemeplus.ofus
 *  com.deemeplus.utils.Backpage
 *  eu.darkbot.api.API
 *  eu.darkbot.api.PluginAPI
 *  eu.darkbot.api.config.ConfigSetting
 *  eu.darkbot.api.game.entities.Portal
 *  eu.darkbot.api.game.enums.PortalType
 *  eu.darkbot.api.game.other.Locatable
 *  eu.darkbot.api.managers.AstralGateAPI
 *  eu.darkbot.api.managers.AstralGateAPI$AstralItem
 *  eu.darkbot.api.managers.AstralGateAPI$ItemStat
 *  eu.darkbot.api.managers.AuthAPI
 *  eu.darkbot.api.managers.ConfigAPI
 *  eu.darkbot.api.managers.EntitiesAPI
 *  eu.darkbot.api.managers.GameScreenAPI
 *  eu.darkbot.api.managers.HeroAPI
 *  eu.darkbot.api.managers.MovementAPI
 *  skidmod.Factory
 */
package com.deemeplus.modules.astral;

import com.deemeplus.modules.astral.AstralMediator;
import com.deemeplus.modules.astral.AstralPlus;
import com.deemeplus.modules.astral.AstralPlusConfig;
import com.deemeplus.modules.astral.CustomItemPriority;
import com.deemeplus.modules.astral.ItemInfo;
import com.deemeplus.modules.astral.PortalInfo;
import com.deemeplus.ofus;
import com.deemeplus.utils.Backpage;
import eu.darkbot.api.API;
import eu.darkbot.api.PluginAPI;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.game.entities.Portal;
import eu.darkbot.api.game.enums.PortalType;
import eu.darkbot.api.game.other.Locatable;
import eu.darkbot.api.managers.AstralGateAPI;
import eu.darkbot.api.managers.AuthAPI;
import eu.darkbot.api.managers.ConfigAPI;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.managers.GameScreenAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.MovementAPI;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import skidmod.Factory;

public class AstralPlus {
    private final AstralGateAPI astralApi;
    private final HeroAPI heroapi;
    private final MovementAPI movement;
    private final String authId;
    private CustomItemPriority customItemPriorityConfig;
    private long nextClick;
    private Collection<? extends Portal> portals;
    private boolean showDevOptions;
    private final List<String> validShips;
    private final List<String> generatorPriority;
    private final List<String> weaponPriority;
    private int maxModules;
    private int maxItemsBound;
    private Random rnd;
    private AstralMediator astralMediator;
    private State currentStatus;
    private AstralPlusConfig astralPlusConfig;
    private static int hogMZvQTRW = 0;
    private transient int tmkyiPrhH8 = 1943846870;
    private static String[] nothing_to_see_here = new String[18];

    public AstralPlus(PluginAPI pluginAPI, CustomItemPriority customItemPriority, AstralPlusConfig astralPlusConfig) {
        long l;
        State state;
        Random random;
        boolean bl;
        Collection collection;
        AstralMediator astralMediator;
        String string;
        HeroAPI heroAPI;
        MovementAPI movementAPI;
        AstralGateAPI astralGateAPI;
        State state2;
        int n;
        int n2;
        String string2;
        String string3;
        String string4;
        String string5;
        String string6;
        String string7;
        String string8;
        String string9;
        String string10;
        String string11;
        String string12;
        String string13;
        String string14;
        String string15;
        String string16;
        String string17;
        String string18;
        String string19;
        String string20;
        String string21;
        String string22;
        String string23;
        String string24;
        String string25;
        String string26;
        String string27;
        String string28;
        boolean bl2;
        long l2;
        int n3 = 0x9112CC0 ^ 0x6602C449;
        AstralPlus astralPlus = this;
        n3 = 0x9A7857D ^ n3;
        n3 = 0x3841C866 ^ (0x3FF9DC82 ^ (0x47FA4003 ^ hogMZvQTRW));
        this.tmkyiPrhH8 = 0x73DCBFD6 ^ hogMZvQTRW;
        n3 = 0x3B40582C ^ n3;
        n3 = 0x22AE556 ^ n3;
        AstralPlus astralPlus2 = this;
        astralPlus2.nextClick = l2 = 0L;
        n3 = 0x22D478D5 ^ n3;
        AstralPlus astralPlus3 = this;
        astralPlus3.showDevOptions = bl2 = false;
        n3 = 0x2C9E44EF ^ n3;
        AstralPlus astralPlus4 = this;
        int n4 = 4;
        String[] stringArray = new String[n4];
        int n5 = 0;
        stringArray[n5] = string28 = "ship_zephyr";
        int n6 = 1;
        stringArray[n6] = string27 = "ship_diminisher";
        int n7 = 2;
        stringArray[n7] = string26 = "ship_sentinel";
        int n8 = 3;
        stringArray[n8] = string25 = "ship_pusat";
        List<String> list = Arrays.asList(stringArray);
        astralPlus4.validShips = list;
        n3 = 0x395443B1 ^ n3;
        AstralPlus astralPlus5 = this;
        int n9 = 14;
        String[] stringArray2 = new String[n9];
        int n10 = 0;
        stringArray2[n10] = string24 = "speed_g3n-7900";
        int n11 = 1;
        stringArray2[n11] = string23 = "speed_g3n-6900";
        int n12 = 2;
        stringArray2[n12] = string22 = "speed_g3n-3310";
        int n13 = 3;
        stringArray2[n13] = string21 = "speed_g3n-3210";
        int n14 = 4;
        stringArray2[n14] = string20 = "speed_g3n-2010";
        int n15 = 5;
        stringArray2[n15] = string19 = "speed_g3n-1010";
        int n16 = 6;
        stringArray2[n16] = string18 = "shield_fs-03";
        int n17 = 7;
        stringArray2[n17] = string17 = "shield_sg3n-b03";
        int n18 = 8;
        stringArray2[n18] = string16 = "shield_fs-02";
        int n19 = 9;
        stringArray2[n19] = string15 = "shield_sg3n-b01";
        int n20 = 10;
        stringArray2[n20] = string14 = "shield_sg3n-b00";
        int n21 = 11;
        stringArray2[n21] = string13 = "shield_sg3n-a03";
        int n22 = 12;
        stringArray2[n22] = string12 = "shield_sg3n-a02";
        int n23 = 13;
        stringArray2[n23] = string11 = "shield_sg3n-a01";
        List<String> list2 = Arrays.asList(stringArray2);
        astralPlus5.generatorPriority = list2;
        n3 = 0x7D632117 ^ n3;
        AstralPlus astralPlus6 = this;
        int n24 = 9;
        String[] stringArray3 = new String[n24];
        int n25 = 0;
        stringArray3[n25] = string10 = "caucasus";
        int n26 = 1;
        stringArray3[n26] = string9 = "pr-l";
        int n27 = 2;
        stringArray3[n27] = string8 = "lf-4-pd";
        int n28 = 3;
        stringArray3[n28] = string7 = "lf-4-hp";
        int n29 = 4;
        stringArray3[n29] = string6 = "lf-4-md";
        int n30 = 5;
        stringArray3[n30] = string5 = "lf-4";
        int n31 = 6;
        stringArray3[n31] = string4 = "lf-3";
        int n32 = 7;
        stringArray3[n32] = string3 = "sll-03";
        int n33 = 8;
        stringArray3[n33] = string2 = "sll-02";
        List<String> list3 = Arrays.asList(stringArray3);
        astralPlus6.weaponPriority = list3;
        n3 = 0x32FB8747 ^ n3;
        AstralPlus astralPlus7 = this;
        astralPlus7.maxModules = n2 = 3;
        n3 = 0x2B1CEB57 ^ n3;
        AstralPlus astralPlus8 = this;
        astralPlus8.maxItemsBound = n = 2;
        n3 = 0x1BBB789C ^ n3;
        AstralPlus astralPlus9 = this;
        astralPlus9.currentStatus = state2 = State.WAIT;
        n3 = 0x53CE82BF ^ n3;
        AstralPlus astralPlus10 = this;
        Class<AstralGateAPI> clazz = AstralGateAPI.class;
        API aPI = pluginAPI.requireAPI(clazz);
        astralPlus10.astralApi = astralGateAPI = (AstralGateAPI)aPI;
        n3 = 0x5F87F9C0 ^ n3;
        AstralPlus astralPlus11 = this;
        Class<MovementAPI> clazz2 = MovementAPI.class;
        API aPI2 = pluginAPI.requireAPI(clazz2);
        astralPlus11.movement = movementAPI = (MovementAPI)aPI2;
        n3 = 0x1CDABC4 ^ n3;
        AstralPlus astralPlus12 = this;
        Class<HeroAPI> clazz3 = HeroAPI.class;
        API aPI3 = pluginAPI.requireAPI(clazz3);
        astralPlus12.heroapi = heroAPI = (HeroAPI)aPI3;
        n3 = 0x250021D9 ^ n3;
        AstralPlus astralPlus13 = this;
        Class<AuthAPI> clazz4 = AuthAPI.class;
        API aPI4 = pluginAPI.requireAPI(clazz4);
        AuthAPI authAPI = (AuthAPI)aPI4;
        astralPlus13.authId = string = authAPI.getAuthId();
        n3 = 0x7D51588A ^ n3;
        AstralPlus astralPlus14 = this;
        Class<GameScreenAPI> clazz5 = GameScreenAPI.class;
        API aPI5 = pluginAPI.requireAPI(clazz5);
        GameScreenAPI gameScreenAPI = (GameScreenAPI)aPI5;
        astralPlus14.astralMediator = astralMediator = new AstralMediator(gameScreenAPI, 1838514951);
        n3 = 0x27438414 ^ n3;
        Class<EntitiesAPI> clazz6 = EntitiesAPI.class;
        API aPI6 = pluginAPI.requireAPI(clazz6);
        EntitiesAPI entitiesAPI = (EntitiesAPI)aPI6;
        n3 = 0x330895DD ^ n3;
        AstralPlus astralPlus15 = this;
        astralPlus15.portals = collection = entitiesAPI.getPortals();
        n3 = 0x53A3573C ^ n3;
        Class<ConfigAPI> clazz7 = ConfigAPI.class;
        API aPI7 = pluginAPI.requireAPI(clazz7);
        ConfigAPI configAPI = (ConfigAPI)aPI7;
        n3 = 0x2E0C313D ^ n3;
        String string29 = "bot_settings.other.dev_stuff";
        ConfigSetting configSetting = configAPI.requireConfig(string29);
        n3 = 0x2EF9E863 ^ n3;
        AstralPlus astralPlus16 = this;
        Object object = configSetting.getValue();
        Boolean bl3 = (Boolean)object;
        astralPlus16.showDevOptions = bl = bl3.booleanValue();
        n3 = 0x790422E5 ^ n3;
        AstralPlus astralPlus17 = this;
        astralPlus17.rnd = random = new Random();
        n3 = 0x5FEC1935 ^ n3;
        AstralPlus astralPlus18 = this;
        astralPlus18.customItemPriorityConfig = customItemPriority;
        n3 = 0x1E996E81 ^ n3;
        AstralPlus astralPlus19 = this;
        astralPlus19.astralPlusConfig = astralPlusConfig;
        n3 = 0x16EAAE70 ^ n3;
        AstralPlus astralPlus20 = this;
        astralPlus20.initCustomItemPriority(247467543);
        n3 = 0x689EA4C ^ n3;
        AstralPlus astralPlus21 = this;
        astralPlus21.currentStatus = state = State.WAIT;
        n3 = 0x242AD36D ^ n3;
        AstralPlus astralPlus22 = this;
        astralPlus22.nextClick = l = 0L;
        n3 = 0x5E90D1FA ^ n3;
    }

    public String getStatus() {
        int n = 0x19454640 ^ (0x87FB476 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 64988806) {
            n = 0x14DED60F ^ n;
            AstralPlus astralPlus = this;
            int n2 = astralPlus.getModules(1332429683);
            AstralPlus astralPlus2 = this;
            int n3 = astralPlus2.maxModules;
            AstralPlus astralPlus3 = this;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 210766598) {
                n = 0xC8515C2 ^ n;
                int n4 = astralPlus3.getWeapons(2006410907);
                AstralPlus astralPlus4 = this;
                if (Factory.jarwsmbhjwbijwmk((int)n) == 47378243) {
                    n = 0x7DC06EE5 ^ n;
                    int n5 = astralPlus4.getMaxWeapons(1502664270);
                    AstralPlus astralPlus5 = this;
                    int n6 = astralPlus5.getGenerators(278962473);
                    AstralPlus astralPlus6 = this;
                    int n7 = astralPlus6.getMaxGenerators(1305786841);
                    AstralPlus astralPlus7 = this;
                    AstralGateAPI astralGateAPI = astralPlus7.astralApi;
                    if (Factory.jarwsmbhjwbijwmk((int)n) == 16701331) {
                        n = 0x1F4722F5 ^ n;
                        int n8 = astralGateAPI.getCurrentRift();
                        AstralPlus astralPlus8 = this;
                        String string = astralPlus8.getExtraStatus(1760174601);
                        String string2 = "\nM: " + n2 + "/" + n3 + " | W: " + n4 + "/" + n5 + " | G: " + n6 + "/" + n7 + " | Wave: " + n8 + string;
                        if (Factory.jarwsmbhjwbijwmk((int)n) == 162338917) {
                            n = 0x5E3CBC73 ^ n;
                            return string2;
                        }
                    } else {
                        while (Factory.jarwsmbhjwbijwmk((int)n) != 16701331 || Factory.jarwsmbhjwbijwmk((int)n) != (0x520710FC ^ n)) {
                        }
                        hogMZvQTRW = 0x5646DE6A ^ n;
                        throw new RuntimeException("double");
                    }
                }
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 47378243 || Factory.jarwsmbhjwbijwmk((int)n) != (0x6EF0FCF8 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x57FEF673 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 210766598 || Factory.jarwsmbhjwbijwmk((int)n) != (0x5471C9BE ^ n)) continue;
            this.tmkyiPrhH8 = 0x41B13F7F ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 162338917 || Factory.jarwsmbhjwbijwmk((int)n) != (0x65AF0209 ^ n)) continue;
            hogMZvQTRW = 0x44DE559B ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 64988806 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2F2579AD ^ n)) continue;
            this.tmkyiPrhH8 = 0x41F8B218 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 198306484 && Factory.jarwsmbhjwbijwmk((int)n) == (0x56330F95 ^ n)) break;
        }
        hogMZvQTRW = 0x7BE5D334 ^ n;
        throw new IOException();
    }

    public int getRift() {
        int n = 0xD9C11CF ^ (0x7E562ED4 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 190009086) {
            n = 0x2E49B007 ^ n;
            AstralPlus astralPlus = this;
            AstralGateAPI astralGateAPI = astralPlus.astralApi;
            int n2 = astralGateAPI.getCurrentRift();
            return n2;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 13077437 || Factory.jarwsmbhjwbijwmk((int)n) != (0x7DFACB96 ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x28BD99AF ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 190009086 && Factory.jarwsmbhjwbijwmk((int)n) == (0x4B0F07A1 ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x3914E800 ^ n;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     */
    private void initCustomItemPriority(int var1_1) {
        block20: {
            block22: {
                block24: {
                    block23: {
                        block21: {
                            var15_2 = 1233129427 ^ (679410236 ^ (this.tmkyiPrhH8 ^ var1_1));
                            if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 8882354) break block20;
                            var15_2 = 1148958201 ^ var15_2;
                            var2_3 = this;
                            var4_4 = var2_3.customItemPriorityConfig;
                            var5_5 = var4_4.itemInfos;
                            var6_6 = var5_5.size();
                            if (var6_6 <= (2121255108 ^ var15_2)) break block21;
                            if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 83421844) {
                                var15_2 = 1496481970 ^ var15_2;
                                return;
                            }
                            break block22;
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(var15_2 = 1461276795 ^ var15_2)) == 5631620) break block23;
                        if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 5631620) break block22;
                        break block24;
                    }
                    var15_2 = 1990200765 ^ var15_2;
                    var7_7 = this;
                    var8_8 = var7_7.generatorPriority;
                    var3_9 = this;
                    var11_10 = (Consumer<String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)V, lambda$initCustomItemPriority$0(java.lang.String ), (Ljava/lang/String;)V)((AstralPlus)var3_9);
                    if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 162504549) {
                        var15_2 = 961765856 ^ var15_2;
                        var8_8.forEach(var11_10);
                        if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 111406320) {
                            var15_2 = 321402662 ^ var15_2;
                            var9_11 = this;
                            var10_12 = var9_11.weaponPriority;
                            var12_13 = this;
                            var13_14 = (Consumer<String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)V, lambda$initCustomItemPriority$1(java.lang.String ), (Ljava/lang/String;)V)((AstralPlus)var12_13);
                            if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 67165926) {
                                var15_2 = 2101429725 ^ var15_2;
                                var10_12.forEach(var13_14);
                                if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 21646140) {
                                    var15_2 = 2108537113 ^ var15_2;
                                    return;
                                }
                            } else {
                                while (Factory.jarwsmbhjwbijwmk((int)var15_2) != 67165926 || Factory.jarwsmbhjwbijwmk((int)var15_2) != (714302067 ^ var15_2)) {
                                }
                                this.tmkyiPrhH8 = 876339343 ^ var15_2;
                                throw new IllegalAccessException("double");
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)var15_2) != 21646140 || Factory.jarwsmbhjwbijwmk((int)var15_2) != (135298496 ^ var15_2)) {
                            }
                            this.tmkyiPrhH8 = 1541503055 ^ var15_2;
                            throw new IOException("double");
                        }
                    }
                    break block22;
                }
                block7: while (true) {
                    switch (Factory.jarwsmbhjwbijwmk((int)var15_2)) {
                        case 59006353: {
                            continue block7;
                        }
                        case 5631620: {
                            var15_2 = 2108874147 ^ var15_2;
                            break block7;
                        }
lbl56:
                        // 2 sources

                        default: {
                            while (true) {
                                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 53354321) ** GOTO lbl56
                                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != (1319416927 ^ var15_2)) continue;
                                this.tmkyiPrhH8 = 645464917 ^ var15_2;
                                if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 205822777 && Factory.jarwsmbhjwbijwmk((int)var15_2) == (1002701794 ^ var15_2)) break;
                            }
                            this.tmkyiPrhH8 = 412742313 ^ var15_2;
                            throw new RuntimeException("double");
                        }
lbl65:
                        // 2 sources

                        case 1127456297: 
                    }
                    break;
                }
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 83421844) ** GOTO lbl65
                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != (387589356 ^ var15_2)) continue;
                AstralPlus.hogMZvQTRW = 1443231452 ^ var15_2;
                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 111406320 || Factory.jarwsmbhjwbijwmk((int)var15_2) != (399732426 ^ var15_2)) continue;
                this.tmkyiPrhH8 = 725794844 ^ var15_2;
                if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 162504549 || Factory.jarwsmbhjwbijwmk((int)var15_2) != (57943614 ^ var15_2)) continue;
                AstralPlus.hogMZvQTRW = 1971329995 ^ var15_2;
                if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 5631620 && Factory.jarwsmbhjwbijwmk((int)var15_2) == (1358925509 ^ var15_2)) break;
            }
            AstralPlus.hogMZvQTRW = 1161699991 ^ var15_2;
            throw new IllegalAccessException();
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var15_2) != 71045410 || Factory.jarwsmbhjwbijwmk((int)var15_2) != (372201117 ^ var15_2)) {
                continue;
            }
            AstralPlus.hogMZvQTRW = 1147492358 ^ var15_2;
            if (Factory.jarwsmbhjwbijwmk((int)var15_2) == 8882354 && Factory.jarwsmbhjwbijwmk((int)var15_2) == (1620066554 ^ var15_2)) break;
        }
        AstralPlus.hogMZvQTRW = 319827311 ^ var15_2;
        throw new IOException("double");
    }

    public void updateConfig(CustomItemPriority customItemPriority, AstralPlusConfig astralPlusConfig) {
        int n = 0x2831AD70 ^ (0x331DC4A8 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 120398158) {
            n = 0x7804664 ^ n;
            AstralPlus astralPlus = this;
            astralPlus.customItemPriorityConfig = customItemPriority;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 242524636) {
                n = 0x23C6203A ^ n;
                AstralPlus astralPlus2 = this;
                astralPlus2.astralPlusConfig = astralPlusConfig;
                if (Factory.jarwsmbhjwbijwmk((int)n) == 232612785) {
                    n = 0x576B2B96 ^ n;
                    return;
                }
            } else {
                while (Factory.jarwsmbhjwbijwmk((int)n) != 242524636 || Factory.jarwsmbhjwbijwmk((int)n) != (0x79FB8304 ^ n)) {
                }
                this.tmkyiPrhH8 = 0x2E6D62F1 ^ n;
                throw new IOException("double");
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 232612785 || Factory.jarwsmbhjwbijwmk((int)n) != (0x45A22476 ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x30ECE892 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 109360727 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2DB0C154 ^ n)) continue;
            this.tmkyiPrhH8 = 0x4C96EB4E ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 120398158 && Factory.jarwsmbhjwbijwmk((int)n) == (0x4F01A67D ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x1FFC2899 ^ n;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     */
    private void addItemToTheTable(String var1_1, int var2_2, int var3_3) {
        block16: {
            block12: {
                block17: {
                    block15: {
                        block14: {
                            block13: {
                                var15_4 = 1251490600 ^ (1485881611 ^ (this.tmkyiPrhH8 ^ var3_3));
                                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 198352766) break block12;
                                var15_4 = 335387912 ^ var15_4;
                                var4_5 = this;
                                var6_6 = var4_5.customItemPriorityConfig;
                                var7_7 = var6_6.itemInfos;
                                var9_9 = var8_8 = var7_7.get(var1_1);
                                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 48877379) break block13;
                                var15_4 = 617603112 ^ var15_4;
                                if (var9_9 != null) break block14;
                                if (Factory.jarwsmbhjwbijwmk((int)var15_4) == 138875386) {
                                    var15_4 = 1200000940 ^ var15_4;
                                    var10_10 = this;
                                    var11_11 = var10_10.customItemPriorityConfig;
                                    var12_12 = var11_11.itemInfos;
                                    var5_13 = new ItemInfo(var2_2, 1934644554);
                                    var13_14 = var12_12.put(var1_1, var5_13);
                                    if (Factory.jarwsmbhjwbijwmk((int)var15_4) == 262330922) {
                                        var15_4 = 1332428423 ^ var15_4;
lbl20:
                                        // 2 sources

                                        return;
                                    }
                                }
                                break block12;
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)var15_4) != 48877379 || Factory.jarwsmbhjwbijwmk((int)var15_4) != (1845202063 ^ var15_4)) {
                            }
                            AstralPlus.hogMZvQTRW = 1178904130 ^ var15_4;
                            throw new IOException("double");
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(var15_4 = AstralPlus.mjbkytyqbyxonfdc(var15_4, 346584169))) == 11431651) break block15;
                        if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 11431651) break block16;
                        break block17;
                    }
                    var15_4 = 474334530 ^ var15_4;
                    ** while (true)
                }
                block6: while (true) {
                    switch (Factory.jarwsmbhjwbijwmk((int)var15_4)) {
                        case 1484893587: {
                            continue block6;
                        }
                        case 11431651: {
                            var15_4 = 1323351136 ^ var15_4;
                        }
                    }
                    break;
                }
            }
lbl43:
            // 2 sources

            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 138875386) ** GOTO lbl43
                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != (672298674 ^ var15_4)) continue;
                this.tmkyiPrhH8 = 1720409500 ^ var15_4;
                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 106673925 || Factory.jarwsmbhjwbijwmk((int)var15_4) != (1158285830 ^ var15_4)) continue;
                AstralPlus.hogMZvQTRW = 454831237 ^ var15_4;
                if (Factory.jarwsmbhjwbijwmk((int)var15_4) != 262330922 || Factory.jarwsmbhjwbijwmk((int)var15_4) != (1993960101 ^ var15_4)) continue;
                AstralPlus.hogMZvQTRW = 103252848 ^ var15_4;
                if (Factory.jarwsmbhjwbijwmk((int)var15_4) == 198352766 && Factory.jarwsmbhjwbijwmk((int)var15_4) == (1298454715 ^ var15_4)) break;
            }
            AstralPlus.hogMZvQTRW = 514857504 ^ var15_4;
            throw new RuntimeException();
        }
        while (Factory.jarwsmbhjwbijwmk((int)var15_4) != 11431651 || Factory.jarwsmbhjwbijwmk((int)var15_4) != (1046450935 ^ var15_4)) {
        }
        this.tmkyiPrhH8 = 117829641 ^ var15_4;
        throw new IllegalAccessException("double");
    }

    private String getExtraStatus(int n) {
        int n2;
        block8: {
            block14: {
                block10: {
                    block12: {
                        block13: {
                            block11: {
                                block9: {
                                    n2 = 0x205123ED ^ (0x501AD169 ^ (this.tmkyiPrhH8 ^ n));
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 104708768) break block8;
                                    n2 = 0x4B5511F1 ^ n2;
                                    AstralPlus astralPlus = this;
                                    int n3 = astralPlus.showDevOptions;
                                    if (n3 != (0x6DE47B9 ^ n2)) break block9;
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 108029411) {
                                        n2 = 0x1E461E2E ^ n2;
                                        String string = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.ocmfunlpsvxaejk(), n2);
                                        return string;
                                    }
                                    break block10;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 1184330100))) == 210606692) break block11;
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 210606692) break block12;
                                break block13;
                            }
                            n2 = 0x1D0DF937 ^ n2;
                            AstralPlus astralPlus = this;
                            State state = astralPlus.currentStatus;
                            String string = state.message;
                            AstralPlus astralPlus2 = this;
                            AstralMediator astralMediator = astralPlus2.astralMediator;
                            boolean bl = astralMediator.isAstralGuiVisible(1622970002);
                            AstralPlus astralPlus3 = this;
                            Collection<? extends Portal> collection = astralPlus3.portals;
                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 78974896) break block8;
                            n2 = 0x62DEB85E ^ n2;
                            int n4 = collection.size();
                            AstralPlus astralPlus4 = this;
                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 188446807) break block8;
                            n2 = 0x2602F856 ^ n2;
                            boolean bl2 = astralPlus4.hasOptionsToChoose(1326288501);
                            AstralPlus astralPlus5 = this;
                            AstralGateAPI astralGateAPI = astralPlus5.astralApi;
                            boolean bl3 = astralGateAPI.allowedToEquip();
                            String string2 = "\nLast Action:" + string + "\nG: " + bl + " | P: " + n4 + " | O: " + bl2 + " | E: " + bl3;
                            if (Factory.jarwsmbhjwbijwmk((int)n2) == 26709884) {
                                n2 = 0x51FC41AD ^ n2;
                                return string2;
                            }
                            break block14;
                        }
                        n2 = 0x7ECF2539 ^ n2;
                        break block8;
                    }
                    while (Factory.jarwsmbhjwbijwmk((int)n2) != 210606692 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x3757B964 ^ n2)) {
                    }
                    this.tmkyiPrhH8 = 0x64D0C601 ^ n2;
                    throw new IOException("double");
                }
                while (Factory.jarwsmbhjwbijwmk((int)n2) != 108029411 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x1E01CA8A ^ n2)) {
                }
                hogMZvQTRW = 0x2B9330C6 ^ n2;
                throw new IllegalAccessException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 262616332 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x615493F7 ^ n2)) {
                    continue;
                }
                this.tmkyiPrhH8 = 0x69AE235 ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) == 26709884 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x201195AF ^ n2)) break;
            }
            this.tmkyiPrhH8 = 0x2ED772F1 ^ n2;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 188446807 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x2BF5B7BF ^ n2)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x302952E ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 104708768 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x62B9338F ^ n2)) continue;
            hogMZvQTRW = 0x279BF18 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 78974896 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x148C8C45 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x8B08033 ^ n2;
        throw new IOException();
    }

    public boolean isValidShip() {
        int n = 0x52D4D698 ^ (0x21A49B77 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 173002747) {
            n = 0x3F57A414 ^ n;
            AstralPlus astralPlus = this;
            HeroAPI heroAPI = astralPlus.heroapi;
            String string = heroAPI.getShipType();
            if (Factory.jarwsmbhjwbijwmk((int)n) == 9051781) {
                n = 0x4BC96EED ^ n;
                AstralPlus astralPlus2 = this;
                List<String> list = astralPlus2.validShips;
                Stream stream = list.stream();
                Predicate<String> predicate = string2 -> {
                    int n = 0x2A19729D ^ (0x9992C94 ^ hogMZvQTRW);
                    if (Factory.jarwsmbhjwbijwmk((int)n) == 85919198) {
                        n = 0x7FE23675 ^ n;
                        boolean bl = string2.equals(string);
                        return bl;
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)n) != 226436947 || Factory.jarwsmbhjwbijwmk((int)n) != (0x77FB99B8 ^ n)) {
                            continue;
                        }
                        hogMZvQTRW = 0x4494C513 ^ n;
                        if (Factory.jarwsmbhjwbijwmk((int)n) == 85919198 && Factory.jarwsmbhjwbijwmk((int)n) == (0x132B43CD ^ n)) break;
                    }
                    hogMZvQTRW = 0x20CCEEB8 ^ n;
                    throw new IOException();
                };
                boolean bl = stream.anyMatch(predicate);
                return bl;
            }
            while (Factory.jarwsmbhjwbijwmk((int)n) != 9051781 || Factory.jarwsmbhjwbijwmk((int)n) != (0x10680243 ^ n)) {
            }
            hogMZvQTRW = 0x111144F5 ^ n;
            throw new IOException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 173002747 || Factory.jarwsmbhjwbijwmk((int)n) != (0x63F258D ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x6625ADB ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 6000972 && Factory.jarwsmbhjwbijwmk((int)n) == (0x3F669037 ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x4BBF8D37 ^ n;
        throw new IOException();
    }

    public boolean allowedToEquip() {
        int n = 0x243932D9 ^ (0x7365466 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 82384073) {
            n = 0x1D77FB8B ^ n;
            AstralPlus astralPlus = this;
            AstralGateAPI astralGateAPI = astralPlus.astralApi;
            boolean bl = astralGateAPI.allowedToEquip();
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 82384073 || Factory.jarwsmbhjwbijwmk((int)n) != (0x113B58EF ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x4CD9755E ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 267353456 && Factory.jarwsmbhjwbijwmk((int)n) == (0x20C95644 ^ n)) break;
        }
        hogMZvQTRW = 0x3FCE39BA ^ n;
        throw new RuntimeException();
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public boolean autoChoose(Map<String, PortalInfo> var1_1, CustomItemPriority var2_2) {
        block44: {
            var20_3 = 788962732 ^ (299043622 ^ this.tmkyiPrhH8);
            if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 20857958) break block44;
            var20_3 = 410695638 ^ var20_3;
            var3_4 = this;
            var3_4.customItemPriorityConfig = var2_2;
            if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 265455421) ** GOTO lbl135
            var20_3 = 93025504 ^ var20_3;
            var4_5 = this;
            var5_6 = var4_5.authId;
            var6_7 = Backpage.isDonorInternal((String)var5_6, (int)930836536);
            if (var6_7 == (1980683129 ^ var20_3)) {
                if (Factory.jarwsmbhjwbijwmk((int)var20_3) == 79482804) {
                    var20_3 = 2088561012 ^ var20_3;
                    var7_8 = (byte)(175258125 ^ var20_3);
                    return var7_8;
                }
                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 79482804 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (926539753 ^ var20_3)) {
                }
                AstralPlus.hogMZvQTRW = 927130890 ^ var20_3;
                throw new RuntimeException("double");
            }
            if (Factory.jarwsmbhjwbijwmk((int)(var20_3 = AstralPlus.mjbkytyqbyxonfdc(var20_3, 1344960799))) == 166145025) ** GOTO lbl33
            if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 166145025) {
                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 166145025 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (1719093437 ^ var20_3)) {
                }
                this.tmkyiPrhH8 = 145492102 ^ var20_3;
                throw new IOException("double");
            }
            block24: while (true) {
                switch (Factory.jarwsmbhjwbijwmk((int)var20_3)) {
                    case 912050291: {
                        continue block24;
                    }
                    case 166145025: {
                        var20_3 = 445236392 ^ var20_3;
                        ** GOTO lbl135
                    }
lbl33:
                    // 1 sources

                    var20_3 = 454847894 ^ var20_3;
                    var8_9 = this;
                    var9_10 = var8_9.waitToClick(902992194);
                    if (var9_10 == (1027131376 ^ var20_3)) ** GOTO lbl42
                    if (Factory.jarwsmbhjwbijwmk((int)var20_3) == 111033945) {
                        var20_3 = 1112893732 ^ var20_3;
                        var10_11 = (byte)(2137894613 ^ var20_3);
                        return var10_11;
                    }
                    ** GOTO lbl122
lbl42:
                    // 1 sources

                    if (Factory.jarwsmbhjwbijwmk((int)(var20_3 = 1442903223 ^ var20_3)) == 264013921) ** GOTO lbl45
                    if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 264013921) ** GOTO lbl-1000
                    ** GOTO lbl120
lbl45:
                    // 1 sources

                    block25: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var20_3)) {
                            case 1865785779: {
                                continue block25;
                            }
                            case 264013921: {
                                var20_3 = 1941258013 ^ var20_3;
                            }
                            case 500488133: {
                                var11_12 = this;
                                var12_13 = var11_12.portals;
                                var13_14 = var12_13.isEmpty();
                                if (var13_14 == (411896922 ^ var20_3)) ** GOTO lbl62
                                if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 261185379) ** GOTO lbl110
                                var20_3 = 1121552592 ^ var20_3;
                                var16_15 = this;
                                var17_16 = var16_15.chooseBestOption(2028312085);
                                if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 251894431) ** GOTO lbl70
                                ** GOTO lbl74
lbl62:
                                // 1 sources

                                if (Factory.jarwsmbhjwbijwmk((int)(var20_3 = 15019282 ^ var20_3)) == 256497683) ** GOTO lbl98
                                if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 256497683) ** GOTO lbl66
                                var20_3 = 616910726 ^ var20_3;
                                ** GOTO lbl135
lbl66:
                                // 2 sources

                                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 256497683 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (2045747213 ^ var20_3)) {
                                }
                                this.tmkyiPrhH8 = 864416951 ^ var20_3;
                                throw new RuntimeException("double");
lbl70:
                                // 2 sources

                                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 251894431 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (1357813671 ^ var20_3)) {
                                }
                                this.tmkyiPrhH8 = 602391660 ^ var20_3;
                                throw new RuntimeException("double");
lbl74:
                                // 1 sources

                                block28: while (true) {
                                    switch (Factory.jarwsmbhjwbijwmk((int)var20_3)) {
                                        case 515273999: {
                                            continue block28;
                                        }
                                        case 251894431: {
                                            var20_3 = 1321062398 ^ var20_3;
                                        }
                                        case 875900513: {
                                            try {
                                                if (Factory.jarwsmbhjwbijwmk((int)var20_3) == 142874329) ** GOTO lbl95
                                                throw null;
                                            }
                                            catch (IOException v0) {
                                                switch (Factory.efuojskrxnitvcvt((int)var20_3)) {
                                                    case 1951567210: {
                                                        var20_3 = 285387818 ^ var20_3;
                                                        ** GOTO lbl91
                                                    }
                                                    case -1488299104: {
                                                        var20_3 = 1264695027 ^ var20_3;
lbl91:
                                                        // 2 sources

                                                        if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 152646845) ** GOTO lbl135
                                                        var20_3 = AstralPlus.mjbkytyqbyxonfdc(var20_3, 1991658448);
                                                        ** GOTO lbl103
                                                    }
                                                }
                                            }
lbl95:
                                            // 2 sources

                                            throw new IOException();
                                        }
                                        default: {
                                            if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 265455421) ** GOTO lbl135
                                        }
                                    }
                                    break;
                                }
lbl98:
                                // 1 sources

                                var20_3 = AstralPlus.mjbkytyqbyxonfdc(var20_3, 292166819);
                                var14_17 = this;
                                var14_17.autoChoosePortal(var1_1, 1664717869);
                                if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 24403701) ** GOTO lbl105
                                var20_3 = 540838844 ^ var20_3;
lbl103:
                                // 2 sources

                                var15_18 = (byte)(691975766 ^ var20_3);
                                return var15_18;
lbl105:
                                // 2 sources

                                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 24403701 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (1781073440 ^ var20_3)) {
                                }
                                AstralPlus.hogMZvQTRW = 1242054565 ^ var20_3;
                                throw new IllegalAccessException("double");
                                throw new IOException("Error in hash");
lbl110:
                                // 2 sources

                                while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 261185379 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (321492830 ^ var20_3)) {
                                }
                                AstralPlus.hogMZvQTRW = 459822426 ^ var20_3;
                                throw new RuntimeException("double");
                            }
lbl114:
                            // 2 sources

                            default: lbl-1000:
                            // 2 sources

                            {
                                do {
                                    if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 264013921) ** GOTO lbl114
                                } while (Factory.jarwsmbhjwbijwmk((int)var20_3) != (1257550871 ^ var20_3));
                                this.tmkyiPrhH8 = 774580452 ^ var20_3;
                                throw new IOException("double");
                            }
                        }
                        break;
                    }
lbl120:
                    // 1 sources

                    var20_3 = AstralPlus.mjbkytyqbyxonfdc(var20_3, 1469406601);
                    ** GOTO lbl135
lbl122:
                    // 3 sources

                    default: {
                        do {
                            if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 111033945) ** GOTO lbl122
                        } while (Factory.jarwsmbhjwbijwmk((int)var20_3) != (6738129 ^ var20_3));
                        this.tmkyiPrhH8 = 1928769829 ^ var20_3;
                        throw new RuntimeException("double");
                    }
                    case 1057941795: {
                        if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 265455421) ** GOTO lbl135
                    }
                }
                break;
            }
        }
        while (Factory.jarwsmbhjwbijwmk((int)var20_3) != 20857958 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (1953012456 ^ var20_3)) {
        }
        AstralPlus.hogMZvQTRW = 940481828 ^ var20_3;
        throw new IOException("double");
lbl135:
        // 10 sources

        if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 265455421) ** GOTO lbl135
        if (Factory.jarwsmbhjwbijwmk((int)var20_3) != (2104190484 ^ var20_3)) ** GOTO lbl135
        AstralPlus.hogMZvQTRW = 1579240918 ^ var20_3;
        if (Factory.jarwsmbhjwbijwmk((int)var20_3) != 152646845 || Factory.jarwsmbhjwbijwmk((int)var20_3) != (1009108981 ^ var20_3)) ** GOTO lbl135
        AstralPlus.hogMZvQTRW = 121868133 ^ var20_3;
        throw new RuntimeException();
    }

    public boolean autoChooseShip(String string) {
        int n;
        block8: {
            block6: {
                block10: {
                    block9: {
                        block7: {
                            n = 0x46F4CBBE ^ (0x4458437D ^ this.tmkyiPrhH8);
                            if (Factory.jarwsmbhjwbijwmk((int)n) != 160689325) break block6;
                            n = 0x1752354F ^ n;
                            AstralPlus astralPlus = this;
                            String string2 = astralPlus.authId;
                            int n2 = Backpage.isDonorInternal((String)string2, (int)930836536);
                            if (n2 == (0x40D43B49 ^ n)) break block7;
                            if (Factory.jarwsmbhjwbijwmk((int)n) == 228291081) {
                                n = 0x41C0B297 ^ n;
                                AstralPlus astralPlus2 = this;
                                astralPlus2.chooseShip(string, 964008139);
                                if (Factory.jarwsmbhjwbijwmk((int)n) == 16990292) {
                                    n = 0x2515F828 ^ n;
                                    boolean bl = (byte)(0x240171F7 ^ n);
                                    return bl;
                                }
                            }
                            break block8;
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(n = 0x6F56477B ^ n)) == 202178338) break block9;
                        if (Factory.jarwsmbhjwbijwmk((int)n) != 202178338) break block6;
                        break block10;
                    }
                    n = AstralPlus.mjbkytyqbyxonfdc(n, 1958120592);
                    boolean bl = (byte)(0x5B34F0A2 ^ n);
                    return bl;
                }
                n = 0x13EFE3BD ^ n;
                break block8;
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n) != 202178338 || Factory.jarwsmbhjwbijwmk((int)n) != (0x69F78E06 ^ n)) {
                    continue;
                }
                hogMZvQTRW = 0x5142BAAB ^ n;
                if (Factory.jarwsmbhjwbijwmk((int)n) == 160689325 && Factory.jarwsmbhjwbijwmk((int)n) == (0x708DC6AB ^ n)) break;
            }
            hogMZvQTRW = 0x14CC25F7 ^ n;
            throw new IOException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 16990292 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2AF39696 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x5553A69A ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 96659661 || Factory.jarwsmbhjwbijwmk((int)n) != (0x4C32ADC ^ n)) continue;
            hogMZvQTRW = 0x7EBD5428 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 11979021 || Factory.jarwsmbhjwbijwmk((int)n) != (0x63F0E4E8 ^ n)) continue;
            this.tmkyiPrhH8 = 0x19F48F21 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 228291081 || Factory.jarwsmbhjwbijwmk((int)n) != (0x7866D994 ^ n)) continue;
            this.tmkyiPrhH8 = 0x345C97C4 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 85219656 && Factory.jarwsmbhjwbijwmk((int)n) == (0x1B605FF ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x7DBE302D ^ n;
        throw new IOException();
    }

    private void updateNextClick(int n) {
        int n2 = 0x621F8AF0 ^ (0x3DF59F32 ^ (this.tmkyiPrhH8 ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 81085844) {
            long l;
            n2 = 0x5971FEFB ^ n2;
            AstralPlus astralPlus = this;
            long l2 = System.currentTimeMillis();
            AstralPlus astralPlus2 = this;
            AstralPlusConfig astralPlusConfig = astralPlus2.astralPlusConfig;
            int n3 = astralPlusConfig.minTimePerAction;
            short s = (short)(0x53B15000 ^ n2);
            int n4 = n3 * s;
            long l3 = n4;
            astralPlus.nextClick = l = l2 + l3;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 36159776) {
                n2 = 0x7BE9E365 ^ n2;
                return;
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 81085844 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x71A86037 ^ n2)) {
                continue;
            }
            this.tmkyiPrhH8 = 0xF419A26 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 237764169 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x74AD6B92 ^ n2)) continue;
            this.tmkyiPrhH8 = 0x55F479FD ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 36159776 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x7867422D ^ n2)) break;
        }
        hogMZvQTRW = 0x51CC89C2 ^ n2;
        throw new IOException();
    }

    /*
     * Exception decompiling
     */
    private void chooseShip(String var1_1, int var2_2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    private boolean chooseItem(int var1_1, int var2_2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private boolean chooseBestOption(int var1_1) {
        block19: {
            block25: {
                block23: {
                    block22: {
                        block26: {
                            block27: {
                                block24: {
                                    block21: {
                                        block20: {
                                            var18_2 = 1033820277 ^ (944693644 ^ (this.tmkyiPrhH8 ^ var1_1));
                                            if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 230455200) break block19;
                                            var18_2 = 1973429707 ^ var18_2;
                                            var2_3 = this;
                                            var4_4 = var2_3.hasOptionsToChoose(1326288501);
                                            if (var4_4 != (1572801762 ^ var18_2)) break block20;
                                            if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 94532938) {
                                                var18_2 = 1307719293 ^ var18_2;
                                                var5_5 = (byte)(273496735 ^ var18_2);
                                                return var5_5;
                                            }
                                            break block19;
                                        }
                                        if (Factory.jarwsmbhjwbijwmk((int)(var18_2 = AstralPlus.mjbkytyqbyxonfdc(var18_2, 790714766))) == 236107551) break block21;
                                        if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 236107551) break block22;
                                        break block23;
                                    }
                                    var18_2 = AstralPlus.mjbkytyqbyxonfdc(var18_2, 1499406551);
                                    var6_6 = this;
                                    var7_7 = var6_6.astralMediator;
                                    var8_8 = var7_7.isAstralGuiVisible(1622970002);
                                    if (var8_8 != (734098875 ^ var18_2)) break block24;
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 80150491) break block22;
                                    var18_2 = 123767245 ^ var18_2;
                                    var9_9 = this;
                                    var9_9.updateNextClick(15892);
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 108656005) break block25;
                                    var18_2 = 999820502 ^ var18_2;
                                    var10_10 = this;
                                    var11_11 = var10_10.astralMediator;
                                    var3_12 = (byte)(389673121 ^ var18_2);
                                    var11_11.setAstralGuiVisible(var3_12, 632231041);
                                    if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 218125583) {
                                        var18_2 = 239541663 ^ var18_2;
                                        var12_13 = (byte)(427748670 ^ var18_2);
                                        return var12_13;
                                    }
                                    break block26;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(var18_2 = AstralPlus.mjbkytyqbyxonfdc(var18_2, 932699970))) == 115865364) break block27;
                                if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 115865364) break block26;
                                var18_2 = AstralPlus.mjbkytyqbyxonfdc(var18_2, 937494603);
                                break block25;
                            }
                            var18_2 = 1253774813 ^ var18_2;
                            var13_14 = this;
                            var14_15 = var13_14.getBestItem(815847415);
                            if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 141358264) {
                                var18_2 = 302583244 ^ var18_2;
                                var15_16 = this;
                                var16_17 = var15_16.chooseItem(var14_15, 94032529);
                                return var16_17;
                            }
                            break block25;
                        }
                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 115865364 || Factory.jarwsmbhjwbijwmk((int)var18_2) != (536337752 ^ var18_2)) {
                                continue;
                            }
                            this.tmkyiPrhH8 = 194623232 ^ var18_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 218125583 && Factory.jarwsmbhjwbijwmk((int)var18_2) == (1100733112 ^ var18_2)) break;
                        }
                        AstralPlus.hogMZvQTRW = 2078912516 ^ var18_2;
                        throw new IOException("double");
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 236107551 || Factory.jarwsmbhjwbijwmk((int)var18_2) != (937350686 ^ var18_2)) {
                            continue;
                        }
                        this.tmkyiPrhH8 = 1975196623 ^ var18_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 80150491 && Factory.jarwsmbhjwbijwmk((int)var18_2) == (941395351 ^ var18_2)) break;
                    }
                    AstralPlus.hogMZvQTRW = 103657463 ^ var18_2;
                    throw new IllegalAccessException("double");
                }
                block7: while (true) {
                    switch (Factory.jarwsmbhjwbijwmk((int)var18_2)) {
                        case 333806950: {
                            continue block7;
                        }
                        case 236107551: {
                            var18_2 = 1495922654 ^ var18_2;
                            break block7;
                        }
lbl78:
                        // 2 sources

                        default: {
                            do {
                                if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 91971141) ** GOTO lbl78
                            } while (Factory.jarwsmbhjwbijwmk((int)var18_2) != (1236779286 ^ var18_2));
                            this.tmkyiPrhH8 = 1676031864 ^ var18_2;
                            throw new IllegalAccessException("double");
                        }
lbl84:
                        // 2 sources

                        case 1824555847: 
                    }
                    break;
                }
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 141358264) ** GOTO lbl84
                if (Factory.jarwsmbhjwbijwmk((int)var18_2) != (0x22204224 ^ var18_2)) continue;
                AstralPlus.hogMZvQTRW = 2045831082 ^ var18_2;
                if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 78897295 || Factory.jarwsmbhjwbijwmk((int)var18_2) != (480541855 ^ var18_2)) continue;
                this.tmkyiPrhH8 = 543755339 ^ var18_2;
                if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 108656005 && Factory.jarwsmbhjwbijwmk((int)var18_2) == (861836901 ^ var18_2)) break;
            }
            this.tmkyiPrhH8 = 1043715383 ^ var18_2;
            throw new IllegalAccessException();
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var18_2) != 230455200 || Factory.jarwsmbhjwbijwmk((int)var18_2) != (1795709239 ^ var18_2)) {
                continue;
            }
            AstralPlus.hogMZvQTRW = 1065342566 ^ var18_2;
            if (Factory.jarwsmbhjwbijwmk((int)var18_2) == 94532938 && Factory.jarwsmbhjwbijwmk((int)var18_2) == (2035543784 ^ var18_2)) break;
        }
        this.tmkyiPrhH8 = 1103978561 ^ var18_2;
        throw new RuntimeException("double");
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     * Handled duff style switch with additional control
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void autoChoosePortal(Map var1_1, int var2_2) {
        var31_3 = 1398355684 ^ (1111561362 ^ (this.tmkyiPrhH8 ^ var2_2));
        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 195397945) ** GOTO lbl194
        var31_3 = 1880911074 ^ var31_3;
        var3_4 = this;
        var7_5 = var3_4.portals;
        var8_6 = var7_5.isEmpty();
        if (var8_6 != (1461148796 ^ var31_3)) {
            if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 146609191) {
                var31_3 = 308265456 ^ var31_3;
                return;
            }
            while (Factory.jarwsmbhjwbijwmk((int)var31_3) != 146609191 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1446357842 ^ var31_3)) {
            }
            this.tmkyiPrhH8 = 415936282 ^ var31_3;
            throw new IOException("double");
        }
        block28: while (true) {
            switch (Factory.jarwsmbhjwbijwmk((int)var31_3)) {
                case 1010077309: {
                    continue block28;
                }
                case 146609191: {
                    var31_3 = 88493487 ^ var31_3;
                }
                case 1494638459: {
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 259866841) ** GOTO lbl26
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 259866841) {
                        var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 477881410);
                    }
                    ** GOTO lbl194
lbl26:
                    // 1 sources

                    var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 627040378);
                    var9_7 = this;
                    var9_7.closeGui(1187805919);
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 111969081) ** GOTO lbl178
                    var31_3 = 891934754 ^ var31_3;
                    var10_8 = this;
                    var10_8.currentStatus = var4_9 = State.CHOOSING_PORTAL;
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 2859610) ** GOTO lbl168
                    var31_3 = 2104813571 ^ var31_3;
                    var11_10 = this;
                    var12_11 = var11_10.getBestPortal(var1_1, 897241544);
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 179468708) ** GOTO lbl194
                    var31_3 = 65058086 ^ var31_3;
                    if (var12_11 != null) ** GOTO lbl120
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 94080674) ** GOTO lbl178
                    var31_3 = 2067557431 ^ var31_3;
                    var13_12 = this;
                    var14_13 = var13_12.portals;
                    var15_14 = var14_13.stream();
                    var28_15 = (Predicate<Portal>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$autoChoosePortal$3(eu.darkbot.api.game.entities.Portal ), (Leu/darkbot/api/game/entities/Portal;)Z)();
                    var16_16 = var15_14.filter(var28_15);
                    var17_17 = var16_16.findFirst();
                    var29_18 = null;
                    var18_19 = var17_17.orElse((Portal)var29_18);
                    var12_11 = var18_19;
                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 178616359) ** GOTO lbl194
                    var31_3 = 581676741 ^ var31_3;
lbl53:
                    // 2 sources

                    while (true) {
                        if (var12_11 != null) ** GOTO lbl59
                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 66536566) {
                            var31_3 = 1673859799 ^ var31_3;
                            return;
                        }
                        ** GOTO lbl164
lbl59:
                        // 1 sources

                        if (Factory.jarwsmbhjwbijwmk((int)(var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 15104126))) == 86575779) ** GOTO lbl62
                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 86575779) ** GOTO lbl168
                        ** GOTO lbl118
lbl62:
                        // 1 sources

                        block30: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var31_3)) {
                                case 1479331553: {
                                    continue block30;
                                }
                                case 86575779: {
                                    var31_3 = 1153037091 ^ var31_3;
                                }
                                case 25409927: {
                                    var19_20 = this;
                                    var20_21 = var19_20.heroapi;
                                    var21_22 = var20_21.distanceTo((Locatable)var12_11);
                                    var5_23 = 200.0;
                                    var23_24 = var21_22 == var5_23 ? 0 : (var21_22 < var5_23 ? -1 : 1);
                                    if (var23_24 >= (561724673 ^ var31_3)) ** GOTO lbl98
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 14608202) ** GOTO lbl178
                                    var31_3 = 1368218798 ^ var31_3;
                                    var24_25 = this;
                                    var25_26 = var24_25.movement;
                                    var25_26.jumpPortal(var12_11);
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 182267317) ** GOTO lbl194
                                    var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 1549682894);
                                    try {
                                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 109444227) {
                                            throw null;
                                        }
                                        throw new RuntimeException();
                                    }
                                    catch (RuntimeException v0) {
                                        switch (Factory.efuojskrxnitvcvt((int)var31_3)) {
                                            case 1698777865: {
                                                var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 1543309184);
                                                ** GOTO lbl93
                                            }
                                            case -1730446567: {
                                                var31_3 = 1145979266 ^ var31_3;
lbl93:
                                                // 2 sources

                                                if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 120946054) {
                                                    var31_3 = 714229567 ^ var31_3;
                                                    return;
                                                }
                                                ** GOTO lbl109
                                            }
                                        }
                                        throw new RuntimeException("Error in hash");
                                    }
lbl98:
                                    // 1 sources

                                    if (Factory.jarwsmbhjwbijwmk((int)(var31_3 = 950302067 ^ var31_3)) == 35685763) ** GOTO lbl102
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 35685763) ** GOTO lbl178
                                    var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 1475722723);
                                    ** GOTO lbl194
lbl102:
                                    // 1 sources

                                    var31_3 = 737284674 ^ var31_3;
                                    var26_27 = this;
                                    var27_28 = var26_27.movement;
                                    var27_28.moveTo((Locatable)var12_11);
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 20442256) {
                                        var31_3 = 1877619694 ^ var31_3;
                                        return;
                                    }
                                }
lbl109:
                                // 5 sources

                                default: {
                                    while (true) {
                                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 20442256) ** GOTO lbl109
                                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != (2103256002 ^ var31_3)) continue;
                                        AstralPlus.hogMZvQTRW = 125191365 ^ var31_3;
                                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 120946054 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (100511851 ^ var31_3)) break;
                                    }
                                    this.tmkyiPrhH8 = 447828906 ^ var31_3;
                                    throw new IllegalAccessException("double");
                                }
                            }
                            break;
                        }
lbl118:
                        // 1 sources

                        var31_3 = AstralPlus.mjbkytyqbyxonfdc(var31_3, 736639923);
                        ** GOTO lbl194
                        break;
                    }
lbl120:
                    // 1 sources

                    block33: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var31_3)) {
                            case 51704288: {
                                continue block33;
                            }
                            case 94080674: {
                                var31_3 = 404022716 ^ var31_3;
                            }
                            case 1447315597: {
                                if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 117738933) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 117738933) break block33;
                                } else {
                                    var31_3 = 1099114318 ^ var31_3;
                                    ** continue;
                                }
                            }
lbl132:
                            // 2 sources

                            default: {
                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 104658039) ** GOTO lbl132
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != (2091112403 ^ var31_3)) continue;
                                    this.tmkyiPrhH8 = 1425611327 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 50664660 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (665360993 ^ var31_3)) break;
                                }
                                AstralPlus.hogMZvQTRW = 1526880238 ^ var31_3;
                                throw new IOException("double");
                            }
                        }
                        break;
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 117738933 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1999326098 ^ var31_3)) {
                            continue;
                        }
                        this.tmkyiPrhH8 = 578289109 ^ var31_3;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 109444227 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (476175435 ^ var31_3)) break;
                    }
                    AstralPlus.hogMZvQTRW = 688565132 ^ var31_3;
                    throw new IllegalAccessException("double");
                    block37: while (true) {
                        cfr_temp_0 = 0;
                        while (true) {
                            switch (cfr_temp_0 == 0 ? Factory.jarwsmbhjwbijwmk((int)var31_3) : cfr_temp_0) {
                                case 126255254: {
                                    continue block37;
                                }
                                case 117738933: {
                                    var31_3 = 1787651203 ^ var31_3;
                                    break block37;
                                }
lbl158:
                                // 2 sources

                                default: {
                                    do {
                                        if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 108235939) ** GOTO lbl158
                                    } while (Factory.jarwsmbhjwbijwmk((int)var31_3) != (1738710595 ^ var31_3));
                                    this.tmkyiPrhH8 = 2023385881 ^ var31_3;
                                    throw new IllegalAccessException("double");
                                }
lbl164:
                                // 2 sources

                                while (Factory.jarwsmbhjwbijwmk((int)var31_3) != 66536566 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (968691728 ^ var31_3)) {
                                }
                                this.tmkyiPrhH8 = 130773127 ^ var31_3;
                                throw new RuntimeException("double");
lbl168:
                                // 2 sources

                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 95551705 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1048077540 ^ var31_3)) {
                                        continue;
                                    }
                                    AstralPlus.hogMZvQTRW = 80582708 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 2859610 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (851769411 ^ var31_3)) continue;
                                    AstralPlus.hogMZvQTRW = 1054884655 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 86575779 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (1739647503 ^ var31_3)) break;
                                }
                                this.tmkyiPrhH8 = 1477852738 ^ var31_3;
                                throw new RuntimeException("double");
lbl178:
                                // 4 sources

                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 111969081 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (144773809 ^ var31_3)) {
                                        continue;
                                    }
                                    this.tmkyiPrhH8 = 1714211070 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 14608202 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (872599313 ^ var31_3)) continue;
                                    this.tmkyiPrhH8 = 184050430 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 94080674 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1575751959 ^ var31_3)) continue;
                                    this.tmkyiPrhH8 = 1978846646 ^ var31_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 35685763 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (914943998 ^ var31_3)) break;
                                }
                                AstralPlus.hogMZvQTRW = 1192149505 ^ var31_3;
                                throw new IllegalAccessException("double");
                                case 1142943129: 
                            }
                            break;
                        }
                        break;
                    }
                    if (true) ** GOTO lbl194
                    block44: while (true) {
                        if (true) ** continue;
lbl194:
                        // 9 sources

                        while (true) {
                            cfr_temp_0 = 1142943129;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 259866841) ** continue;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != (1325709052 ^ var31_3)) continue;
                            AstralPlus.hogMZvQTRW = 122943747 ^ var31_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 179468708 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1458128671 ^ var31_3)) continue;
                            this.tmkyiPrhH8 = 490280078 ^ var31_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 195397945 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (2129819723 ^ var31_3)) continue;
                            this.tmkyiPrhH8 = 1918807104 ^ var31_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 182267317 || Factory.jarwsmbhjwbijwmk((int)var31_3) != (1858996514 ^ var31_3)) continue;
                            AstralPlus.hogMZvQTRW = 1202730057 ^ var31_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_3) == 178616359 && Factory.jarwsmbhjwbijwmk((int)var31_3) == (243244848 ^ var31_3)) break block44;
                        }
                        break;
                    }
                    this.tmkyiPrhH8 = 1845872390 ^ var31_3;
                    throw new RuntimeException();
                }
            }
            break;
        }
lbl209:
        // 2 sources

        do {
            if (Factory.jarwsmbhjwbijwmk((int)var31_3) != 125007330) ** GOTO lbl209
        } while (Factory.jarwsmbhjwbijwmk((int)var31_3) != (1546643949 ^ var31_3));
        this.tmkyiPrhH8 = 352175126 ^ var31_3;
        throw new IllegalAccessException("double");
    }

    /*
     * Unable to fully structure code
     */
    private int getBestItem(int var1_1) {
        block32: {
            block30: {
                block31: {
                    block29: {
                        block28: {
                            var31_2 = 2073375555 ^ (594967068 ^ (this.tmkyiPrhH8 ^ var1_1));
                            if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 117154362) ** GOTO lbl-1000
                            var31_2 = 125910820 ^ var31_2;
                            var2_3 = this;
                            var4_4 = var2_3.astralApi;
                            var5_5 = var4_4.getRewardsItems();
                            var6_6 = var5_5.stream();
                            var7_7 = var6_6.findFirst();
                            var3_8 = null;
                            var8_9 = var7_7.orElse(var3_8);
                            var9_10 = var8_9;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 35780341) ** GOTO lbl-1000
                            var31_2 = 719278692 ^ var31_2;
                            if (var9_10 == null) break block28;
                            if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 258695984) ** GOTO lbl-1000
                            var31_2 = 1822813756 ^ var31_2;
                            var10_11 = var9_10.getLootId();
                            var11_13 = var10_11.contains(var25_12 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.slezuvhykrococn(), var31_2));
                            if (var11_13 != (2089982737 ^ var31_2)) {
                                if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 22835426) {
                                    var31_2 = 248852256 ^ var31_2;
                                    var23_14 = this;
                                    var24_15 = var23_14.getBestWeaponItem(1123737865);
                                    return var24_15;
                                } else {
                                    ** GOTO lbl26
                                }
                            }
                            break block29;
lbl26:
                            // 2 sources

                            ** GOTO lbl-1000
                        }
                        block15: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var31_2)) {
                                case 295767451: {
                                    continue block15;
                                }
                                case 258695984: {
                                    var31_2 = 878844838 ^ var31_2;
                                }
                                case 1489037011: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 107324024) ** GOTO lbl39
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 107324024) ** GOTO lbl-1000
                                    var31_2 = 2124271439 ^ var31_2;
                                    ** GOTO lbl-1000
lbl39:
                                    // 1 sources

                                    var31_2 = 1944165589 ^ var31_2;
                                    ** GOTO lbl106
                                }
                                default: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 266658723) ** GOTO lbl138
                                }
                            }
                            break;
                        }
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)(var31_2 = AstralPlus.mjbkytyqbyxonfdc(var31_2, 946190870))) == 94276362) break block30;
                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 94276362) break block31;
                    var31_2 = AstralPlus.mjbkytyqbyxonfdc(var31_2, 507478211);
                    ** GOTO lbl-1000
                }
                while (Factory.jarwsmbhjwbijwmk((int)var31_2) != 94276362 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (1671508999 ^ var31_2)) {
                }
                AstralPlus.hogMZvQTRW = 217527185 ^ var31_2;
                throw new IOException("double");
            }
            var31_2 = AstralPlus.mjbkytyqbyxonfdc(var31_2, 1061462376);
            var12_16 = var9_10.getLootId();
            var13_18 = var12_16.contains(var26_17 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.vyttbcxwmxoajud(), var31_2));
            if (var13_18 == (2075361391 ^ var31_2)) break block32;
            if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 262910820) {
                var31_2 = 157910036 ^ var31_2;
                var21_19 = this;
                var22_20 = var21_19.getBestGeneratorItem(556026445);
                return var22_20;
            }
            ** GOTO lbl-1000
        }
        block17: while (true) {
            switch (Factory.jarwsmbhjwbijwmk((int)var31_2)) {
                case 2116319501: {
                    continue block17;
                }
                case 262910820: {
                    var31_2 = 281845495 ^ var31_2;
                }
                case 1781943844: {
                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 4595769) ** GOTO lbl75
                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 4595769) ** GOTO lbl138
                    ** GOTO lbl118
lbl75:
                    // 1 sources

                    block18: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var31_2)) {
                            case 1577914565: {
                                continue block18;
                            }
                            case 4595769: {
                                var31_2 = 1480778207 ^ var31_2;
                                break block18;
                            }
lbl82:
                            // 2 sources

                            default: lbl-1000:
                            // 3 sources

                            {
                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 35780341) ** GOTO lbl82
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != (347824430 ^ var31_2)) continue;
                                    AstralPlus.hogMZvQTRW = 1379271932 ^ var31_2;
                                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 107324024 && Factory.jarwsmbhjwbijwmk((int)var31_2) == (134743567 ^ var31_2)) break;
                                }
                                AstralPlus.hogMZvQTRW = 2142924690 ^ var31_2;
                                throw new IllegalAccessException("double");
                            }
                            case 1525493201: 
                        }
                        break;
                    }
                    var14_21 = var9_10.getLootId();
                    var27_22 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.qmewkctiywiazpl(), var31_2);
                    var15_23 = var14_21.contains(var27_22);
                    if (var15_23 == (859701063 ^ var31_2)) ** GOTO lbl102
                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 55061924) {
                        var31_2 = 75819633 ^ var31_2;
                        var16_24 = this;
                        var17_25 = var16_24.getBestModuleItem(1246807404);
                        return var17_25;
                    }
                    ** GOTO lbl138
lbl102:
                    // 1 sources

                    if (Factory.jarwsmbhjwbijwmk((int)(var31_2 = 2126282099 ^ var31_2)) == 103457221) ** GOTO lbl105
                    if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 103457221) ** GOTO lbl112
                    ** GOTO lbl116
lbl105:
                    // 1 sources

                    var31_2 = 439709802 ^ var31_2;
lbl106:
                    // 2 sources

                    var18_26 = this;
                    var19_27 = var18_26.rnd;
                    var28_28 = this;
                    var29_29 = var28_28.maxItemsBound;
                    var20_30 = var19_27.nextInt(var29_29);
                    return var20_30;
lbl112:
                    // 2 sources

                    while (Factory.jarwsmbhjwbijwmk((int)var31_2) != 103457221 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (1243853774 ^ var31_2)) {
                    }
                    this.tmkyiPrhH8 = 2071266939 ^ var31_2;
                    throw new IOException("double");
lbl116:
                    // 1 sources

                    var31_2 = AstralPlus.mjbkytyqbyxonfdc(var31_2, 390669296);
                    ** GOTO lbl119
lbl118:
                    // 1 sources

                    var31_2 = 834125660 ^ var31_2;
                }
lbl119:
                // 4 sources

                default: lbl-1000:
                // 7 sources

                {
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 258695984) ** GOTO lbl119
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != (1148242392 ^ var31_2)) continue;
                        this.tmkyiPrhH8 = 1708640608 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 166642210 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (7941031 ^ var31_2)) continue;
                        AstralPlus.hogMZvQTRW = 172936265 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 262910820 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (634378417 ^ var31_2)) continue;
                        this.tmkyiPrhH8 = 1506625194 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 200715937 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (1580022207 ^ var31_2)) continue;
                        AstralPlus.hogMZvQTRW = 1942513506 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 117154362 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (933148395 ^ var31_2)) continue;
                        AstralPlus.hogMZvQTRW = 65011717 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 22835426 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (182470296 ^ var31_2)) continue;
                        this.tmkyiPrhH8 = 1086718553 ^ var31_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 225091417 && Factory.jarwsmbhjwbijwmk((int)var31_2) == (1682735674 ^ var31_2)) break;
                    }
                    AstralPlus.hogMZvQTRW = 977703500 ^ var31_2;
                    throw new IOException();
                }
            }
            break;
        }
lbl138:
        // 7 sources

        if (Factory.jarwsmbhjwbijwmk((int)var31_2) == 266658723) ** GOTO lbl140
        ** GOTO lbl138
lbl140:
        // 2 sources

        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != (928585630 ^ var31_2)) ** GOTO lbl138
        AstralPlus.hogMZvQTRW = 1016551926 ^ var31_2;
        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 4595769 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (1351590135 ^ var31_2)) ** GOTO lbl138
        AstralPlus.hogMZvQTRW = 1960747363 ^ var31_2;
        if (Factory.jarwsmbhjwbijwmk((int)var31_2) != 55061924 || Factory.jarwsmbhjwbijwmk((int)var31_2) != (484059658 ^ var31_2)) ** GOTO lbl138
        this.tmkyiPrhH8 = 1440410387 ^ var31_2;
        throw new RuntimeException("double");
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     */
    private int getBestModuleItem(int var1_1) {
        block25: {
            block26: {
                var42_2 = 587128335 ^ (4981372 ^ (this.tmkyiPrhH8 ^ var1_1));
                if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 129271934) ** GOTO lbl118
                var42_2 = 530933429 ^ var42_2;
                var2_3 = this;
                var4_4 = var2_3.astralApi;
                var5_5 = var4_4.getRewardsItems();
                var6_6 = var5_5.stream();
                var3_7 = this;
                var30_8 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getBestModuleItem$4(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)((AstralPlus)var3_7);
                if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 45429034) break block25;
                var42_2 = 293891231 ^ var42_2;
                var7_9 = var6_6.filter(var30_8);
                var31_10 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestModuleItem$5(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 76849512) ** GOTO lbl118
                var42_2 = 1651643512 ^ var42_2;
                var32_11 = Comparator.comparing(var31_10);
                var8_12 = var7_9.max(var32_11);
                var33_13 = null;
                var9_14 = var8_12.orElse((AstralGateAPI.AstralItem)var33_13);
                var10_15 = var9_14;
                if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 236788588) ** GOTO lbl83
                var42_2 = 632658693 ^ var42_2;
                if (var10_15 == null) break block26;
                if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 18425695) {
                    var42_2 = 1008941934 ^ var42_2;
                    var26_16 = this;
                    var27_17 = var26_16.astralApi;
                    var28_18 = var27_17.getRewardsItems();
                    return var28_18.indexOf(var10_15);
                }
                ** GOTO lbl118
            }
            block10: while (true) {
                switch (Factory.jarwsmbhjwbijwmk((int)var42_2)) {
                    case 1693768559: {
                        continue block10;
                    }
                    case 18425695: {
                        var42_2 = 194629600 ^ var42_2;
                    }
                    case 638032134: {
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 125063732) ** GOTO lbl42
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 125063732) ** GOTO lbl95
                        ** GOTO lbl103
lbl42:
                        // 1 sources

                        var42_2 = AstralPlus.mjbkytyqbyxonfdc(var42_2, 584018124);
                        var11_20 = this;
                        var12_21 = var11_20.astralApi;
                        var13_22 = var12_21.getRewardsItems();
                        var14_23 = var13_22.stream();
                        var34_24 = this;
                        var35_25 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getBestModuleItem$6(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)((AstralPlus)var34_24);
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 85570079) break block10;
                        var42_2 = 1822020174 ^ var42_2;
                        var15_26 = var14_23.filter(var35_25);
                        var36_27 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestModuleItem$7(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 10811710) ** GOTO lbl83
                        var42_2 = 54981982 ^ var42_2;
                        var37_28 = Comparator.comparing(var36_27);
                        var16_29 = var15_26.max(var37_28);
                        var38_30 = null;
                        var17_31 = var16_29.orElse((AstralGateAPI.AstralItem)var38_30);
                        var18_32 = var17_31;
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 35948817) ** GOTO lbl83
                        var42_2 = 1380508053 ^ var42_2;
                        if (var18_32 != null) {
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 198155898) {
                                var42_2 = 1071757168 ^ var42_2;
                                var22_33 = this;
                                var23_34 = var22_33.astralApi;
                                var24_35 = var23_34.getRewardsItems();
                                return var24_35.indexOf(var18_32);
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)var42_2) != 198155898 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (264172255 ^ var42_2)) {
                            }
                            this.tmkyiPrhH8 = 594117233 ^ var42_2;
                            throw new RuntimeException("double");
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(var42_2 = 207215053 ^ var42_2)) == 44152597) {
                            var42_2 = 1464661806 ^ var42_2;
                            var19_37 = this;
                            var20_38 = var19_37.rnd;
                            var39_39 = this;
                            var40_40 = var39_39.maxItemsBound;
                            return var20_38.nextInt(var40_40);
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 44152597) ** GOTO lbl95
                        var42_2 = 1442008468 ^ var42_2;
                        ** GOTO lbl118
lbl83:
                        // 3 sources

                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 35948817 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (1679159528 ^ var42_2)) {
                                continue;
                            }
                            this.tmkyiPrhH8 = 661689721 ^ var42_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 10811710 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (1370720417 ^ var42_2)) continue;
                            this.tmkyiPrhH8 = 717147766 ^ var42_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 236788588 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (391754077 ^ var42_2)) continue;
                            this.tmkyiPrhH8 = 1284882130 ^ var42_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 251115847 && Factory.jarwsmbhjwbijwmk((int)var42_2) == (1769861922 ^ var42_2)) break;
                        }
                        AstralPlus.hogMZvQTRW = 689808254 ^ var42_2;
                        throw new RuntimeException("double");
lbl95:
                        // 2 sources

                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 125063732 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (1998129122 ^ var42_2)) {
                                continue;
                            }
                            AstralPlus.hogMZvQTRW = 1361098738 ^ var42_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 44152597 && Factory.jarwsmbhjwbijwmk((int)var42_2) == (68234637 ^ var42_2)) break;
                        }
                        AstralPlus.hogMZvQTRW = 1145988768 ^ var42_2;
                        throw new IOException("double");
lbl103:
                        // 1 sources

                        block14: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var42_2)) {
                                case 1831983034: {
                                    continue block14;
                                }
                                case 125063732: {
                                    var42_2 = 1190279696 ^ var42_2;
                                    ** break;
                                }
lbl110:
                                // 2 sources

                                default: {
                                    do {
                                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 137164012) ** GOTO lbl110
                                    } while (Factory.jarwsmbhjwbijwmk((int)var42_2) != (135147322 ^ var42_2));
                                    AstralPlus.hogMZvQTRW = 1038718246 ^ var42_2;
                                    throw new IOException("double");
                                }
                                case 105934273: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 194591146) ** GOTO lbl120
                                }
                            }
                            break;
                        }
lbl118:
                        // 11 sources

                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 194591146) ** GOTO lbl120
                        ** GOTO lbl118
lbl120:
                        // 2 sources

                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != (487979120 ^ var42_2)) ** GOTO lbl118
                        this.tmkyiPrhH8 = 172881707 ^ var42_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 18425695 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (126687337 ^ var42_2)) ** GOTO lbl118
                        this.tmkyiPrhH8 = 1783060539 ^ var42_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 76849512 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (688038005 ^ var42_2)) ** GOTO lbl118
                        AstralPlus.hogMZvQTRW = 423151872 ^ var42_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 129271934 || Factory.jarwsmbhjwbijwmk((int)var42_2) != (409331101 ^ var42_2)) ** GOTO lbl118
                        this.tmkyiPrhH8 = 419012531 ^ var42_2;
                        throw new RuntimeException();
                    }
                }
                break;
            }
        }
lbl130:
        // 2 sources

        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != 85570079) ** GOTO lbl130
            if (Factory.jarwsmbhjwbijwmk((int)var42_2) != (148516706 ^ var42_2)) continue;
            AstralPlus.hogMZvQTRW = 2070848979 ^ var42_2;
            if (Factory.jarwsmbhjwbijwmk((int)var42_2) == 45429034 && Factory.jarwsmbhjwbijwmk((int)var42_2) == (1121399450 ^ var42_2)) break;
        }
        AstralPlus.hogMZvQTRW = 705865643 ^ var42_2;
        throw new IOException("double");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private boolean hasThatModuleType(String var1_1, int var2_2) {
        block39: {
            block38: {
                block42: {
                    block41: {
                        block40: {
                            var37_3 = 337364869 ^ (1855681243 ^ (this.tmkyiPrhH8 ^ var2_2));
                            if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 105509) ** GOTO lbl138
                            var37_3 = 1486889206 ^ var37_3;
                            var3_4 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.kihwcssiczmhgvp(), var37_3);
                            var4_5 = var1_1.contains(var3_4);
                            if (var4_5 == (725469155 ^ var37_3)) break block40;
                            if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 63412346) {
                                var37_3 = 1355430839 ^ var37_3;
                                var5_6 = this;
                                var6_7 = var5_6.astralApi;
                                var7_8 = var6_7.getInventoryItems();
                                var8_9 = var7_8.stream();
                                var29_10 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$hasThatModuleType$8(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)();
                                var9_11 = var8_9.anyMatch(var29_10);
                                return var9_11;
                            }
                            break block38;
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(var37_3 = AstralPlus.mjbkytyqbyxonfdc(var37_3, 852383339))) == 38268932) break block41;
                        if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 38268932) break block42;
                        var37_3 = 1479242411 ^ var37_3;
                        ** GOTO lbl138
                    }
                    block15: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var37_3)) {
                            case 2037947479: {
                                continue block15;
                            }
                            case 38268932: {
                                var37_3 = 1019375481 ^ var37_3;
                            }
                            case 704000387: {
                                var30_12 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.okstuiadokgnutb(), var37_3);
                                var10_13 = var1_1.contains(var30_12);
                                if (var10_13 == (624033521 ^ var37_3)) ** GOTO lbl43
                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 135329987) {
                                    var37_3 = 979219685 ^ var37_3;
                                    var11_14 = this;
                                    var12_15 = var11_14.astralApi;
                                    var13_16 = var12_15.getInventoryItems();
                                    var14_17 = var13_16.stream();
                                    var31_18 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$hasThatModuleType$9(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)();
                                    var15_19 = var14_17.anyMatch(var31_18);
                                    return var15_19;
                                }
                                ** GOTO lbl138
lbl43:
                                // 1 sources

                                if (Factory.jarwsmbhjwbijwmk((int)(var37_3 = 1364526064 ^ var37_3)) == 25419349) ** GOTO lbl46
                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 25419349) break block38;
                                ** GOTO lbl116
lbl46:
                                // 1 sources

                                var37_3 = 1484085164 ^ var37_3;
                                var32_20 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.azeyqigbwnhufbp(), var37_3);
                                var16_21 = var1_1.contains(var32_20);
                                if (var16_21 != (739355309 ^ var37_3)) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 90314430) {
                                        var37_3 = 273735105 ^ var37_3;
                                        var24_22 = this;
                                        var25_23 = var24_22.astralApi;
                                        var26_24 = var25_23.getInventoryItems();
                                        var27_25 = var26_24.stream();
                                        var35_26 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$hasThatModuleType$10(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)();
                                        var28_27 = var27_25.anyMatch(var35_26);
                                        return var28_27;
                                    }
                                    break block39;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(var37_3 = AstralPlus.mjbkytyqbyxonfdc(var37_3, 1572124945))) == 204449689) ** GOTO lbl64
                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 204449689) ** GOTO lbl118
                                var37_3 = AstralPlus.mjbkytyqbyxonfdc(var37_3, 813559967);
                                ** GOTO lbl138
lbl64:
                                // 1 sources

                                var37_3 = 1955314513 ^ var37_3;
                                var33_28 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.wkcuxqbytdfjqdz(), var37_3);
                                var17_29 = var1_1.contains(var33_28);
                                if (var17_29 != (86943981 ^ var37_3)) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 81509232) {
                                        var37_3 = 1290003346 ^ var37_3;
                                        var18_30 = this;
                                        var19_31 = var18_30.astralApi;
                                        var20_32 = var19_31.getInventoryItems();
                                        var21_33 = var20_32.stream();
                                        var34_34 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$hasThatModuleType$11(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)();
                                        var22_35 = var21_33.anyMatch(var34_34);
                                        return var22_35;
                                    }
                                    while (Factory.jarwsmbhjwbijwmk((int)var37_3) != 81509232 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (1625250647 ^ var37_3)) {
                                    }
                                    this.tmkyiPrhH8 = 1293218401 ^ var37_3;
                                    throw new IOException("double");
                                }
                                block17: while (true) {
                                    switch (Factory.jarwsmbhjwbijwmk((int)var37_3)) {
                                        case 371474292: {
                                            continue block17;
                                        }
                                        case 81509232: {
                                            var37_3 = 1329472168 ^ var37_3;
                                            break block17;
                                        }
lbl88:
                                        // 2 sources

                                        default: {
                                            do {
                                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 79620896) ** GOTO lbl88
                                            } while (Factory.jarwsmbhjwbijwmk((int)var37_3) != (582295770 ^ var37_3));
                                            AstralPlus.hogMZvQTRW = 1403237898 ^ var37_3;
                                            throw new RuntimeException("double");
                                        }
                                        case 1924345353: 
                                    }
                                    break;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 260063733) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 260063733) break block39;
                                } else {
                                    var37_3 = AstralPlus.mjbkytyqbyxonfdc(var37_3, 726292900);
                                    var23_36 = (byte)(1633342433 ^ var37_3);
                                    return var23_36;
                                }
                                block20: while (true) {
                                    switch (Factory.jarwsmbhjwbijwmk((int)var37_3)) {
                                        case 546884081: {
                                            continue block20;
                                        }
                                        case 260063733: {
                                            var37_3 = 197683558 ^ var37_3;
                                            ** GOTO lbl138
                                        }
lbl108:
                                        // 2 sources

                                        default: {
                                            do {
                                                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 251497355) ** GOTO lbl108
                                            } while (Factory.jarwsmbhjwbijwmk((int)var37_3) != (1854165803 ^ var37_3));
                                            AstralPlus.hogMZvQTRW = 652141321 ^ var37_3;
                                            throw new IllegalAccessException("double");
                                        }
                                        case 846956080: {
                                            if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 105509) ** GOTO lbl138
                                        }
                                    }
                                    break;
                                }
lbl116:
                                // 1 sources

                                var37_3 = AstralPlus.mjbkytyqbyxonfdc(var37_3, 901520930);
                                ** GOTO lbl138
                            }
lbl118:
                            // 3 sources

                            default: {
                                do {
                                    if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 204449689) ** GOTO lbl118
                                } while (Factory.jarwsmbhjwbijwmk((int)var37_3) != (1685996305 ^ var37_3));
                                this.tmkyiPrhH8 = 24419531 ^ var37_3;
                                throw new RuntimeException("double");
                            }
                        }
                        break;
                    }
                }
                while (Factory.jarwsmbhjwbijwmk((int)var37_3) != 38268932 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (2106974844 ^ var37_3)) {
                }
                this.tmkyiPrhH8 = 1978342886 ^ var37_3;
                throw new IllegalAccessException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 25419349 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (411230519 ^ var37_3)) {
                    continue;
                }
                this.tmkyiPrhH8 = 884061438 ^ var37_3;
                if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 63412346 && Factory.jarwsmbhjwbijwmk((int)var37_3) == (511013761 ^ var37_3)) break;
            }
            this.tmkyiPrhH8 = 1175463585 ^ var37_3;
            throw new RuntimeException("double");
lbl138:
            // 10 sources

            if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 105509) ** GOTO lbl140
            ** GOTO lbl138
lbl140:
            // 2 sources

            if (Factory.jarwsmbhjwbijwmk((int)var37_3) != (1866940957 ^ var37_3)) ** GOTO lbl138
            AstralPlus.hogMZvQTRW = 438203386 ^ var37_3;
            if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 135329987 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (1658235852 ^ var37_3)) ** GOTO lbl138
            AstralPlus.hogMZvQTRW = 581740903 ^ var37_3;
            throw new RuntimeException();
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 260063733 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (198416194 ^ var37_3)) {
                continue;
            }
            this.tmkyiPrhH8 = 641784136 ^ var37_3;
            if (Factory.jarwsmbhjwbijwmk((int)var37_3) != 90314430 || Factory.jarwsmbhjwbijwmk((int)var37_3) != (1344575399 ^ var37_3)) continue;
            AstralPlus.hogMZvQTRW = 715570397 ^ var37_3;
            if (Factory.jarwsmbhjwbijwmk((int)var37_3) == 261388747 && Factory.jarwsmbhjwbijwmk((int)var37_3) == (580469683 ^ var37_3)) break;
        }
        this.tmkyiPrhH8 = 1760295308 ^ var37_3;
        throw new IllegalAccessException("double");
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    private boolean hasNegativeStat(List list, int n) {
        int n2 = 0x736C7F29 ^ (0x4C1409B ^ (this.tmkyiPrhH8 ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 32113208) {
            n2 = 0x5C94718E ^ n2;
            Stream stream = list.stream();
            Predicate<AstralGateAPI.ItemStat> predicate = var0 -> {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: First case is not immediately after switch.
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:442)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:94)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                 *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
                 *     at org.benf.cfr.reader.Main.main(Main.java:54)
                 */
                throw new IllegalStateException("Decompilation failed");
            };
            boolean bl = stream.anyMatch(predicate);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 32113208 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x6DA97EDE ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x6DFB7771 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 242935432 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x649055C1 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x332054E8 ^ n2;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     */
    private int getBestWeaponItem(int var1_1) {
        block34: {
            block33: {
                block35: {
                    block38: {
                        block37: {
                            block36: {
                                var49_2 = 1189066982 ^ (615882223 ^ (this.tmkyiPrhH8 ^ var1_1));
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 68719847) ** GOTO lbl164
                                var49_2 = 1493133170 ^ var49_2;
                                var2_3 = this;
                                var4_4 = var2_3.customItemPriorityConfig;
                                var5_5 = var4_4.active;
                                if (var5_5 == (759506359 ^ var49_2)) break block35;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 129356887) ** GOTO lbl102
                                var49_2 = 2096149595 ^ var49_2;
                                var6_6 = this;
                                var7_7 = var6_6.astralApi;
                                var8_8 = var7_7.getRewardsItems();
                                var9_9 = var8_8.stream();
                                var3_10 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, getUpgradeLevel(), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 240098060) ** GOTO lbl171
                                var49_2 = 737161522 ^ var49_2;
                                var33_11 = Comparator.comparing(var3_10);
                                var34_12 = var33_11.reversed();
                                var10_13 = var9_9.sorted(var34_12);
                                var35_14 = this;
                                var36_15 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestWeaponItem$13(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)((AstralPlus)var35_14);
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 216440299) ** GOTO lbl102
                                var49_2 = 1330602289 ^ var49_2;
                                var37_16 = Comparator.comparing(var36_15);
                                var11_17 = var10_13.min(var37_16);
                                var38_18 = null;
                                var12_19 = var11_17.orElse((AstralGateAPI.AstralItem)var38_18);
                                var13_20 = var12_19;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 113566389) ** GOTO lbl164
                                var49_2 = 578848506 ^ var49_2;
                                if (var13_20 == null) break block36;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 228326962) {
                                    var49_2 = 1754069685 ^ var49_2;
                                    var14_21 = this;
                                    var15_22 = var14_21.astralApi;
                                    var16_23 = var15_22.getRewardsItems();
                                    return var16_23.indexOf(var13_20);
                                }
                                ** GOTO lbl171
                            }
                            if (Factory.jarwsmbhjwbijwmk((int)(var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 1632938034))) == 105879941) break block37;
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 105879941) ** GOTO lbl127
                            break block38;
                        }
                        block15: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var49_2)) {
                                case 243366124: {
                                    continue block15;
                                }
                                case 105879941: {
                                    var49_2 = 1674378527 ^ var49_2;
                                }
                                case 754062385: {
                                    ** GOTO lbl70
                                }
                                default: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 68719847) ** GOTO lbl164
                                }
                            }
                            break;
                        }
                    }
                    var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 734380800);
                    ** GOTO lbl171
                }
                block16: while (true) {
                    switch (Factory.jarwsmbhjwbijwmk((int)var49_2)) {
                        case 40757287: {
                            continue block16;
                        }
                        case 129356887: {
                            var49_2 = 1883165923 ^ var49_2;
                        }
                        case 1448841741: {
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 86015721) ** GOTO lbl69
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 86015721) break block16;
                            var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 6348147);
                            ** GOTO lbl171
lbl69:
                            // 1 sources

                            var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 1214899052);
lbl70:
                            // 2 sources

                            var18_25 = this;
                            var19_26 = var18_25.astralApi;
                            var20_27 = var19_26.getRewardsItems();
                            var21_28 = var20_27.stream();
                            var39_29 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, getUpgradeLevel(), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 148051550) ** GOTO lbl102
                            var49_2 = 509156255 ^ var49_2;
                            var40_30 = Comparator.comparing(var39_29);
                            var41_31 = var40_30.reversed();
                            var22_32 = var21_28.sorted(var41_31);
                            var42_33 = this;
                            var43_34 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestWeaponItem$14(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)((AstralPlus)var42_33);
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 98658808) ** GOTO lbl127
                            var49_2 = 600992180 ^ var49_2;
                            var44_35 = Comparator.comparing(var43_34);
                            var23_36 = var22_32.min(var44_35);
                            var45_37 = null;
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 245957309) break block16;
                            var49_2 = 571954404 ^ var49_2;
                            var24_38 = var23_36.orElse((AstralGateAPI.AstralItem)var45_37);
                            var25_39 = var24_38;
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 74681360) ** GOTO lbl-1000
                            var49_2 = 1145734007 ^ var49_2;
                            if (var25_39 != null) {
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 144534860) {
                                    var49_2 = 1034312320 ^ var49_2;
                                    var26_40 = this;
                                    var27_41 = var26_40.astralApi;
                                    var28_42 = var27_41.getRewardsItems();
                                    return var28_42.indexOf(var25_39);
                                } else {
                                    ** GOTO lbl101
                                }
                            }
                            ** GOTO lbl114
lbl101:
                            // 2 sources

                            ** GOTO lbl171
lbl102:
                            // 3 sources

                            while (true) {
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 216440299 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (711451463 ^ var49_2)) {
                                    continue;
                                }
                                AstralPlus.hogMZvQTRW = 867268333 ^ var49_2;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 148051550 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1964703288 ^ var49_2)) continue;
                                this.tmkyiPrhH8 = 2117488670 ^ var49_2;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 129356887 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1297393326 ^ var49_2)) continue;
                                this.tmkyiPrhH8 = 2042443221 ^ var49_2;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 102592608 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (125121018 ^ var49_2)) break;
                            }
                            AstralPlus.hogMZvQTRW = 2611590 ^ var49_2;
                            throw new IllegalAccessException("double");
lbl114:
                            // 1 sources

                            if (Factory.jarwsmbhjwbijwmk((int)(var49_2 = 250051795 ^ var49_2)) == 205855187) {
                                var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 111589223);
                                var30_44 = this;
                                var31_45 = var30_44.rnd;
                                var46_46 = this;
                                var47_47 = var46_46.maxItemsBound;
                                return var31_45.nextInt(var47_47);
                            }
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 205855187) {
                                while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 205855187 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (239991385 ^ var49_2)) {
                                }
                                this.tmkyiPrhH8 = 1155073759 ^ var49_2;
                                throw new RuntimeException("double");
                            }
                            break block33;
                        }
lbl127:
                        // 4 sources

                        default: {
                            while (true) {
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 98658808) ** GOTO lbl127
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != (766510541 ^ var49_2)) continue;
                                this.tmkyiPrhH8 = 1270939937 ^ var49_2;
                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 105879941 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1983725024 ^ var49_2)) break;
                            }
                            this.tmkyiPrhH8 = 985394866 ^ var49_2;
                            throw new RuntimeException("double");
                        }
                    }
                    break;
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 245957309 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1381109412 ^ var49_2)) {
                        continue;
                    }
                    this.tmkyiPrhH8 = 1826358355 ^ var49_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 86015721 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (788003957 ^ var49_2)) continue;
                    this.tmkyiPrhH8 = 1310158926 ^ var49_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 73726096 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1746651861 ^ var49_2)) break;
                }
                AstralPlus.hogMZvQTRW = 1655970311 ^ var49_2;
                throw new IllegalAccessException("double");
            }
            block22: while (true) {
                switch (Factory.jarwsmbhjwbijwmk((int)var49_2)) {
                    case 1276998733: {
                        continue block22;
                    }
                    case 205855187: {
                        var49_2 = 490268532 ^ var49_2;
                        ** GOTO lbl171
                    }
                    case 1581405668: {
                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 228326962) {
                            break block34;
                        }
                        ** GOTO lbl171
                    }
lbl158:
                    // 2 sources

                    default: lbl-1000:
                    // 2 sources

                    {
                        do {
                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 74681360) ** GOTO lbl158
                        } while (Factory.jarwsmbhjwbijwmk((int)var49_2) != (671283362 ^ var49_2));
                        this.tmkyiPrhH8 = 543020353 ^ var49_2;
                        throw new RuntimeException("double");
                    }
                }
                break;
            }
lbl164:
            // 6 sources

            if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 68719847) ** GOTO lbl166
            ** GOTO lbl164
lbl166:
            // 2 sources

            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != (1056256622 ^ var49_2)) ** GOTO lbl164
            AstralPlus.hogMZvQTRW = 452970168 ^ var49_2;
            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 113566389 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (980983437 ^ var49_2)) ** GOTO lbl164
            this.tmkyiPrhH8 = 1856802249 ^ var49_2;
            throw new IllegalAccessException("double");
lbl171:
            // 12 sources

            ** if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 228326962) goto lbl173
lbl172:
            // 1 sources

            ** GOTO lbl171
        }
        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != (1375747723 ^ var49_2)) ** GOTO lbl171
        AstralPlus.hogMZvQTRW = 1408913621 ^ var49_2;
        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 240098060 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (134400344 ^ var49_2)) ** GOTO lbl171
        this.tmkyiPrhH8 = 1657502119 ^ var49_2;
        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 144893473 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1998001646 ^ var49_2)) ** GOTO lbl171
        AstralPlus.hogMZvQTRW = 693850374 ^ var49_2;
        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 144534860 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (2045305549 ^ var49_2)) ** GOTO lbl171
        this.tmkyiPrhH8 = 1622839521 ^ var49_2;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     */
    private int getBestGeneratorItem(int var1_1) {
        block38: {
            block45: {
                block43: {
                    block39: {
                        block41: {
                            block42: {
                                block46: {
                                    block44: {
                                        block40: {
                                            var49_2 = 1483930337 ^ (744508219 ^ (this.tmkyiPrhH8 ^ var1_1));
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 1750368) break block40;
                                            var49_2 = 412933762 ^ var49_2;
                                            var2_3 = this;
                                            var4_4 = var2_3.customItemPriorityConfig;
                                            var5_5 = var4_4.active;
                                            if (var5_5 == (411083728 ^ var49_2)) break block41;
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 259597795) break block42;
                                            var49_2 = 2124464659 ^ var49_2;
                                            var6_6 = this;
                                            var7_7 = var6_6.astralApi;
                                            var8_8 = var7_7.getRewardsItems();
                                            var9_9 = var8_8.stream();
                                            var3_10 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, getUpgradeLevel(), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 98491209) break block38;
                                            var49_2 = 834192748 ^ var49_2;
                                            var33_11 = Comparator.comparing(var3_10);
                                            var34_12 = var33_11.reversed();
                                            var10_13 = var9_9.sorted(var34_12);
                                            var35_14 = this;
                                            var36_15 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestGeneratorItem$15(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)((AstralPlus)var35_14);
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 163047563) break block43;
                                            var49_2 = 1746662329 ^ var49_2;
                                            var37_16 = Comparator.comparing(var36_15);
                                            var11_17 = var10_13.min(var37_16);
                                            var38_18 = null;
                                            var12_19 = var11_17.orElse((AstralGateAPI.AstralItem)var38_18);
                                            var13_20 = var12_19;
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 185485113) ** GOTO lbl-1000
                                            var49_2 = 237065738 ^ var49_2;
                                            if (var13_20 == null) break block44;
                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 2741124) {
                                                var49_2 = 667430509 ^ var49_2;
                                                var29_21 = this;
                                                var30_22 = var29_21.astralApi;
                                                var31_23 = var30_22.getRewardsItems();
                                                var32_24 = var31_23.indexOf(var13_20);
                                                return var32_24;
                                            }
                                            break block45;
                                        }
                                        while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 1750368 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (945763071 ^ var49_2)) {
                                        }
                                        AstralPlus.hogMZvQTRW = 1566671605 ^ var49_2;
                                        throw new IllegalAccessException("double");
                                    }
                                    if (Factory.jarwsmbhjwbijwmk((int)(var49_2 = 909595577 ^ var49_2)) == 119274223) break block46;
                                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 119274223) {
                                        while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 119274223 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (314559149 ^ var49_2)) {
                                        }
                                        AstralPlus.hogMZvQTRW = 801027500 ^ var49_2;
                                        throw new IOException("double");
                                    }
                                    var49_2 = 1671221321 ^ var49_2;
                                    break block39;
                                }
                                var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 1920952927);
                                ** GOTO lbl-1000
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 259597795 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (608463157 ^ var49_2)) {
                            }
                            this.tmkyiPrhH8 = 1572158752 ^ var49_2;
                            throw new IOException("double");
                        }
                        block13: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var49_2)) {
                                case 376290753: {
                                    continue block13;
                                }
                                case 259597795: {
                                    var49_2 = 2021148991 ^ var49_2;
                                }
                                case 386853407: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 199370711) {
                                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 199370711) break block38;
                                        var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 82929667);
                                        break block39;
                                    }
                                    block14: while (true) {
                                        switch (Factory.jarwsmbhjwbijwmk((int)var49_2)) {
                                            case 398176245: {
                                                continue block14;
                                            }
                                            case 199370711: {
                                                var49_2 = 353554965 ^ var49_2;
                                            }
                                            case 736999755: lbl-1000:
                                            // 2 sources

                                            {
                                                var14_25 = this;
                                                var15_26 = var14_25.astralApi;
                                                var16_27 = var15_26.getRewardsItems();
                                                var17_28 = var16_27.stream();
                                                var39_29 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, getUpgradeLevel(), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)();
                                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 74824590) ** GOTO lbl127
                                                var49_2 = 1564297795 ^ var49_2;
                                                var40_30 = Comparator.comparing(var39_29);
                                                var41_31 = var40_30.reversed();
                                                var18_32 = var17_28.sorted(var41_31);
                                                var42_33 = this;
                                                var43_34 = (Function<AstralGateAPI.AstralItem, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestGeneratorItem$16(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Ljava/lang/Integer;)((AstralPlus)var42_33);
                                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 253838993) {
                                                    var49_2 = 1448150049 ^ var49_2;
                                                    var44_35 = Comparator.comparing(var43_34);
                                                    var19_36 = var18_32.min(var44_35);
                                                    var45_37 = null;
                                                    ** if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 86309696) goto lbl-1000
lbl-1000:
                                                    // 1 sources

                                                    {
                                                        var49_2 = 791875181 ^ var49_2;
                                                        var20_38 = var19_36.orElse(var45_37);
                                                        var21_39 = (AstralGateAPI.AstralItem)var20_38;
                                                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 240110575) break block38;
                                                        var49_2 = 155810025 ^ var49_2;
                                                        if (var21_39 != null) {
                                                            if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 208148682) {
                                                                var49_2 = 1434387970 ^ var49_2;
                                                                var25_40 = this;
                                                                var26_41 = var25_40.astralApi;
                                                                var27_42 = var26_41.getRewardsItems();
                                                                var28_43 = var27_42.indexOf((Object)var21_39);
                                                                return var28_43;
                                                            } else {
                                                                ** GOTO lbl112
                                                            }
lbl112:
                                                            // 2 sources

                                                            break block39;
                                                        } else {
                                                            ** GOTO lbl113
                                                        }
lbl113:
                                                        // 2 sources

                                                        ** GOTO lbl131
                                                    }
                                                }
                                                ** GOTO lbl123
lbl-1000:
                                                // 2 sources

                                                {
                                                    while (true) {
                                                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 86309696 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (222160771 ^ var49_2)) {
                                                            continue;
                                                        }
                                                        AstralPlus.hogMZvQTRW = 83791968 ^ var49_2;
                                                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 185485113 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1453525405 ^ var49_2)) break;
                                                    }
                                                    AstralPlus.hogMZvQTRW = 966599339 ^ var49_2;
                                                    throw new IllegalAccessException("double");
lbl123:
                                                    // 2 sources

                                                    while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 253838993 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1251279887 ^ var49_2)) {
                                                    }
                                                    AstralPlus.hogMZvQTRW = 1925312672 ^ var49_2;
                                                    throw new IllegalAccessException("double");
lbl127:
                                                    // 2 sources

                                                    while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 74824590 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (667987573 ^ var49_2)) {
                                                    }
                                                    this.tmkyiPrhH8 = 608584648 ^ var49_2;
                                                    throw new IllegalAccessException("double");
                                                }
lbl131:
                                                // 1 sources

                                                if (Factory.jarwsmbhjwbijwmk((int)(var49_2 = 1528387135 ^ var49_2)) == 61276965) break block14;
                                                if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 61276965) break block13;
                                            }
lbl133:
                                            // 3 sources

                                            default: {
                                                do {
                                                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 61276965) ** GOTO lbl133
                                                } while (Factory.jarwsmbhjwbijwmk((int)var49_2) != (948283416 ^ var49_2));
                                                this.tmkyiPrhH8 = 1902439695 ^ var49_2;
                                                throw new IllegalAccessException("double");
                                            }
                                        }
                                        break;
                                    }
                                    var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 1188478472);
                                    var22_44 = this;
                                    var23_45 = var22_44.rnd;
                                    var46_46 = this;
                                    var47_47 = var46_46.maxItemsBound;
                                    var24_48 = var23_45.nextInt(var47_47);
                                    return var24_48;
                                }
lbl146:
                                // 2 sources

                                default: {
                                    do {
                                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 102028512) ** GOTO lbl146
                                    } while (Factory.jarwsmbhjwbijwmk((int)var49_2) != (779316344 ^ var49_2));
                                    this.tmkyiPrhH8 = 989040904 ^ var49_2;
                                    throw new RuntimeException("double");
                                }
                            }
                            break;
                        }
                        var49_2 = AstralPlus.mjbkytyqbyxonfdc(var49_2, 1743574223);
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 191737216 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (430337802 ^ var49_2)) {
                            continue;
                        }
                        AstralPlus.hogMZvQTRW = 647762994 ^ var49_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 208148682 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1850172981 ^ var49_2)) break;
                    }
                    AstralPlus.hogMZvQTRW = 476418907 ^ var49_2;
                    throw new IllegalAccessException();
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 163047563 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (469496037 ^ var49_2)) {
                        continue;
                    }
                    AstralPlus.hogMZvQTRW = 1861433807 ^ var49_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 30542240 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1498051428 ^ var49_2)) break;
                }
                this.tmkyiPrhH8 = 1633441374 ^ var49_2;
                throw new IOException("double");
            }
            while (Factory.jarwsmbhjwbijwmk((int)var49_2) != 2741124 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1470558501 ^ var49_2)) {
            }
            this.tmkyiPrhH8 = 1023915262 ^ var49_2;
            throw new IllegalAccessException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 98491209 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (1615980873 ^ var49_2)) {
                continue;
            }
            this.tmkyiPrhH8 = 1810455292 ^ var49_2;
            if (Factory.jarwsmbhjwbijwmk((int)var49_2) != 199370711 || Factory.jarwsmbhjwbijwmk((int)var49_2) != (641749638 ^ var49_2)) continue;
            this.tmkyiPrhH8 = 221075599 ^ var49_2;
            if (Factory.jarwsmbhjwbijwmk((int)var49_2) == 240110575 && Factory.jarwsmbhjwbijwmk((int)var49_2) == (1001392080 ^ var49_2)) break;
        }
        AstralPlus.hogMZvQTRW = 1470295213 ^ var49_2;
        throw new RuntimeException("double");
    }

    /*
     * WARNING - void declaration
     */
    private boolean waitToClick(int n) {
        int n2;
        block20: {
            block25: {
                block19: {
                    block22: {
                        block26: {
                            void var6_9;
                            block23: {
                                block24: {
                                    block21: {
                                        block18: {
                                            n2 = 0x7A0499BD ^ (0x6A0003FD ^ (this.tmkyiPrhH8 ^ n));
                                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 183050829) break block20;
                                            n2 = 0x2C6B50BE ^ n2;
                                            long l = System.currentTimeMillis();
                                            AstralPlus astralPlus = this;
                                            long l2 = astralPlus.nextClick;
                                            long l3 = l - l2;
                                            long l4 = l3 == 0L ? 0 : (l3 < 0L ? -1 : 1);
                                            if (l4 >= (0x5C97DD79 ^ n2)) break block21;
                                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 57041293) break block19;
                                            n2 = 0x5D954C64 ^ n2;
                                            byte bl = (byte)(0x102911C ^ n2);
                                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 15886601) break block22;
                                            n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 941334492);
                                            if (Factory.jarwsmbhjwbijwmk((int)n2) == 245413260) break block18;
                                            throw null;
                                        }
                                        try {
                                            throw new RuntimeException();
                                        }
                                        catch (RuntimeException runtimeException) {
                                            switch (Factory.efuojskrxnitvcvt((int)n2)) {
                                                default: {
                                                    throw new IllegalAccessException("Error in hash");
                                                }
                                                case -1208339920: {
                                                    n2 = 0x16BDD69D ^ n2;
                                                    break;
                                                }
                                                case -926386679: {
                                                    n2 = 0x395B9E1A ^ n2;
                                                }
                                            }
                                            if (Factory.jarwsmbhjwbijwmk((int)n2) != 4089743) break block19;
                                            n2 = 0x3C3BF739 ^ n2;
                                        }
                                        break block23;
                                    }
                                    if (Factory.jarwsmbhjwbijwmk((int)(n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 766719725))) == 188217674) break block24;
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 188217674) break block25;
                                    n2 = 0x1056681A ^ n2;
                                    break block19;
                                }
                                n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 940678434);
                                byte by = (byte)(0x493576B6 ^ n2);
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 232251189) break block26;
                                n2 = 0x754C1154 ^ n2;
                            }
                            return (boolean)var6_9;
                        }
                        while (Factory.jarwsmbhjwbijwmk((int)n2) != 232251189 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x658CD8EF ^ n2)) {
                        }
                        hogMZvQTRW = 0x4B9E86BA ^ n2;
                        throw new IOException("double");
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 15886601 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x4F5EB1DD ^ n2)) {
                            continue;
                        }
                        hogMZvQTRW = 0x293E7A27 ^ n2;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 210669272 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x534F1B89 ^ n2)) break;
                    }
                    hogMZvQTRW = 0x5F8BE556 ^ n2;
                    throw new IllegalAccessException("double");
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 86705468 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x7E01D4E1 ^ n2)) {
                        continue;
                    }
                    hogMZvQTRW = 0x72E61FFB ^ n2;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 57041293 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5E818114 ^ n2)) continue;
                    this.tmkyiPrhH8 = 0x240C4892 ^ n2;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 4089743 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x22179414 ^ n2)) break;
                }
                this.tmkyiPrhH8 = 0x49D4254 ^ n2;
                throw new RuntimeException();
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 188217674 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x6A329626 ^ n2)) {
                    continue;
                }
                hogMZvQTRW = 0x11FB43D7 ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) == 214810513 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x778C10D5 ^ n2)) break;
            }
            hogMZvQTRW = 0x79365D8F ^ n2;
            throw new RuntimeException("double");
        }
        while (Factory.jarwsmbhjwbijwmk((int)n2) != 183050829 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x2BEA5140 ^ n2)) {
        }
        this.tmkyiPrhH8 = 0x78CE05BC ^ n2;
        throw new IllegalAccessException("double");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void closeGui(int n) {
        int n2;
        block16: {
            block18: {
                block17: {
                    block13: {
                        block14: {
                            block15: {
                                State state;
                                n2 = 0x4FF2E930 ^ (0x6406E149 ^ (this.tmkyiPrhH8 ^ n));
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 212107629) break block13;
                                n2 = 0x6506F058 ^ n2;
                                AstralPlus astralPlus = this;
                                AstralMediator astralMediator = astralPlus.astralMediator;
                                int n3 = astralMediator.isAstralGuiVisible(1622970002);
                                if (n3 == (0x5D14043B ^ n2)) break block14;
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 72783142) break block15;
                                n2 = 0x1565BE12 ^ n2;
                                AstralPlus astralPlus2 = this;
                                astralPlus2.currentStatus = state = State.CLOSE_ITEM_SELECTION;
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 207360222) break block13;
                                n2 = 0x1079CB43 ^ n2;
                                AstralPlus astralPlus3 = this;
                                astralPlus3.updateNextClick(15892);
                                if (Factory.jarwsmbhjwbijwmk((int)n2) == 177210293) {
                                    n2 = 0x26AB2F72 ^ n2;
                                    AstralPlus astralPlus4 = this;
                                    AstralMediator astralMediator2 = astralPlus4.astralMediator;
                                    boolean bl = (byte)(0x7EA35E18 ^ n2);
                                    astralMediator2.setAstralGuiVisible(bl, 632231041);
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 89979117) {
                                        n2 = 0xD4E1B0A ^ n2;
                                        return;
                                    }
                                    while (Factory.jarwsmbhjwbijwmk((int)n2) != 89979117 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x45E68B25 ^ n2)) {
                                    }
                                    this.tmkyiPrhH8 = 0x15D5E3A1 ^ n2;
                                    throw new IllegalAccessException("double");
                                }
                                break block16;
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)n2) != 72783142 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x127853C6 ^ n2)) {
                            }
                            this.tmkyiPrhH8 = 0x477EE606 ^ n2;
                            throw new RuntimeException("double");
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(n2 = 0x2FE85C7 ^ n2)) == 165020722) break block17;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 165020722) break block18;
                        n2 = 0x70FEE3B5 ^ n2;
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 212107629 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x6894215B ^ n2)) {
                            continue;
                        }
                        hogMZvQTRW = 0x37840162 ^ n2;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 207360222 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x57BA1C16 ^ n2)) break;
                    }
                    this.tmkyiPrhH8 = 0x7F77EFE9 ^ n2;
                    throw new RuntimeException();
                }
                n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 738706670);
                return;
            }
            while (Factory.jarwsmbhjwbijwmk((int)n2) != 165020722 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x6A8FE0D0 ^ n2)) {
            }
            this.tmkyiPrhH8 = 0x2575AE11 ^ n2;
            throw new RuntimeException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 188174713 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x45714082 ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x114EE14A ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 177210293 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x1E37F747 ^ n2)) break;
        }
        hogMZvQTRW = 0x6C9A8D66 ^ n2;
        throw new RuntimeException("double");
    }

    public boolean hasOptions() {
        int n = 0x464B60FD ^ (0xDB05BB2 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 196493802) {
            n = 0x700E6A68 ^ n;
            AstralPlus astralPlus = this;
            boolean bl = astralPlus.hasOptionsToChoose(1326288501);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 114416794 || Factory.jarwsmbhjwbijwmk((int)n) != (0x50F231E6 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x7D009756 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 196493802 && Factory.jarwsmbhjwbijwmk((int)n) == (0x2F573409 ^ n)) break;
        }
        hogMZvQTRW = 0x44FE626A ^ n;
        throw new RuntimeException();
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private boolean hasOptionsToChoose(int var1_1) {
        block14: {
            block19: {
                block20: {
                    block16: {
                        block17: {
                            block18: {
                                block15: {
                                    var17_2 = 1665937192 ^ (278797638 ^ (this.tmkyiPrhH8 ^ var1_1));
                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 223038459) break block14;
                                    var17_2 = 1245326255 ^ var17_2;
                                    var2_3 = this;
                                    var4_4 = var2_3.astralApi;
                                    var5_5 = var4_4.getRewardsItems();
                                    var6_6 = var5_5.isEmpty();
                                    if (var6_6 != (600772977 ^ var17_2)) break block15;
                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 90247685) break block16;
                                    var17_2 = 1332085217 ^ var17_2;
                                    var7_7 = this;
                                    var8_8 = var7_7.astralApi;
                                    var9_9 = var8_8.allowedToEquip();
                                    if (var9_9 != (1823019152 ^ var17_2)) {
                                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 42466494) {
                                            var17_2 = 2053355116 ^ var17_2;
lbl17:
                                            // 2 sources

                                            while (true) {
                                                var15_10 = (byte)(382379772 ^ var17_2);
                                                return var15_10;
                                            }
                                        } else {
                                            ** GOTO lbl21
                                        }
                                    }
                                    break block17;
lbl21:
                                    // 2 sources

                                    break block16;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(var17_2 = 412403912 ^ var17_2)) == 50450061) break block18;
                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 50450061) {
                                    var17_2 = AstralPlus.mjbkytyqbyxonfdc(var17_2, 1177402850);
                                }
                                break block16;
                            }
                            var17_2 = 764510021 ^ var17_2;
                            ** while (true)
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(var17_2 = 2078839965 ^ var17_2)) == 219084952) break block19;
                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 219084952) break block20;
                        var17_2 = AstralPlus.mjbkytyqbyxonfdc(var17_2, 1782046806);
                    }
                    while (true) {
                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 42466494 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (473610092 ^ var17_2)) {
                            continue;
                        }
                        AstralPlus.hogMZvQTRW = 1877123976 ^ var17_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 90247685 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (2085750754 ^ var17_2)) continue;
                        this.tmkyiPrhH8 = 1054963228 ^ var17_2;
                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 50450061 && Factory.jarwsmbhjwbijwmk((int)var17_2) == (726467010 ^ var17_2)) break;
                    }
                    this.tmkyiPrhH8 = 371747976 ^ var17_2;
                    throw new IllegalAccessException();
                }
                while (Factory.jarwsmbhjwbijwmk((int)var17_2) != 219084952 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1743054568 ^ var17_2)) {
                }
                AstralPlus.hogMZvQTRW = 1785192941 ^ var17_2;
                throw new RuntimeException("double");
            }
            var17_2 = 109392247 ^ var17_2;
            var10_11 = this;
            var11_12 = var10_11.astralApi;
            var12_13 = var11_12.getRewardsItems();
            var13_14 = var12_13.stream();
            var3_15 = (Predicate<AstralGateAPI.AstralItem>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$hasOptionsToChoose$17(eu.darkbot.api.managers.AstralGateAPI$AstralItem ), (Leu/darkbot/api/managers/AstralGateAPI$AstralItem;)Z)();
            var14_16 = var13_14.noneMatch(var3_15);
            return var14_16;
        }
        while (Factory.jarwsmbhjwbijwmk((int)var17_2) != 223038459 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (2073479765 ^ var17_2)) {
        }
        AstralPlus.hogMZvQTRW = 1348795862 ^ var17_2;
        throw new RuntimeException("double");
    }

    private int getGenerators(int n) {
        int n2;
        block12: {
            n2 = 0x7E42B2C8 ^ (0x6A95B18B ^ (this.tmkyiPrhH8 ^ n));
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 228861518) {
                block13: {
                    n2 = 0x476741EA ^ n2;
                    try {
                        List list;
                        AstralPlus astralPlus = this;
                        AstralGateAPI astralGateAPI = astralPlus.astralApi;
                        List list2 = astralGateAPI.getInventoryItems();
                        Stream stream = list2.stream();
                        Predicate<AstralGateAPI.AstralItem> predicate = astralItem -> {
                            int n = 0x56854840 ^ (0x47700247 ^ hogMZvQTRW);
                            if (Factory.jarwsmbhjwbijwmk((int)n) == 177641973) {
                                n = 0x553D4197 ^ n;
                                String string = astralItem.getLootId();
                                String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.rmflfoynstpruwg(), n);
                                boolean bl = string.contains(string2);
                                return bl;
                            }
                            while (true) {
                                if (Factory.jarwsmbhjwbijwmk((int)n) != 240706531 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2158F31A ^ n)) {
                                    continue;
                                }
                                hogMZvQTRW = 0x3B7A678 ^ n;
                                if (Factory.jarwsmbhjwbijwmk((int)n) == 177641973 && Factory.jarwsmbhjwbijwmk((int)n) == (0x7FF09DA3 ^ n)) break;
                            }
                            hogMZvQTRW = 0x34B31CEC ^ n;
                            throw new IOException();
                        };
                        Stream<AstralGateAPI.AstralItem> stream2 = stream.filter(predicate);
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 185676303) break block12;
                        n2 = 0x7BB263E0 ^ n2;
                        Collector collector = Collectors.toList();
                        List list3 = list = stream2.collect(collector);
                        int n3 = list3.size();
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 70771031) break block13;
                        n2 = 0x14D84DDC ^ n2;
                        return n3;
                    }
                    catch (Exception exception) {
                        switch (Factory.efuojskrxnitvcvt((int)n2)) {
                            default: {
                                throw new RuntimeException("Error in hash");
                            }
                            case -1311561176: {
                                n2 = 0x42CF97B2 ^ n2;
                                break;
                            }
                            case 1816147243: {
                                n2 = 0x397DF452 ^ n2;
                            }
                        }
                        Exception exception2 = exception;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 77386315) {
                            n2 = 0xC500F1A ^ n2;
                            byte by = (byte)(0x58A5FD89 ^ n2);
                            return by;
                        }
                        break block12;
                    }
                }
                while (Factory.jarwsmbhjwbijwmk((int)n2) != 70771031 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x1C1209B0 ^ n2)) {
                }
                hogMZvQTRW = 0x309FA61C ^ n2;
                throw new IOException("double");
            }
            while (Factory.jarwsmbhjwbijwmk((int)n2) != 228861518 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x2B70BE8D ^ n2)) {
            }
            hogMZvQTRW = 0x28CEBBE5 ^ n2;
            throw new IllegalAccessException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 185676303 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x2E8E8A63 ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x606B79D1 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 77386315 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5483DC81 ^ n2)) continue;
            hogMZvQTRW = 0x4FEBFF0C ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 185330538 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x44879AE5 ^ n2)) continue;
            hogMZvQTRW = 0xB66AB10 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 197247838 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x3EDCD05C ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x373089FF ^ n2;
        throw new IOException();
    }

    /*
     * Exception decompiling
     */
    private int getModules(int var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[CATCHBLOCK]], but top level block is 3[CASE]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private int getWeapons(int n) {
        int n2;
        block13: {
            n2 = 0x1785653E ^ (0x1D97DF14 ^ (this.tmkyiPrhH8 ^ n));
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 248748015) {
                block12: {
                    n2 = 0x7ADCF9FF ^ n2;
                    try {
                        List list;
                        AstralPlus astralPlus = this;
                        AstralGateAPI astralGateAPI = astralPlus.astralApi;
                        List list2 = astralGateAPI.getInventoryItems();
                        Stream stream = list2.stream();
                        Predicate<AstralGateAPI.AstralItem> predicate = astralItem -> {
                            int n = 0x7129FAF7 ^ (0x1668FA0E ^ hogMZvQTRW);
                            if (Factory.jarwsmbhjwbijwmk((int)n) == 257114498) {
                                n = 0x3F86F764 ^ n;
                                String string = astralItem.getLootId();
                                String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.miwmkirbmeqzxip(), n);
                                boolean bl = string.contains(string2);
                                return bl;
                            }
                            while (true) {
                                if (Factory.jarwsmbhjwbijwmk((int)n) != 75562498 || Factory.jarwsmbhjwbijwmk((int)n) != (0x64203283 ^ n)) {
                                    continue;
                                }
                                hogMZvQTRW = 0x64E0FB3F ^ n;
                                if (Factory.jarwsmbhjwbijwmk((int)n) == 257114498 && Factory.jarwsmbhjwbijwmk((int)n) == (0x13EF366E ^ n)) break;
                            }
                            hogMZvQTRW = 0xCAC9989 ^ n;
                            throw new IOException();
                        };
                        Stream<AstralGateAPI.AstralItem> stream2 = stream.filter(predicate);
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 264265390) break block12;
                        n2 = 0x700319B4 ^ n2;
                        Collector collector = Collectors.toList();
                        List list3 = list = stream2.collect(collector);
                        int n3 = list3.size();
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 45763242) {
                            n2 = 0x40EB7F6F ^ n2;
                            return n3;
                        }
                        break block13;
                    }
                    catch (Exception exception) {
                        switch (Factory.efuojskrxnitvcvt((int)n2)) {
                            case -1818420134: {
                                n2 = 0x289AB493 ^ n2;
                                break;
                            }
                            default: {
                                throw new IOException("Error in hash");
                            }
                            case 327537145: {
                                n2 = 0x5899AD27 ^ n2;
                            }
                        }
                        Exception exception2 = exception;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 237233751) break block12;
                        n2 = 0x72820119 ^ n2;
                        byte by = (byte)(0x86B1665 ^ n2);
                        return by;
                    }
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 264265390 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5EA48FB2 ^ n2)) {
                        continue;
                    }
                    hogMZvQTRW = 0x4858AE11 ^ n2;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 252554448 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x60D06312 ^ n2)) continue;
                    this.tmkyiPrhH8 = 0x2868A165 ^ n2;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 237233751 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x666F55F7 ^ n2)) break;
                }
                this.tmkyiPrhH8 = 0x135748C5 ^ n2;
                throw new IllegalAccessException();
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 45763242 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x551529F3 ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x3ED934A9 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 248748015 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x1223B505 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x65B4EC79 ^ n2;
        throw new IOException("double");
    }

    /*
     * Exception decompiling
     */
    private int getMaxGenerators(int var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: First case is not immediately after switch.
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:442)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:94)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Unable to fully structure code
     */
    private int getMaxWeapons(int var1_1) {
        block39: {
            block41: {
                block45: {
                    block36: {
                        block37: {
                            block44: {
                                block40: {
                                    block43: {
                                        block42: {
                                            block38: {
                                                var17_2 = 740104099 ^ (1935134464 ^ (this.tmkyiPrhH8 ^ var1_1));
                                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 44192829) ** GOTO lbl130
                                                var17_2 = 1706973630 ^ var17_2;
                                                var2_3 = this;
                                                var4_4 = var2_3.heroapi;
                                                var5_5 = var4_4.getShipType();
                                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 154697820) ** GOTO lbl89
                                                var17_2 = 848510161 ^ var17_2;
                                                var3_6 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.pwmucdwevmjbdro(), var17_2);
                                                var6_7 = var5_5.equals(var3_6);
                                                if (var6_7 != (81653063 ^ var17_2)) break block38;
                                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 76548783) break block39;
                                                var17_2 = 1729147367 ^ var17_2;
                                                var13_8 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.muqnivhdkbiwktw(), var17_2);
                                                var7_9 = var5_5.equals(var13_8);
                                                if (var7_9 == (1674402976 ^ var17_2)) break block40;
                                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 22905019) {
                                                    var17_2 = 1433265703 ^ var17_2;
lbl19:
                                                    // 3 sources

                                                    while (true) {
                                                        var12_10 = (byte)(916503176 ^ var17_2);
                                                        return var12_10;
                                                    }
                                                }
                                                break block41;
                                            }
                                            if (Factory.jarwsmbhjwbijwmk((int)(var17_2 = 998262260 ^ var17_2)) == 180701575) break block42;
                                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 180701575) ** GOTO lbl130
                                            break block43;
                                        }
                                        block21: while (true) {
                                            switch (Factory.jarwsmbhjwbijwmk((int)var17_2)) {
                                                case 514363832: {
                                                    continue block21;
                                                }
                                                case 691583127: {
                                                    ** GOTO lbl19
                                                }
                                                case 180701575: {
                                                    var17_2 = 167580212 ^ var17_2;
                                                    ** continue;
                                                }
                                                default: {
                                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 49173377) ** GOTO lbl89
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    var17_2 = 1773833329 ^ var17_2;
                                    ** GOTO lbl130
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(var17_2 = 532159487 ^ var17_2)) == 19088957) break block44;
                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 19088957) ** GOTO lbl-1000
                                break block45;
                            }
                            block22: while (true) {
                                switch (Factory.jarwsmbhjwbijwmk((int)var17_2)) {
                                    case 60442079: {
                                        continue block22;
                                    }
                                    case 19088957: {
                                        var17_2 = 1394349551 ^ var17_2;
                                    }
                                    case 2087580364: {
                                        var14_11 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.okenuahnosfybbt(), var17_2);
                                        var8_12 = var5_5.equals(var14_11);
                                        if (var8_12 != (795431600 ^ var17_2)) ** GOTO lbl68
                                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 198983516) ** GOTO lbl130
                                        var17_2 = 890652968 ^ var17_2;
                                        var15_13 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.jnopxpbhyjnblct(), var17_2);
                                        var9_14 = var5_5.equals(var15_13);
                                        if (var9_14 == (444537752 ^ var17_2)) break block36;
                                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 55992073) {
                                            var17_2 = 1508687531 ^ var17_2;
lbl64:
                                            // 3 sources

                                            while (true) {
                                                var11_15 = (byte)(1133749567 ^ var17_2);
                                                return var11_15;
                                            }
                                        }
                                        ** GOTO lbl83
lbl68:
                                        // 1 sources

                                        if (Factory.jarwsmbhjwbijwmk((int)(var17_2 = 1886675786 ^ var17_2)) == 138989513) ** GOTO lbl72
                                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 138989513) {
                                            break block37;
                                        }
                                        ** GOTO lbl-1000
lbl72:
                                        // 1 sources

                                        block24: while (true) {
                                            switch (Factory.jarwsmbhjwbijwmk((int)var17_2)) {
                                                case 1623334260: {
                                                    continue block24;
                                                }
                                                case 1359606132: {
                                                    ** GOTO lbl64
                                                }
                                                case 138989513: {
                                                    var17_2 = 479106249 ^ var17_2;
                                                    ** continue;
                                                }
                                                default: {
                                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 114420979) ** GOTO lbl114
                                                }
                                            }
                                            break;
                                        }
lbl83:
                                        // 2 sources

                                        while (Factory.jarwsmbhjwbijwmk((int)var17_2) != 55992073 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1520616752 ^ var17_2)) {
                                        }
                                        AstralPlus.hogMZvQTRW = 2065186114 ^ var17_2;
                                        throw new RuntimeException("double");
                                    }
                                    default: {
                                        if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 44192829) ** GOTO lbl130
                                    }
                                }
                                break;
                            }
lbl89:
                            // 5 sources

                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 49173377) ** GOTO lbl91
                            ** GOTO lbl89
lbl91:
                            // 2 sources

                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) != (1395040083 ^ var17_2)) ** GOTO lbl89
                            this.tmkyiPrhH8 = 598310096 ^ var17_2;
                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 154697820 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1456711982 ^ var17_2)) ** GOTO lbl89
                            AstralPlus.hogMZvQTRW = 2026903776 ^ var17_2;
                            throw new IllegalAccessException("double");
                        }
                        var17_2 = 167384376 ^ var17_2;
                        ** GOTO lbl130
                    }
                    block26: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var17_2)) {
                            case 2051187900: {
                                continue block26;
                            }
                            case 55992073: {
                                var17_2 = 238253693 ^ var17_2;
                            }
                            case 665309712: {
                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 122945703) ** GOTO lbl111
                                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 122945703) {
                                    var17_2 = 1118521639 ^ var17_2;
                                }
                                ** GOTO lbl130
lbl111:
                                // 1 sources

                                var17_2 = 1798276340 ^ var17_2;
                                var10_16 = (byte)(2137254161 ^ var17_2);
                                return var10_16;
                            }
lbl114:
                            // 3 sources

                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 114420979) ** GOTO lbl116
                            ** GOTO lbl114
lbl116:
                            // 2 sources

                            if (Factory.jarwsmbhjwbijwmk((int)var17_2) != (488034699 ^ var17_2)) ** GOTO lbl114
                            this.tmkyiPrhH8 = 624714813 ^ var17_2;
                            throw new IOException("double");
lbl119:
                            // 2 sources

                            default: lbl-1000:
                            // 3 sources

                            {
                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 19088957) ** GOTO lbl119
                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) != (1567876198 ^ var17_2)) continue;
                                    AstralPlus.hogMZvQTRW = 913854003 ^ var17_2;
                                    if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 138989513 && Factory.jarwsmbhjwbijwmk((int)var17_2) == (1100151643 ^ var17_2)) break;
                                }
                                AstralPlus.hogMZvQTRW = 853060611 ^ var17_2;
                                throw new IllegalAccessException("double");
                            }
                        }
                        break;
                    }
                }
                var17_2 = 714236829 ^ var17_2;
lbl130:
                // 13 sources

                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 44192829) ** GOTO lbl132
                ** GOTO lbl130
lbl132:
                // 2 sources

                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != (2085556059 ^ var17_2)) ** GOTO lbl130
                AstralPlus.hogMZvQTRW = 457758126 ^ var17_2;
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 198983516 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1846596459 ^ var17_2)) ** GOTO lbl130
                AstralPlus.hogMZvQTRW = 1819478383 ^ var17_2;
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 122945703 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1700075286 ^ var17_2)) ** GOTO lbl130
                AstralPlus.hogMZvQTRW = 627800705 ^ var17_2;
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 180701575 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (2131445366 ^ var17_2)) ** GOTO lbl130
                AstralPlus.hogMZvQTRW = 2048436675 ^ var17_2;
                throw new IOException();
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 140476880 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1833339619 ^ var17_2)) {
                    continue;
                }
                AstralPlus.hogMZvQTRW = 1014211707 ^ var17_2;
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) != 165109701 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (71016462 ^ var17_2)) continue;
                this.tmkyiPrhH8 = 1767953298 ^ var17_2;
                if (Factory.jarwsmbhjwbijwmk((int)var17_2) == 22905019 && Factory.jarwsmbhjwbijwmk((int)var17_2) == (1815427766 ^ var17_2)) break;
            }
            this.tmkyiPrhH8 = 1418658659 ^ var17_2;
            throw new RuntimeException("double");
        }
        while (Factory.jarwsmbhjwbijwmk((int)var17_2) != 76548783 || Factory.jarwsmbhjwbijwmk((int)var17_2) != (1366614043 ^ var17_2)) {
        }
        AstralPlus.hogMZvQTRW = 555279926 ^ var17_2;
        throw new IOException("double");
    }

    /*
     * Exception decompiling
     */
    private boolean shouldEnter(PortalType var1_1, int var2_2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: First case is not immediately after switch.
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:442)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:94)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    private boolean ignoreLimit(PortalType portalType, Map map, int n) {
        int n2;
        block10: {
            block7: {
                block8: {
                    block12: {
                        block13: {
                            block11: {
                                block9: {
                                    n2 = 0x5A0C3ABE ^ (0x61CB15EB ^ (this.tmkyiPrhH8 ^ n));
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 204250337) break block7;
                                    n2 = 0x3C386DE2 ^ n2;
                                    String string = portalType.name();
                                    Object v = map.get(string);
                                    PortalInfo portalInfo = (PortalInfo)v;
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 106852447) break block8;
                                    n2 = 0x53F31A4B ^ n2;
                                    if (portalInfo == null) break block9;
                                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 183182514) {
                                        n2 = 0xF5F3F7F ^ n2;
                                        boolean bl = portalInfo.ignoreLimit;
                                        return bl;
                                    }
                                    break block10;
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)(n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 346674018))) == 81155744) break block11;
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 81155744) break block12;
                                break block13;
                            }
                            n2 = 0x57BAA4B9 ^ n2;
                            boolean bl = (byte)(0x5D7B9349 ^ n2);
                            return bl;
                        }
                        n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 583180900);
                        break block7;
                    }
                    while (Factory.jarwsmbhjwbijwmk((int)n2) != 81155744 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x57742D77 ^ n2)) {
                    }
                    this.tmkyiPrhH8 = 0x20BDD6D3 ^ n2;
                    throw new IllegalAccessException("double");
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 22817580 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x14F97EBF ^ n2)) {
                        continue;
                    }
                    hogMZvQTRW = 0x4497DA44 ^ n2;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 106852447 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x76BCA548 ^ n2)) break;
                }
                hogMZvQTRW = 0x34D553C0 ^ n2;
                throw new RuntimeException("double");
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 85930022 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x7EB63A49 ^ n2)) {
                    continue;
                }
                hogMZvQTRW = 0x5A7E4CB ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) == 204250337 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x7D8D4198 ^ n2)) break;
            }
            this.tmkyiPrhH8 = 0x338FBD1E ^ n2;
            throw new IllegalAccessException();
        }
        while (Factory.jarwsmbhjwbijwmk((int)n2) != 183182514 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x32ACE7B4 ^ n2)) {
        }
        this.tmkyiPrhH8 = 0x3C5BCDE ^ n2;
        throw new IllegalAccessException("double");
    }

    private int getItemPriority(String string, int n) {
        int n2;
        block10: {
            block7: {
                block12: {
                    block11: {
                        block9: {
                            block8: {
                                ItemInfo itemInfo;
                                n2 = 0x676D4D1C ^ (0x39D29A90 ^ (this.tmkyiPrhH8 ^ n));
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 1800200) break block7;
                                n2 = 0xAB20853 ^ n2;
                                AstralPlus astralPlus = this;
                                CustomItemPriority customItemPriority = astralPlus.customItemPriorityConfig;
                                Map<String, ItemInfo> map = customItemPriority.itemInfos;
                                ItemInfo itemInfo2 = itemInfo = map.get(string);
                                if (Factory.jarwsmbhjwbijwmk((int)n2) != 61211330) break block8;
                                n2 = 0x5EDCED04 ^ n2;
                                if (itemInfo2 == null) break block9;
                                if (Factory.jarwsmbhjwbijwmk((int)n2) == 76150577) {
                                    n2 = 0x23B73318 ^ n2;
                                    int n3 = itemInfo2.priority;
                                    return n3;
                                }
                                break block10;
                            }
                            while (Factory.jarwsmbhjwbijwmk((int)n2) != 61211330 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x707A4D54 ^ n2)) {
                            }
                            this.tmkyiPrhH8 = 0x9F90BF3 ^ n2;
                            throw new RuntimeException("double");
                        }
                        if (Factory.jarwsmbhjwbijwmk((int)(n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 724840998))) == 144996635) break block11;
                        if (Factory.jarwsmbhjwbijwmk((int)n2) != 144996635) break block7;
                        break block12;
                    }
                    n2 = 0x21E8A89A ^ n2;
                    AstralPlus astralPlus = this;
                    byte by = (byte)(0x676EFAF8 ^ n2);
                    astralPlus.addItemToTheTable(string, by, 391033119);
                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 140992814) {
                        n2 = 0x4F6F1A57 ^ n2;
                        byte by2 = (byte)(0x2801E0CB ^ n2);
                        return by2;
                    }
                    break block7;
                }
                n2 = 0x469D065B ^ n2;
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 140992814 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x55532A98 ^ n2)) {
                    continue;
                }
                this.tmkyiPrhH8 = 0x2767D8B0 ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 120201095 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x3D542222 ^ n2)) continue;
                hogMZvQTRW = 0x6DF8A8D ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 1800200 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5F32BE37 ^ n2)) continue;
                hogMZvQTRW = 0x4993BA2E ^ n2;
                if (Factory.jarwsmbhjwbijwmk((int)n2) == 144996635 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x1456A163 ^ n2)) break;
            }
            this.tmkyiPrhH8 = 0x451D290 ^ n2;
            throw new IllegalAccessException();
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 226724690 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x541D020 ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x1D2E866D ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 76150577 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x32168A66 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x6E388A9D ^ n2;
        throw new IOException("double");
    }

    private int getWeaponPriority(String string, int n) {
        int n2 = 0x24884E45 ^ (0x3B42A5F6 ^ (this.tmkyiPrhH8 ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 41985707) {
            n2 = 0x446DE494 ^ n2;
            AstralPlus astralPlus = this;
            AstralPlus astralPlus2 = this;
            List<String> list = astralPlus2.weaponPriority;
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.suybvnygxhhhavc(), n2);
            int n3 = astralPlus.getItemPriorityFromList(list, string, string2, 332376817);
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 101345703 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x2C996A9E ^ n2)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x533E2F76 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 41985707 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x1C5EB662 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x2CC1D80C ^ n2;
        throw new IllegalAccessException();
    }

    private int getGeneratorPriority(String string, int n) {
        int n2 = 0xFEE7E5B ^ (0x365BF9E8 ^ (this.tmkyiPrhH8 ^ n));
        if (Factory.jarwsmbhjwbijwmk((int)n2) == 139469940) {
            n2 = 0x48BC3FC1 ^ n2;
            AstralPlus astralPlus = this;
            AstralPlus astralPlus2 = this;
            List<String> list = astralPlus2.generatorPriority;
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.pxagyrukkyrdkdq(), n2);
            int n3 = astralPlus.getItemPriorityFromList(list, string, string2, 332376817);
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 230756174 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x128D554A ^ n2)) {
                continue;
            }
            hogMZvQTRW = 0x13805CDB ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 139469940 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x43B90143 ^ n2)) break;
        }
        this.tmkyiPrhH8 = 0x6D7E0296 ^ n2;
        throw new IOException();
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    private int getItemPriorityFromList(List list, String string, String string2, int n) {
        int n2;
        block9: {
            block11: {
                block10: {
                    n2 = 0x403ACC8E ^ (0x62A4ACFF ^ (this.tmkyiPrhH8 ^ n));
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 44992446) break block9;
                    n2 = 0x4746FA06 ^ n2;
                    String string3 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.rpwkkhuxshhqiec(), n2);
                    String string4 = string.replace(string2, string3);
                    String string5 = string4.trim();
                    int n3 = list.indexOf(string5);
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 71807516) break block10;
                    byte by = (byte)(0xF29C6F69 ^ (n2 = 0x2E5E26D5 ^ n2));
                    if (n3 == by) break block11;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) == 166782273) {
                        n2 = 0x22D8664D ^ n2;
                        return n3;
                    }
                    break block9;
                }
                while (Factory.jarwsmbhjwbijwmk((int)n2) != 71807516 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5327C290 ^ n2)) {
                }
                this.tmkyiPrhH8 = 0x111AE21D ^ n2;
                throw new IOException("double");
            }
            if (Factory.jarwsmbhjwbijwmk((int)(n2 = 0x4B6D7153 ^ n2)) != 129799011) {
                if (Factory.jarwsmbhjwbijwmk((int)n2) != 129799011) {
                    while (Factory.jarwsmbhjwbijwmk((int)n2) != 129799011 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x1F858475 ^ n2)) {
                    }
                    this.tmkyiPrhH8 = 0x4C214B0 ^ n2;
                    throw new IOException("double");
                }
                n2 = 0x385CB310 ^ n2;
            } else {
                n2 = 0x28AE8580 ^ n2;
                AstralPlus astralPlus = this;
                byte by = (byte)(0x6EA06445 ^ n2);
                astralPlus.addItemToTheTable(string, by, 391033119);
                if (Factory.jarwsmbhjwbijwmk((int)n2) == 106343653) {
                    n2 = 0x21A14B42 ^ n2;
                    byte by2 = (byte)(0x4F012F63 ^ n2);
                    return by2;
                }
            }
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 106343653 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x49706B5 ^ n2)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x7B426B75 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 44992446 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x66A6C22 ^ n2)) continue;
            this.tmkyiPrhH8 = 0x3978B4E2 ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 166782273 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x6EEBAA2F ^ n2)) break;
        }
        hogMZvQTRW = 0x1A55BC56 ^ n2;
        throw new IOException();
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    private int getPriority(PortalType portalType, Map map, int n) {
        int n2;
        block5: {
            block7: {
                block6: {
                    n2 = 0xEFF1AB ^ (0x2429B1E7 ^ (this.tmkyiPrhH8 ^ n));
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 0xCACCAB) break block5;
                    n2 = 0x6592D7E4 ^ n2;
                    String string = portalType.name();
                    Object v = map.get(string);
                    PortalInfo portalInfo = (PortalInfo)v;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 91646867) break block5;
                    n2 = 0x22C6E6FF ^ n2;
                    if (portalInfo != null) {
                        if (Factory.jarwsmbhjwbijwmk((int)n2) == 99038424) {
                            n2 = 0x77FA7BA ^ n2;
                            int n3 = portalInfo.priority;
                            return n3;
                        }
                        while (Factory.jarwsmbhjwbijwmk((int)n2) != 99038424 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x50889B5 ^ n2)) {
                        }
                        hogMZvQTRW = 0x26E4660F ^ n2;
                        throw new IOException("double");
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)(n2 = AstralPlus.mjbkytyqbyxonfdc(n2, 892872013))) == 17323430) break block6;
                    if (Factory.jarwsmbhjwbijwmk((int)n2) != 17323430) break block5;
                    break block7;
                }
                n2 = 0x706C2496 ^ n2;
                byte by = (byte)(0x2370CE41 ^ n2);
                return by;
            }
            n2 = 0x57226156 ^ n2;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 0xCACCAB || Factory.jarwsmbhjwbijwmk((int)n2) != (0x5C8CD2BA ^ n2)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x5808A0DC ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) != 91646867 || Factory.jarwsmbhjwbijwmk((int)n2) != (0x446E6E7E ^ n2)) continue;
            hogMZvQTRW = 0x7EDF54EE ^ n2;
            if (Factory.jarwsmbhjwbijwmk((int)n2) == 17323430 && Factory.jarwsmbhjwbijwmk((int)n2) == (0x60E9718 ^ n2)) break;
        }
        hogMZvQTRW = 0x66FDEE43 ^ n2;
        throw new IOException();
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     * Unable to fully structure code
     */
    private Portal getBestPortal(Map var1_1, int var2_2) {
        block9: {
            block10: {
                block12: {
                    block11: {
                        block8: {
                            var28_3 = 1810418711 ^ (1793265934 ^ (this.tmkyiPrhH8 ^ var2_2));
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 211892957) break block8;
                            var28_3 = 812914623 ^ var28_3;
                            var3_4 = this;
                            var5_5 = var3_4.portals;
                            var6_6 = var5_5.stream();
                            var4_7 = (Predicate<Portal>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getBestPortal$21(eu.darkbot.api.game.entities.Portal ), (Leu/darkbot/api/game/entities/Portal;)Z)();
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 223088724) break block9;
                            var28_3 = 1293968768 ^ var28_3;
                            var7_8 = var6_6.filter(var4_7);
                            var19_9 = this;
                            var20_10 = (Predicate<Portal>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getBestPortal$22(java.util.Map eu.darkbot.api.game.entities.Portal ), (Leu/darkbot/api/game/entities/Portal;)Z)((AstralPlus)var19_9, (Map)var1_1);
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 106884316) break block10;
                            var28_3 = 1799434867 ^ var28_3;
                            var8_11 = var7_8.filter(var20_10);
                            var21_12 = this;
                            var22_13 = (Function<Portal, Integer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBestPortal$23(java.util.Map eu.darkbot.api.game.entities.Portal ), (Leu/darkbot/api/game/entities/Portal;)Ljava/lang/Integer;)((AstralPlus)var21_12, (Map)var1_1);
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 120119108) break block10;
                            var28_3 = 1600221380 ^ var28_3;
                            var23_14 = Comparator.comparing(var22_13);
                            var9_15 = var8_11.min(var23_14);
                            var24_16 = null;
                            var10_17 = var9_15.orElse((Portal)var24_16);
                            var11_18 = var10_17;
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 232542625) break block9;
                            var28_3 = 733982688 ^ var28_3;
                            if (var11_18 != null) {
                                if (Factory.jarwsmbhjwbijwmk((int)var28_3) == 56039348) {
                                    var28_3 = 314100882 ^ var28_3;
                                    return var11_18;
                                } else {
                                    ** GOTO lbl32
                                }
                            }
                            break block11;
lbl32:
                            // 2 sources

                            break block9;
                        }
                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 211892957 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (1938494027 ^ var28_3)) {
                                continue;
                            }
                            AstralPlus.hogMZvQTRW = 341803611 ^ var28_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var28_3) == 20920085 && Factory.jarwsmbhjwbijwmk((int)var28_3) == (732222529 ^ var28_3)) break;
                        }
                        AstralPlus.hogMZvQTRW = 1899436871 ^ var28_3;
                        throw new IllegalAccessException("double");
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)(var28_3 = 242710741 ^ var28_3)) == 183527824) break block12;
                    if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 183527824) break block10;
                    var28_3 = AstralPlus.mjbkytyqbyxonfdc(var28_3, 946724261);
                    break block9;
                }
                var28_3 = AstralPlus.mjbkytyqbyxonfdc(var28_3, 1603012728);
                var12_19 = this;
                var13_20 = var12_19.portals;
                var14_21 = var13_20.stream();
                var25_22 = (Predicate<Portal>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getBestPortal$24(eu.darkbot.api.game.entities.Portal ), (Leu/darkbot/api/game/entities/Portal;)Z)();
                var15_23 = var14_21.filter(var25_22);
                var16_24 = var15_23.findFirst();
                var26_25 = null;
                var17_26 = var16_24.orElse((Portal)var26_25);
                var18_27 = var17_26;
                return var18_27;
            }
            while (true) {
                if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 263210799 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (1694584613 ^ var28_3)) {
                    continue;
                }
                AstralPlus.hogMZvQTRW = 926769435 ^ var28_3;
                if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 120119108 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (1616813500 ^ var28_3)) continue;
                this.tmkyiPrhH8 = 1452226613 ^ var28_3;
                if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 183527824 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (496222406 ^ var28_3)) continue;
                this.tmkyiPrhH8 = 1564799506 ^ var28_3;
                if (Factory.jarwsmbhjwbijwmk((int)var28_3) == 106884316 && Factory.jarwsmbhjwbijwmk((int)var28_3) == (532857482 ^ var28_3)) break;
            }
            AstralPlus.hogMZvQTRW = 1888327573 ^ var28_3;
            throw new IllegalAccessException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 56039348 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (1406176229 ^ var28_3)) {
                continue;
            }
            AstralPlus.hogMZvQTRW = 538795932 ^ var28_3;
            if (Factory.jarwsmbhjwbijwmk((int)var28_3) != 232542625 || Factory.jarwsmbhjwbijwmk((int)var28_3) != (2098037627 ^ var28_3)) continue;
            AstralPlus.hogMZvQTRW = 2053413051 ^ var28_3;
            if (Factory.jarwsmbhjwbijwmk((int)var28_3) == 223088724 && Factory.jarwsmbhjwbijwmk((int)var28_3) == (483143219 ^ var28_3)) break;
        }
        this.tmkyiPrhH8 = 258293581 ^ var28_3;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private static /* synthetic */ boolean lambda$getBestPortal$24(Portal var0) {
        block24: {
            block31: {
                block25: {
                    block27: {
                        block29: {
                            block26: {
                                block30: {
                                    block28: {
                                        var6_1 = 350375072 ^ (86963383 ^ AstralPlus.hogMZvQTRW);
                                        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 184643277) break block24;
                                        var6_1 = 777547215 ^ var6_1;
                                        var2_2 = var0.getPortalType();
                                        if (var2_2 == (var1_3 = PortalType.STANDARD)) break block28;
                                        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 19903403) break block29;
                                        var6_1 = 1466464038 ^ var6_1;
                                        var3_4 = (byte)(1309318124 ^ var6_1);
                                        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 120897553) break block24;
                                        break block30;
                                    }
                                    block12: while (true) {
                                        switch (Factory.jarwsmbhjwbijwmk((int)var6_1)) {
                                            case 925741085: {
                                                continue block12;
                                            }
                                            case 19903403: {
                                                var6_1 = 922577561 ^ var6_1;
                                            }
                                            case 1228181766: {
                                                if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 205863841) {
                                                    if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 205863841) {
                                                        var6_1 = 1478338408 ^ var6_1;
                                                    }
                                                    break block24;
                                                }
                                                var6_1 = AstralPlus.mjbkytyqbyxonfdc(var6_1, 755495484);
                                                break block25;
                                            }
                                            default: {
                                                if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 267060800) ** GOTO lbl78
                                            }
                                        }
                                        break;
                                    }
                                }
                                var6_1 = 1491341723 ^ var6_1;
                                if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 208550570) break block26;
                                throw null;
                            }
                            try {
                                throw new IllegalAccessException();
                            }
                            catch (IllegalAccessException v0) {
                                switch (Factory.efuojskrxnitvcvt((int)var6_1)) {
                                    default: {
                                        throw new IOException("Error in hash");
                                    }
                                    case -1668126045: {
                                        var6_1 = 1754868458 ^ var6_1;
                                        break;
                                    }
                                    case -1217113168: {
                                        var6_1 = AstralPlus.mjbkytyqbyxonfdc(var6_1, 233172424);
                                    }
                                }
                                if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 73794059) break block27;
                            }
                        }
                        while (true) {
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 73794059 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (1561818492 ^ var6_1)) {
                                continue;
                            }
                            AstralPlus.hogMZvQTRW = 838008358 ^ var6_1;
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 19903403 && Factory.jarwsmbhjwbijwmk((int)var6_1) == (26308916 ^ var6_1)) break;
                        }
                        AstralPlus.hogMZvQTRW = 709307467 ^ var6_1;
                        throw new IOException("double");
                    }
                    var6_1 = 1085618556 ^ var6_1;
                    break block31;
                }
                var3_4 = (byte)(43545710 ^ var6_1);
                if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 40824767) break block24;
                var6_1 = 1495673516 ^ var6_1;
            }
            return var3_4;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 120897553 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (586701666 ^ var6_1)) {
                continue;
            }
            AstralPlus.hogMZvQTRW = 1016935753 ^ var6_1;
            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 184643277 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (466464547 ^ var6_1)) continue;
            AstralPlus.hogMZvQTRW = 1728125420 ^ var6_1;
            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 40824767 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (2116077100 ^ var6_1)) continue;
            AstralPlus.hogMZvQTRW = 1502783028 ^ var6_1;
            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 205863841 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (1471757063 ^ var6_1)) continue;
            AstralPlus.hogMZvQTRW = 2095909955 ^ var6_1;
            if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 208550570 && Factory.jarwsmbhjwbijwmk((int)var6_1) == (171908717 ^ var6_1)) break;
        }
        AstralPlus.hogMZvQTRW = 1100757464 ^ var6_1;
        throw new IOException();
lbl78:
        // 3 sources

        if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 267060800) ** GOTO lbl80
        ** GOTO lbl78
lbl80:
        // 2 sources

        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != (2114540832 ^ var6_1)) ** GOTO lbl78
        AstralPlus.hogMZvQTRW = 511014737 ^ var6_1;
        throw new RuntimeException("double");
    }

    private /* synthetic */ Integer lambda$getBestPortal$23(Map map, Portal portal) {
        int n = 0x5FB1C9E8 ^ (0xC626F37 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 109676769) {
            n = 0x63D21639 ^ n;
            AstralPlus astralPlus = this;
            PortalType portalType = portal.getPortalType();
            int n2 = astralPlus.getPriority(portalType, map, 1352415340);
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 109676769 || Factory.jarwsmbhjwbijwmk((int)n) != (0x71CB04D9 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0xA95ECA4 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 67362248 && Factory.jarwsmbhjwbijwmk((int)n) == (0x512A6DC9 ^ n)) break;
        }
        hogMZvQTRW = 0x65E5F7FE ^ n;
        throw new RuntimeException();
    }

    /*
     * Unable to fully structure code
     */
    private /* synthetic */ boolean lambda$getBestPortal$22(Map var1_1, Portal var2_2) {
        block26: {
            block25: {
                block29: {
                    block27: {
                        block28: {
                            var12_3 = 1991558525 ^ (1274029633 ^ this.tmkyiPrhH8);
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 173379068) ** GOTO lbl-1000
                            var12_3 = 148665056 ^ var12_3;
                            var3_4 = this;
                            var8_5 = var2_2.getPortalType();
                            var4_6 = var3_4.shouldEnter(var8_5, 1910889661);
                            if (var4_6 != (1622284057 ^ var12_3)) ** GOTO lbl37
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 190409842) ** GOTO lbl-1000
                            var12_3 = 1734597225 ^ var12_3;
                            var5_7 = this;
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 122992456) break block26;
                            var12_3 = 1223266410 ^ var12_3;
                            var9_8 = var2_2.getPortalType();
                            var6_9 = var5_7.ignoreLimit(var9_8, var1_1, 525220523);
                            if (var6_9 == (1329093914 ^ var12_3)) break block27;
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 159218522) break block28;
                            var12_3 = 1509644515 ^ var12_3;
lbl18:
                            // 2 sources

                            while (true) {
                                var7_10 = (byte)(381879800 ^ var12_3);
                                if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 203017553) ** GOTO lbl48
                                ** GOTO lbl52
                                break;
                            }
                        }
                        while (Factory.jarwsmbhjwbijwmk((int)var12_3) != 159218522 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (148822774 ^ var12_3)) {
                        }
                        AstralPlus.hogMZvQTRW = 863365207 ^ var12_3;
                        throw new RuntimeException("double");
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)(var12_3 = 150650166 ^ var12_3)) == 185193879) break block29;
                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 185193879) ** GOTO lbl-1000
                    block14: while (true) {
                        switch (Factory.jarwsmbhjwbijwmk((int)var12_3)) {
                            case 699942734: {
                                continue block14;
                            }
                            case 185193879: {
                                var12_3 = 68093609 ^ var12_3;
                                ** GOTO lbl58
                            }
lbl37:
                            // 1 sources

                            if (Factory.jarwsmbhjwbijwmk((int)(var12_3 = AstralPlus.mjbkytyqbyxonfdc(var12_3, 391439502))) == 139395090) ** GOTO lbl40
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 139395090) ** GOTO lbl42
                            ** GOTO lbl46
lbl40:
                            // 1 sources

                            var12_3 = 1629877870 ^ var12_3;
                            ** continue;
lbl42:
                            // 2 sources

                            while (Factory.jarwsmbhjwbijwmk((int)var12_3) != 139395090 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (642520649 ^ var12_3)) {
                            }
                            this.tmkyiPrhH8 = 1018252642 ^ var12_3;
                            throw new IllegalAccessException("double");
lbl46:
                            // 1 sources

                            var12_3 = AstralPlus.mjbkytyqbyxonfdc(var12_3, 875238674);
                            ** GOTO lbl58
lbl48:
                            // 2 sources

                            while (Factory.jarwsmbhjwbijwmk((int)var12_3) != 203017553 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (311176731 ^ var12_3)) {
                            }
                            AstralPlus.hogMZvQTRW = 835116962 ^ var12_3;
                            throw new RuntimeException("double");
lbl52:
                            // 1 sources

                            var12_3 = 2081032478 ^ var12_3;
                            if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 249946678) ** GOTO lbl57
                            throw null;
lbl57:
                            // 2 sources

                            throw new RuntimeException();
lbl58:
                            // 4 sources

                            case 2082883815: lbl-1000:
                            // 4 sources

                            {
                                while (true) {
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 185193879) ** GOTO lbl58
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != (1753294397 ^ var12_3)) continue;
                                    AstralPlus.hogMZvQTRW = 673946555 ^ var12_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 56536428 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (1810386103 ^ var12_3)) continue;
                                    AstralPlus.hogMZvQTRW = 1161236768 ^ var12_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 173379068 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (1600419959 ^ var12_3)) continue;
                                    AstralPlus.hogMZvQTRW = 784022119 ^ var12_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 3507927 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (300418612 ^ var12_3)) continue;
                                    AstralPlus.hogMZvQTRW = 557562316 ^ var12_3;
                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 190409842 && Factory.jarwsmbhjwbijwmk((int)var12_3) == (431983370 ^ var12_3)) break;
                                }
                                this.tmkyiPrhH8 = 1687868180 ^ var12_3;
                                throw new IOException();
                            }
                            default: {
                                if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 249946678) ** GOTO lbl103
                            }
                        }
                        catch (RuntimeException v0) {
                            switch (Factory.efuojskrxnitvcvt((int)var12_3)) {
                                case -539579893: {
                                    var12_3 = 1872106370 ^ var12_3;
                                    ** GOTO lbl82
                                }
                                case 1447585595: {
                                    var12_3 = 2124794404 ^ var12_3;
lbl82:
                                    // 2 sources

                                    if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 127063413) {
                                        var12_3 = AstralPlus.mjbkytyqbyxonfdc(var12_3, 1947597030);
                                        break block25;
                                    }
                                    ** GOTO lbl103
                                }
                            }
                        }
                        break;
                    }
                }
                var12_3 = AstralPlus.mjbkytyqbyxonfdc(var12_3, 817836200);
                var7_10 = (byte)(2004734084 ^ var12_3);
                if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 126071938) break block26;
                var12_3 = 386199201 ^ var12_3;
            }
            return (boolean)var7_10;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 183142430 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (2133754206 ^ var12_3)) continue;
            this.tmkyiPrhH8 = 1178277640 ^ var12_3;
            if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 122992456 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (840976650 ^ var12_3)) continue;
            AstralPlus.hogMZvQTRW = 242936720 ^ var12_3;
            if (Factory.jarwsmbhjwbijwmk((int)var12_3) == 126071938 && Factory.jarwsmbhjwbijwmk((int)var12_3) == (1826590000 ^ var12_3)) break;
        }
        AstralPlus.hogMZvQTRW = 1140195035 ^ var12_3;
        throw new IllegalAccessException("double");
lbl103:
        // 5 sources

        if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 249946678) ** GOTO lbl103
        if (Factory.jarwsmbhjwbijwmk((int)var12_3) != (1394969837 ^ var12_3)) ** GOTO lbl103
        AstralPlus.hogMZvQTRW = 310837741 ^ var12_3;
        if (Factory.jarwsmbhjwbijwmk((int)var12_3) != 127063413 || Factory.jarwsmbhjwbijwmk((int)var12_3) != (434398035 ^ var12_3)) ** GOTO lbl103
        this.tmkyiPrhH8 = 1017644835 ^ var12_3;
        throw new IOException("double");
        throw new IllegalAccessException("Error in hash");
    }

    /*
     * WARNING - void declaration
     */
    private static /* synthetic */ boolean lambda$getBestPortal$21(Portal portal) {
        int n;
        block15: {
            void var3_6;
            block17: {
                block18: {
                    block16: {
                        block14: {
                            PortalType portalType;
                            n = 0x573A7BDC ^ (0x3D02B11A ^ hogMZvQTRW);
                            if (Factory.jarwsmbhjwbijwmk((int)n) != 80786594) break block15;
                            n = 0x60CE1EC6 ^ n;
                            PortalType portalType2 = portal.getPortalType();
                            if (portalType2 == (portalType = PortalType.STANDARD)) break block16;
                            if (Factory.jarwsmbhjwbijwmk((int)n) != 88205140) break block15;
                            n = 0x70CC8542 ^ n;
                            byte bl = (byte)(0x5CCC6850 ^ n);
                            if (Factory.jarwsmbhjwbijwmk((int)n) != 63723216) break block15;
                            n = 0x4554031 ^ n;
                            if (Factory.jarwsmbhjwbijwmk((int)n) == 195618467) break block14;
                            throw null;
                        }
                        try {
                            throw new IllegalAccessException();
                        }
                        catch (IllegalAccessException illegalAccessException) {
                            switch (Factory.efuojskrxnitvcvt((int)n)) {
                                case 477751418: {
                                    n = 0x405EF16E ^ n;
                                    break;
                                }
                                default: {
                                    throw new IOException("Error in hash");
                                }
                                case -993443070: {
                                    n = AstralPlus.mjbkytyqbyxonfdc(n, 1531503617);
                                }
                            }
                            if (Factory.jarwsmbhjwbijwmk((int)n) != 60075083) break block15;
                            n = 0x446A8517 ^ n;
                        }
                        break block17;
                    }
                    if (Factory.jarwsmbhjwbijwmk((int)(n = AstralPlus.mjbkytyqbyxonfdc(n, 321359175))) == 173831557) break block18;
                    if (Factory.jarwsmbhjwbijwmk((int)n) == 173831557) {
                        n = AstralPlus.mjbkytyqbyxonfdc(n, 684637013);
                    }
                    break block15;
                }
                n = 0x343C863 ^ n;
                byte by = (byte)(0x3C64A837 ^ n);
                if (Factory.jarwsmbhjwbijwmk((int)n) != 84097422) break block15;
                n = 0x7BDFE141 ^ n;
            }
            return (boolean)var3_6;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 88205140 || Factory.jarwsmbhjwbijwmk((int)n) != (0x7F42D557 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x1712478B ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 48612802 || Factory.jarwsmbhjwbijwmk((int)n) != (0x6DE84B53 ^ n)) continue;
            hogMZvQTRW = 0x24DB9E71 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 184195567 || Factory.jarwsmbhjwbijwmk((int)n) != (0x79169093 ^ n)) continue;
            hogMZvQTRW = 0x5979C19D ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 63723216 || Factory.jarwsmbhjwbijwmk((int)n) != (0x3F8A7C02 ^ n)) continue;
            hogMZvQTRW = 0x5A86B65E ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 173831557 || Factory.jarwsmbhjwbijwmk((int)n) != (0x694113AE ^ n)) continue;
            hogMZvQTRW = 0x27CCEECC ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 240455608 || Factory.jarwsmbhjwbijwmk((int)n) != (0x571097C6 ^ n)) continue;
            hogMZvQTRW = 0x1DC3E9D ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 60075083 || Factory.jarwsmbhjwbijwmk((int)n) != (0x765D63C1 ^ n)) continue;
            hogMZvQTRW = 0x72F59F95 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 195618467 || Factory.jarwsmbhjwbijwmk((int)n) != (0x5ED074C2 ^ n)) continue;
            hogMZvQTRW = 0xE801E4A ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) != 84097422 || Factory.jarwsmbhjwbijwmk((int)n) != (0x5CCD2432 ^ n)) continue;
            hogMZvQTRW = 0x6FF729BC ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 80786594 && Factory.jarwsmbhjwbijwmk((int)n) == (0x29E390FF ^ n)) break;
        }
        hogMZvQTRW = 0x678541F4 ^ n;
        throw new IllegalAccessException();
    }

    private static /* synthetic */ boolean lambda$getModules$19(AstralGateAPI.AstralItem astralItem) {
        int n = 0x25EF3580 ^ (0x78CFE5BE ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 267393041) {
            n = 0x69557F0B ^ n;
            String string = astralItem.getLootId();
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.nmmoelleavwiyjh(), n);
            boolean bl = string.contains(string2);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 64938090 || Factory.jarwsmbhjwbijwmk((int)n) != (0x465221DB ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x28A05DB9 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 267393041 && Factory.jarwsmbhjwbijwmk((int)n) == (0x3778FD52 ^ n)) break;
        }
        hogMZvQTRW = 0x4E2A0F5C ^ n;
        throw new RuntimeException();
    }

    private static /* synthetic */ boolean lambda$hasOptionsToChoose$17(AstralGateAPI.AstralItem astralItem) {
        int n = 0x3334E6A2 ^ (0x65FCF8D5 ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 158866543) {
            n = 0x5C3117F9 ^ n;
            boolean bl = astralItem.isEquipped();
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 158866543 || Factory.jarwsmbhjwbijwmk((int)n) != (0x6390CCAE ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x4A1777DD ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 89997887 && Factory.jarwsmbhjwbijwmk((int)n) == (0x2A6B2AA ^ n)) break;
        }
        hogMZvQTRW = 0x76B1077C ^ n;
        throw new IOException();
    }

    private /* synthetic */ Integer lambda$getBestGeneratorItem$16(AstralGateAPI.AstralItem astralItem) {
        int n = 0xDA81998 ^ (0x7B063A4 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 141726992) {
            n = 0x2E0C7B08 ^ n;
            AstralPlus astralPlus = this;
            String string = astralItem.getLootId();
            int n2 = astralPlus.getGeneratorPriority(string, 868107932);
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 141726992 || Factory.jarwsmbhjwbijwmk((int)n) != (0x75910D2A ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x7511074B ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 191452764 && Factory.jarwsmbhjwbijwmk((int)n) == (0x4B2AE29B ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x6BEB7D38 ^ n;
        throw new IOException();
    }

    private /* synthetic */ Integer lambda$getBestGeneratorItem$15(AstralGateAPI.AstralItem astralItem) {
        int n = 0x46016AC1 ^ (0x1B3F3AE3 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 127107436) {
            n = 0x71DCD375 ^ n;
            AstralPlus astralPlus = this;
            String string = astralItem.getLootId();
            int n2 = astralPlus.getItemPriority(string, 843699290);
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 127107436 || Factory.jarwsmbhjwbijwmk((int)n) != (0x6DAC761D ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x6936D664 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 200536834 && Factory.jarwsmbhjwbijwmk((int)n) == (0x7F575123 ^ n)) break;
        }
        hogMZvQTRW = 0x241CCDEB ^ n;
        throw new IOException();
    }

    private /* synthetic */ Integer lambda$getBestWeaponItem$14(AstralGateAPI.AstralItem astralItem) {
        int n = 0x25AE9F70 ^ (0x4F76C085 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 199670591) {
            n = 0x19BC266F ^ n;
            AstralPlus astralPlus = this;
            String string = astralItem.getLootId();
            int n2 = astralPlus.getWeaponPriority(string, 2016898855);
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 171518086 || Factory.jarwsmbhjwbijwmk((int)n) != (0x5C0A62E ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x65B4359E ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 199670591 && Factory.jarwsmbhjwbijwmk((int)n) == (0x106F27EA ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x378EE06 ^ n;
        throw new IllegalAccessException();
    }

    private /* synthetic */ Integer lambda$getBestWeaponItem$13(AstralGateAPI.AstralItem astralItem) {
        int n = 0x1FB8071D ^ (0xBD872B1 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 243365585) {
            n = 0x2E29C876 ^ n;
            AstralPlus astralPlus = this;
            String string = astralItem.getLootId();
            int n2 = astralPlus.getItemPriority(string, 843699290);
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 131083759 || Factory.jarwsmbhjwbijwmk((int)n) != (0x74B266D0 ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x1C73E6BE ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 243365585 && Factory.jarwsmbhjwbijwmk((int)n) == (0x5209F9A9 ^ n)) break;
        }
        this.tmkyiPrhH8 = 0xEC25B49 ^ n;
        throw new IllegalAccessException();
    }

    private static /* synthetic */ boolean lambda$hasThatModuleType$11(AstralGateAPI.AstralItem astralItem) {
        int n = 0x3530FD44 ^ (0x47ACB60B ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 59664888) {
            n = 0xB897940 ^ n;
            String string = astralItem.getLootId();
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.mhgekypfgfcjtzp(), n);
            boolean bl = string.contains(string2);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 164084325 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2EEA0B20 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x4AB57F32 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 59664888 && Factory.jarwsmbhjwbijwmk((int)n) == (0x14B67712 ^ n)) break;
        }
        hogMZvQTRW = 0x63D09192 ^ n;
        throw new RuntimeException();
    }

    private static /* synthetic */ boolean lambda$hasThatModuleType$10(AstralGateAPI.AstralItem astralItem) {
        int n = 0x4E19FFA3 ^ (0x2BEAABD4 ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 31113460) {
            n = 0x7B9A7582 ^ n;
            String string = astralItem.getLootId();
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.vcivdfyqtilbigs(), n);
            boolean bl = string.contains(string2);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 31113460 || Factory.jarwsmbhjwbijwmk((int)n) != (0x3BE84B4B ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x67A0104B ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 229931170 && Factory.jarwsmbhjwbijwmk((int)n) == (0x1D558850 ^ n)) break;
        }
        hogMZvQTRW = 0x7ECF2DB7 ^ n;
        throw new IllegalAccessException();
    }

    private static /* synthetic */ boolean lambda$hasThatModuleType$9(AstralGateAPI.AstralItem astralItem) {
        int n = 0x4C09C47D ^ (0x600D685C ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 87428066) {
            n = 0x149D5D70 ^ n;
            String string = astralItem.getLootId();
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.lyuevsudgftedwt(), n);
            boolean bl = string.contains(string2);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 87428066 || Factory.jarwsmbhjwbijwmk((int)n) != (0x58F6CFBC ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x6ABECAA4 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 184061328 && Factory.jarwsmbhjwbijwmk((int)n) == (0x156F0ADA ^ n)) break;
        }
        hogMZvQTRW = 0x30DC0B9B ^ n;
        throw new RuntimeException();
    }

    private static /* synthetic */ boolean lambda$hasThatModuleType$8(AstralGateAPI.AstralItem astralItem) {
        int n = 0x5F4C3441 ^ (0x437576BD ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 32651984) {
            n = 0x65C31B89 ^ n;
            String string = astralItem.getLootId();
            String string2 = AstralPlus.thisIsAInsaneEncryptionMethod(AstralPlus.qgsbzbmppfvpbzd(), n);
            boolean bl = string.contains(string2);
            return bl;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 136816073 || Factory.jarwsmbhjwbijwmk((int)n) != (0x50FE9E21 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x13E9BACE ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 32651984 && Factory.jarwsmbhjwbijwmk((int)n) == (0x329D7F7F ^ n)) break;
        }
        hogMZvQTRW = 0x6CAB8D80 ^ n;
        throw new RuntimeException();
    }

    private static /* synthetic */ Integer lambda$getBestModuleItem$7(AstralGateAPI.AstralItem astralItem) {
        int n = 0x32C48036 ^ (0x25E37C4D ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 8806312) {
            n = 0x59619598 ^ n;
            List list = astralItem.getStats();
            int n2 = list.size();
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 8806312 || Factory.jarwsmbhjwbijwmk((int)n) != (0x79A445F4 ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x27EB74B7 ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 181793889 && Factory.jarwsmbhjwbijwmk((int)n) == (0x77299F26 ^ n)) break;
        }
        hogMZvQTRW = 0x1793A4FD ^ n;
        throw new RuntimeException();
    }

    /*
     * Exception decompiling
     */
    private /* synthetic */ boolean lambda$getBestModuleItem$6(AstralGateAPI.AstralItem var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static /* synthetic */ Integer lambda$getBestModuleItem$5(AstralGateAPI.AstralItem astralItem) {
        int n = 0x59EC2E5F ^ (0x3FB316F1 ^ hogMZvQTRW);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 222770167) {
            n = 0x136382D4 ^ n;
            List list = astralItem.getStats();
            int n2 = list.size();
            Integer n3 = n2;
            return n3;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 222770167 || Factory.jarwsmbhjwbijwmk((int)n) != (0x2EC9052D ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x4B5B80DF ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 39325009 && Factory.jarwsmbhjwbijwmk((int)n) == (0x18DDA1B4 ^ n)) break;
        }
        hogMZvQTRW = 0x70EADC47 ^ n;
        throw new IOException();
    }

    /*
     * Unable to fully structure code
     */
    private /* synthetic */ boolean lambda$getBestModuleItem$4(AstralGateAPI.AstralItem var1_1) {
        block31: {
            block33: {
                block32: {
                    var11_2 = 1106413980 ^ (188220872 ^ this.tmkyiPrhH8);
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 230820760) break block31;
                    var11_2 = 1102657410 ^ var11_2;
                    var2_3 = this;
                    var7_4 = var1_1.getStats();
                    var3_5 = var2_3.hasNegativeStat(var7_4, 1322274712);
                    if (var3_5 != (1582906643 ^ var11_2)) break block32;
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 114079757) break block31;
                    var11_2 = 1484424908 ^ var11_2;
                    var4_6 = this;
                    var8_7 = var1_1.getLootId();
                    var5_8 = var4_6.hasThatModuleType(var8_7, 1547162766);
                    if (var5_8 != (103005151 ^ var11_2)) ** GOTO lbl51
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 96568834) break block33;
                    var11_2 = 1396395655 ^ var11_2;
                    var6_9 = (byte)(1427699033 ^ var11_2);
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 81792578) ** GOTO lbl85
                    ** GOTO lbl33
                }
                block17: while (true) {
                    switch (Factory.jarwsmbhjwbijwmk((int)var11_2)) {
                        case 2030949193: {
                            continue block17;
                        }
                        case 114079757: {
                            var11_2 = 1323301415 ^ var11_2;
                        }
                        case 1017707668: {
                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 340338) ** GOTO lbl31
                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 340338) break block17;
                            var11_2 = 643710465 ^ var11_2;
                            ** GOTO lbl66
lbl31:
                            // 1 sources

                            var11_2 = 1910517848 ^ var11_2;
                            ** GOTO lbl-1000
                        }
lbl33:
                        // 1 sources

                        var11_2 = 533901774 ^ var11_2;
                        try {
                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 15183400) ** GOTO lbl50
                            throw null;
                        }
                        catch (IllegalAccessException v0) {
                            switch (Factory.efuojskrxnitvcvt((int)var11_2)) {
                                case 1448223922: {
                                    var11_2 = 707526257 ^ var11_2;
                                    break;
                                }
                                default: {
                                    throw new RuntimeException("Error in hash");
                                }
                                case 183803457: {
                                    var11_2 = AstralPlus.mjbkytyqbyxonfdc(var11_2, 1103046959);
                                }
                            }
                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 196426494) ** GOTO lbl85
                            var11_2 = AstralPlus.mjbkytyqbyxonfdc(var11_2, 1401353351);
                            ** GOTO lbl65
                        }
lbl50:
                        // 2 sources

                        throw new IllegalAccessException();
lbl51:
                        // 1 sources

                        if (Factory.jarwsmbhjwbijwmk((int)(var11_2 = 537999997 ^ var11_2)) == 167902491) ** GOTO lbl55
                        if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 167902491) {
                            var11_2 = AstralPlus.mjbkytyqbyxonfdc(var11_2, 283801751);
                        }
                        ** GOTO lbl66
lbl55:
                        // 1 sources

                        block18: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var11_2)) {
                                case 361559537: {
                                    continue block18;
                                }
                                case 167902491: {
                                    var11_2 = 1196717774 ^ var11_2;
                                }
                                case 1808974078: lbl-1000:
                                // 2 sources

                                {
                                    var6_9 = (byte)(1634137964 ^ var11_2);
                                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 213327367) ** GOTO lbl66
                                    var11_2 = 1375844620 ^ var11_2;
lbl65:
                                    // 2 sources

                                    return (boolean)var6_9;
lbl66:
                                    // 5 sources

                                    default: {
                                        while (true) {
                                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 213327367) ** GOTO lbl66
                                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) != (1852941415 ^ var11_2)) continue;
                                            this.tmkyiPrhH8 = 1315142510 ^ var11_2;
                                            if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 167902491 && Factory.jarwsmbhjwbijwmk((int)var11_2) == (793227555 ^ var11_2)) break;
                                        }
                                        this.tmkyiPrhH8 = 2126317569 ^ var11_2;
                                        throw new IllegalAccessException();
                                    }
                                }
                                default: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 60298429) ** GOTO lbl85
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                while (true) {
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 172250126 || Factory.jarwsmbhjwbijwmk((int)var11_2) != (1758962430 ^ var11_2)) {
                        continue;
                    }
                    this.tmkyiPrhH8 = 1503034862 ^ var11_2;
                    if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 340338 && Factory.jarwsmbhjwbijwmk((int)var11_2) == (1231305239 ^ var11_2)) break;
                }
                this.tmkyiPrhH8 = 1028715615 ^ var11_2;
                throw new RuntimeException("double");
lbl85:
                // 7 sources

                if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 60298429) ** GOTO lbl87
                ** GOTO lbl85
lbl87:
                // 2 sources

                if (Factory.jarwsmbhjwbijwmk((int)var11_2) != (1646343168 ^ var11_2)) ** GOTO lbl85
                AstralPlus.hogMZvQTRW = 1312404285 ^ var11_2;
                if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 196426494 || Factory.jarwsmbhjwbijwmk((int)var11_2) != (1495898495 ^ var11_2)) ** GOTO lbl85
                this.tmkyiPrhH8 = 1582555772 ^ var11_2;
                if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 81792578 || Factory.jarwsmbhjwbijwmk((int)var11_2) != (677070932 ^ var11_2)) ** GOTO lbl85
                AstralPlus.hogMZvQTRW = 1990804867 ^ var11_2;
                throw new IllegalAccessException("double");
            }
            while (Factory.jarwsmbhjwbijwmk((int)var11_2) != 96568834 || Factory.jarwsmbhjwbijwmk((int)var11_2) != (532989900 ^ var11_2)) {
            }
            this.tmkyiPrhH8 = 419813213 ^ var11_2;
            throw new IllegalAccessException("double");
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)var11_2) != 114079757 || Factory.jarwsmbhjwbijwmk((int)var11_2) != (520830288 ^ var11_2)) {
                continue;
            }
            this.tmkyiPrhH8 = 1342120470 ^ var11_2;
            if (Factory.jarwsmbhjwbijwmk((int)var11_2) == 230820760 && Factory.jarwsmbhjwbijwmk((int)var11_2) == (2101257378 ^ var11_2)) break;
        }
        this.tmkyiPrhH8 = 876061171 ^ var11_2;
        throw new IOException("double");
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static /* synthetic */ boolean lambda$autoChoosePortal$3(Portal var0) {
        block25: {
            block24: {
                block21: {
                    block22: {
                        block23: {
                            var6_1 = 459046358 ^ (1197404501 ^ AstralPlus.hogMZvQTRW);
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 238708019) ** GOTO lbl64
                            var6_1 = 88389460 ^ var6_1;
                            var2_2 = var0.getPortalType();
                            if (var2_2 != (var1_3 = PortalType.STANDARD)) break block23;
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 124091115) ** GOTO lbl64
                            var6_1 = 131999837 ^ var6_1;
                            var3_4 = (byte)(2020485784 ^ var6_1);
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 156568294) break block24;
                            var6_1 = AstralPlus.mjbkytyqbyxonfdc(var6_1, 1262476517);
                            ** try [egrp 0[TRYBLOCK] [0 : 200->213)] { 
lbl12:
                            // 1 sources

                            ** GOTO lbl-1000
                        }
                        block12: while (true) {
                            switch (Factory.jarwsmbhjwbijwmk((int)var6_1)) {
                                case 953717040: {
                                    continue block12;
                                }
                                case 124091115: {
                                    var6_1 = 1635785577 ^ var6_1;
                                }
                                case 1155549568: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 175976335) {
                                        if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 175976335) {
                                            var6_1 = AstralPlus.mjbkytyqbyxonfdc(var6_1, 1672165437);
                                        }
                                    } else {
                                        var6_1 = 281788919 ^ var6_1;
                                        break block21;
                                    }
                                }
                                default: {
                                    if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 57605825) ** GOTO lbl64
                                    break block12;
                                }
                            }
                            break;
                        }
                        break block25;
lbl-1000:
                        // 1 sources

                        {
                            if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 57605825) throw new RuntimeException();
                            throw null;
                        }
lbl33:
                        // 1 sources

                        catch (RuntimeException v0) {
                            switch (Factory.efuojskrxnitvcvt((int)var6_1)) {
                                case -18847965: {
                                    var6_1 = AstralPlus.mjbkytyqbyxonfdc(var6_1, 1063334244);
                                    ** GOTO lbl40
                                }
                                case -1701858335: {
                                    var6_1 = 1944898556 ^ var6_1;
lbl40:
                                    // 2 sources

                                    if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 225372053) break;
                                    break block22;
                                }
                            }
                        }
                        while (Factory.jarwsmbhjwbijwmk((int)var6_1) != 225372053 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (767228696 ^ var6_1)) {
                        }
                        AstralPlus.hogMZvQTRW = 150250937 ^ var6_1;
                        throw new IllegalAccessException("double");
                    }
                    var6_1 = 2022014405 ^ var6_1;
                    return var3_4;
                }
                var3_4 = (byte)(251385946 ^ var6_1);
                if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 218627541) {
                    var6_1 = 918788127 ^ var6_1;
                    return var3_4;
                }
                while (Factory.jarwsmbhjwbijwmk((int)var6_1) != 218627541 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (272328329 ^ var6_1)) {
                }
                AstralPlus.hogMZvQTRW = 688338861 ^ var6_1;
                throw new RuntimeException("double");
                throw new IOException("Error in hash");
            }
            while (Factory.jarwsmbhjwbijwmk((int)var6_1) != 156568294 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (193349380 ^ var6_1)) {
            }
            AstralPlus.hogMZvQTRW = 902253762 ^ var6_1;
            throw new RuntimeException("double");
lbl64:
            // 11 sources

            if (Factory.jarwsmbhjwbijwmk((int)var6_1) == 57605825) ** GOTO lbl66
            ** GOTO lbl64
        }
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != (895012008 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 2137462399 ^ var6_1;
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 56421532 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (304326263 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 2023906852 ^ var6_1;
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 124091115 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (1731080382 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 2013298775 ^ var6_1;
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 238708019 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (54905811 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 613259980 ^ var6_1;
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 175976335 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (1241022671 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 1661901097 ^ var6_1;
        if (Factory.jarwsmbhjwbijwmk((int)var6_1) != 129640229 || Factory.jarwsmbhjwbijwmk((int)var6_1) != (701905994 ^ var6_1)) ** GOTO lbl64
        AstralPlus.hogMZvQTRW = 1141285164 ^ var6_1;
        throw new IllegalAccessException();
    }

    private /* synthetic */ void lambda$initCustomItemPriority$1(String string) {
        int n = 0x42D868E2 ^ (0x7FC31D37 ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 165719774) {
            n = 0x10F7E89F ^ n;
            AstralPlus astralPlus = this;
            String string2 = "equipment_weapon_laser_" + string;
            AstralPlus astralPlus2 = this;
            List<String> list = astralPlus2.weaponPriority;
            int n2 = list.indexOf(string);
            astralPlus.addItemToTheTable(string2, n2, 391033119);
            return;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 167751075 || Factory.jarwsmbhjwbijwmk((int)n) != (0x415279AF ^ n)) {
                continue;
            }
            hogMZvQTRW = 0x13C0890F ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 165719774 && Factory.jarwsmbhjwbijwmk((int)n) == (0x725F1573 ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x66BA7701 ^ n;
        throw new RuntimeException();
    }

    private /* synthetic */ void lambda$initCustomItemPriority$0(String string) {
        int n = 0x6A6FE61D ^ (0x64FA408A ^ this.tmkyiPrhH8);
        if (Factory.jarwsmbhjwbijwmk((int)n) == 29496609) {
            n = 0x78B8E7FF ^ n;
            AstralPlus astralPlus = this;
            String string2 = "equipment_generator_" + string;
            AstralPlus astralPlus2 = this;
            List<String> list = astralPlus2.generatorPriority;
            int n2 = list.indexOf(string);
            astralPlus.addItemToTheTable(string2, n2, 391033119);
            return;
        }
        while (true) {
            if (Factory.jarwsmbhjwbijwmk((int)n) != 64958936 || Factory.jarwsmbhjwbijwmk((int)n) != (0x420B811E ^ n)) {
                continue;
            }
            this.tmkyiPrhH8 = 0x1C7F593F ^ n;
            if (Factory.jarwsmbhjwbijwmk((int)n) == 29496609 && Factory.jarwsmbhjwbijwmk((int)n) == (0x1E3F7762 ^ n)) break;
        }
        this.tmkyiPrhH8 = 0x77C99121 ^ n;
        throw new IllegalAccessException();
    }

    static {
        AstralPlus.nothing_to_see_here[0] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u20ac\u00e2\u00a1\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[1] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00b6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u201e\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[2] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u00bf\u00e2\u00a0\u0178\u00e2\u00a0\u203a\u00e2\u00a0\u00bb\u00e2\u00a3\u00bf\u00e2\u00a0\u2020\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[3] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2020\u00e2\u00a3\u20ac\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a0\u201a\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[4] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a0\u00bb\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u2026\u00e2\u00a0\u203a\u00e2\u00a0\u2039\u00e2\u00a0\u02c6\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[5] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u02dc\u00e2\u00a2\u00bc\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u0192\u00e2\u00a0\u00a0\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[6] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u0178\u00e2\u00a1\u00bf\u00e2\u00a0\u0192\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[7] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u203a\u00e2\u00a3\u203a\u00e2\u00a3\u00ab\u00e2\u00a1\u201e\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00a6\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[8] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00a0\u00e2\u00a3\u00b4\u00e2\u00a3\u00be\u00e2\u00a1\u2020\u00e2\u00a0\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u00b7\u00e2\u00a0\u201a\u00e2\u00a0\u00a8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b6\u00e2\u00a3\u00a6\u00e2\u00a3\u00a4\u00e2\u00a3\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[9] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00a4\u00e2\u00a3\u00be\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a1\u00bf\u00e2\u00a0\u2039\u00e2\u00a0\ufffd\u00e2\u00a2\u20ac\u00e2\u00a1\u00b6\u00e2\u00a0\u00aa\u00e2\u00a3\u2030\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[10] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\ufffd\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00a6\u00e2\u00a1\u2122\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\ufffd\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[11] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u02c6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u2021\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00b7\u00e2\u00a3\u00a6\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[12] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00a0\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u2021\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[13] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u201e\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[14] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u00b8\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[15] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00a0\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a1\u00bf\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[16] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a0\u0192\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        AstralPlus.nothing_to_see_here[17] = "\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a2\u00b9\u00e2\u00a3\u00bf\u00e2\u00a3\u00b5\u00e2\u00a3\u00be\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00bf\u00e2\u00a3\u00af\u00e2\u00a1\ufffd\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac\u00e2\u00a0\u20ac";
        int n = 0x623C6B59 ^ (0x308550E4 ^ ofus.get((String)"rsftdrwujjegcbdu"));
        int n2 = new Random(7005990667439658748L).nextInt();
        hogMZvQTRW = 0xC5632861 ^ n2;
    }

    private static String thisIsAInsaneEncryptionMethod(byte[] byArray, int n) {
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        int n12;
        int n13;
        int n14;
        int n15;
        int n16;
        int n17;
        int n18;
        String string = Integer.toString(n);
        byte[] byArray2 = string.getBytes();
        int n19 = 16;
        byte[] byArray3 = new byte[n19];
        int n20 = 0;
        byArray3[n20] = n18 = 24;
        int n21 = 1;
        byArray3[n21] = n17 = 14;
        int n22 = 2;
        byArray3[n22] = n16 = 8;
        int n23 = 3;
        byArray3[n23] = n15 = 71;
        int n24 = 4;
        byArray3[n24] = n14 = 98;
        int n25 = 5;
        byArray3[n25] = n13 = 30;
        int n26 = 6;
        byArray3[n26] = n12 = 109;
        int n27 = 7;
        byArray3[n27] = n11 = 38;
        int n28 = 8;
        byArray3[n28] = n10 = 13;
        int n29 = 9;
        byArray3[n29] = n9 = 82;
        int n30 = 10;
        byArray3[n30] = n8 = 121;
        int n31 = 11;
        byArray3[n31] = n7 = 37;
        int n32 = 12;
        byArray3[n32] = n6 = 40;
        int n33 = 13;
        byArray3[n33] = n5 = 5;
        int n34 = 14;
        byArray3[n34] = n4 = 64;
        int n35 = 15;
        byArray3[n35] = n3 = 90;
        for (int i = 0; i < (n2 = byArray.length); ++i) {
            byte by;
            byte by2;
            byte by3 = byArray[i];
            int n36 = byArray2.length;
            int n37 = i % n36;
            byte by4 = byArray2[n37];
            int n38 = by3 ^ by4;
            byArray[i] = by2 = (byte)n38;
            byte by5 = byArray[i];
            int n39 = byArray3.length;
            int n40 = i % n39;
            byte by6 = byArray3[n40];
            int n41 = by5 ^ by6;
            byArray[i] = by = (byte)n41;
        }
        Charset charset = StandardCharsets.UTF_16;
        String string2 = new String(byArray, charset);
        return string2;
    }

    private static byte[] miwmkirbmeqzxip() {
        return new byte[]{-44, -64, 57, 7, 83, 66, 94, 116, 56, 26, 75, 123, 25, 92};
    }

    private static byte[] nmmoelleavwiyjh() {
        return new byte[]{-43, -64, 56, 2, 83, 69, 88, 123, 63, 17, 72, 96, 30, 68, 115, 8, 44, 78, 59, 23, 82, 76, 92, 112};
    }

    private static byte[] rmflfoynstpruwg() {
        return new byte[]{-41, -57, 60, 24, 80, 79, 94, 123, 62, 6, 72, 97, 28, 92, 114, 26, 43, 82, 59, 4};
    }

    private static byte[] mhgekypfgfcjtzp() {
        return new byte[]{-41, -57, 56, 23, 85, 95};
    }

    private static byte[] vcivdfyqtilbigs() {
        return new byte[]{-33, -59, 49, 26, 87, 67, 91, 120};
    }

    private static byte[] lyuevsudgftedwt() {
        return new byte[]{-45, -64, 56, 21, 86, 65, 95, 117};
    }

    private static byte[] qgsbzbmppfvpbzd() {
        return new byte[]{-41, -60, 49, 0, 84, 66, 91, 116};
    }

    private static byte[] ocmfunlpsvxaejk() {
        return new byte[0];
    }

    private static byte[] slezuvhykrococn() {
        return new byte[]{-44, -63, 48, 9, 91, 67, 95, 112, 62, 21, 75, 122, 16, 82};
    }

    private static byte[] qmewkctiywiazpl() {
        return new byte[]{-34, -60, 49, 3, 82, 71, 93, 121, 62, 26, 76, 105, 31, 69, 113, 13, 46, 79, 48, 19, 91, 77, 93, 114};
    }

    private static byte[] vyttbcxwmxoajud() {
        return new byte[]{-44, -63, 63, 21, 81, 77, 92, 123, 52, 6, 75, 103, 31, 81, 115, 24, 41, 82, 49, 4};
    }

    private static byte[] okstuiadokgnutb() {
        return new byte[]{-48, -61, 60, 19, 81, 64, 88, 115};
    }

    private static byte[] kihwcssiczmhgvp() {
        return new byte[]{-47, -61, 61, 0, 84, 79, 92, 119};
    }

    private static byte[] azeyqigbwnhufbp() {
        return new byte[]{-47, -62, 49, 7, 87, 91, 94, 117};
    }

    private static byte[] wkcuxqbytdfjqdz() {
        return new byte[]{-34, -57, 49, 27, 81, 87};
    }

    private static byte[] uiwuaorprqxvtzf() {
        return new byte[]{-43, -58, 48, 4, 82, 79, 89, 126, 56, 17, 78, 66, 24, 81, 121, 7, 41, 86, 59, 25, 90, 64, 93, 118, 57, 16, 76, 126, 31, 88, 112, 24};
    }

    private static byte[] nslpinxuxcwcnoz() {
        return new byte[]{-41, -62, 56, 3, 85, 68, 93, 118, 53, 21, 72, 73, 24, 72, 119, 13, 40, 71, 48, 24, 83, 84, 93, 99};
    }

    private static byte[] zvnwfggzuwregpf() {
        return new byte[]{-41, -60, 48, 5, 84, 71, 91, 127, 52, 20, 72, 79, 16, 68, 118, 30, 46, 77, 49, 16, 83, 95};
    }

    private static byte[] ctrpduhrcbklazu() {
        return new byte[]{-41, -59, 62, 2, 91, 69, 88, 119, 57, 23, 72, 78, 30, 64, 121, 12, 45, 88, 60, 6, 83, 67, 91, 126, 52, 4, 76, 113};
    }

    private static byte[] okenuahnosfybbt() {
        return new byte[]{-47, -56, 61, 0, 81, 71, 91, 127, 61, 21, 64, 79, 28, 76, 113, 9, 40, 78, 63, 22, 87, 83, 94, 101};
    }

    private static byte[] pwmucdwevmjbdro() {
        return new byte[]{-34, -64, 62, 1, 81, 70, 91, 124, 53, 19, 79, 79, 27, 70, 118, 12, 32, 81, 62, 6, 81, 71, 91, 123, 53, 6, 79, 124};
    }

    private static byte[] muqnivhdkbiwktw() {
        return new byte[]{-41, -57, 63, 0, 86, 70, 95, 118, 58, 20, 72, 76, 31, 85, 116, 3, 42, 90, 63, 24, 83, 70, 90, 123, 57, 17, 75, 116, 31, 86, 113, 30};
    }

    private static byte[] jnopxpbhyjnblct() {
        return new byte[]{-46, -59, 60, 1, 81, 65, 90, 122, 63, 22, 77, 78, 29, 70, 119, 24, 45, 79, 60, 18, 86, 95};
    }

    private static byte[] suybvnygxhhhavc() {
        return new byte[]{-41, -56, 49, 19, 91, 89, 93, 97, 59, 10, 72, 108, 17, 89, 121, 9, 40, 82, 62, 2, 83, 120, 84, 96, 52, 1, 73, 118, 30, 68, 113, 12, 33, 81, 49, 46, 82, 64, 91, 118, 60, 24, 64, 113, 17, 65, 112, 55};
    }

    private static byte[] pxagyrukkyrdkdq() {
        return new byte[]{-43, -56, 62, 19, 91, 90, 85, 107, 62, 8, 64, 99, 25, 81, 117, 7, 32, 83, 59, 10, 84, 112, 84, 116, 53, 15, 74, 120, 17, 86, 113, 17, 45, 87, 48, 0, 81, 72, 91, 101, 52, 56};
    }

    private static byte[] rpwkkhuxshhqiec() {
        return new byte[0];
    }

    private static int mjbkytyqbyxonfdc(int n, int n2) {
        return n2 ^ n;
    }
}
