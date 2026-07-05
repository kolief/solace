package net.solace.impl.movement.pathfinder;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

@RequiredArgsConstructor
@Getter
public final class TransportDto {
    @SerializedName("source")
    private final WorldPoint source;
    @SerializedName("destination")
    private final WorldPoint destination;
    @SerializedName("action")
    private final String action;
    @SerializedName("objectId")
    private final Integer objectId;
    @SerializedName("requirements")
    private final Requirements requirements;
    @SerializedName("dialogs")
    private final String[] dialogs;
    @SerializedName("type")
    private TransportType type;
    @SerializedName("delay")
    private Integer delay;

    public Transport toTransport(ITransportLoader transportLoader) {
        if (type == null) {
            type = TransportType.OBJECT;
        }

        if (delay == null) {
            delay = 1;
        }

        Transport transport;
        if (dialogs != null && dialogs.length > 0) {
            if (type == TransportType.NPC) {
                transport = transportLoader.npcDialogTransport(source, destination, objectId, requirements, dialogs);
            } else {
                transport = transportLoader.objectDialogTransport(source, destination, objectId, action, requirements, dialogs);
            }
        } else if (type == TransportType.NPC) {
            transport = transportLoader.npcTransport(source, destination, objectId, requirements, action);
        } else {
            transport = transportLoader.objectTransport(source, destination, objectId, action, requirements);
        }

        transport.setDelay(delay);
        transport.setWeight(4);
        return transport;
    }

    private enum TransportType {
        OBJECT,
        NPC,
    }
}