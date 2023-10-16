package dev.shadowsoffire.apotheosis.mixin.adventure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.client.BossSpawnMessage;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient.BOSS_SPAWNS;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

/*
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderStateShard$OutputStateShard.clearRenderState ()V", shift = At.Shift.BEFORE, ordinal = 0))
    private void test(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci){
        if (Apotheosis.enableAdventure) {
            renderBossBeaconBeam(stack, partialTick, finishNanoTime, renderBlockOutline, camera, gameRenderer, lightTexture, projectionMatrix);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/ParticleEngine.render (Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", shift = At.Shift.AFTER, ordinal = 1))
    private void test2(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci){
        if (Apotheosis.enableAdventure) {
            renderBossBeaconBeam(stack, partialTick, finishNanoTime, renderBlockOutline, camera, gameRenderer, lightTexture, projectionMatrix);
        }
    }*/
    /*
@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/LevelRenderer.renderChunkLayer (Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", ordinal = 4))
private void test(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci){
    if (Apotheosis.enableAdventure) {
    //    renderBossBeaconBeam(stack, partialTick, finishNanoTime, renderBlockOutline, camera, gameRenderer, lightTexture, projectionMatrix);
    }
}

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/LevelRenderer.renderChunkLayer (Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", ordinal = 6))
    private void test2(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci){
        if (Apotheosis.enableAdventure) {
        //    renderBossBeaconBeam(stack, partialTick, finishNanoTime, renderBlockOutline, camera, gameRenderer, lightTexture, projectionMatrix);
        }
    }

    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderType.clearRenderState ()V", shift = At.Shift.BEFORE))
    private void hopethisworks(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci){
        if (renderType == RenderType.tripwire() && Apotheosis.enableAdventure) {
            var mc = Minecraft.getInstance();
            var profiler = mc.getProfiler();
            profiler.push("tripwire");
            renderBossBeacon(poseStack);
            profiler.pop();
        }
    }

    @Unique
    private void renderBossBeacon(PoseStack stack){
        Minecraft mc = Minecraft.getInstance();

        MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Player p = Minecraft.getInstance().player;
        for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
            BossSpawnMessage.BossSpawnData data = BOSS_SPAWNS.get(i);
            AdventureModule.LOGGER.info("EPIC BOSS SPAWN DATA: pos: {}, ticks: {}", data.pos(), data.ticks());
            stack.pushPose();
            float partials = mc.isPaused() ? mc.pausePartialTick : mc.timer.partialTick;
            Vec3 vec = mc.getCameraEntity().getEyePosition(partials);
            stack.translate(-vec.x, -vec.y, -vec.z);
            stack.translate(data.pos().getX(), data.pos().getY(), data.pos().getZ());
            BeaconRenderer.renderBeaconBeam(stack, buf, BeaconRenderer.BEAM_LOCATION, partials, 1, p.level().getGameTime(), 0, 64, data.color(), 0.166F, 0.33F);
            stack.popPose();
        }
        buf.endBatch();
    }

    @Unique
    private void renderBossBeaconBeam(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix){
        Minecraft mc = Minecraft.getInstance();

        MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Player p = Minecraft.getInstance().player;
        for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
            BossSpawnMessage.BossSpawnData data = BOSS_SPAWNS.get(i);
            AdventureModule.LOGGER.info("BOSS SPAWN DATA: pos: {}, ticks: {}", data.pos(), data.ticks());
            stack.pushPose();
            float partials = mc.isPaused() ? mc.pausePartialTick : mc.timer.partialTick;
            Vec3 vec = camera.getPosition(); // mc.getCameraEntity().getEyePosition(partials);
            stack.translate(-vec.x, -vec.y, -vec.z);
            stack.translate(data.pos().getX(), data.pos().getY(), data.pos().getZ());
            BeaconRenderer.renderBeaconBeam(stack, buf, BeaconRenderer.BEAM_LOCATION, partials, 1, p.level().getGameTime(), 0, 64, data.color(), 0.166F, 0.33F);
            stack.popPose();
        }
        buf.endBatch();
    }*/
}
