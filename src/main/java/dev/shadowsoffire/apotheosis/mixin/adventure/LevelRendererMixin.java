package dev.shadowsoffire.apotheosis.mixin.adventure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.client.BossSpawnMessage;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient.BOSS_SPAWNS;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {


    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderType.tripwire ()Lnet/minecraft/client/renderer/RenderType;", shift = At.Shift.AFTER))
    private void test(PoseStack stack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci){
        if (Apotheosis.enableAdventure) {

            Minecraft mc = Minecraft.getInstance();

            MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Player p = Minecraft.getInstance().player;
            for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
                BossSpawnMessage.BossSpawnData data = BOSS_SPAWNS.get(i);
                AdventureModule.LOGGER.info("BOSS SPAWN DATA: pos: {}, ticks: {}", data.pos(), data.ticks());
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
    }
}
