package net.solace.loader.plugins.profiles.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import net.solace.api.plugins.DoNotRename;

@Data
@Getter
@DoNotRename
public class AccountData {
    @SerializedName("name")
    private final String name;
    @SerializedName("username")
    private final String username;
    @SerializedName("password")
    private final String password;
    @SerializedName("type")
    private final AccountType type;

    // For JL accounts
    @SerializedName("characterId")
    private final String characterId;
    @SerializedName("sessionId")
    private final String sessionId;
    @SerializedName("displayName")
    private final String displayName;

    public AccountData(String name, String username, String password, AccountType type,
                       String characterId, String sessionId, String displayName) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.type = type;
        this.characterId = characterId;
        this.sessionId = sessionId;
        this.displayName = displayName;
    }

    public AccountData(String name, String username, String password) {
        this(name.isEmpty() ? username : name, username, password, AccountType.NORMAL, null, null, null);
    }

    public AccountData(String name, String characterId, String sessionId, String displayName) {
        this(name.isEmpty() ? displayName : name, null, null, AccountType.JL, characterId, sessionId, displayName);
    }

    public enum AccountType {
        NORMAL,
        JL
    }

}