package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Cancelable
public class AttackEntityEvent extends PlayerEvent {
    public final Entity target;

    public AttackEntityEvent(PlayerEntity player, Entity target) {
        super(player);
        this.target = target;
    }
}