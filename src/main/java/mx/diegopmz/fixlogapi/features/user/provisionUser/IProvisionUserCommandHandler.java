package mx.diegopmz.fixlogapi.features.user.provisionUser;

import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

public interface IProvisionUserCommandHandler {
    Result<Created> handle(ProvisionUserCommand command);
}
