package net.solace.loader.plugins.agility;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.coords.Area;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.sdk.entities.Players;

@Slf4j
public enum Course {
    GNOME_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(2472, 3436, 0), new WorldPoint(2490, 3438, 0)),
                    "Log balance", "Walk-across"),
            new Obstacle(Area.fromCorners(new WorldPoint(2470, 3426, 0), new WorldPoint(2477, 3431, 0)),
                    "Obstacle net", "Climb-over"),
            new Obstacle(Area.fromCorners(new WorldPoint(2471, 3422, 1), new WorldPoint(2476, 3424, 1)),
                    "Tree branch", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(2472, 3418, 2), new WorldPoint(2478, 3421, 2)),
                    "Balancing rope", "Walk-on"),
            new Obstacle(Area.fromCorners(new WorldPoint(2483, 3418, 2), new WorldPoint(2488, 3421, 2)),
                    "Tree branch", "Climb-down"),
            new Obstacle(Area.fromCorners(new WorldPoint(2482, 3419, 0), new WorldPoint(2489, 3425, 0)),
                    "Obstacle net", "Climb-over"),
            new Obstacle(Area.fromCorners(new WorldPoint(2482, 3427, 0), new WorldPoint(2490, 3431, 0)),
                    "Obstacle pipe", "Squeeze-through")
    ),

    DRAYNOR_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3060, 3147, 0), new WorldPoint(3110, 3281, 0)),
                    "Rough wall", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(3096, 3276, 3), new WorldPoint(3103, 3282, 3)),
                    "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(3088, 3273, 3), new WorldPoint(3092, 3277, 3)),
                    "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(3089, 3265, 3), new WorldPoint(3095, 3267, 3)),
                    "Narrow wall", "Balance"),
            new Obstacle(Area.fromCorners(new WorldPoint(3088, 3257, 3), new WorldPoint(3089, 3262, 3)),
                    "Wall", "Jump-up"),
            new Obstacle(new WorldArea(new WorldPoint(3087, 3255, 3), 8, 1),
                    "Gap", "Jump"),
            new Obstacle(new WorldArea(new WorldPoint(3096, 3256, 3), 6, 6),
                    "Crate", "Climb-down")
    ),

    AL_KHARID_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3263, 3156, 0), new WorldPoint(3319, 3201, 0)),
                    "Rough wall", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(3269, 3178, 3), new WorldPoint(3278, 3195, 3)),
                    "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(3264, 3160, 3), new WorldPoint(3273, 3174, 3)),
                    "Cable", "Swing-across"),
            new Obstacle(Area.fromCorners(new WorldPoint(3283, 3160, 3), new WorldPoint(3302, 3171, 3)),
                    "Zip line", "Teeth-grip"),
            new Obstacle(Area.fromCorners(new WorldPoint(3312, 3159, 1), new WorldPoint(3319, 3166, 1)),
                    "Tropical tree", "Swing-across"),
            new Obstacle(Area.fromCorners(new WorldPoint(3310, 3172, 2), new WorldPoint(3319, 3180, 2)),
                    "Roof top beams", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(3311, 3179, 3), new WorldPoint(3319, 3187, 3)),
                    "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(3296, 3185, 3), new WorldPoint(3308, 3197, 3)),
                    "Gap", "Jump")
    ),


    VARROCK_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3186, 3392, 0), new WorldPoint(3249, 3431, 0)),
                    "Rough wall", "Climb", 14412),
            new Obstacle(Area.fromCorners(new WorldPoint(3214, 3410, 3), new WorldPoint(3220, 3420, 3)),
                    "Clothes line", "Cross", 14413),
            new Obstacle(Area.fromCorners(new WorldPoint(3201, 3413, 3), new WorldPoint(3209, 3420, 3)),
                    "Gap", "Leap", 14414),
            new Obstacle(Area.fromCorners(new WorldPoint(3193, 3415, 1), new WorldPoint(3198, 3417, 1)),
                    "Wall", "Balance", 14832),
            new Obstacle(Area.fromCorners(new WorldPoint(3192, 3402, 3), new WorldPoint(3199, 3407, 3)),
                    "Gap", "Leap", 14833),
            new Obstacle(Area.fromCorners(new WorldPoint(3182, 3382, 3), new WorldPoint(3209, 3399, 3)),
                    "Gap", "Leap", 14834),
            new Obstacle(Area.fromCorners(new WorldPoint(3218, 3393, 3), new WorldPoint(3233, 3403, 3)),
                    "Gap", "Leap", 14835),
            new Obstacle(Area.fromCorners(new WorldPoint(3236, 3403, 3), new WorldPoint(3242, 3410, 3)),
                    "Ledge", "Hurdle", 14836),
            new Obstacle(Area.fromCorners(new WorldPoint(3236, 3410, 3), new WorldPoint(3241, 3416, 3)),
                    "Edge", "Jump-off", 14841)
    ),

    CANIFIS_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3465, 3470, 0), new WorldPoint(3523, 3519, 0)),
                    "Tall tree", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(3502, 3491, 2), new WorldPoint(3512, 3499, 2)),
                    "Gap", "Jump", new WorldPoint(3505, 3498, 2)),
            new Obstacle(Area.fromCorners(new WorldPoint(3496, 3503, 2), new WorldPoint(3505, 3508, 2)),
                    "Gap", "Jump", new WorldPoint(3496, 3504, 2)),
            new Obstacle(Area.fromCorners(new WorldPoint(3485, 3498, 2), new WorldPoint(3494, 3506, 2)),
                    "Gap", "Jump", new WorldPoint(3485, 3499, 2)),
            new Obstacle(Area.fromCorners(new WorldPoint(3474, 3491, 3), new WorldPoint(3481, 3501, 3)),
                    "Gap", "Jump", new WorldPoint(3478, 3491, 3)),
            new Obstacle(Area.fromCorners(new WorldPoint(3476, 3480, 2), new WorldPoint(3485, 3488, 2)),
                    "Pole-vault", "Vault"),
            new Obstacle(Area.fromCorners(new WorldPoint(3486, 3468, 3), new WorldPoint(3505, 3480, 3)),
                    "Gap", "Jump", new WorldPoint(3503, 3476, 3)),
            new Obstacle(Area.fromCorners(new WorldPoint(3509, 3474, 2), new WorldPoint(3517, 3484, 2)),
                    "Gap", "Jump", new WorldPoint(3510, 3483, 2))
    ),

    FALADOR_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3003, 3328, 0), new WorldPoint(3060, 3364, 0)),
                    "Rough wall", "Climb", 14898),
            new Obstacle(Area.fromCorners(new WorldPoint(3036, 3342, 3), new WorldPoint(3042, 3344, 3)),
                    "Tightrope", "Cross", new WorldPoint(3040, 3343, 3)),
            new Obstacle(Area.fromCorners(new WorldPoint(3045, 3341, 3), new WorldPoint(3052, 3351, 3)),
                    "Hand holds", "Cross", new WorldPoint(3050, 3350, 3)),
            new Obstacle(Area.fromCorners(new WorldPoint(3048, 3357, 3), new WorldPoint(3051, 3359, 3)),
                    "Gap", "Jump", 14903),
            new Obstacle(Area.fromCorners(new WorldPoint(3045, 3361, 3), new WorldPoint(3049, 3368, 3)),
                    "Gap", "Jump", 14904),
            new Obstacle(Area.fromCorners(new WorldPoint(3034, 3361, 3), new WorldPoint(3042, 3365, 3)),
                    "Tightrope", "Cross", 14905),
            new Obstacle(Area.fromCorners(new WorldPoint(3026, 3352, 3), new WorldPoint(3030, 3355, 3)),
                    "Tightrope", "Cross", new WorldPoint(3026, 3353, 3)),
            new Obstacle(Area.fromCorners(new WorldPoint(3009, 3353, 3), new WorldPoint(3022, 3359, 3)),
                    "Gap", "Jump", 14919),
            new Obstacle(Area.fromCorners(new WorldPoint(3016, 3343, 3), new WorldPoint(3023, 3350, 3)),
                    "Ledge", "Jump", 14920),
            new Obstacle(Area.fromCorners(new WorldPoint(3011, 3344, 3), new WorldPoint(3015, 3347, 3)),
                    "Ledge", "Jump", 14921),
            new Obstacle(Area.fromCorners(new WorldPoint(3009, 3335, 3), new WorldPoint(3014, 3343, 3)),
                    "Ledge", "Jump", 14923),
            new Obstacle(Area.fromCorners(new WorldPoint(3012, 3331, 3), new WorldPoint(3018, 3335, 3)),
                    "Ledge", "Jump", 14924),
            new Obstacle(Area.fromCorners(new WorldPoint(3019, 3332, 3), new WorldPoint(3025, 3336, 3)),
                    "Edge", "Jump", new WorldPoint(3025, 3332, 3))
    ),

    WILDY_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(2988, 3930, 0), new WorldPoint(3006, 3941, 0)),
                    "Obstacle pipe", "Squeeze-through"),
            new Obstacle(Area.fromCorners(new WorldPoint(3003, 3948, 0), new WorldPoint(3009, 3955, 0)),
                    "Ropeswing", "Swing-on"),
            new Obstacle(Area.fromCorners(new WorldPoint(2990, 10335, 0), new WorldPoint(3009, 10366, 0)), //For if you fail obstacles and fall
                    "Ladder", "Climb-up", new WorldPoint(3005, 10363, 0)),
            new Obstacle(Area.fromCorners(new WorldPoint(2999, 3955, 0), new WorldPoint(3008, 3967, 0)),
                    "Stepping stone", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(2995, 3945, 0), new WorldPoint(3002, 3962, 0)),
                    "Log balance", "Walk-across", new WorldPoint(3001, 3945, 0)),
            new Obstacle(Area.fromCorners(new WorldPoint(2990, 3935, 0), new WorldPoint(2999, 3947, 0)),
                    "Rocks", "Climb")
    ),

    SEERS_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(2682, 3451, 0), new WorldPoint(2735, 3511, 0)), "Wall", "Climb-up", 14927),
            new Obstacle(Area.fromCorners(new WorldPoint(2721, 3490, 3), new WorldPoint(2730, 3497, 3)), "Gap", "Jump", 14928),
            new Obstacle(Area.fromCorners(new WorldPoint(2705, 3488, 2), new WorldPoint(2714, 3495, 2)), "Tightrope", "Cross", 14932),
            new Obstacle(Area.fromCorners(new WorldPoint(2710, 3477, 2), new WorldPoint(2716, 3482, 2)), "Gap", "Jump", 14929),
            new Obstacle(Area.fromCorners(new WorldPoint(2700, 3470, 3), new WorldPoint(2715, 3475, 3)), "Gap", "Jump", 14930),
            new Obstacle(Area.fromCorners(new WorldPoint(2698, 3460, 2), new WorldPoint(2703, 3475, 2)), "Edge", "Jump", 14931)
    ),

    POLLNIVNEACH_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(3344, 2900, 0), new WorldPoint(3401, 3004, 0)), "Basket", "Climb-on", new WorldPoint(3351, 2962, 0)),
            new Obstacle(Area.fromCorners(new WorldPoint(3346, 2961, 1), new WorldPoint(3352, 2969, 1)), "Market stall", "Jump-on"),
            new Obstacle(Area.fromCorners(new WorldPoint(3352, 2973, 1), new WorldPoint(3356, 2977, 1)), "Banner", "Grab"),
            new Obstacle(Area.fromCorners(new WorldPoint(3360, 2977, 1), new WorldPoint(3363, 2980, 1)), "Gap", "Leap"),
            new Obstacle(Area.fromCorners(new WorldPoint(3366, 2974, 1), new WorldPoint(3370, 2977, 1)), "Tree", "Jump-to"),
            new Obstacle(Area.fromCorners(new WorldPoint(3365, 2982, 1), new WorldPoint(3370, 2987, 1)), "Rough wall", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(3355, 2981, 2), new WorldPoint(3366, 2986, 2)), "Monkeybars", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(3357, 2990, 2), new WorldPoint(3371, 2996, 2)), "Tree", "Jump-on"),
            new Obstacle(Area.fromCorners(new WorldPoint(3356, 3000, 2), new WorldPoint(3363, 3005, 2)), "Drying line", "Jump-to")
    ),

    RELLEKKA_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(2620, 3647, 0), new WorldPoint(2676, 3682, 0)), "Rough wall", "Climb"),
            new Obstacle(Area.fromCorners(new WorldPoint(2622, 3672, 3), new WorldPoint(2627, 3677, 3)), "Gap", "Leap"),
            new Obstacle(Area.fromCorners(new WorldPoint(2615, 3658, 3), new WorldPoint(2623, 3669, 3)), "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(2626, 3651, 3), new WorldPoint(2630, 3656, 3)), "Gap", "Leap"),
            new Obstacle(Area.fromCorners(new WorldPoint(2639, 3649, 3), new WorldPoint(2644, 3654, 3)), "Gap", "Hurdle"),
            new Obstacle(Area.fromCorners(new WorldPoint(2643, 3657, 3), new WorldPoint(2651, 3663, 3)), "Tightrope", "Cross"),
            new Obstacle(Area.fromCorners(new WorldPoint(2655, 3665, 3), new WorldPoint(2664, 3686, 3)), "Pile of fish", "Jump-in")
    ),

    ARDY_COURSE(
            new Obstacle(Area.fromCorners(new WorldPoint(2647, 3286, 0), new WorldPoint(2680, 3328, 0)), "Wooden Beams", "Climb-up", 15608),
            new Obstacle(Area.fromCorners(new WorldPoint(2670, 3297, 3), new WorldPoint(2673, 3311, 3)), "Gap", "Jump", 15609),
            new Obstacle(Area.fromCorners(new WorldPoint(2660, 3316, 3), new WorldPoint(2667, 3321, 3)), "Plank", "Walk-on", 26635),
            new Obstacle(Area.fromCorners(new WorldPoint(2652, 3316, 3), new WorldPoint(2658, 3321, 3)), "Gap", "Jump", 15610),
            new Obstacle(Area.fromCorners(new WorldPoint(2652, 3309, 3), new WorldPoint(2655, 3316, 3)), "Gap", "Jump", 15611),
            new Obstacle(Area.fromCorners(new WorldPoint(2650, 3299, 3), new WorldPoint(2656, 3311, 3)), "Steep roof", "Balance-across", 28912),
            new Obstacle(Area.fromCorners(new WorldPoint(2654, 3296, 3), new WorldPoint(2659, 3301, 3)), "Gap", "Jump", 15612)
    ),

    NEAREST((Obstacle) null);

    private final Obstacle[] obstacles;

    Course(Obstacle... obstacles) {
        this.obstacles = obstacles;
    }

    public static Course getNearest() {
        var local = Players.getLocal();
        var nearest = Course.GNOME_COURSE;
        var dist = Double.MAX_VALUE;

        for (var course : values()) {
            if (course == NEAREST) continue;
            var obstacles = course.getObstacles();
            var area = obstacles[0].getArea();
            double dist2 = Area.centerOf(area).distanceTo(local.getWorldLocation());
            if (dist2 < dist) {
                dist = dist2;
                nearest = course;
            }
        }

        return nearest;
    }

    public Obstacle getNext(IPlayer player) {
        for (var obstacle : obstacles) {
            var area = obstacle.getArea();
            if (area.intersectsWith(player.getWorldArea())) {
                return obstacle;
            }
        }
        return null;
    }

    public Obstacle get(ITileObject gameObject) {
        for (var obstacle : obstacles) {
            if (gameObject.getId() == obstacle.getId())
                return obstacle;
        }

        return null;
    }

    public int getRequiredLevel() {
        switch (this) {
            case GNOME_COURSE:
                return 0;
            case DRAYNOR_COURSE:
                return 10;
            default:
                return (ordinal() + 1) * 10;
        }
    }

    @Override
    public String toString() {
        var name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }

    public Obstacle[] getObstacles() {
        return obstacles;
    }
}
