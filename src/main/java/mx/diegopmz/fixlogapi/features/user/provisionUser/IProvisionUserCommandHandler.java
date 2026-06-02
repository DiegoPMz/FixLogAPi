package mx.diegopmz.fixlogapi.features.user.provisionUser;

import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;

public interface IProvisionUserCommandHandler {
    Result<Created> handle(ProvisionUserCommand command);
}
