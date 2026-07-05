package net.solace.api.commons;

import net.runelite.client.util.Text;

public class JagStrings {
    public static String standardize(String str) {
        if (str == null) {
            return null;
        }
        String removedTags = Text.removeTags((String)str);
        if (removedTags == null) {
            return null;
        }
        return Text.sanitize((String)removedTags);
    }

    public static String[] standardize(String ... strs) {
        if (strs == null) {
            return null;
        }
        String[] sanitized = new String[strs.length];
        for (int i = 0; i < sanitized.length; ++i) {
            sanitized[i] = JagStrings.standardize(strs[i]);
        }
        return sanitized;
    }
}

