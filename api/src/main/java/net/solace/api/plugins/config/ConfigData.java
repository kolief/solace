package net.solace.api.plugins.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigData {
    private static final Logger log = LoggerFactory.getLogger(ConfigData.class);
    private final File configPath;
    private final ConcurrentHashMap<String, String> properties;
    private Map<String, String> patchChanges = new HashMap<String, String>();

    public ConfigData(File configPath) {
        this.configPath = configPath;
        Properties props = new Properties();
        try (FileInputStream in2 = new FileInputStream(configPath);
             InputStreamReader reader = new InputStreamReader((InputStream)in2, StandardCharsets.UTF_8);){
            props.load(reader);
        }
        catch (FileNotFoundException in2) {
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.properties = new ConcurrentHashMap(props.size());
        props.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(k, v) -> this.properties.put((String)k, (String)v)));
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    public synchronized String setProperty(String key, String value) {
        String old = this.properties.put(key, value);
        if (!Objects.equals(old, value)) {
            this.patchChanges.put(key, value);
        }
        return old;
    }

    public synchronized String unset(String key) {
        String old = this.properties.remove(key);
        if (old != null) {
            this.patchChanges.put(key, null);
        }
        return old;
    }

    public synchronized void putAll(Map<String, String> values) {
        this.patchChanges.putAll(values);
        this.properties.putAll(values);
    }

    public Set<String> keySet() {
        return this.properties.keySet();
    }

    public Map<String, String> get() {
        return Collections.unmodifiableMap(this.properties);
    }

    public synchronized Map<String, String> swapChanges() {
        if (this.patchChanges.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> p = this.patchChanges;
        this.patchChanges = new HashMap<String, String>();
        return p;
    }

    public void patch(Map<String, String> patch) {
        File lckFile = new File(this.configPath.getParentFile(), this.configPath.getName() + ".lck");
        try (FileOutputStream lockOut = new FileOutputStream(lckFile);
             FileChannel lckChannel = lockOut.getChannel();){
            lckChannel.lock();
            Properties tempProps = new Properties();
            try (FileInputStream in = new FileInputStream(this.configPath);
                 InputStreamReader reader = new InputStreamReader((InputStream)in, StandardCharsets.UTF_8);){
                tempProps.load(reader);
            }
            catch (FileNotFoundException e) {
                log.debug("config file {} does not exist", (Object)this.configPath);
            }
            if (tempProps.isEmpty()) {
                tempProps.putAll((Map<?, ?>)this.properties);
            } else {
                for (Map.Entry<String, String> entry : patch.entrySet()) {
                    if (entry.getValue() == null) {
                        tempProps.remove(entry.getKey());
                        continue;
                    }
                    tempProps.put(entry.getKey(), entry.getValue());
                }
            }
            File tempFile = File.createTempFile("runelite_config", null, this.configPath.getParentFile());
            try (FileOutputStream out = new FileOutputStream(tempFile);
                 FileChannel channel = out.getChannel();
                 OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, StandardCharsets.UTF_8);){
                channel.lock();
                tempProps.store(writer, "RuneLite configuration");
                writer.flush();
                channel.force(true);
            }
            try {
                Files.move(tempFile.toPath(), this.configPath.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                log.debug("Saved configuration file {}", (Object)this.configPath);
            }
            catch (AtomicMoveNotSupportedException ex) {
                log.debug("atomic move not supported", (Throwable)ex);
                Files.move(tempFile.toPath(), this.configPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex) {
            log.error("unable to save configuration file", (Throwable)ex);
        }
        lckFile.delete();
    }
}

