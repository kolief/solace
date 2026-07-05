package net.solace.api.account;

public class GameAccount {
    private final String username;
    private final String password;
    private String displayName;
    private String auth;
    private String bankPin;

    public boolean isJagexLauncher() {
        return this.username != null && this.password != null && this.displayName != null;
    }

    public GameAccount(String username, String password, String displayName, String auth, String bankPin) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.auth = auth;
        this.bankPin = bankPin;
    }

    public GameAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getAuth() {
        return this.auth;
    }

    public String getBankPin() {
        return this.bankPin;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setBankPin(String bankPin) {
        this.bankPin = bankPin;
    }
}

