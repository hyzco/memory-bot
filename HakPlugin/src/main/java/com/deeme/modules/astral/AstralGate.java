package com.deeme.modules.astral;

import eu.darkbot.api.PluginAPI;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.types.ShipMode;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.extensions.Module;
import eu.darkbot.api.game.entities.Entity;
import eu.darkbot.api.game.entities.Npc;
import eu.darkbot.api.game.entities.Pet;
import eu.darkbot.api.game.entities.Player;
import eu.darkbot.api.game.entities.Portal;
import eu.darkbot.api.game.entities.Ship;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.game.enums.PortalType;
import eu.darkbot.api.game.enums.ReviveLocation;
import eu.darkbot.api.game.items.ItemFlag;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.game.other.Health;
import eu.darkbot.api.game.other.Locatable;
import eu.darkbot.api.game.other.Location;
import eu.darkbot.api.managers.AstralGateAPI;
import eu.darkbot.api.managers.AttackAPI;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.managers.GameScreenAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.HeroItemsAPI;
import eu.darkbot.api.managers.MovementAPI;
import eu.darkbot.api.managers.PetAPI;
import eu.darkbot.api.managers.RepairAPI;
import eu.darkbot.shared.utils.PortalJumper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Feature(name = "Astral Gate", description = "Automates Astral travel, ship selection, reward choice, portal choice, and combat")
public class AstralGate implements Module, Configurable<AstralConfig> {
    private static final long AMMO_CHECK_INTERVAL = 250L;
    private static final long ROCKET_CHECK_INTERVAL = 300L;
    private static final long CPU_RETRY_INTERVAL = 5_000L;
    private static final long CLICK_UNIT_MILLIS = 100L;
    private static final long ABILITY_COOLDOWN = 30_000L;
    private static final long SPECIAL_ITEM_COOLDOWN = 8_000L;
    private static final long TARGET_REEVAL_INTERVAL = 4_000L;
    private static final long STUCK_TIMEOUT = 25_000L;
    private static final long PORTAL_JUMP_TIMEOUT = 15_000L;
    private static final long FORMATION_CHANGE_COOLDOWN = 5_000L;
    private static final long SHIP_SELECTION_SETTLE_DELAY = 350L;
    private static final long SHIP_ACCEPT_SETTLE_DELAY = 500L;
    private static final long SHIP_ACCEPT_RETRY_DELAY = 300L;
    private static final long WAVE_CLEAR_GRACE_PERIOD = 4_000L;
    private static final double HP_RECOVER_THRESHOLD = 0.9;
    private static final int MAX_MODULES = 3;
    private static final int RANDOM_ITEM_BOUND = 2;
    private static final int MAX_GUI_RETRIES = 12;

    private static final double HP_CRITICAL = 0.25;
    private static final double HP_DANGER = 0.50;
    private static final double HP_SAFE = 0.80;
    private static final double SHIELD_KITE_THRESHOLD = 0.10;

    private static final List<String> WEAPON_PRIORITY = List.of(
            "caucasus",
            "pr-l",
            "lf-4-pd",
            "lf-4-hp",
            "lf-4-md",
            "lf-4",
            "lf-3",
            "sll-03",
            "sll-02"
    );

    private static final List<String> GENERATOR_PRIORITY = List.of(
            "speed_g3n-7900",
            "speed_g3n-6900",
            "speed_g3n-3310",
            "speed_g3n-3210",
            "speed_g3n-2010",
            "speed_g3n-1010",
            "shield_fs-03",
            "shield_sg3n-b03",
            "shield_fs-02",
            "shield_sg3n-b01",
            "shield_sg3n-b00",
            "shield_sg3n-a03",
            "shield_sg3n-a02",
            "shield_sg3n-a01"
    );

            private static final List<String> ASTRAL_HOSTILE_HINTS = List.of(
                "raider",
                "decoy",
                "defcom",
                "yamato",
                "spinelus",
                "lordakia",
                "mordon",
                "saimon",
                "protegit",
                "attacker",
                "invader"
            );

    private static final Map<PortalType, Integer> DEFAULT_PORTAL_PRIORITIES = buildPortalPriorities();

    private final PluginAPI api;
    private final PetAPI pet;
    private final HeroAPI hero;
    private final HeroItemsAPI heroItems;
    private final AttackAPI attack;
    private final MovementAPI movement;
    private final EntitiesAPI entities;
    private final AstralGateAPI astralApi;
    private final RepairAPI repair;
    private final PortalJumper portalJumper;
    private final AstralMediator mediator;
    private final Random random = new Random();

    private ConfigSetting<AstralConfig> configSetting;
    private AstralConfig config = new AstralConfig();

    private long nextUiActionAt;
    private long nextAmmoCheckAt;
    private long nextRocketCheckAt;
    private long nextCpuAttemptAt;
    private long nextAbilityUseAt;
    private long nextSpecialItemAt;
    private long nextTargetReEvalAt;
    private long nextFormationChangeAt;
    private long lastCombatActivityAt;
    private long portalJumpStartedAt;
    private boolean shipChosen;
    private boolean shipAccepted;
    private boolean orbitClockwise = true;
    private int guiRetryCount;
    private int runCount;
    private int deathsThisSession;
    private int lastKnownDeaths;
    private int lastCombatTargetId = -1;
    private long lastEnemySeenAt;
    private int lastRift;
    private boolean wasInAstral;
    private Portal lastJumpTarget;
    private String status = "Idle";

    public AstralGate(PluginAPI api) {
        this.api = api;
        this.pet = api.requireAPI(PetAPI.class);
        this.hero = api.requireAPI(HeroAPI.class);
        this.heroItems = api.requireAPI(HeroItemsAPI.class);
        this.attack = api.requireAPI(AttackAPI.class);
        this.movement = api.requireAPI(MovementAPI.class);
        this.entities = api.requireAPI(EntitiesAPI.class);
        this.astralApi = Optional.ofNullable(api.getAPI(AstralGateAPI.class)).orElseGet(EmptyAstralGateAPI::new);
        this.repair = api.requireAPI(RepairAPI.class);
        this.portalJumper = new PortalJumper(api);
        this.mediator = new AstralMediator(api.requireAPI(GameScreenAPI.class));
        this.lastKnownDeaths = 0;
    }

    private static class EmptyAstralGateAPI implements AstralGateAPI {
        @Override
        public int getCpuCount() {
            return 0;
        }

        @Override
        public int getCurrentScore() {
            return 0;
        }

        @Override
        public int getCurrentRift() {
            return 0;
        }

        @Override
        public int getHighScore() {
            return 0;
        }

        @Override
        public boolean allowedToEquip() {
            return false;
        }

        @Override
        public List<? extends AstralItem> getInventoryItems() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends AstralItem> getRewardsItems() {
            return Collections.emptyList();
        }
    }

    @Override
    public void setConfig(ConfigSetting<AstralConfig> configSetting) {
        this.configSetting = configSetting;
        this.config = configSetting.getValue();
        ensurePortalDefaults(this.config);
    }

    @Override
    public void onTickModule() {
        this.config = currentConfig();
        ensurePortalDefaults(this.config);

        trackDeaths();

        if (repair.isDestroyed()) {
            status = "Destroyed - waiting for respawn (deaths: " + deathsThisSession + ")";
            handleRespawn();
            return;
        }

        if (config.maxDeaths > 0 && deathsThisSession >= config.maxDeaths) {
            status = "Max deaths (" + config.maxDeaths + ") reached - stopping";
            attack.setTarget(null);
            hero.setLocalTarget(null);
            movement.stop(false);
            return;
        }

        if (!isInAstral()) {
            // Ship selection GUI can appear before entering astral (on base map)
            if (mediator.isShipSelectionGuiVisible()) {
                status = "Choosing ship (pre-entry)";
                managePet(false);
                handleShipSelection();
                return;
            }
            if (wasInAstral) {
                runCount++;
                wasInAstral = false;
            }
            if (config.maxRuns > 0 && runCount >= config.maxRuns) {
                status = "Max runs (" + config.maxRuns + ") reached - stopping";
                return;
            }
            status = "Traveling to Astral (run " + (runCount + 1)
                    + (config.maxRuns > 0 ? "/" + config.maxRuns : "") + ")";
            managePet(false);
            resetSelectionState();
            attack.setTarget(null);
            hero.setLocalTarget(null);
            travelToAstral();
            return;
        }

        wasInAstral = true;

        if (isAstralLobby()) {
            managePet(false);

            if (mediator.isShipSelectionGuiVisible()) {
                status = "Choosing ship " + desiredShipLabel();
                handleShipSelection();
                return;
            }

            if (config.autoChoose && !isDesiredShipLoaded()) {
                status = "Opening ship selection " + desiredShipLabel()
                        + " (current " + currentShipLabel() + ")";
                shipChosen = false;
                shipAccepted = false;
                mediator.setShipSelectionGuiVisible(true);
                scheduleNextUiAction();
                return;
            }

            shipChosen = false;
            shipAccepted = false;
            status = "Jumping to Astral gate";
            travelToAstral();
            return;
        }

        // Only reset ship selection state, NOT jump tracking
        shipChosen = false;
        shipAccepted = false;

        // Track rift changes to reset wave-clear state
        int currentRift = astralApi.getCurrentRift();
        if (currentRift != lastRift) {
            lastRift = currentRift;
            // Initialize lastEnemySeenAt to give enemies time to spawn on rift entry
            // This prevents the bot from immediately saying "Waiting for next wave"
            lastEnemySeenAt = System.currentTimeMillis();
        }

        // STEP 1: Fight enemies if present - always prioritize combat
        if (hasVisibleCombatTargets()) {
            lastEnemySeenAt = System.currentTimeMillis();
            // Pick rewards between fights if safe
            if (config.autoChoose && hasOptionsToChoose()
                    && hero.getHealth().hpPercent() > HP_SAFE && !waitToClick()) {
                handleRewardOnly();
            }
            managePet(config.usePet);
            handleCombat();
            return;
        }

        // STEP 2: No enemies visible - grace period before assuming wave is clear
        {
            long now = System.currentTimeMillis();
            if (lastEnemySeenAt > 0 && (now - lastEnemySeenAt) < WAVE_CLEAR_GRACE_PERIOD) {
                managePet(config.usePet);
                if (config.autoChoose && hasOptionsToChoose() && !waitToClick()) {
                    handleRewardOnly();
                }
                long remaining = (WAVE_CLEAR_GRACE_PERIOD - (now - lastEnemySeenAt)) / 1000;
                status = "Scanning for hostiles (" + remaining + "s) | rift " + currentRift
                        + " | W " + getWeaponCount() + "/" + config.shipType.getMaxWeapons()
                        + " G " + getGeneratorCount() + "/" + config.shipType.getMaxGenerators();
                return;
            }
        }

        // STEP 3: Wave confirmed clear - handle rewards and portal selection
        if (config.autoChoose && handleRewardOrPortalSelection()) return;

        // STEP 4: Fallback (autoChoose off or nothing to do) - let combat handle idle + portal
        managePet(config.usePet);
        handleCombat();
    }

    @Override
    public void onTickStopped() {
        resetSelectionState();
        attack.setTarget(null);
        hero.setLocalTarget(null);
    }

    @Override
    public boolean canRefresh() {
        if (isInAstral()) return false;
        return !attack.isAttacking();
    }

    @Override
    public String getStatus() {
        return status;
    }

    private AstralConfig currentConfig() {
        if (configSetting != null) return configSetting.getValue();
        return config;
    }

    private void trackDeaths() {
        int current = repair.getDeathAmount();
        if (current > lastKnownDeaths) {
            deathsThisSession += (current - lastKnownDeaths);
        }
        lastKnownDeaths = current;
    }

    private void handleRespawn() {
        if (repair.isReady(ReviveLocation.SPOT)) {
            repair.isAvailableIn(ReviveLocation.SPOT);
        } else if (repair.isReady(ReviveLocation.SPAWN_POINT)) {
            repair.isAvailableIn(ReviveLocation.SPAWN_POINT);
        }
    }

    private void managePet(boolean shouldBeActive) {
        if (shouldBeActive) {
            if (!pet.isActive()) {
                pet.setEnabled(true);
            }
            try {
                if (pet.hasGear(PetGear.COMBO_GUARD)) {
                    pet.setGear(PetGear.COMBO_GUARD);
                } else if (pet.hasGear(PetGear.GUARD)) {
                    pet.setGear(PetGear.GUARD);
                }
            } catch (Exception ignored) {
                // PET gear change may fail if PET is not ready
            }
        } else {
            if (pet.isActive()) {
                pet.setEnabled(false);
            }
        }
    }

    private void travelToAstral() {
        int cpus = astralApi.getCpuCount();
        // Only enforce threshold when proxy has data (cpus > 0) and minCPUs is set
        if (config.minCPUs > 0 && cpus > 0 && cpus <= config.minCPUs) {
            status = "CPU threshold reached (have: " + cpus + ", min: " + config.minCPUs + ")";
            return;
        }

        Portal astralEntry = entities.getPortals().stream()
                .filter(Objects::nonNull)
                .filter(Portal::isValid)
                .filter(this::isAstralEntryPortal)
                .findFirst()
                .orElse(null);

        if (astralEntry != null) {
            status = "Entering Astral portal";
            portalJumper.travelAndJump(astralEntry);
            return;
        }

        long now = System.currentTimeMillis();
        if (now < nextCpuAttemptAt) {
            status = "Waiting to use Astral CPU";
            return;
        }

        if (heroItems.useItem(SelectableItem.Cpu.ASTRAL_CPU, ItemFlag.USABLE).isSuccessful()) {
            status = "Opening Astral gate";
        } else {
            status = "Astral CPU unavailable";
        }
        nextCpuAttemptAt = now + CPU_RETRY_INTERVAL;
    }

    private boolean isAstralEntryPortal(Portal portal) {
        if (portal.getPortalType() == PortalType.ROUGE_LITE) return true;

        return portal.getTargetMap()
                .map(target -> target.isGG() && target.getName() != null
                        && target.getName().toLowerCase(Locale.ROOT).contains("astral"))
                .orElse(false);
    }

    private boolean isInAstral() {
        if (!hero.getMap().isGG()) return false;
        if (astralApi.getCurrentRift() > 0) return true;
        if (!safeList(astralApi.getInventoryItems()).isEmpty()) return true;
        if (!safeList(astralApi.getRewardsItems()).isEmpty()) return true;
        return entities.getPortals().stream()
                .map(Portal::getPortalType)
                .anyMatch(type -> type != null && type != PortalType.STANDARD);
    }

    private boolean isAstralPortalType(PortalType portalType) {
        return portalType != null && portalType.name().startsWith("ROUGE_LITE");
    }

    private boolean isAstralLobby() {
        return astralApi.getCurrentRift() <= 0
                && entities.getPortals().stream()
                .filter(Objects::nonNull)
                .filter(Portal::isValid)
                .anyMatch(this::isAstralEntryPortal);
    }

    private void handleShipSelection() {
        if (waitToClick()) return;

        if (guiRetryCount >= MAX_GUI_RETRIES) {
            status = "Ship selection failed after " + MAX_GUI_RETRIES + " retries - resetting";
            guiRetryCount = 0;
            shipChosen = false;
            shipAccepted = false;
            mediator.setShipSelectionGuiVisible(false);
            scheduleNextUiAction();
            return;
        }

        if (!mediator.isShipSelectionGuiVisible()) {
            mediator.setShipSelectionGuiVisible(true);
            shipChosen = false;
            shipAccepted = false;
            guiRetryCount++;
            scheduleNextUiAction();
            return;
        }

        if (isDesiredShipLoaded()) {
            if (shipAccepted) {
                status = "Waiting for ship confirmation";
                shipAccepted = false;
                scheduleNextUiAction(SHIP_ACCEPT_RETRY_DELAY);
                return;
            }

            status = "Confirming ship selection";
            mediator.clickAcceptButtonInShipSelection();
            shipChosen = true;
            shipAccepted = true;
            guiRetryCount++;
            scheduleNextUiAction(SHIP_ACCEPT_SETTLE_DELAY);
            return;
        }

        int shipIndex = config.shipType.ordinal();
        if (!shipChosen) {
            mediator.chooseShip(shipIndex, config.astralPlusConfig.enableRandom);
            shipChosen = true;
            shipAccepted = false;
            guiRetryCount++;
            status = "Selecting ship: " + config.shipType.name().toLowerCase(Locale.ROOT);
            scheduleNextUiAction(SHIP_SELECTION_SETTLE_DELAY);
            return;
        }

        if (shipAccepted) {
            status = "Waiting for ship confirmation";
            shipAccepted = false;
            scheduleNextUiAction(SHIP_ACCEPT_RETRY_DELAY);
            return;
        }

        status = "Confirming ship selection";
        mediator.clickAcceptButtonInShipSelection();
        shipAccepted = true;
        guiRetryCount++;
        scheduleNextUiAction(SHIP_ACCEPT_SETTLE_DELAY);
    }

    private boolean handleRewardOrPortalSelection() {
        if (hasOptionsToChoose()) {
            status = "Choosing reward";
            if (waitToClick()) return true;

            if (guiRetryCount >= MAX_GUI_RETRIES) {
                status = "Reward selection failed after " + MAX_GUI_RETRIES + " retries - resetting";
                guiRetryCount = 0;
                mediator.setAstralGuiVisible(false);
                scheduleNextUiAction();
                return true;
            }

            if (!mediator.isAstralGuiVisible()) {
                mediator.setAstralGuiVisible(true);
                guiRetryCount++;
                scheduleNextUiAction();
                return true;
            }

            int rewardIndex = getBestRewardIndex();
            mediator.chooseItem(rewardIndex, config.astralPlusConfig.enableRandom);
            guiRetryCount++;
            scheduleNextUiAction();
            return true;
        }

        guiRetryCount = 0;

        if (hasVisibleCombatTargets()) return false;

        Portal bestPortal = getBestPortal();
        if (bestPortal == null) return false;

        status = "Choosing portal " + bestPortal.getPortalType().name();
        mediator.setAstralGuiVisible(false);
        mediator.setShipSelectionGuiVisible(false);

        if (lastJumpTarget != bestPortal) {
            lastJumpTarget = bestPortal;
            portalJumpStartedAt = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - portalJumpStartedAt > PORTAL_JUMP_TIMEOUT) {
            status = "Portal jump stuck - retrying";
            lastJumpTarget = null;
            portalJumpStartedAt = 0;
        }

        jumpToPortal(bestPortal);
        return true;
    }

    private boolean handleRewardOnly() {
        if (!hasOptionsToChoose()) return false;
        if (waitToClick()) return true;

        if (guiRetryCount >= MAX_GUI_RETRIES) {
            guiRetryCount = 0;
            mediator.setAstralGuiVisible(false);
            scheduleNextUiAction();
            return true;
        }

        if (!mediator.isAstralGuiVisible()) {
            mediator.setAstralGuiVisible(true);
            guiRetryCount++;
            scheduleNextUiAction();
            return true;
        }

        int rewardIndex = getBestRewardIndex();
        mediator.chooseItem(rewardIndex, config.astralPlusConfig.enableRandom);
        guiRetryCount++;
        scheduleNextUiAction();
        return true;
    }

    private boolean hasOptionsToChoose() {
        List<? extends AstralGateAPI.AstralItem> rewards = safeList(astralApi.getRewardsItems());
        if (rewards.isEmpty()) return false;
        if (astralApi.allowedToEquip()) return true;
        return rewards.stream().anyMatch(item -> !item.isEquipped());
    }

    private Portal getBestPortal() {
        List<Portal> candidatePortals = entities.getPortals().stream()
                .filter(Objects::nonNull)
                .filter(Portal::isValid)
                .filter(portal -> portal.getPortalType() != PortalType.STANDARD)
                .collect(Collectors.toList());

        if (candidatePortals.isEmpty()) return null;

        int currentRift = astralApi.getCurrentRift();

        // 1st pass: preferred portals (pass shouldEnter, skip brutal if unsafe)
        Portal best = candidatePortals.stream()
                .filter(portal -> isAstralPortalType(portal.getPortalType()))
                .filter(portal -> shouldEnter(portal.getPortalType()) || ignoreLimit(portal.getPortalType()))
                .filter(portal -> !isBrutalAndUnsafe(portal.getPortalType(), currentRift))
                .min(Comparator.comparingInt(portal -> getDynamicPriority(portal.getPortalType(), currentRift)))
                .orElse(null);

        if (best != null) return best;

        // 2nd pass: any astral portal, skip shouldEnter filter (still avoid brutal if unsafe)
        best = candidatePortals.stream()
                .filter(portal -> isAstralPortalType(portal.getPortalType()))
                .filter(portal -> !isBrutalAndUnsafe(portal.getPortalType(), currentRift))
                .min(Comparator.comparingInt(portal -> getDynamicPriority(portal.getPortalType(), currentRift)))
                .orElse(null);

        if (best != null) return best;

        // 3rd pass: any astral portal at all (including brutal)
        best = candidatePortals.stream()
                .filter(portal -> isAstralPortalType(portal.getPortalType()))
                .min(Comparator.comparingInt(portal -> getDynamicPriority(portal.getPortalType(), currentRift)))
                .orElse(null);

        if (best != null) return best;

        // 4th pass: any non-standard portal (handles UNKNOWN types, stage transitions)
        return candidatePortals.stream()
                .min(Comparator.comparingDouble(hero::distanceTo))
                .orElse(null);
    }

    private boolean isBrutalAndUnsafe(PortalType portalType, int currentRift) {
        if (config.minRiftForBrutal <= 0) return false;
        if (currentRift >= config.minRiftForBrutal) return false;
        String name = portalType.name();
        return name.contains("BRUTAL");
    }

    private int getDynamicPriority(PortalType portalType, int currentRift) {
        int basePriority = getPriority(portalType);

        // Boost weapon portal priority if weapons are lacking
        int weaponCount = getWeaponCount();
        int genCount = getGeneratorCount();
        int maxWeapons = config.shipType.getMaxWeapons();
        int maxGens = config.shipType.getMaxGenerators();

        switch (portalType) {
            case ROUGE_LITE_WEAPON:
            case ROUGE_LITE_WEAPON_BRUTAL:
                if (weaponCount < maxWeapons) basePriority -= (maxWeapons - weaponCount) * 2;
                break;
            case ROUGE_LITE_GENERATOR:
            case ROUGE_LITE_GENERATOR_BRUTAL:
                if (genCount < maxGens) basePriority -= (maxGens - genCount) * 2;
                break;
            case ROUGE_LITE_HP_RECOVER:
                if (hero.getHealth().hpPercent() < 0.5) basePriority -= 5;
                break;
            default:
                break;
        }

        // In later rifts, prefer non-brutal for safety
        if (currentRift >= 8 && portalType.name().contains("BRUTAL")) {
            basePriority += 3;
        }

        return basePriority;
    }

    private boolean shouldEnter(PortalType portalType) {
        switch (portalType) {
            case ROUGE_LITE_HP_RECOVER:
                return hero.getHealth().hpPercent() < HP_RECOVER_THRESHOLD;
            case ROUGE_LITE_MODULE:
            case ROUGE_LITE_MODULE_BRUTAL:
                return getModuleCount() < MAX_MODULES;
            case ROUGE_LITE_GENERATOR:
            case ROUGE_LITE_GENERATOR_BRUTAL:
                return getGeneratorCount() < config.shipType.getMaxGenerators();
            case ROUGE_LITE_WEAPON:
            case ROUGE_LITE_WEAPON_BRUTAL:
                return getWeaponCount() < config.shipType.getMaxWeapons();
            default:
                return true;
        }
    }

    private boolean ignoreLimit(PortalType portalType) {
        PortalInfo portalInfo = config.portalInfos.get(portalType.name());
        return portalInfo != null && portalInfo.ignoreLimit;
    }

    private int getPriority(PortalType portalType) {
        PortalInfo portalInfo = config.portalInfos.get(portalType.name());
        if (portalInfo != null) return portalInfo.priority;
        return DEFAULT_PORTAL_PRIORITIES.getOrDefault(portalType, 99);
    }

    private int getBestRewardIndex() {
        List<? extends AstralGateAPI.AstralItem> rewards = safeList(astralApi.getRewardsItems());
        if (rewards.isEmpty()) return 0;

        RewardCategory rewardCategory = detectRewardCategory(rewards);
        switch (rewardCategory) {
            case WEAPON:
                return getBestPriorityReward(rewards, true, false);
            case GENERATOR:
                return getBestPriorityReward(rewards, false, true);
            case MODULE:
                return getBestModuleReward(rewards);
            default:
                return random.nextInt(Math.max(1, Math.min(RANDOM_ITEM_BOUND, rewards.size())));
        }
    }

    private RewardCategory detectRewardCategory(List<? extends AstralGateAPI.AstralItem> rewards) {
        for (AstralGateAPI.AstralItem reward : rewards) {
            String normalizedId = normalizeLootId(reward.getLootId());
            if (isWeaponId(normalizedId)) return RewardCategory.WEAPON;
            if (isGeneratorId(normalizedId)) return RewardCategory.GENERATOR;
            if (isModuleId(normalizedId) || !safeList(reward.getStats()).isEmpty()) return RewardCategory.MODULE;
        }
        return RewardCategory.OTHER;
    }

    private int getBestPriorityReward(List<? extends AstralGateAPI.AstralItem> rewards,
                                      boolean weapon,
                                      boolean generator) {
        Comparator<RewardChoice> comparator = Comparator
                .comparingInt((RewardChoice choice) -> itemPriority(choice.item, weapon, generator))
                .thenComparing((RewardChoice left, RewardChoice right)
                        -> Integer.compare(right.item.getUpgradeLevel(), left.item.getUpgradeLevel()));

        return rewards.stream()
                .map(reward -> new RewardChoice(rewards.indexOf(reward), reward))
                .min(comparator)
                .map(choice -> choice.index)
                .orElseGet(() -> random.nextInt(Math.max(1, Math.min(RANDOM_ITEM_BOUND, rewards.size()))));
    }

    private int getBestModuleReward(List<? extends AstralGateAPI.AstralItem> rewards) {
        return rewards.stream()
                .map(reward -> new RewardChoice(rewards.indexOf(reward), reward))
                .max(Comparator.comparingInt(this::moduleScore)
                        .thenComparing(choice -> choice.item.getUpgradeLevel()))
                .map(choice -> choice.index)
                .orElseGet(() -> random.nextInt(Math.max(1, Math.min(RANDOM_ITEM_BOUND, rewards.size()))));
    }

    private int moduleScore(RewardChoice choice) {
        if (config.customItemPriority.active) {
            return 10_000 - itemPriority(choice.item, false, false);
        }

        List<? extends AstralGateAPI.ItemStat> stats = safeList(choice.item.getStats());
        int positiveStats = (int) stats.stream().filter(stat -> stat.getValue() > 0).count();
        int negativeStats = (int) stats.stream().filter(stat -> stat.getValue() < 0).count();
        int duplicatePenalty = hasThatModuleType(choice.item.getLootId()) ? 1 : 0;
        return positiveStats * 100 - negativeStats * 1_000 - duplicatePenalty * 25 + stats.size();
    }

    private boolean hasThatModuleType(String lootId) {
        String family = moduleFamily(lootId);
        if (family.isEmpty()) return false;

        return safeList(astralApi.getInventoryItems()).stream()
                .map(AstralGateAPI.AstralItem::getLootId)
                .map(this::moduleFamily)
                .anyMatch(family::equals);
    }

    private String moduleFamily(String lootId) {
        String normalized = normalizeLootId(lootId);
        if (!normalized.contains("module")) return normalized;

        int separator = Math.max(normalized.lastIndexOf('-'), normalized.lastIndexOf('_'));
        if (separator > 0) return normalized.substring(0, separator);
        return normalized;
    }

    private int itemPriority(AstralGateAPI.AstralItem item, boolean weapon, boolean generator) {
        String rawId = Objects.toString(item.getLootId(), "");
        if (config.customItemPriority.active) return getCustomPriority(rawId);

        String normalized = normalizeLootId(rawId);
        if (weapon) return priorityFromList(WEAPON_PRIORITY, normalized);
        if (generator) return priorityFromList(GENERATOR_PRIORITY, normalized);
        return getCustomPriority(rawId);
    }

    private int getCustomPriority(String itemId) {
        ItemInfo info = config.customItemPriority.itemInfos.get(itemId);
        if (info != null) return info.priority;

        String normalized = normalizeLootId(itemId);
        info = config.customItemPriority.itemInfos.get(normalized);
        if (info != null) return info.priority;

        ItemInfo newInfo = new ItemInfo();
        config.customItemPriority.itemInfos.putIfAbsent(normalized, newInfo);
        return newInfo.priority;
    }

    private int priorityFromList(List<String> priorityList, String normalizedId) {
        for (int index = 0; index < priorityList.size(); index++) {
            if (normalizedId.contains(priorityList.get(index))) return index;
        }
        return priorityList.size() + 10;
    }

    private String normalizeLootId(String lootId) {
        String normalized = Objects.toString(lootId, "").toLowerCase(Locale.ROOT).trim();

        String[] prefixes = {
                "equipment_weapon_laser_",
                "equipment_weapon_rocketlauncher_",
                "equipment_weapon_",
                "equipment_extra_generator_",
                "equipment_generator_",
                "equipment_extra_",
                "equipment_",
                "item_",
                "loot_"
        };

        for (String prefix : prefixes) {
            if (normalized.startsWith(prefix)) {
                normalized = normalized.substring(prefix.length());
                break;
            }
        }

        return normalized;
    }

    private boolean isWeaponId(String normalizedId) {
        return normalizedId.contains("weapon")
                || normalizedId.contains("laser")
                || WEAPON_PRIORITY.stream().anyMatch(normalizedId::contains);
    }

    private boolean isGeneratorId(String normalizedId) {
        return normalizedId.contains("generator")
                || normalizedId.startsWith("shield_")
                || normalizedId.startsWith("speed_")
                || GENERATOR_PRIORITY.stream().anyMatch(normalizedId::contains);
    }

    private boolean isModuleId(String normalizedId) {
        return normalizedId.contains("module");
    }

    private int getWeaponCount() {
        return (int) safeList(astralApi.getInventoryItems()).stream()
                .map(AstralGateAPI.AstralItem::getLootId)
                .map(this::normalizeLootId)
                .filter(this::isWeaponId)
                .count();
    }

    private int getGeneratorCount() {
        return (int) safeList(astralApi.getInventoryItems()).stream()
                .map(AstralGateAPI.AstralItem::getLootId)
                .map(this::normalizeLootId)
                .filter(this::isGeneratorId)
                .count();
    }

    private int getModuleCount() {
        return (int) safeList(astralApi.getInventoryItems()).stream()
                .map(AstralGateAPI.AstralItem::getLootId)
                .map(this::normalizeLootId)
                .filter(this::isModuleId)
                .count();
    }

    private void handleCombat() {
        Health heroHealth = hero.getHealth();
        double hpPct = heroHealth.hpPercent();
        double shieldPct = heroHealth.shieldPercent();
        long now = System.currentTimeMillis();

        Ship target = currentTarget(now);
        if (target == null) {
            attack.setTarget(null);
            hero.setLocalTarget(null);
            lastCombatTargetId = -1;

            // Grace period: enemies may still be spawning between waves
            if (lastEnemySeenAt > 0 && (now - lastEnemySeenAt) < WAVE_CLEAR_GRACE_PERIOD) {
                status = "Scanning for targets | rift " + astralApi.getCurrentRift();
                if (config.useFormations) applyFormation(hpPct, shieldPct, false, now);
                return;
            }

            String visibility = combatVisibilitySummary();
            Portal nextPortal = getBestPortal();
            if (nextPortal != null) {
                status = "Wave clear - moving to portal" + visibility;
                jumpToPortal(nextPortal);
            } else {
                movement.stop(false);
                status = "Waiting for next wave | rift " + astralApi.getCurrentRift()
                        + " | deaths " + deathsThisSession + " | run " + (runCount + 1)
                        + visibility;
            }

            if (config.useFormations) applyFormation(hpPct, shieldPct, false, now);
            return;
        }

        lastEnemySeenAt = now;

        if (target.getId() != lastCombatTargetId) {
            lastCombatTargetId = target.getId();
            lastCombatActivityAt = now;
        }

        hero.setLocalTarget(target);
        attack.setTarget(target);
        ensureLockAndAttack(target, now);

        applyAmmoSelection(target);
        useRocketIfReady(target);
        useSpecialItems(target, hpPct, shieldPct, now);
        useAbility(hpPct, shieldPct, now);

        String riftInfo = " | rift " + astralApi.getCurrentRift()
                + " | W " + getWeaponCount() + "/" + config.shipType.getMaxWeapons()
                + " G " + getGeneratorCount() + "/" + config.shipType.getMaxGenerators()
                + " | deaths " + deathsThisSession
                + " | run " + (runCount + 1);

        if (hpPct < HP_CRITICAL) {
            hero.setRunMode();
            if (config.useFormations) applyFormation(hpPct, shieldPct, true, now);
            fleeFromTarget(target);
            status = "CRITICAL HP " + pct(hpPct) + "% - fleeing" + riftInfo;
        } else if (hpPct < HP_DANGER || shieldPct < SHIELD_KITE_THRESHOLD) {
            hero.setRunMode();
            setAttackModeFor(target);
            if (config.useFormations) applyFormation(hpPct, shieldPct, true, now);
            kiteAroundTarget(target);
            status = "Kiting (HP " + pct(hpPct) + "% SH " + pct(shieldPct) + "%) "
                    + targetName(target) + riftInfo;
        } else {
            setAttackModeFor(target);
            if (config.useFormations) applyFormation(hpPct, shieldPct, false, now);
            if (shouldChaseLowHpTarget(target)) {
                chaseLowHpTarget(target);
                status = "Chasing low HP " + targetName(target)
                        + " (HP " + pct(target.getHealth().hpPercent()) + "%)" + riftInfo;
            } else {
                moveAroundTarget(target);
                status = "Fighting " + targetName(target)
                        + " (HP " + pct(hpPct) + "%)" + riftInfo;
            }
        }

        if (attack.isLocked() || attack.isAttacking() || target.getHealth().hpDecreasedIn(1_500)) {
            lastCombatActivityAt = now;
        }

        // Stuck detection: if trying to engage same target for too long with no progress
        if (now - lastCombatActivityAt > STUCK_TIMEOUT) {
            attack.setTarget(null);
            hero.setLocalTarget(null);
            lastCombatTargetId = -1;
            nextTargetReEvalAt = 0;
            status = "Stuck - re-evaluating target";
        }
    }

    private void ensureLockAndAttack(Ship target, long now) {
        if (!target.isSelectable()) {
            lastCombatActivityAt = now;
            return;
        }

        attack.tryLockAndAttack();

        if (!attack.isLocked()) {
            if (target.trySelect(true)) {
                hero.setLocalTarget(target);
                lastCombatActivityAt = now;
            }
            return;
        }

        if (!attack.isAttacking() && hero.triggerLaserAttack()) {
            lastCombatActivityAt = now;
        }
    }

    private int pct(double value) {
        return (int) (value * 100);
    }

    private Ship currentTarget(long now) {
        Ship currentTarget = attack.getTargetAs(Ship.class);

        // Periodic re-evaluation: switch to a better target if available
        if (isValidTarget(currentTarget) && now >= nextTargetReEvalAt) {
            nextTargetReEvalAt = now + TARGET_REEVAL_INTERVAL;
            Ship betterTarget = selectBestTarget();
            if (betterTarget != null && betterTarget != currentTarget) {
                double currentScore = targetScore(currentTarget);
                double betterScore = targetScore(betterTarget);
                // Only switch if significantly better (avoid flip-flopping)
                if (betterScore < currentScore - 500) {
                    attack.setTarget(betterTarget);
                    return betterTarget;
                }
            }
            return currentTarget;
        }

        if (isValidTarget(currentTarget)) return currentTarget;

        Ship newTarget = selectBestTarget();
        attack.setTarget(newTarget);
        nextTargetReEvalAt = now + TARGET_REEVAL_INTERVAL;
        return newTarget;
    }

    private Ship selectBestTarget() {
        Comparator<Ship> comparator = Comparator.comparingDouble(this::targetScore);
        return getVisibleCombatTargets().stream()
                .filter(this::isValidTarget)
                .min(comparator)
                .orElse(null);
    }

    private List<Ship> getVisibleCombatTargets() {
        List<Ship> candidates = new ArrayList<>();
        entities.getNpcs().forEach(ship -> addCandidateIfMissing(candidates, ship));
        addAstralHostileShips(candidates, entities.getShips());
        addAstralHostileShips(candidates, entities.getPets());
        if (candidates.stream().noneMatch(this::isValidTarget)) {
            addAstralHostileEntities(candidates, entities.getAll());
        }
        return candidates;
    }

    private void addAstralHostileShips(List<Ship> candidates, Collection<? extends Ship> ships) {
        ships.stream()
                .filter(this::isAstralHostileShip)
                .forEach(ship -> addCandidateIfMissing(candidates, ship));
    }

    private void addAstralHostileEntities(List<Ship> candidates, Collection<? extends Entity> visibleEntities) {
        visibleEntities.stream()
                .filter(Ship.class::isInstance)
                .map(Ship.class::cast)
                .filter(this::isAstralHostileShip)
                .forEach(ship -> addCandidateIfMissing(candidates, ship));
    }

    private void addCandidateIfMissing(List<Ship> candidates, Ship ship) {
        if (ship == null) return;
        if (candidates.stream().noneMatch(candidate -> candidate.getId() == ship.getId())) {
            candidates.add(ship);
        }
    }

    private boolean hasVisibleCombatTargets() {
        // Check if we have valid targets to attack
        if (getVisibleCombatTargets().stream().anyMatch(this::isValidTarget)) {
            return true;
        }

        // Also detect if hero is currently being attacked (HP decreased recently)
        // This handles cases where NPCs have attacked but haven't been properly registered yet
        if (hero.getHealth().hpDecreasedIn(3_000)) {
            return true;
        }

        return false;
    }

    private boolean isValidTarget(Ship ship) {
        return ship != null
            && ship != hero
                && ship.isValid()
                && !ship.isInvisible()
                && !ship.isBlacklisted()
                && ship.getHealth().getHp() > 0
                && (ship instanceof Npc || isAstralHostileShip(ship));
    }

    private boolean isAstralHostileShip(Ship ship) {
        if (ship == null || ship == hero) return false;
        if (ship instanceof Pet && isHeroOwnedPet((Pet) ship)) return false;

        String username = Objects.toString(ship.getEntityInfo().getUsername(), "").toLowerCase(Locale.ROOT);
        String shipType = ship instanceof Player
            ? Objects.toString(((Player) ship).getShipType(), "").toLowerCase(Locale.ROOT)
            : "";

        return ship.getEntityInfo().isEnemy()
                || ship.isAttacking(hero)
                || ship instanceof Pet
            || (!(ship instanceof Player) && !username.isBlank())
            || ASTRAL_HOSTILE_HINTS.stream().anyMatch(username::contains)
            || ASTRAL_HOSTILE_HINTS.stream().anyMatch(shipType::contains);
    }

    private double targetScore(Ship target) {
        double distance = hero.distanceTo(target);
        if (config.alwaysTheClosestNPC) return distance;

        Health health = target.getHealth();
        double hpPct = health.hpPercent();
        double shieldPct = health.shieldPercent();

        double score = distance * 0.8;

        if (config.chaseLowHpNpcs && hpPct <= config.lowHpChaseThreshold) score -= 2600;
        else if (hpPct < 0.15) score -= 2000;
        else if (hpPct < 0.40) score -= 800;

        score += hpPct * 500;
        score += shieldPct * 200;

        if (target.getLocationInfo().getSpeed() > 1.0) score += 300;

        if (!(target instanceof Npc)) score -= 200;

        return score;
    }

    private void applyAmmoSelection(Ship target) {
        long now = System.currentTimeMillis();
        if (now < nextAmmoCheckAt) return;

        SelectableItem.Laser selectedLaser = pickLaser(target);
        if (selectedLaser != null && selectedLaser != hero.getLaser()) {
            heroItems.useItem(selectedLaser, ItemFlag.NOT_SELECTED);
        }

        SelectableItem.Rocket selectedRocket = SelectableItem.Rocket.of(config.defaultRocket);
        if (selectedRocket != null && selectedRocket != hero.getRocket()) {
            heroItems.useItem(selectedRocket, ItemFlag.NOT_SELECTED);
        }

        nextAmmoCheckAt = now + AMMO_CHECK_INTERVAL;
    }

    private SelectableItem.Laser pickLaser(Ship target) {
        SelectableItem.Laser fallback = Optional.ofNullable(SelectableItem.Laser.of(config.defaultLaser))
                .orElse(SelectableItem.Laser.LCB_10);

        if (config.useBestAmmoLogic == BestAmmoConfig.ONLY_MARKED) return firstAvailable(List.of(fallback));

        boolean useSpecialAmmo = config.useBestAmmoLogic == BestAmmoConfig.ALWAYS || shouldUseSpecialLogic(target);
        if (useSpecialAmmo) {
            SelectableItem.Laser premium = firstAvailable(List.of(
                    SelectableItem.Laser.RCB_140,
                    SelectableItem.Laser.RSB_75,
                    SelectableItem.Laser.UCB_100,
                    SelectableItem.Laser.MCB_50,
                    SelectableItem.Laser.MCB_25,
                    fallback
            ));
            if (premium != null) return premium;
        }

        return firstAvailable(List.of(fallback, SelectableItem.Laser.MCB_50, SelectableItem.Laser.MCB_25,
                SelectableItem.Laser.LCB_10));
    }

    private boolean shouldUseSpecialLogic(Ship target) {
        Health heroHealth = hero.getHealth();
        Health targetHealth = target.getHealth();
        int rift = astralApi.getCurrentRift();
        return rift >= 10
                || heroHealth.hpPercent() < HP_DANGER
                || heroHealth.shieldPercent() < config.minShieldToRepair
                || targetHealth.hpPercent() < 0.3
                || (target.getLocationInfo().getSpeed() > 0.8 && targetHealth.shieldPercent() < 0.3);
    }

    private SelectableItem.Laser firstAvailable(List<SelectableItem.Laser> candidates) {
        for (SelectableItem.Laser candidate : candidates) {
            if (candidate == null) continue;
            if (heroItems.getItem(candidate, ItemFlag.USABLE).isPresent()) return candidate;
        }
        return null;
    }

    private void useRocketIfReady(Ship target) {
        long now = System.currentTimeMillis();
        if (now < nextRocketCheckAt) return;
        if (!attack.isLocked() && !attack.isAttacking()) return;

        // Rocket escalation: use stronger rockets in higher rifts or on tough targets
        int rift = astralApi.getCurrentRift();
        Health targetHealth = target.getHealth();

        if (rift >= 6 || targetHealth.shieldPercent() > 0.5) {
            // Try premium rockets first
            SelectableItem.Rocket premium = firstAvailableRocket(List.of(
                    SelectableItem.Rocket.PLD_8,
                    SelectableItem.Rocket.DCR_250,
                    SelectableItem.Rocket.PLT_3030
            ));
            if (premium != null) {
                heroItems.useItem(premium, ItemFlag.USABLE);
                nextRocketCheckAt = now + ROCKET_CHECK_INTERVAL;
                return;
            }
        }

        hero.launchRocket();
        nextRocketCheckAt = now + ROCKET_CHECK_INTERVAL;
    }

    private SelectableItem.Rocket firstAvailableRocket(List<SelectableItem.Rocket> candidates) {
        for (SelectableItem.Rocket candidate : candidates) {
            if (candidate == null) continue;
            if (heroItems.getItem(candidate, ItemFlag.USABLE).isPresent()) return candidate;
        }
        return null;
    }

    private void setAttackModeFor(Ship target) {
        hero.setAttackMode(target instanceof Npc ? (Npc) target : null);
    }

    private void applyFormation(double hpPct, double shieldPct, boolean defensive, long now) {
        if (now < nextFormationChangeAt) return;

        SelectableItem.Formation desiredFormation;
        if (defensive) {
            if (hpPct < HP_CRITICAL) {
                desiredFormation = SelectableItem.Formation.DIAMOND;
            } else if (shieldPct < SHIELD_KITE_THRESHOLD) {
                desiredFormation = SelectableItem.Formation.RING;
            } else {
                desiredFormation = SelectableItem.Formation.DOME;
            }
        } else {
            if (shieldPct > 0.6 && hpPct > HP_SAFE) {
                desiredFormation = SelectableItem.Formation.STANDARD;
            } else if (shieldPct < 0.3) {
                desiredFormation = SelectableItem.Formation.DOME;
            } else {
                desiredFormation = SelectableItem.Formation.HEART;
            }
        }

        ShipMode desiredMode = ShipMode.of(HeroAPI.Configuration.FIRST, desiredFormation);
        if (!hero.isInMode(desiredMode)) {
            hero.setMode(desiredMode);
            nextFormationChangeAt = now + FORMATION_CHANGE_COOLDOWN;
        }
    }

    private void useAbility(double hpPct, double shieldPct, long now) {
        if (!config.useShipAbility) return;
        if (now < nextAbilityUseAt) return;

        SelectableItem.Ability ability = getShipAbility();
        if (ability == null) return;

        // Use ability when: HP < 70%, or shield low, or in later rifts
        boolean shouldUse = hpPct < 0.70
                || shieldPct < 0.20
                || astralApi.getCurrentRift() >= 6;

        if (shouldUse && heroItems.useItem(ability, ItemFlag.USABLE).isSuccessful()) {
            nextAbilityUseAt = now + ABILITY_COOLDOWN;
        }
    }

    private SelectableItem.Ability getShipAbility() {
        switch (config.shipType) {
            case SENTINEL:
                return SelectableItem.Ability.SENTINEL;
            case DIMINISHER:
                return SelectableItem.Ability.DIMINISHER;
            case ZEPHYR:
                // Try both Zephyr abilities
                if (heroItems.getItem(SelectableItem.Ability.ZEPHYR_MMT, ItemFlag.USABLE).isPresent())
                    return SelectableItem.Ability.ZEPHYR_MMT;
                if (heroItems.getItem(SelectableItem.Ability.ZEPHYR_TBR, ItemFlag.USABLE).isPresent())
                    return SelectableItem.Ability.ZEPHYR_TBR;
                return null;
            case PUSAT:
                if (heroItems.getItem(SelectableItem.Ability.PUSAT_PLUS_SPEED_SAP, ItemFlag.USABLE).isPresent())
                    return SelectableItem.Ability.PUSAT_PLUS_SPEED_SAP;
                return null;
            default:
                return null;
        }
    }

    private void useSpecialItems(Ship target, double hpPct, double shieldPct, long now) {
        if (now < nextSpecialItemAt) return;
        if (!attack.isLocked() && !attack.isAttacking()) return;

        Health targetHealth = target.getHealth();
        int rift = astralApi.getCurrentRift();

        // ISH-01: Use when shields are low
        if (shieldPct < 0.30 || hpPct < HP_DANGER) {
            if (heroItems.useItem(SelectableItem.Special.ISH_01, ItemFlag.USABLE).isSuccessful()) {
                nextSpecialItemAt = now + SPECIAL_ITEM_COOLDOWN;
                return;
            }
        }

        // SMB-01: Use smartbombs on tough enemies or groups
        if (rift >= 4 || targetHealth.hpPercent() > 0.7 || countVisibleCombatTargets() > 2) {
            if (heroItems.useItem(SelectableItem.Special.SMB_01, ItemFlag.USABLE).isSuccessful()) {
                nextSpecialItemAt = now + SPECIAL_ITEM_COOLDOWN;
                return;
            }
        }

        // SAB-50: Use shield absorber on shielded targets
        if (targetHealth.shieldPercent() > 0.4) {
            SelectableItem.Laser currentLaser = hero.getLaser();
            if (currentLaser != SelectableItem.Laser.SAB_50) {
                heroItems.useItem(SelectableItem.Laser.SAB_50, ItemFlag.USABLE);
            }
        }
    }

    private int countVisibleCombatTargets() {
        return (int) getVisibleCombatTargets().stream().filter(this::isValidTarget).count();
    }

    private String targetName(Ship target) {
        return Objects.toString(target.getEntityInfo().getUsername(), "target");
    }

    private boolean isHeroOwnedPet(Pet pet) {
        return pet.getOwnerId() == hero.getId()
                || pet.getOwner().map(owner -> owner.getId() == hero.getId()).orElse(false);
    }

    private boolean isDesiredShipLoaded() {
        String currentShipType = hero.getShipType();
        return currentShipType != null && config.shipType.matches(currentShipType);
    }

    private String desiredShipLabel() {
        return config.shipType.name().toLowerCase(Locale.ROOT);
    }

    private String currentShipLabel() {
        String currentShipType = Objects.toString(hero.getShipType(), "unknown").toLowerCase(Locale.ROOT);
        return currentShipType.startsWith("ship_") ? currentShipType.substring(5) : currentShipType;
    }

    private String combatVisibilitySummary() {
        long unknownShips = entities.getUnknown().stream()
                .filter(Ship.class::isInstance)
                .map(Ship.class::cast)
                .filter(this::isValidTarget)
                .count();
        return " | seen " + countVisibleCombatTargets() + " | unknown " + unknownShips;
    }

    private double targetRadius(Ship target) {
        if (target instanceof Npc) {
            return Math.max(config.radioMin, attack.modifyRadius(((Npc) target).getInfo().getRadius()));
        }
        return Math.max(config.radioMin, attack.modifyRadius(config.radioMin));
    }

    private void moveAroundTarget(Ship target) {
        double desiredRadius = targetRadius(target);

        double currentDistance = hero.distanceTo(target);
        double angle = Math.atan2(target.getY() - hero.getY(), target.getX() - hero.getX());
        if (currentDistance > desiredRadius + 120) {
            movement.moveTo(Location.of(target, angle, desiredRadius));
            return;
        }

        if (currentDistance < Math.max(200, desiredRadius - 120)) {
            movement.moveTo(Location.of(target, angle, desiredRadius + 100));
            return;
        }

        double step = orbitClockwise ? 0.55 : -0.55;
        Location orbitLocation = Location.of(target, angle + step, desiredRadius);
        if (!movement.canMove(orbitLocation)) {
            orbitClockwise = !orbitClockwise;
            orbitLocation = Location.of(target, angle - step, desiredRadius);
        }

        movement.moveTo(orbitLocation);
    }

    private void chaseLowHpTarget(Ship target) {
        double desiredRadius = Math.max(220, targetRadius(target) - 180);

        double currentDistance = hero.distanceTo(target);
        double angle = Math.atan2(target.getY() - hero.getY(), target.getX() - hero.getX());
        if (currentDistance > desiredRadius + 60) {
            movement.moveTo(Location.of(target, angle, desiredRadius));
            return;
        }

        double step = orbitClockwise ? 0.35 : -0.35;
        Location orbitLocation = Location.of(target, angle + step, desiredRadius);
        if (!movement.canMove(orbitLocation)) {
            orbitClockwise = !orbitClockwise;
            orbitLocation = Location.of(target, angle - step, desiredRadius);
        }

        movement.moveTo(orbitLocation);
    }

    private boolean shouldChaseLowHpTarget(Ship target) {
        if (!config.chaseLowHpNpcs || !isValidTarget(target)) return false;

        Health heroHealth = hero.getHealth();
        return target.getHealth().hpPercent() <= config.lowHpChaseThreshold
                && heroHealth.hpPercent() >= HP_DANGER
                && heroHealth.shieldPercent() >= Math.max(config.minShieldToRepair, SHIELD_KITE_THRESHOLD);
    }

    private void jumpToPortal(Portal portal) {
        double distance = hero.distanceTo(portal);
        if (distance < 200) {
            movement.jumpPortal(portal);
        } else {
            double angle = Math.atan2(portal.getY() - hero.getY(), portal.getX() - hero.getX());
            movement.moveTo(Location.of(portal, angle, 100));
        }
    }

    private void kiteAroundTarget(Ship target) {
        double baseRadius = targetRadius(target);
        double kiteRadius = baseRadius * 1.6 + 300;

        double currentDistance = hero.distanceTo(target);
        double angle = Math.atan2(target.getY() - hero.getY(), target.getX() - hero.getX());

        if (currentDistance < kiteRadius - 80) {
            movement.moveTo(Location.of(target, angle, kiteRadius + 150));
            return;
        }

        double step = orbitClockwise ? 0.45 : -0.45;
        Location orbitLocation = Location.of(target, angle + step, kiteRadius);
        if (!movement.canMove(orbitLocation)) {
            orbitClockwise = !orbitClockwise;
            orbitLocation = Location.of(target, angle - step, kiteRadius);
        }

        movement.moveTo(orbitLocation);
    }

    private void fleeFromTarget(Ship target) {
        double fleeRadius = config.radioMin * 2.5 + 600;
        double angle = Math.atan2(hero.getY() - target.getY(), hero.getX() - target.getX());

        Location fleeLocation = Location.of(target, angle, fleeRadius);
        if (!movement.canMove(fleeLocation)) {
            fleeLocation = Location.of(target, angle + 1.2, fleeRadius);
            if (!movement.canMove(fleeLocation)) {
                fleeLocation = Location.of(target, angle - 1.2, fleeRadius);
            }
        }

        movement.moveTo(fleeLocation);
    }

    private boolean waitToClick() {
        return System.currentTimeMillis() < nextUiActionAt;
    }

    private void scheduleNextUiAction() {
        scheduleNextUiAction(0L);
    }

    private void scheduleNextUiAction(long extraDelay) {
        long baseDelay = Math.max(1, config.astralPlusConfig.minTimePerAction) * CLICK_UNIT_MILLIS;
        if (config.astralPlusConfig.enableRandom) baseDelay += random.nextInt(250);
        nextUiActionAt = System.currentTimeMillis() + baseDelay + extraDelay;
    }

    private void resetSelectionState() {
        shipChosen = false;
        shipAccepted = false;
        guiRetryCount = 0;
        lastJumpTarget = null;
        lastEnemySeenAt = 0;
        lastRift = 0;
    }

    private void ensurePortalDefaults(AstralConfig config) {
        for (Map.Entry<PortalType, Integer> entry : DEFAULT_PORTAL_PRIORITIES.entrySet()) {
            config.portalInfos.computeIfAbsent(entry.getKey().name(), key -> {
                PortalInfo info = new PortalInfo();
                info.priority = entry.getValue();
                return info;
            });
        }
    }

    private static Map<PortalType, Integer> buildPortalPriorities() {
        Map<PortalType, Integer> defaults = new EnumMap<>(PortalType.class);
        defaults.put(PortalType.ROUGE_LITE_HP_RECOVER, 0);
        defaults.put(PortalType.ROUGE_LITE_AMMUNITION, 1);
        defaults.put(PortalType.ROUGE_LITE_BOOSTER, 2);
        defaults.put(PortalType.ROUGE_LITE_MODULE, 3);
        defaults.put(PortalType.ROUGE_LITE_GENERATOR, 4);
        defaults.put(PortalType.ROUGE_LITE_WEAPON, 5);
        defaults.put(PortalType.ROUGE_LITE_RESOURCE, 6);
        defaults.put(PortalType.ROUGE_LITE_AMMUNITION_BRUTAL, 7);
        defaults.put(PortalType.ROUGE_LITE_BOOSTER_BRUTAL, 8);
        defaults.put(PortalType.ROUGE_LITE_MODULE_BRUTAL, 9);
        defaults.put(PortalType.ROUGE_LITE_GENERATOR_BRUTAL, 10);
        defaults.put(PortalType.ROUGE_LITE_WEAPON_BRUTAL, 11);
        defaults.put(PortalType.ROUGE_LITE_RESOURCE_BRUTAL, 12);
        defaults.put(PortalType.ROUGE_LITE_FINAL_BOSS, 13);
        defaults.put(PortalType.ROUGE_LITE, 25);
        return Collections.unmodifiableMap(defaults);
    }

    private static <T> List<T> safeList(List<? extends T> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(items);
    }

    private enum RewardCategory {
        WEAPON,
        GENERATOR,
        MODULE,
        OTHER
    }

    private static final class RewardChoice {
        private final int index;
        private final AstralGateAPI.AstralItem item;

        private RewardChoice(int index, AstralGateAPI.AstralItem item) {
            this.index = index;
            this.item = item;
        }
    }
}
