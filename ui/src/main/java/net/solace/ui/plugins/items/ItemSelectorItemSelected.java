package net.solace.ui.plugins.items;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Data
@RequiredArgsConstructor
public class ItemSelectorItemSelected {
    private final String configGroup;
    private final String configKey;
    private final int id;
    private final String name;
    private Consumer<ItemSelector> consumer;
}