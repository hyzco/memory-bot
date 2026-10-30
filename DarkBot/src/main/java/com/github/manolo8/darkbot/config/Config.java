package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.config.actions.Condition;
import com.github.manolo8.darkbot.config.types.suppliers.BrowserApi;
import com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag;
import com.github.manolo8.darkbot.config.types.suppliers.LanguageSupplier;
import com.github.manolo8.darkbot.config.types.suppliers.ModuleSupplier;
import com.github.manolo8.darkbot.config.types.suppliers.PetGears;
import com.github.manolo8.darkbot.config.types.suppliers.ReviveLocation;
import com.github.manolo8.darkbot.config.utils.ItemUtils;
import com.github.manolo8.darkbot.core.api.GameAPI;
import com.github.manolo8.darkbot.core.manager.StarManager;
import com.github.manolo8.darkbot.core.utils.Lazy;
import com.github.manolo8.darkbot.gui.MainGui;
import com.github.manolo8.darkbot.gui.tree.editors.CharacterEditor;
import com.github.manolo8.darkbot.gui.tree.utils.NpcTableModel;
import com.github.manolo8.darkbot.gui.tree.utils.TableHelpers;
import com.github.manolo8.darkbot.utils.OSUtil;
import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Dropdown;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;
import eu.darkbot.api.config.annotations.Percentage;
import eu.darkbot.api.config.annotations.Table;
import eu.darkbot.api.config.annotations.Tag;
import eu.darkbot.api.config.annotations.Visibility;
import eu.darkbot.api.config.annotations.Visibility.Level;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.game.items.ItemCategory;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.game.other.GameMap;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.shared.modules.LootCollectorModule;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.BOOSTER_AREA;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.GROUP_AREA;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.GROUP_NAMES;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.HERO_NAME;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.HP_SHIELD_NUM;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.SHOW_PET;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.STATS_AREA;
import static com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag.ZONES;

public class Config implements eu.darkbot.api.config.legacy.Config {

    // Defined map areas
    public Map<Integer, ZoneInfo> AVOIDED = new HashMap<>();
    public Map<Integer, ZoneInfo> PREFERRED = new HashMap<>();
    public Map<Integer, Set<SafetyInfo>> SAFETY = new HashMap<>();
    public transient Lazy<SafetyInfo> ADDED_SAFETY = new Lazy.NoCache<>();

    // Player list & resolver
    public Map<Integer, PlayerInfo> PLAYER_INFOS = new HashMap<>();
    public Set<PlayerTag> PLAYER_TAGS = new HashSet<>();
    public transient Lazy<Integer> PLAYER_UPDATED = new Lazy.NoCache<>();
    public Queue<UnresolvedPlayer> UNRESOLVED = new LinkedList<>();


    // Plugin custom configuration objects
    public Map<String, Object> CUSTOM_CONFIGS = new HashMap<>();
    public Map<String, PluginConfig> PLUGIN_INFOS = new HashMap<>();

    public transient boolean changed;
    public transient long changedAt;

    public @Option General GENERAL = new General();
    public static class General {
        @Option @Dropdown(options = ModuleSupplier.class)
        public String CURRENT_MODULE = LootCollectorModule.class.getCanonicalName();
        public @Option @Dropdown(options = StarManager.MapOptions.class) int WORKING_MAP = 26;
        public @Option ShipConfig OFFENSIVE = new ShipConfig(1, '8');
        public @Option ShipConfig ROAM = new ShipConfig(1, '9');
        public @Option ShipConfig RUN = new ShipConfig(2, '9');
        @Option("config.general.hak_clan_tutucu")
        public HakClanTutucu HAK_CLAN_TUTUCU = new HakClanTutucu();

        public static class HakClanTutucu {
            @Option("config.general.hak_clan_tutucu.selector_key")
            public String SELECTOR_KEY = "L";

            @Option("config.general.hak_clan_tutucu.enable_locking")
            public boolean ENABLE_LOCKING = true;

            @Option("config.general.hak_clan_tutucu.auto_attack")
            public boolean AUTO_ATTACK = true;

            @Option("config.general.hak_clan_tutucu.target_type")
            @Dropdown
            public TargetType TARGET_TYPE = TargetType.PLAYERS;

            @Option("config.general.hak_clan_tutucu.priority")
            @Dropdown
            public Priority PRIORITY = Priority.SMART;

            @Option("config.general.hak_clan_tutucu.cycle_key")
            public String CYCLE_KEY = "K";

            @Option("config.general.hak_clan_tutucu.only_marked_npcs")
            public boolean ONLY_MARKED_NPCS = false;

            @Option("config.general.hak_clan_tutucu.auto_next_target")
            public boolean AUTO_NEXT_TARGET = false;

            @Option("config.general.hak_clan_tutucu.max_lock_distance")
            @Number(min = 200, max = 3000, step = 50)
            public int MAX_LOCK_DISTANCE = 1100;

            @Option("config.general.hak_clan_tutucu.lock_on_nearest")
            public boolean LOCK_ON_NEAREST = true;

            @Option("config.general.hak_clan_tutucu.pin_key")
            public String PIN_KEY = "";

            @Option("config.general.hak_clan_tutucu.auto_retaliate")
            public boolean AUTO_RETALIATE = false;

            @Option("config.general.hak_clan_tutucu.lock_friends")
            public boolean LOCK_FRIENDS = false;

            @Option("config.general.hak_clan_tutucu.auto_pin_manual")
            public boolean AUTO_PIN_MANUAL = false;

            @Option("config.general.hak_clan_tutucu.vs_pilot")
            @Dropdown
            public VsPilot VS_PILOT = VsPilot.CLASSIC;

            @Option("config.general.hak_clan_tutucu.vs_fly")
            public boolean VS_FLY = true;

            @Option("config.general.hak_clan_tutucu.vs_fly_style")
            @Dropdown
            public FlyStyle VS_FLY_STYLE = FlyStyle.ORBIT;

            @Option("config.general.hak_clan_tutucu.vs_fight_range")
            @Number(min = 300, max = 900, step = 10)
            public int VS_FIGHT_RANGE = 560;

            @Option("config.general.hak_clan_tutucu.vs_flee")
            public boolean VS_FLEE = true;

            @Option("config.general.hak_clan_tutucu.vs_rockets")
            public boolean VS_ROCKETS = true;

            @Option("config.general.hak_clan_tutucu.vs_ammo_learn")
            public boolean VS_AMMO_LEARN = true;

            @Option("config.general.hak_clan_tutucu.vs_abandon_runners")
            public boolean VS_ABANDON_RUNNERS = true;

            @Option("config.general.hak_clan_tutucu.vs_profile")
            public boolean VS_PROFILE = true;

            @Option("config.general.hak_clan_tutucu.vs_save_ammo")
            public boolean VS_SAVE_AMMO = true;

            @Option("config.general.hak_clan_tutucu.vs_emp_on_cloak")
            public boolean VS_EMP_ON_CLOAK = true;

            @Option("config.general.hak_clan_tutucu.vs_emp_on_ish")
            public boolean VS_EMP_ON_ISH = false;

            @Option("config.general.hak_clan_tutucu.vs_assist_safety")
            public boolean VS_ASSIST_SAFETY = true;

            @Option("config.general.hak_clan_tutucu.pin_pet_key")
            public String PIN_PET_KEY = "";

            @Option("config.general.hak_clan_tutucu.assist_key")
            public String ASSIST_KEY = "";

            @Option("config.general.hak_clan_tutucu.vs_mode")
            public boolean VS_MODE = false;

            @Option("config.general.hak_clan_tutucu.vs_priority")
            @Dropdown
            public VsPriority VS_PRIORITY = VsPriority.BALANCED;

            @Option("config.general.hak_clan_tutucu.vs_switch_delay")
            @Number(min = 300, max = 6000, step = 100)
            public int VS_SWITCH_DELAY = 1500;

            @Option("config.general.hak_clan_tutucu.vs_macro_enabled")
            public boolean VS_MACRO_ENABLED = true;

            @Option("config.general.hak_clan_tutucu.vs_macro_trigger")
            @Dropdown
            public MacroTrigger VS_MACRO_TRIGGER = MacroTrigger.FIRST_HIT;

            @Option("config.general.hak_clan_tutucu.vs_macro_on_players")
            public boolean VS_MACRO_ON_PLAYERS = true;

            @Option("config.general.hak_clan_tutucu.vs_macro_on_npcs")
            public boolean VS_MACRO_ON_NPCS = false;

            @Option("config.general.hak_clan_tutucu.vs_macro_on_friends")
            public boolean VS_MACRO_ON_FRIENDS = false;

            @Option("config.general.hak_clan_tutucu.vs_macro_below_hp")
            @Number(min = 10, max = 100, step = 5)
            public int VS_MACRO_BELOW_HP = 100;

            @Option("config.general.hak_clan_tutucu.vs_macro")
            public String VS_MACRO = "ISH_01@0; EMP_01@3000";

            @Option("config.general.hak_clan_tutucu.vs_trigger_distance")
            @Number(min = 300, max = 2500, step = 50)
            public int VS_TRIGGER_DISTANCE = 900;

            @Option("config.general.hak_clan_tutucu.vs_pet_enabled")
            public boolean VS_PET_ENABLED = false;

            @Option("config.general.hak_clan_tutucu.vs_pet_gear")
            @Dropdown
            public eu.darkbot.api.game.enums.PetGear VS_PET_GEAR = eu.darkbot.api.game.enums.PetGear.HP_LINK;

            @Option("config.general.hak_clan_tutucu.vs_auto_defense")
            public boolean VS_AUTO_DEFENSE = false;

            @Option("config.general.hak_clan_tutucu.vs_defence_below")
            @Number(min = 10, max = 90, step = 5)
            public int VS_DEFENCE_BELOW = 45;

            @Option("config.general.hak_clan_tutucu.vs_auto_setup")
            public boolean VS_AUTO_SETUP = false;

            @Option("config.general.hak_clan_tutucu.vs_advisor")
            public boolean VS_ADVISOR = true;

            /** Battle preparation chosen in the VS strategy dialog; null = let the advisor decide / leave as is. */
            public eu.darkbot.api.game.items.SelectableItem.Formation VS_PREP_FORMATION = null;
            public eu.darkbot.api.game.items.SelectableItem.Laser VS_PREP_LASER = null;
            public eu.darkbot.api.game.items.SelectableItem.Rocket VS_PREP_ROCKET = null;

            @Option("config.general.hak_clan_tutucu.vs_ammo_mix")
            public boolean VS_AMMO_MIX = false;

            @Option("config.general.hak_clan_tutucu.vs_ammo_mix_mode")
            @Dropdown
            public AmmoMixMode VS_AMMO_MIX_MODE = AmmoMixMode.ALTERNATE;

            @Option("config.general.hak_clan_tutucu.vs_ammo_a")
            @Dropdown
            public eu.darkbot.api.game.items.SelectableItem.Laser VS_AMMO_A = eu.darkbot.api.game.items.SelectableItem.Laser.UCB_100;

            @Option("config.general.hak_clan_tutucu.vs_ammo_b")
            @Dropdown
            public eu.darkbot.api.game.items.SelectableItem.Laser VS_AMMO_B = eu.darkbot.api.game.items.SelectableItem.Laser.RSB_75;

            @Option("config.general.hak_clan_tutucu.vs_ammo_a_seconds")
            @Number(min = 1, max = 30, step = 1)
            public int VS_AMMO_A_SECONDS = 4;

            @Option("config.general.hak_clan_tutucu.vs_ammo_b_seconds")
            @Number(min = 1, max = 30, step = 1)
            public int VS_AMMO_B_SECONDS = 2;

            @Option("config.general.hak_clan_tutucu.vs_ammo_shield_below")
            @Number(min = 5, max = 95, step = 5)
            public int VS_AMMO_SHIELD_BELOW = 30;

            /** Mixing only makes sense in VS mode with two different ammos picked. */
            public boolean VS_MIX_ACTIVE() {
                return VS_MODE && VS_AMMO_MIX && VS_AMMO_A != null && VS_AMMO_B != null && VS_AMMO_A != VS_AMMO_B;
            }

            public enum FlyStyle {
                ORBIT("circle the target"), HOLD_RANGE("hold range (back off when too close)"), STAND("stand still once in range");

                private final String label;

                FlyStyle(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum VsPilot {
                CLASSIC("classic (macro + defence rules)"), AUTO("AI pilot - simulates and flies it"),
                ASSISTED("manual fight with assistance");

                private final String label;

                VsPilot(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum AmmoMixMode {
                ALTERNATE("alternate on a timer (A, then B, ...)"), BY_SHIELD("B when the enemy's shield is low, else A");

                private final String label;

                AmmoMixMode(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum VsPriority {
                BALANCED("balanced (threat + low life)"), THREAT("most damage to me"), LEAST_LIFE("least life");

                private final String label;

                VsPriority(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum MacroTrigger {
                ENEMY_APPROACH("enemy closing in and aiming at me"), FIRST_HIT("I am under attack"), ON_LOCK("when I lock a target");

                private final String label;

                MacroTrigger(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum Priority {
                NEAREST("nearest"), SMART("smart (attackers, weak, near)"), LOWEST_HEALTH("lowest health");

                private final String label;

                Priority(String label) {
                    this.label = label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }

            public enum TargetType {
                PLAYERS("players"), NPCS("NPCs"), BOTH("players + NPCs");

                private final String label;

                TargetType(String label) {
                    this.label = label;
                }

                public String label() {
                    return label;
                }

                @Override
                public String toString() {
                    return label;
                }
            }
        }

        public @Option Safety SAFETY = new Safety();
        public static class Safety {
            public @Option PercentRange REPAIR_HP_RANGE = new PercentRange(0.4, 0.95);
            public @Option @Percentage double REPAIR_HP_NO_NPC = 0.5;
            public @Option @Percentage double REPAIR_TO_SHIELD = 1;
            public @Option ShipConfig REPAIR = new ShipConfig(1, '9');
            public @Option @Number(min = 1, max = 9999) @Number.Disabled(value = -1, def = 10) int MAX_DEATHS = 10;
            @Deprecated
            public long REVIVE_LOCATION = 1L;
            public @Option("config.general.safety.revive_location") @Dropdown ReviveLocation REVIVE = ReviveLocation.BASE;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(min = 5, max = 60, step = 10) int WAIT_BEFORE_REVIVE = 5;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(min = 3, max = 15 * 60, step = 10) int WAIT_AFTER_REVIVE = 90;
            public @Option @Visibility(Level.INTERMEDIATE)
            @Number(min = 0, max = 99999, step = 50) @Number.Disabled(value = 0, def = 500) int INSTANT_REPAIR = 0;
        }

        public @Option Running RUNNING = new Running();
        public static class Running {
            public @Option boolean RUN_FROM_ENEMIES = true;
            public @Option @Tag(Tag.Default.NONE) PlayerTag ENEMIES_TAG = null;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(max = 24 * 60 * 60, step = 300) int REMEMBER_ENEMIES_FOR = 300;
            public @Option boolean RUN_FROM_ENEMIES_SIGHT = false;
            public @Option boolean STOP_RUNNING_NO_SIGHT = true;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(min = 500, max = 20000, step = 500) int MAX_SIGHT_DISTANCE = 4000;
            public @Option Character SHIP_ABILITY;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(max = 20000, step = 500) int SHIP_ABILITY_MIN = 1500;
            public @Option @Visibility(Level.INTERMEDIATE) @Number(max = 20000, step = 500) int RUN_FURTHEST_PORT = 1500;
        }

        public @Option @Visibility(Level.INTERMEDIATE) Roaming ROAMING = new Roaming();
        public static class Roaming {
            public @Option boolean KEEP = true;
            public @Option boolean SEQUENTIAL = false;
            public @Option boolean ONLY_KILL_PREFERRED = false;
            public @Option boolean ENEMY_CBS_INVISIBLE = false;
        }
    }

    public @Option Collect COLLECT = new Collect();
    public static class Collect {
        public @Option boolean STAY_AWAY_FROM_ENEMIES;
        public @Option boolean AUTO_CLOACK;
        public @Option Character AUTO_CLOACK_KEY;
        public @Option @Number(max = 10000, step = 50) int RADIUS = 400;
        public @Option boolean IGNORE_CONTESTED_BOXES = true;

        @Option
        @Table(decorator = TableHelpers.BoxInfoDecorator.class)
        public Map<String, BoxInfo> BOX_INFOS = new HashMap<>();
        public transient Lazy<String> ADDED_BOX = new Lazy.NoCache<>();
    }

    public @Option Loot LOOT = new Loot();
    public static class Loot {
        public @Option Sab SAB = new Sab();
        public @Option Rsb RSB = new Rsb();
        public static class Sab {
            public @Option boolean ENABLED = false;
            public @Option Character KEY = '2';
            public @Option @Percentage double PERCENT = 0.8;
            public @Option @Number(min = 500, max = 1_000_000, step = 1000) int NPC_AMOUNT = 12000;
            public @Option @Visibility(Level.ADVANCED) Condition CONDITION;
        }
        public static class Rsb {
            public @Option boolean ENABLED = false;
            public @Option Character KEY = '3';
            public @Deprecated int AMMO_REFRESH = 3500;
        }
        public @Option Character AMMO_KEY = '1';
        public @Option @Visibility(Level.INTERMEDIATE) Character SHIP_ABILITY;
        public @Option @Visibility(Level.INTERMEDIATE) @Number(min = 50_000, max = 5_000_000, step = 50_000) int SHIP_ABILITY_MIN = 150_000;
        public @Option @Visibility(Level.ADVANCED) @Number(max = 10, step = 1) int MAX_CIRCLE_ITERATIONS = 5;
        public @Option boolean RUN_CONFIG_IN_CIRCLE = true;

        public @Option boolean GROUP_NPCS = true;
        @Table(controls = {Table.Control.SEARCH, Table.Control.CUSTOM, Table.Control.ADD, Table.Control.REMOVE},
                customControls = TableHelpers.MapPickerBuilder.class,
                customModel = NpcTableModel.class,
                decorator = TableHelpers.NpcTableDecorator.class)
        public @Option Map<String, NpcInfo> NPC_INFOS = new HashMap<>();
        public transient Lazy<String> MODIFIED_NPC = new Lazy.NoCache<>();

        public @Option @Visibility(Level.INTERMEDIATE) @Number(min = 1000, max = 20000, step = 500) int NPC_DISTANCE_IGNORE = 3000;
    }

    public @Option PetSettings PET = new PetSettings();
    public static class PetSettings {
        public @Option boolean ENABLED = false;
        public @Option @Dropdown(options = PetGears.class) PetGear MODULE_ID = PetGear.PASSIVE;
    }

    public @Option GroupSettings GROUP = new GroupSettings();
    public static class GroupSettings {
        public @Option boolean ACCEPT_INVITES = false;
        public @Option @Tag(Tag.Default.ALL) PlayerTag WHITELIST_TAG = null;
        public @Option @Tag(Tag.Default.NONE) PlayerTag INVITE_TAG = null;
        public @Option boolean OPEN_INVITES = false;
        public @Option boolean BLOCK_INVITES = false;
        public @Option boolean LEAVE_NO_WHITELISTED = false;
        public @Option boolean KICK_NO_INVITED = false;
        //public @Option @Tag(TagDefault.NONE) PlayerTag KICK_TAG = null;
    }

    public @Option Miscellaneous MISCELLANEOUS = new Miscellaneous();
    public static class Miscellaneous {
        public @Option boolean REFRESH_AFTER_REVIVE = false;
        public @Option @Number(max = 60 * 12, step = 10) int REFRESH_TIME = 60;
        public @Option @Number(max = 60 * 12, step = 10) int PAUSE_FOR = 0;
        public @Option boolean RESET_REFRESH = true;
        public @Option boolean SOLVE_BACKPAGE_CAPTCHA = false;
        public @Option @Visibility(Level.INTERMEDIATE) boolean UPDATE_STATS_WHILE_PAUSED = true;
        public @Option @Visibility(Level.INTERMEDIATE) @Percentage double DRONE_REPAIR_PERCENTAGE = 0.9;
        public @Option @Visibility(Level.INTERMEDIATE) boolean HONOR_LOST_EXACT = true;
        public @Option @Visibility(Level.INTERMEDIATE) boolean LOG_CHAT = false;
        public @Option @Visibility(Level.INTERMEDIATE) boolean LOG_DEATHS = false;
        public @Option @Visibility(Level.INTERMEDIATE) boolean AVOID_MINES = true;
        public @Option @Visibility(Level.INTERMEDIATE) boolean AVOID_CBS = true;
        public @Option @Visibility(Level.INTERMEDIATE) boolean AVOID_RADIATION = true;
        public @Option @Visibility(Level.INTERMEDIATE) boolean USERNAME_ON_TITLE = false;
        public @Option @Visibility(Level.ADVANCED) boolean AUTO_REFINE = false;
    }

    public @Option BotSettings BOT_SETTINGS = new BotSettings();
    public static class BotSettings {
        public @Option BotGui BOT_GUI = new BotGui();
        public static class BotGui {
            @Option @Dropdown(options = LanguageSupplier.class)
            public Locale LOCALE = new Locale(Locale.getDefault().getLanguage());

            public @Option @Visibility(Level.INTERMEDIATE) boolean CONFIRM_EXIT = true;
            public @Option @Visibility(Level.INTERMEDIATE) boolean SAVE_GUI_POS = false;
            public @Option @Visibility(Level.ADVANCED) @Number(min = 1, max = 20, step = 1) int BUTTON_SIZE = 4;

            public boolean ALWAYS_ON_TOP = true; // No @Option. Edited via button
            public WindowPosition MAIN_GUI_WINDOW = new WindowPosition();
            public WindowPosition CONFIG_GUI_WINDOW = new WindowPosition();

            public Visibility.Level CONFIG_LEVEL = Level.BASIC;

            public static class WindowPosition {
                // x and y refer to top left coordinates of window
                public int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE;
                public int width = MainGui.DEFAULT_WIDTH, height = MainGui.DEFAULT_HEIGHT;
            }
        }

        public @Option @Visibility(Level.ADVANCED) APIConfig API_CONFIG = new APIConfig();
        public static class APIConfig {
            public @Option @Dropdown BrowserApi BROWSER_API = OSUtil.getDefaultAPI();
            public @Option boolean FULLY_HIDE_API = false;
            public @Option boolean FORCE_GAME_LANGUAGE = false;
            public @Option boolean ENFORCE_HW_ACCEL = true;
            public @Option boolean USE_3D = false;
            public @Option boolean USE_PROXY = false;
            public @Option boolean CLEAR_CACHE_ON_STUCK = true;

            public int width = 1280;
            public int height = 800;

            public boolean attachToBot = false;
            public GameAPI.Handler.GameQuality gameQuality = GameAPI.Handler.GameQuality.LOW;
            public transient int transparency = 100, volume = 100;

            public @Option @Table @Visibility(Level.DEVELOPER) Map<String, PatternInfo> BLOCK_PATTERNS = new HashMap<>();

            @Configuration("config.bot_settings.api_config.block_patterns")
            public static class PatternInfo {
                public String regex = "";
                public String filePath = "";
                public boolean enable = true;
            }
        }

        public @Option MapDisplay MAP_DISPLAY = new MapDisplay();
        public static class MapDisplay {
            @Option @Dropdown(multi = true)
            public Set<DisplayFlag> TOGGLE = EnumSet.of(
                    HERO_NAME, HP_SHIELD_NUM, ZONES, STATS_AREA, BOOSTER_AREA, GROUP_NAMES, GROUP_AREA, SHOW_PET);
            public @Option @Visibility(Level.INTERMEDIATE) @Number(max = 300, step = 1) int TRAIL_LENGTH = 15;
            public @Option @Visibility(Level.INTERMEDIATE) boolean ROUND_ENTITIES = false;
            public @Option @Visibility(Level.INTERMEDIATE) @Percentage double MAP_ZOOM = 1;
            public @Option @Visibility(Level.ADVANCED) @Number(min = 1, step = 1, max = 25) int REFRESH_DELAY = 1;
            public @Option @Visibility(Level.INTERMEDIATE) boolean MAP_START_STOP = false;
            public @Option boolean NEON_THEME = true;
            public @Option("colors") @Visibility(Level.ADVANCED) ColorScheme cs = new ColorScheme();
        }

        public @Option @Visibility(Level.INTERMEDIATE) CustomBackground CUSTOM_BACKGROUND = new CustomBackground();
        public static class CustomBackground {
            public @Option boolean ENABLED = false;
            public @Option boolean USE_GAME_BACKGROUND = false;
            public @Option @Percentage double OPACITY = 0.3f;
            public @Option ImageWrapper IMAGE = new ImageWrapper();
        }

        public @Option Performance PERFORMANCE = new Performance();
        public static class Performance {
            public @Option boolean ALWAYS_ENABLED = true;
            public @Option("config.bot_settings.api_config.disable_render") boolean DISABLE_RENDER = false;
            public @Option("config.bot_settings.api_config.max_fps")
            @Number(min = 1, max = 60) @Number.Disabled(value = 0, def = 15) int MAX_FPS = 0;

            public @Option("config.bot_settings.other.min_tick") @Visibility(Level.ADVANCED)
            @Number(min = 10, max = 250) @Number.Disabled(value = 15, def = 15) int MIN_TICK = 15;
        }

        public @Option @Visibility(Level.INTERMEDIATE) Other OTHER = new Other();
        public static class Other {
            public @Option boolean SHOW_INSTRUCTIONS = true;
            public @Option boolean DISABLE_MASTER_PASSWORD = false;
            public @Option boolean AUTO_LOGIN = false;
            /** true: game window floats over the bot window and follows it (input stays responsive);
             *  false: game window is reparented into the bot window (SetParent). */
            public @Option boolean DOCK_GAME_WINDOW = true;
            public String LAST_LOGIN_USER = null;
            public @Option boolean ALWAYS_SHOW_CAPTCHA = false;
            public @Option @Number(min = 10, max = 300) int ZONE_RESOLUTION = 30;
            public @Option @Visibility(Level.ADVANCED) boolean DEV_STUFF = false;
        }
    }

    public /*@Option("Extra actions")*/ ExtraActions EXTRA = new ExtraActions();
    public static class ExtraActions {
        // Dummy testing condition
        public @Option Condition CONDITION;

        public @Option @Table Map<String, ActionInfo> ACTION_INFOS = new HashMap<>();
        public transient Lazy<String> MODIFIED_ACTIONS = new Lazy.NoCache<>();
    }

    public static class ShipConfig implements LegacyShipMode {
        public int CONFIG = 1;
        public Character FORMATION;

        public ShipConfig() {}
        public ShipConfig(int CONFIG, Character FORMATION) {
            this.CONFIG = CONFIG;
            this.FORMATION = FORMATION;
        }

        @Override
        public HeroAPI.Configuration getConfiguration() {
            return HeroAPI.Configuration.of(CONFIG);
        }

        @Override
        public SelectableItem.Formation getFormation() {
            return ItemUtils.findAssociatedItem(ItemCategory.DRONE_FORMATIONS, FORMATION)
                    .map(it -> SelectableItem.Formation.of(it.id))
                    .orElse(SelectableItem.Formation.STANDARD);
        }

        @Override
        public String toString() {
            return "Config: " + CONFIG + "   Formation: " + CharacterEditor.getDisplay(FORMATION);
        }

        @Override
        public @Nullable Character getLegacyFormation() {
            return FORMATION;
        }
    }

    public static class PercentRange implements eu.darkbot.api.config.types.PercentRange {
        public double min, max;

        public PercentRange() {}
        public PercentRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public double getMin() {
            return min;
        }

        @Override
        public double getMax() {
            return max;
        }

        @Override
        public String toString() {
            return Math.round(min * 100) + "%-" + Math.round(max * 100) + "%";
        }
    }

    @Override
    public eu.darkbot.api.config.types.BoxInfo getOrCreateBoxInfo(String name) {
        return ConfigEntity.INSTANCE.getOrCreateBoxInfo(name);
    }

    @Override
    public eu.darkbot.api.config.types.NpcInfo getOrCreateNpcInfo(String name) {
        return ConfigEntity.INSTANCE.getOrCreateNpcInfo(name);
    }

    @Override
    public Collection<? extends eu.darkbot.api.config.types.SafetyInfo> getSafeties(GameMap gameMap) {
        return SAFETY.getOrDefault(gameMap.getId(), Collections.emptySet());
    }

    @Override
    public eu.darkbot.api.config.types.ZoneInfo getPreferredZone(GameMap gameMap) {
        return PREFERRED.computeIfAbsent(gameMap.getId(), id -> new ZoneInfo(BOT_SETTINGS.OTHER.ZONE_RESOLUTION));
    }

    @Override
    public eu.darkbot.api.config.types.ZoneInfo getAvoidedZone(GameMap gameMap) {
        return AVOIDED.computeIfAbsent(gameMap.getId(), id -> new ZoneInfo(BOT_SETTINGS.OTHER.ZONE_RESOLUTION));
    }

    @Override
    public Map<Integer, ? extends eu.darkbot.api.config.types.PlayerInfo> getPlayerInfos() {
        return PLAYER_INFOS;
    }

    @Override
    public eu.darkbot.api.config.types.PlayerInfo getPlayerInfo(int id) {
        return PLAYER_INFOS.computeIfAbsent(id, i -> new PlayerInfo(null, i, null));
    }

    @Override
    public void refreshPlayerList() {
        PLAYER_UPDATED.send(null);
    }

}
