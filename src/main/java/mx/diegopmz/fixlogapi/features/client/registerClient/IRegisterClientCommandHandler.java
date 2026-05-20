package mx.diegopmz.fixlogapi.features.client.registerClient;

import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

public interface IRegisterClientCommandHandler {
    Result<Created> handle(RegisterClientCommand command);
}
