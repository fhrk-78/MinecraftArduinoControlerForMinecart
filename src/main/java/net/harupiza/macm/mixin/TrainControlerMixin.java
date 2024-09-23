package net.harupiza.macm.mixin;

import net.harupiza.macm.DataStore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class TrainControlerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Unique
    private String getPowerID(Integer value) {
        if (value > 935) {
            return "EB";
        } else if (value > 857) {
            return "B7";
        } else if (value > 779) {
            return "B6";
        } else if (value > 701) {
            return "B5";
        } else if (value > 623) {
            return "B4";
        } else if (value > 545) {
            return "B3";
        } else if (value > 467) {
            return "B2";
        } else if (value > 389) {
            return "B1";
        } else if (value > 311) {
            return "N";
        } else if (value > 233) {
            return "P1";
        } else if (value > 155) {
            return "P2";
        } else if (value > 77) {
            return "P3";
        } else {
            return "P4";
        }
    }

    @Unique
    private int getWaittick(String powerID) {
        if (powerID.equals("EB")) {
            return 0;
        } else if (powerID.equals("B7")) {
            return 1;
        } else if (powerID.equals("B6")) {
            return 2;
        } else if (powerID.equals("B5")) {
            return 4;
        } else if (powerID.equals("B4")) {
            return 8;
        } else if (powerID.equals("B3")) {
            return 12;
        } else if (powerID.equals("B2")) {
            return 16;
        } else if (powerID.equals("B1")) {
            return 20;
        } else if (powerID.equals("N")) {
            return 0;
        } else if (powerID.equals("P1")) {
            return 4;
        } else if (powerID.equals("P2")) {
            return 2;
        } else if (powerID.equals("P3")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Unique
    private int waittick = 0;

    @Unique
    private String previousPowerID = "";

    @Unique
    private boolean isMinecart = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        String powerID = getPowerID(DataStore.Companion.getControlerval());
        if (client == null) return;

        var player = client.player;
        if (player == null) return;

        if(player.getVehicle() instanceof MinecartEntity) {
            isMinecart = true;

            if (!previousPowerID.equals(powerID)) {
                previousPowerID = powerID;
                waittick = 0;
            }

            LOGGER.info(client.options.forwardKey.isPressed() + "" + client.options.backKey.isPressed());

            if (powerID.equals("N")) {
                client.options.forwardKey.setPressed(false);
                client.options.backKey.setPressed(false);
            } else if (waittick <= 0) {
                if (powerID.startsWith("B") || powerID.startsWith("E")) {
                    client.options.backKey.setPressed(true);
                    client.options.forwardKey.setPressed(false);
                } else {
                    client.options.forwardKey.setPressed(true);
                    client.options.backKey.setPressed(false);
                }

                waittick = getWaittick(powerID);
            } else {
                waittick--;
            }
        } else {
            if (isMinecart) {
                client.options.forwardKey.setPressed(false);
                client.options.backKey.setPressed(false);
                isMinecart = false;
            }
        }
    }
}
