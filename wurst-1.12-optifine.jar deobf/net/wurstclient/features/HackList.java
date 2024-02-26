// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import java.util.Collections;
import java.util.Collection;
import java.lang.reflect.Field;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.util.TreeMap;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.features.mods.blocks.TunnellerMod;
import net.wurstclient.features.mods.render.TrueSightMod;
import net.wurstclient.features.mods.items.TrollPotionMod;
import net.wurstclient.features.mods.combat.TriggerBotMod;
import net.wurstclient.features.mods.render.TrajectoriesMod;
import net.wurstclient.features.mods.combat.TpAuraMod;
import net.wurstclient.features.mods.fun.TiredMod;
import net.wurstclient.features.mods.other.TimerMod;
import net.wurstclient.features.mods.other.ThrowMod;
import net.wurstclient.features.mods.blocks.TemplateToolMod;
import net.wurstclient.features.mods.movement.StepMod;
import net.wurstclient.features.mods.movement.SpiderMod;
import net.wurstclient.features.mods.blocks.SpeedNukerMod;
import net.wurstclient.features.mods.movement.SpeedHackMod;
import net.wurstclient.features.mods.chat.SpammerMod;
import net.wurstclient.features.mods.movement.SneakMod;
import net.wurstclient.features.mods.fun.SkinDerpMod;
import net.wurstclient.features.mods.render.SearchMod;
import net.wurstclient.features.mods.blocks.ScaffoldWalkMod;
import net.wurstclient.features.mods.movement.SafeWalkMod;
import net.wurstclient.features.mods.render.RemoteViewMod;
import net.wurstclient.features.mods.retro.RegenMod;
import net.wurstclient.features.mods.other.ReachMod;
import net.wurstclient.features.mods.fun.RainbowUiMod;
import net.wurstclient.features.mods.render.RadarMod;
import net.wurstclient.features.mods.combat.ProtectMod;
import net.wurstclient.features.mods.render.ProphuntEspMod;
import net.wurstclient.features.mods.other.PotionSaverMod;
import net.wurstclient.features.mods.render.PlayerFinderMod;
import net.wurstclient.features.mods.render.PlayerEspMod;
import net.wurstclient.features.mods.movement.PhaseMod;
import net.wurstclient.features.mods.movement.ParkourMod;
import net.wurstclient.features.mods.other.PanicMod;
import net.wurstclient.features.mods.render.OverlayMod;
import net.wurstclient.features.mods.blocks.NukerLegitMod;
import net.wurstclient.features.mods.blocks.NukerMod;
import net.wurstclient.features.mods.movement.NoWebMod;
import net.wurstclient.features.mods.render.NoWeatherMod;
import net.wurstclient.features.mods.movement.NoSlowdownMod;
import net.wurstclient.features.mods.render.NoOverlayMod;
import net.wurstclient.features.mods.render.NoHurtcamMod;
import net.wurstclient.features.mods.movement.NoFallMod;
import net.wurstclient.features.mods.movement.NoClipMod;
import net.wurstclient.features.mods.other.NavigatorMod;
import net.wurstclient.features.mods.render.NameTagsMod;
import net.wurstclient.features.mods.render.NameProtectMod;
import net.wurstclient.features.mods.combat.MultiAuraMod;
import net.wurstclient.features.mods.render.MobSpawnEspMod;
import net.wurstclient.features.mods.render.MobEspMod;
import net.wurstclient.features.mods.fun.MileyCyrusMod;
import net.wurstclient.features.mods.chat.MassTpaMod;
import net.wurstclient.features.mods.fun.LsdMod;
import net.wurstclient.features.mods.other.LogSpammerMod;
import net.wurstclient.features.mods.blocks.LiquidsMod;
import net.wurstclient.features.mods.items.KillPotionMod;
import net.wurstclient.features.mods.combat.KillauraMod;
import net.wurstclient.features.mods.combat.KillauraLegitMod;
import net.wurstclient.features.mods.blocks.KaboomMod;
import net.wurstclient.features.mods.movement.JetpackMod;
import net.wurstclient.features.mods.movement.JesusMod;
import net.wurstclient.features.mods.items.ItemGeneratorHack;
import net.wurstclient.features.mods.render.ItemEspMod;
import net.wurstclient.features.mods.movement.InvWalkMod;
import net.wurstclient.features.mods.blocks.InstantBunkerMod;
import net.wurstclient.features.mods.chat.HomeMod;
import net.wurstclient.features.mods.movement.HighJumpMod;
import net.wurstclient.features.mods.render.HealthTagsMod;
import net.wurstclient.features.mods.fun.HeadRollMod;
import net.wurstclient.features.mods.fun.HeadlessMod;
import net.wurstclient.features.mods.blocks.HandNoClipHack;
import net.wurstclient.features.mods.movement.GlideMod;
import net.wurstclient.features.mods.render.FullbrightMod;
import net.wurstclient.features.mods.render.FreecamMod;
import net.wurstclient.features.mods.retro.ForcePushMod;
import net.wurstclient.features.mods.chat.ForceOpMod;
import net.wurstclient.features.mods.movement.FollowMod;
import net.wurstclient.features.mods.movement.FlightMod;
import net.wurstclient.features.mods.movement.FishMod;
import net.wurstclient.features.mods.combat.FightBotMod;
import net.wurstclient.features.mods.blocks.FastPlaceMod;
import net.wurstclient.features.mods.movement.FastLadderMod;
import net.wurstclient.features.mods.retro.FastEatMod;
import net.wurstclient.features.mods.retro.FastBowMod;
import net.wurstclient.features.mods.blocks.FastBreakMod;
import net.wurstclient.features.mods.chat.FancyChatMod;
import net.wurstclient.features.mods.blocks.ExcavatorMod;
import net.wurstclient.features.mods.movement.DolphinMod;
import net.wurstclient.features.mods.fun.DerpMod;
import net.wurstclient.features.mods.combat.CriticalsMod;
import net.wurstclient.features.mods.items.CrashTagMod;
import net.wurstclient.features.mods.items.CrashChestMod;
import net.wurstclient.features.mods.items.CmdBlockMod;
import net.wurstclient.features.mods.other.ClickGuiMod;
import net.wurstclient.features.mods.combat.ClickAuraMod;
import net.wurstclient.features.mods.render.ChestEspMod;
import net.wurstclient.features.mods.render.CaveFinderMod;
import net.wurstclient.features.mods.render.CameraNoClipMod;
import net.wurstclient.features.mods.movement.BunnyHopMod;
import net.wurstclient.features.mods.blocks.BuildRandomMod;
import net.wurstclient.features.mods.combat.BowAimbotMod;
import net.wurstclient.features.mods.blocks.BonemealAuraMod;
import net.wurstclient.features.mods.movement.BlinkMod;
import net.wurstclient.features.mods.render.BaseFinderMod;
import net.wurstclient.features.mods.movement.AutoWalkMod;
import net.wurstclient.features.mods.blocks.AutoToolMod;
import net.wurstclient.features.mods.combat.AutoSwordMod;
import net.wurstclient.features.mods.items.AutoSwitchMod;
import net.wurstclient.features.mods.items.AutoStealMod;
import net.wurstclient.features.mods.movement.AutoSprintMod;
import net.wurstclient.features.mods.other.AutoSoupMod;
import net.wurstclient.features.mods.blocks.AutoSignMod;
import net.wurstclient.features.mods.combat.AutoRespawnMod;
import net.wurstclient.features.mods.other.AutoPotionHack;
import net.wurstclient.features.mods.blocks.AutoMineMod;
import net.wurstclient.features.mods.combat.AutoLeaveMod;
import net.wurstclient.features.mods.other.AutoFishMod;
import net.wurstclient.features.mods.blocks.AutoFarmMod;
import net.wurstclient.features.mods.items.AutoEatMod;
import net.wurstclient.features.mods.items.AutoDropHack;
import net.wurstclient.features.mods.blocks.AutoBuildMod;
import net.wurstclient.features.mods.combat.AutoArmorMod;
import net.wurstclient.features.mods.render.AntiWobbleHack;
import net.wurstclient.features.mods.movement.AntiWaterPushHack;
import net.wurstclient.features.mods.chat.AntiSpamMod;
import net.wurstclient.features.mods.retro.AntiPotionMod;
import net.wurstclient.features.mods.combat.AntiKnockbackMod;
import net.wurstclient.features.mods.retro.AntiFireMod;
import net.wurstclient.features.mods.render.AntiBlindMod;
import net.wurstclient.features.mods.blocks.AntiCactusMod;
import net.wurstclient.features.mods.other.AntiAfkMod;

public class HackList
{
    public final AntiAfkMod antiAfkMod;
    public final AntiCactusMod antiCactusMod;
    public final AntiBlindMod antiBlindMod;
    public final AntiFireMod antiFireMod;
    public final AntiKnockbackMod antiKnockbackMod;
    public final AntiPotionMod antiPotionMod;
    public final AntiSpamMod antiSpamMod;
    public final AntiWaterPushHack antiWaterPushHack;
    public final AntiWobbleHack antiWobbleHack;
    public final AutoArmorMod autoArmorMod;
    public final AutoBuildMod autoBuildMod;
    public final AutoDropHack autoDropHack;
    public final AutoEatMod autoEatMod;
    public final AutoFarmMod autoFarmMod;
    public final AutoFishMod autoFishMod;
    public final AutoLeaveMod autoLeaveMod;
    public final AutoMineMod autoMineMod;
    public final AutoPotionHack autoPotionHack;
    public final AutoRespawnMod autoRespawnMod;
    public final AutoSignMod autoSignMod;
    public final AutoSoupMod autoSoupMod;
    public final AutoSprintMod autoSprintMod;
    public final AutoStealMod autoStealMod;
    public final AutoSwitchMod autoSwitchMod;
    public final AutoSwordMod autoSwordMod;
    public final AutoToolMod autoToolMod;
    public final AutoWalkMod autoWalkMod;
    public final BaseFinderMod baseFinderMod;
    public final BlinkMod blinkMod;
    public final BonemealAuraMod bonemealAuraMod;
    public final BowAimbotMod bowAimbotMod;
    public final BuildRandomMod buildRandomMod;
    public final BunnyHopMod bunnyHopMod;
    public final CameraNoClipMod cameraNoClipMod;
    public final CaveFinderMod caveFinderMod;
    public final ChestEspMod chestEspMod;
    public final ClickAuraMod clickAuraMod;
    public final ClickGuiMod clickGuiMod;
    public final CmdBlockMod cmdBlockMod;
    public final CrashChestMod crashChestMod;
    public final CrashTagMod crashTagMod;
    public final CriticalsMod criticalsMod;
    public final DerpMod derpMod;
    public final DolphinMod dolphinMod;
    public final ExcavatorMod excavatorMod;
    public final FancyChatMod fancyChatMod;
    public final FastBreakMod fastBreakMod;
    public final FastBowMod fastBowMod;
    public final FastEatMod fastEatMod;
    public final FastLadderMod fastLadderMod;
    public final FastPlaceMod fastPlaceMod;
    public final FightBotMod fightBotMod;
    public final FishMod fishMod;
    public final FlightMod flightMod;
    public final FollowMod followMod;
    public final ForceOpMod forceOpMod;
    public final ForcePushMod forcePushMod;
    public final FreecamMod freecamMod;
    public final FullbrightMod fullbrightMod;
    public final GlideMod glideMod;
    public final HandNoClipHack handNoClipHack;
    public final HeadlessMod headlessMod;
    public final HeadRollMod headRollMod;
    public final HealthTagsMod healthTagsMod;
    public final HighJumpMod highJumpMod;
    public final HomeMod homeMod;
    public final InstantBunkerMod instantBunkerMod;
    public final InvWalkMod invWalkMod;
    public final ItemEspMod itemEspMod;
    public final ItemGeneratorHack itemGeneratorHack;
    public final JesusMod jesusMod;
    public final JetpackMod jetpackMod;
    public final KaboomMod kaboomMod;
    public final KillauraLegitMod killauraLegitMod;
    public final KillauraMod killauraMod;
    public final KillPotionMod killPotionMod;
    public final LiquidsMod liquidsMod;
    public final LogSpammerMod logSpammerMod;
    public final LsdMod lsdMod;
    public final MassTpaMod massTpaMod;
    public final MileyCyrusMod mileyCyrusMod;
    public final MobEspMod mobEspMod;
    public final MobSpawnEspMod mobSpawnEspMod;
    public final MultiAuraMod multiAuraMod;
    public final NameProtectMod nameProtectMod;
    public final NameTagsMod nameTagsMod;
    public final NavigatorMod navigatorMod;
    public final NoClipMod noClipMod;
    public final NoFallMod noFallMod;
    public final NoHurtcamMod noHurtcamMod;
    public final NoOverlayMod noOverlayMod;
    public final NoSlowdownMod noSlowdownMod;
    public final NoWeatherMod noWeatherMod;
    public final NoWebMod noWebMod;
    public final NukerMod nukerMod;
    public final NukerLegitMod nukerLegitMod;
    public final OverlayMod overlayMod;
    public final PanicMod panicMod;
    public final ParkourMod parkourMod;
    public final PhaseMod phaseMod;
    public final PlayerEspMod playerEspMod;
    public final PlayerFinderMod playerFinderMod;
    public final PotionSaverMod potionSaverMod;
    public final ProphuntEspMod prophuntEspMod;
    public final ProtectMod protectMod;
    public final RadarMod radarMod;
    public final RainbowUiMod rainbowUiMod;
    public final ReachMod reachMod;
    public final RegenMod regenMod;
    public final RemoteViewMod remoteViewMod;
    public final SafeWalkMod safeWalkMod;
    public final ScaffoldWalkMod scaffoldWalkMod;
    public final SearchMod searchMod;
    public final SkinDerpMod skinDerpMod;
    public final SneakMod sneakMod;
    public final SpammerMod spammerMod;
    public final SpeedHackMod speedHackMod;
    public final SpeedNukerMod speedNukerMod;
    public final SpiderMod spiderMod;
    public final StepMod stepMod;
    public final TemplateToolMod templateToolMod;
    public final ThrowMod throwMod;
    public final TimerMod timerMod;
    public final TiredMod tiredMod;
    public final TpAuraMod tpAuraMod;
    public final TrajectoriesMod trajectoriesMod;
    public final TriggerBotMod triggerBotMod;
    public final TrollPotionMod trollPotionMod;
    public final TrueSightMod trueSightMod;
    public final TunnellerMod tunnellerMod;
    public final XRayMod xRayMod;
    protected final TreeMap<String, Hack> hax;
    
    public HackList() {
        this.antiAfkMod = new AntiAfkMod();
        this.antiCactusMod = new AntiCactusMod();
        this.antiBlindMod = new AntiBlindMod();
        this.antiFireMod = new AntiFireMod();
        this.antiKnockbackMod = new AntiKnockbackMod();
        this.antiPotionMod = new AntiPotionMod();
        this.antiSpamMod = new AntiSpamMod();
        this.antiWaterPushHack = new AntiWaterPushHack();
        this.antiWobbleHack = new AntiWobbleHack();
        this.autoArmorMod = new AutoArmorMod();
        this.autoBuildMod = new AutoBuildMod();
        this.autoDropHack = new AutoDropHack();
        this.autoEatMod = new AutoEatMod();
        this.autoFarmMod = new AutoFarmMod();
        this.autoFishMod = new AutoFishMod();
        this.autoLeaveMod = new AutoLeaveMod();
        this.autoMineMod = new AutoMineMod();
        this.autoPotionHack = new AutoPotionHack();
        this.autoRespawnMod = new AutoRespawnMod();
        this.autoSignMod = new AutoSignMod();
        this.autoSoupMod = new AutoSoupMod();
        this.autoSprintMod = new AutoSprintMod();
        this.autoStealMod = new AutoStealMod();
        this.autoSwitchMod = new AutoSwitchMod();
        this.autoSwordMod = new AutoSwordMod();
        this.autoToolMod = new AutoToolMod();
        this.autoWalkMod = new AutoWalkMod();
        this.baseFinderMod = new BaseFinderMod();
        this.blinkMod = new BlinkMod();
        this.bonemealAuraMod = new BonemealAuraMod();
        this.bowAimbotMod = new BowAimbotMod();
        this.buildRandomMod = new BuildRandomMod();
        this.bunnyHopMod = new BunnyHopMod();
        this.cameraNoClipMod = new CameraNoClipMod();
        this.caveFinderMod = new CaveFinderMod();
        this.chestEspMod = new ChestEspMod();
        this.clickAuraMod = new ClickAuraMod();
        this.clickGuiMod = new ClickGuiMod();
        this.cmdBlockMod = new CmdBlockMod();
        this.crashChestMod = new CrashChestMod();
        this.crashTagMod = new CrashTagMod();
        this.criticalsMod = new CriticalsMod();
        this.derpMod = new DerpMod();
        this.dolphinMod = new DolphinMod();
        this.excavatorMod = new ExcavatorMod();
        this.fancyChatMod = new FancyChatMod();
        this.fastBreakMod = new FastBreakMod();
        this.fastBowMod = new FastBowMod();
        this.fastEatMod = new FastEatMod();
        this.fastLadderMod = new FastLadderMod();
        this.fastPlaceMod = new FastPlaceMod();
        this.fightBotMod = new FightBotMod();
        this.fishMod = new FishMod();
        this.flightMod = new FlightMod();
        this.followMod = new FollowMod();
        this.forceOpMod = new ForceOpMod();
        this.forcePushMod = new ForcePushMod();
        this.freecamMod = new FreecamMod();
        this.fullbrightMod = new FullbrightMod();
        this.glideMod = new GlideMod();
        this.handNoClipHack = new HandNoClipHack();
        this.headlessMod = new HeadlessMod();
        this.headRollMod = new HeadRollMod();
        this.healthTagsMod = new HealthTagsMod();
        this.highJumpMod = new HighJumpMod();
        this.homeMod = new HomeMod();
        this.instantBunkerMod = new InstantBunkerMod();
        this.invWalkMod = new InvWalkMod();
        this.itemEspMod = new ItemEspMod();
        this.itemGeneratorHack = new ItemGeneratorHack();
        this.jesusMod = new JesusMod();
        this.jetpackMod = new JetpackMod();
        this.kaboomMod = new KaboomMod();
        this.killauraLegitMod = new KillauraLegitMod();
        this.killauraMod = new KillauraMod();
        this.killPotionMod = new KillPotionMod();
        this.liquidsMod = new LiquidsMod();
        this.logSpammerMod = new LogSpammerMod();
        this.lsdMod = new LsdMod();
        this.massTpaMod = new MassTpaMod();
        this.mileyCyrusMod = new MileyCyrusMod();
        this.mobEspMod = new MobEspMod();
        this.mobSpawnEspMod = new MobSpawnEspMod();
        this.multiAuraMod = new MultiAuraMod();
        this.nameProtectMod = new NameProtectMod();
        this.nameTagsMod = new NameTagsMod();
        this.navigatorMod = new NavigatorMod();
        this.noClipMod = new NoClipMod();
        this.noFallMod = new NoFallMod();
        this.noHurtcamMod = new NoHurtcamMod();
        this.noOverlayMod = new NoOverlayMod();
        this.noSlowdownMod = new NoSlowdownMod();
        this.noWeatherMod = new NoWeatherMod();
        this.noWebMod = new NoWebMod();
        this.nukerMod = new NukerMod();
        this.nukerLegitMod = new NukerLegitMod();
        this.overlayMod = new OverlayMod();
        this.panicMod = new PanicMod();
        this.parkourMod = new ParkourMod();
        this.phaseMod = new PhaseMod();
        this.playerEspMod = new PlayerEspMod();
        this.playerFinderMod = new PlayerFinderMod();
        this.potionSaverMod = new PotionSaverMod();
        this.prophuntEspMod = new ProphuntEspMod();
        this.protectMod = new ProtectMod();
        this.radarMod = new RadarMod();
        this.rainbowUiMod = new RainbowUiMod();
        this.reachMod = new ReachMod();
        this.regenMod = new RegenMod();
        this.remoteViewMod = new RemoteViewMod();
        this.safeWalkMod = new SafeWalkMod();
        this.scaffoldWalkMod = new ScaffoldWalkMod();
        this.searchMod = new SearchMod();
        this.skinDerpMod = new SkinDerpMod();
        this.sneakMod = new SneakMod();
        this.spammerMod = new SpammerMod();
        this.speedHackMod = new SpeedHackMod();
        this.speedNukerMod = new SpeedNukerMod();
        this.spiderMod = new SpiderMod();
        this.stepMod = new StepMod();
        this.templateToolMod = new TemplateToolMod();
        this.throwMod = new ThrowMod();
        this.timerMod = new TimerMod();
        this.tiredMod = new TiredMod();
        this.tpAuraMod = new TpAuraMod();
        this.trajectoriesMod = new TrajectoriesMod();
        this.triggerBotMod = new TriggerBotMod();
        this.trollPotionMod = new TrollPotionMod();
        this.trueSightMod = new TrueSightMod();
        this.tunnellerMod = new TunnellerMod();
        this.xRayMod = new XRayMod();
        this.hax = new TreeMap<String, Hack>((o1, o2) -> o1.compareToIgnoreCase(o2));
        this.registerHax(HackList.class, "Initializing Wurst hacks");
    }
    
    protected final void registerHax(final Class<?> clars, final String errorMessage) {
        try {
            Field[] declaredFields;
            for (int length = (declaredFields = clars.getDeclaredFields()).length, i = 0; i < length; ++i) {
                final Field field = declaredFields[i];
                if (field.getName().endsWith("Hack") || field.getName().endsWith("Mod")) {
                    final Hack hack = (Hack)field.get(this);
                    this.hax.put(hack.getName(), hack);
                    hack.initSettings();
                }
            }
        }
        catch (final Exception e) {
            final CrashReport report = CrashReport.makeCrashReport(e, errorMessage);
            throw new ReportedException(report);
        }
    }
    
    public final Hack getByName(final String name) {
        return this.hax.get(name);
    }
    
    public final Collection<Hack> getAll() {
        return Collections.unmodifiableCollection((Collection<? extends Hack>)this.hax.values());
    }
    
    public final int size() {
        return this.hax.size();
    }
}
