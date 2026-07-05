package net.solace.loader.plugins.profiles.panel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.solace.loader.plugins.profiles.data.AccountData;
import net.solace.loader.plugins.profiles.data.AccountManager;
import net.solace.loader.plugins.profiles.data.JLUtil;
import net.solace.loader.plugins.profiles.panel.dialog.AccountSelectionDialog;
import net.solace.loader.plugins.profiles.panel.dialog.CustomNameDialog;
import net.solace.loader.plugins.profiles.panel.dialog.PasswordDialog;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Slf4j
public class AccountImporter {
    private final AccountManager accountManager;

    public AccountImporter(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void importFromFile() throws Exception {
        File credentialsFile = new File(RuneLite.RUNELITE_DIR, "credentials.properties");
        if (!credentialsFile.exists()) {
            throw new FileNotFoundException("No credentials.properties file found");
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(credentialsFile)) {
            props.load(fis);
        }

        String characterId = props.getProperty("JX_CHARACTER_ID");
        String sessionId = props.getProperty("JX_SESSION_ID");
        String displayName = props.getProperty("JX_DISPLAY_NAME");

        if (characterId == null || sessionId == null || displayName == null) {
            throw new IllegalStateException("Missing required properties in credentials file");
        }

        AccountData account = new AccountData("", characterId, sessionId, displayName);
        showImportNameDialog(account);
    }

    public void importFromClient() throws Exception {
        AccountData clientAccount = JLUtil.getAccountFromClient();
        if (clientAccount == null) {
            throw new IllegalStateException("No JL account data found in client");
        }

        showImportNameDialog(clientAccount);
    }

    public void importFromLauncher() throws Exception {
        PasswordDialog passwordDialog = new PasswordDialog(null, "Enter Launcher Password");
        passwordDialog.setVisible(true);

        if (!passwordDialog.isConfirmed()) {
            return;
        }

        List<AccountData> accounts = decryptLauncherAccounts(passwordDialog.getPassword());
        showAccountSelectionDialog(accounts);
    }

    private void showImportNameDialog(AccountData account) {
        CustomNameDialog dialog = new CustomNameDialog(null, account);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                String customName = dialog.getCustomName();
                String accountName = customName.isEmpty() ? account.getName() : customName;

                AccountData newAccount;
                if (account.getType() == AccountData.AccountType.JL) {
                    newAccount = new AccountData(accountName, account.getCharacterId(),
                            account.getSessionId(), account.getDisplayName());
                } else {
                    newAccount = new AccountData(accountName, account.getUsername(),
                            account.getPassword());
                }

                accountManager.validateAndAddAccount(newAccount);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to import account: " + ex.getMessage());
            }
        }
    }

    private List<AccountData> decryptLauncherAccounts(char[] password) throws Exception {
        File solaceDir = new File(System.getProperty("user.home"), ".solace");
        File cacheDir = new File(solaceDir, "cache");
        File accountsFile = new File(cacheDir, "accounts.json");

        if (!accountsFile.exists()) {
            throw new FileNotFoundException("No launcher accounts file found");
        }

        List<AccountData> accounts = new ArrayList<>();
        try (FileReader reader = new FileReader(accountsFile)) {
            String[] encryptedStrings = new Gson().fromJson(reader, String[].class);
            if (encryptedStrings == null || encryptedStrings.length == 0) {
                throw new IllegalStateException("No accounts found in launcher file");
            }

            for (String encryptedString : encryptedStrings) {
                byte[] encryptedData = Base64.getDecoder().decode(encryptedString);
                byte[] decryptedData = decryptLauncherData(encryptedData, password);
                String json = new String(decryptedData);

                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                AccountData account = parseJsonToAccount(jsonObject);
                accounts.add(account);

                log.info("Decrypted account: {}", account);
            }
        }
        return accounts;
    }

    private AccountData parseJsonToAccount(JsonObject jsonObject) {
        if (jsonObject.has("characterId") && jsonObject.has("sessionId") && jsonObject.has("displayName")) {
            String displayName = jsonObject.get("displayName").getAsString();
            return new AccountData(
                    displayName,
                    jsonObject.get("characterId").getAsString(),
                    jsonObject.get("sessionId").getAsString(),
                    displayName
            );
        } else {
            String username = jsonObject.get("username").getAsString();
            return new AccountData(
                    username,
                    username,
                    jsonObject.get("password").getAsString()
            );
        }
    }

    private byte[] decryptLauncherData(byte[] encryptedData, char[] password) throws Exception {
        byte[] iv = "~fGZsgJT74o#9bY@D<6Hp59cgcW{tJ".getBytes();

        PBEKeySpec spec = new PBEKeySpec(password, iv, 65536, 256);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
        SecretKey secretKey = new SecretKeySpec(key, "AES");

        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        byte[] cipherBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherBytes);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        return cipher.doFinal(cipherBytes);
    }

    private void showAccountSelectionDialog(List<AccountData> accounts) {
        AccountSelectionDialog dialog = new AccountSelectionDialog(null, accounts, accountManager);
        dialog.setVisible(true);
    }
}