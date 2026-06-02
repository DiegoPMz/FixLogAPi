package mx.diegopmz.fixlogapi.features.order.createOrder;

import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;

public interface ICreateOrderCommandHandler {
    Result<Created> handle(CreateOrderCommand command);
}
