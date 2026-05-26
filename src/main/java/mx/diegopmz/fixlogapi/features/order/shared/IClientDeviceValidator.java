package mx.diegopmz.fixlogapi.features.order.shared;

import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.common.domain.result.Success;

import java.util.UUID;

public interface IClientDeviceValidator {
    Result<Success> validateOwnership(UUID clientId, UUID deviceId);
}
