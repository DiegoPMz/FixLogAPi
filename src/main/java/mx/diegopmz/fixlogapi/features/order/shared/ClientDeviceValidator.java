package mx.diegopmz.fixlogapi.features.order.shared;

import lombok.RequiredArgsConstructor;
import mx.diegopmz.fixlogapi.common.result.Result;
import mx.diegopmz.fixlogapi.common.result.Success;
import mx.diegopmz.fixlogapi.features.client.shared.ClientErrors;
import mx.diegopmz.fixlogapi.features.client.shared.ClientReadModel;
import mx.diegopmz.fixlogapi.features.client.shared.IClientReadRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientDeviceValidator implements IClientDeviceValidator {
    private final IClientReadRepository clientReadRepository;

    @Override
    public Result<Success> validateOwnership(UUID clientId, UUID deviceId) {
        Optional<ClientReadModel> client = clientReadRepository.findById(clientId);

        if (client.isEmpty()) {
            return Result.failure(ClientErrors.clientNotFound(clientId));
        }

        boolean ownsDevice = client.get().devices().stream()
            .anyMatch(device -> device.id().equals(deviceId));

        if (!ownsDevice) {
            return Result.failure(ClientErrors.deviceNotOwnedByClient(deviceId, clientId));
        }

        return Success.result();
    }
}
