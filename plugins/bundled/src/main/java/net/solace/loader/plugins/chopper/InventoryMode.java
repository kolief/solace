package net.solace.loader.plugins.chopper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum InventoryMode {
    DROP,
    FLETCH,
    FIRE,
    BANK

}
