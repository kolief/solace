package net.solace.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.api.Projectile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectileSpawned {
    /**
     * The spawned projectile.
     */
    private Projectile projectile;
}