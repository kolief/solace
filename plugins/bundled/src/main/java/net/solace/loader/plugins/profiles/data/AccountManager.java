package net.solace.loader.plugins.profiles.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AccountManager {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public final File MASTER_FILE;
    public final File ACCOUNTS_FILE;
    private SecretKey masterKey;

    private List<AccountData> decryptedAccounts = null;

    public AccountManager() {
        MASTER_FILE = new File(RuneLite.RUNELITE_DIR, "account-switcher/master.data");
        ACCOUNTS_FILE = new File(RuneLite.RUNELITE_DIR, "account-switcher/accounts.data");
    }

    public boolean isMasterPasswordSet() {
        return MASTER_FILE.exists() && MASTER_FILE.length() >= 20;
    }

    public void setMasterPassword(char[] password) throws Exception {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (password.length < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (String.valueOf(password).trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be only whitespace");
        }

        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        this.masterKey = generateKey(password, salt);

        MASTER_FILE.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(MASTER_FILE)) {
            fos.write(salt);
            fos.write(encrypt("verification".getBytes()));
        }

        decryptedAccounts = new ArrayList<>();
        saveAccountsEncrypted();
    }

    public boolean unlock(char[] password) throws Exception {
        if (!isMasterPasswordSet()) return false;

        try (FileInputStream fis = new FileInputStream(MASTER_FILE)) {
            byte[] salt = new byte[16];
            fis.read(salt);

            this.masterKey = generateKey(password, salt);

            byte[] encrypted = fis.readAllBytes();
            byte[] decrypted = decrypt(encrypted);

            if (!new String(decrypted).equals("verification")) {
                this.masterKey = null;
                return false;
            }

            // Load accounts if verification succeeded
            loadAccountsEncrypted();
            return true;
        }
    }

    private void loadAccountsEncrypted() throws Exception {
        if (!ACCOUNTS_FILE.exists()) {
            decryptedAccounts = new ArrayList<>();
            return;
        }

        byte[] encrypted = Files.readAllBytes(ACCOUNTS_FILE.toPath());
        byte[] decrypted = decrypt(encrypted);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<AccountData>>() {
        }.getType();
        decryptedAccounts = gson.fromJson(new String(decrypted), listType);
//        log.info("Decrypted accounts: {}", decryptedAccounts);
    }

    private void saveAccountsEncrypted() throws Exception {
        if (masterKey == null || decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }

        Gson gson = new Gson();
        String json = gson.toJson(decryptedAccounts);
        byte[] encrypted = encrypt(json.getBytes());

        ACCOUNTS_FILE.getParentFile().mkdirs();
        Files.write(ACCOUNTS_FILE.toPath(), encrypted);
    }

    public void addAccount(AccountData account) throws Exception {
        if (masterKey == null || decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }

        decryptedAccounts.add(account);
        saveAccountsEncrypted();
    }

    public void deleteAccount(AccountData account) throws Exception {
        if (masterKey == null || decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }

        decryptedAccounts.removeIf(a ->
                Objects.equals(account.getName(), a.getName())
        );
        saveAccountsEncrypted();
    }

    public List<AccountData> getAccounts() {
        if (masterKey == null || decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }
        return new ArrayList<>(decryptedAccounts);
    }

    public boolean accountExists(String name) {
        if (decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }
        return decryptedAccounts.stream()
                .anyMatch(acc -> acc.getName().equalsIgnoreCase(name));
    }

    public boolean accountIdExists(String characterId) {
        if (decryptedAccounts == null) {
            throw new IllegalStateException("Manager not unlocked");
        }
        return decryptedAccounts.stream()
                .anyMatch(acc -> acc.getCharacterId() != null && acc.getCharacterId().equalsIgnoreCase(characterId));
    }

    public void logout() {
        masterKey = null;
        decryptedAccounts = null;
    }

    public void renameAccount(AccountData oldAccount, String newName) throws Exception {
        if (accountExists(newName)) {
            throw new IllegalArgumentException("An account with this name already exists");
        }

        // Create new account with updated name
        AccountData newAccount;
        if (oldAccount.getType() == AccountData.AccountType.JL) {
            newAccount = new AccountData(newName,
                    oldAccount.getCharacterId(),
                    oldAccount.getSessionId(),
                    oldAccount.getDisplayName());
        } else {
            newAccount = new AccountData(newName,
                    oldAccount.getUsername(),
                    oldAccount.getPassword());
        }

        deleteAccount(oldAccount);
        addAccount(newAccount);
    }

    public void validateAndAddAccount(AccountData account) throws Exception {
        if (account.getName().isBlank()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }

        if (accountExists(account.getName())) {
            throw new IllegalArgumentException("An account with the name '" + account.getName() + "' already exists");
        }

        if (account.getType() == AccountData.AccountType.NORMAL) {
            validateNormalAccount(account);
        } else {
            validateJLAccount(account);
        }

        addAccount(account);
    }

    private void validateNormalAccount(AccountData account) {
        if (account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (account.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (accountExists(account.getUsername())) {
            throw new IllegalArgumentException("An account with the username '" + account.getUsername() + "' already exists");
        }
    }


    private void validateJLAccount(AccountData account) {
        if (account.getCharacterId() == null || account.getCharacterId().isBlank()) {
            throw new IllegalArgumentException("Character ID cannot be empty");
        }
        if (account.getSessionId() == null || account.getSessionId().isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }
        if (account.getDisplayName() == null || account.getDisplayName().isBlank()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        if (accountIdExists(account.getCharacterId())) {
            throw new IllegalArgumentException("An account with this character ID already exists");
        }
    }


    private SecretKey generateKey(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] encrypt(byte[] data) throws Exception {
        if (masterKey == null) {
            throw new IllegalStateException("No master key available");
        }

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, spec);

        byte[] encrypted = cipher.doFinal(data);

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return combined;
    }

    private byte[] decrypt(byte[] combined) throws Exception {
        if (masterKey == null) {
            throw new IllegalStateException("No master key available");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] encrypted = Arrays.copyOfRange(combined, 12, combined.length);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, masterKey, spec);

        return cipher.doFinal(encrypted);
    }
}
