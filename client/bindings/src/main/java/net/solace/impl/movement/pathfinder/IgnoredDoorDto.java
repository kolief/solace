package net.solace.impl.movement.pathfinder;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.IgnoredDoor;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

@RequiredArgsConstructor
@Getter
public final class IgnoredDoorDto {
    @SerializedName("source")
    private final WorldPoint source;
    @SerializedName("destination")
    private final WorldPoint destination;
    @SerializedName("requireMembers")
    private final Boolean requireMembers;
    @SerializedName("requirements")
    private final Requirements requirements;

    public IgnoredDoor toIgnoredDoor() {
        return IgnoredDoor.builder()
                .source(source)
                .destination(destination)
                .requireMembers(requireMembers != null && requireMembers)
                .requirements(requirements)
                .build();
    }
}