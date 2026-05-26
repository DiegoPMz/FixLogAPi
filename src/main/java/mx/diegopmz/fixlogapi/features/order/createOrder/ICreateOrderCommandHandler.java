package mx.diegopmz.fixlogapi.features.order.createOrder;

import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

public interface ICreateOrderCommandHandler {
    Result<Created> handle(CreateOrderCommand command);
}
