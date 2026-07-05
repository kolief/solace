package net.solace.api.ui;

import java.awt.Color;

public class ColorScheme {
    /** Crimson — primary Solace accent (#EF4444). */
    public static final Color BRAND_CRIMSON = new Color(0xEF4444);
    /** Lighter crimson for hover / active states (#F87171). */
    public static final Color BRAND_CRIMSON_HOVER = new Color(0xF87171);
    /** Crimson at ~40% opacity for overlays. */
    public static final Color BRAND_CRIMSON_TRANSPARENT = new Color(239, 68, 68, 102);

    /** OSRS in-game &lt;col&gt; tag hex (no # prefix). */
    public static final String BRAND_HEX = "EF4444";

    public static String brandCol(String text) {
        return "<col=" + BRAND_HEX + ">" + text + "</col>";
    }
}
