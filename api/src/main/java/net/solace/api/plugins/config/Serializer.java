package net.solace.api.plugins.config;

public interface Serializer<T> {
    public String serialize(T var1);

    public T deserialize(String var1);
}

