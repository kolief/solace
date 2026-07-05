package net.solace.loader.plugins.profiles.data;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.Static;

@Slf4j
public class JLUtil {


    public static AccountData getAccountFromClient() {
        String displayName = Static.getClient().getDisplayName();
        String sessionId = Static.getClient().getSessionId();
        String charId = Static.getClient().getCharacterId();

        if (displayName == null || sessionId == null || charId == null) {
            return null;
        }

        return new AccountData("", charId, sessionId, displayName);
    }

    public static void setAccountToClient(AccountData data) {
        if (data == null) {
            // Clear all fields
            Static.getClient().setDisplayName(null);
            Static.getClient().setSessionId(null);
            Static.getClient().setCharacterId(null);
        } else {
            Static.getClient().setCharacterId(data.getCharacterId());
            Static.getClient().setSessionId(data.getSessionId());
            Static.getClient().setDisplayName(data.getDisplayName());
        }
    }

    public static void prepJlLogin() {
        Static.getClient().setOAuthLoginMode();
    }

    public static void prepNormalLogin() {
        Static.getClient().setNormalLoginMode();
    }
}