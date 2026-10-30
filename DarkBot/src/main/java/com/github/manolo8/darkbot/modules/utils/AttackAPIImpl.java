package com.github.manolo8.darkbot.modules.utils;

import com.github.manolo8.darkbot.config.NpcExtra;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.FakeNpc;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.manager.SettingsManager;
import com.github.manolo8.darkbot.core.objects.facades.HighlightProxy;
import com.github.manolo8.darkbot.extensions.features.handlers.LaserSelectorHandler;
import com.github.manolo8.darkbot.utils.MathUtils;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.events.EventHandler;
import eu.darkbot.api.events.Listener;
import eu.darkbot.api.game.enums.EntityEffect;
import eu.darkbot.api.game.items.Item;
import eu.darkbot.api.game.items.ItemFlag;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.game.other.Lockable;
import eu.darkbot.api.managers.BotAPI;
import eu.darkbot.api.managers.ConfigAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.HeroItemsAPI;
import eu.darkbot.impl.managers.AbstractAttackImpl;
import eu.darkbot.util.Timer;
import org.jetbrains.annotations.Nullable;

public class AttackAPIImpl extends AbstractAttackImpl implements Listener {

    private final LaserSelectorHandler laserHandler;
    private final HighlightProxy highlight;
    private final SettingsManager settingsManager;
    private final MapManager mapManager;

    private final Timer buggedTimer = Timer.get(20_000), highLightTimer = Timer.get(500);

    private final ConfigSetting<Character> shipAbility;
    private final ConfigSetting<Integer> shipAbilityMinHp;

    public AttackAPIImpl(HeroItemsAPI heroItems, HeroAPI hero, LaserSelectorHandler laserHandler,
                         HighlightProxy highlight, SettingsManager settingsManager, MapManager mapManager,
                         ConfigAPI config) {
        super(heroItems, hero);
        this.laserHandler = laserHandler;
        this.highlight = highlight;
        this.settingsManager = settingsManager;
        this.mapManager = mapManager;

        this.shipAbility = config.requireConfig("loot.ship_ability");
        this.shipAbilityMinHp = config.requireConfig("loot.ship_ability_min");
    }

    @Override
    protected boolean isAttackViaSlotBarEnabled() {
        return settingsManager.attackViaSlotbar;
    }

    /** Set by Tutucu's VS mode while it mixes ammo; null = use the normal ammo selection. */
    public static volatile java.util.function.Supplier<SelectableItem.Laser> ammoOverride;

    @Override
    protected SelectableItem.Laser getBestLaserAmmo() {
        java.util.function.Supplier<SelectableItem.Laser> override = ammoOverride;
        SelectableItem.Laser mixed = override == null ? null : override.get();
        return mixed != null ? mixed : laserHandler.getBest();
    }

    @Override
    public String getStatus() {
//        String supplierName = Optional.ofNullable(laserHandler.getLastUsedSupplier())
//                .filter(s -> isLocked())
//                .map(Object::getClass)
//                .map(c -> c.getAnnotation(Feature.class))
//                .map(Feature::name)
//                .orElse("");

        return hasTarget() ? ("Targeting " + target.getClass().getSimpleName()
                + " [" + (isLocked() ? "L " : "")
                + (isAttacking() ? "S " : "")
                + (isCastingAbility() ? "A " : "")
                + ("B" + Math.round(buggedTimer.getRemainingFuse() / 1000.0)) + "]")
                : "Waiting";
    }

    @Override
    public boolean isBugged() {
        return buggedTimer.isInactive();
    }

    @Override
    public void setTarget(@Nullable Lockable target) {
        if (this.target != target) {
            buggedTimer.activate();
            highLightTimer.disarm();
        }

        super.setTarget(target);
    }

    // TODO: 11.07.2022 use offensive abilities on its own (venom/cyborg or diminisher)
    @Override
    public boolean isCastingAbility() {
        return false;
    }

    @Override
    public void tryLockTarget() {
        if (target instanceof FakeNpc) return;
        super.tryLockTarget();
    }

    @Override
    public boolean isLocked() {
        return super.isLocked() && mapManager.isTarget((Entity) this.target);
    }

    @Override
    public void tryLockAndAttack() {
        super.tryLockAndAttack();

        if (!hasTarget()) return;
        if (hero.distanceTo(target) > 1_000 || (isAttacking() && (
                target.hasEffect(EntityEffect.NPC_ISH)
                        || target.hasEffect(EntityEffect.ISH)
                        || target.getHealth().hpDecreasedIn(3_000)
                        // Shield (or hull) soaking the hits is progress too; judging by HP alone declared big-shield
                        // targets "bugged" every few seconds and the bot stopped and re-started the attack.
                        || target.getHealth().shieldDecreasedIn(3_000)
                        || target.getHealth().hullDecreasedIn(3_000)))) {
            buggedTimer.activate();
        }

        // try to use item without changing radius for now
        Character ability = shipAbility.getValue();
        if (ability != null && isLocked() && isAttacking() && getTarget().getHealth().getHp() > shipAbilityMinHp.getValue()) {
            Item item = heroItems.getItem(ability);
            if (item != null) {
                heroItems.useItem(item, 5_000);
            }
        }
    }

    @Override
    public boolean isAttacking() {
        if (!attacked)
            highLightTimer.disarm();

        if (highlight.isAttacking())
            highLightTimer.activate();

        return super.isAttacking() && highLightTimer.isActive();
    }

    @Override
    public double modifyRadius(double radius) {
        if (target.getHealth().hpPercent() < 0.25 && hasExtraFlag(NpcExtra.AGGRESSIVE_FOLLOW))
            radius *= 0.75;

        if (!isAttacking() && target.isMoving() && MathUtils.angleDiff(target.getLocationInfo().getAngle(), hero.angleTo(target)) > 1.6)
            radius = Math.min(560, radius);

        if (isCastingAbility())
            radius = Math.min(560, radius);

        if (!target.isMoving() || target.getHealth().hpPercent() < 0.25)
            radius = Math.min(600, radius);

        return radius + heroItems.getItem(SelectableItem.Ability.ZEPHYR_MMT, ItemFlag.AVAILABLE)
                .or(() -> heroItems.getItem(SelectableItem.Ability.HECATE_PLUS_STOCKPILE, ItemFlag.AVAILABLE))
                .map(Item::getQuantity)
                .orElse(0d) * 5;
    }

    @EventHandler
    public void onRunningToggle(BotAPI.RunningToggleEvent event) {
        buggedTimer.activate();
    }
}
