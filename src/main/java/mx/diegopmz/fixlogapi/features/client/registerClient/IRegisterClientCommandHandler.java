package mx.diegopmz.fixlogapi.features.client.registerClient;

import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;

public interface IRegisterClientCommandHandler {
    Result<Created> handle(RegisterClientCommand command);
}
