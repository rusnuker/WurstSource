// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;
    
    public MovementInputFromOptions(final GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }
    
    @Override
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0f;
        this.moveForward = 0.0f;
        if (this.gameSettings.keyBindForward.isPressed()) {
            ++this.moveForward;
            this.forwardKeyDown = true;
        }
        else {
            this.forwardKeyDown = false;
        }
        if (this.gameSettings.keyBindBack.isPressed()) {
            --this.moveForward;
            this.backKeyDown = true;
        }
        else {
            this.backKeyDown = false;
        }
        if (this.gameSettings.keyBindLeft.isPressed()) {
            ++this.moveStrafe;
            this.leftKeyDown = true;
        }
        else {
            this.leftKeyDown = false;
        }
        if (this.gameSettings.keyBindRight.isPressed()) {
            --this.moveStrafe;
            this.rightKeyDown = true;
        }
        else {
            this.rightKeyDown = false;
        }
        this.jump = this.gameSettings.keyBindJump.isPressed();
        this.sneak = this.gameSettings.keyBindSneak.isPressed();
        if (this.sneak) {
            this.moveStrafe *= (float)0.3;
            this.moveForward *= (float)0.3;
        }
    }
}
