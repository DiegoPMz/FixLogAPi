package mx.diegopmz.fixlogapi.features.order.createOrder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.common.domain.result.Success;
import mx.diegopmz.fixlogapi.features.order.shared.IClientDeviceValidator;
import mx.diegopmz.fixlogapi.features.order.shared.IOrderWriteRepository;
import mx.diegopmz.fixlogapi.features.order.shared.Order;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateOrderCommandHandler implements ICreateOrderCommandHandler {
    private final IOrderWriteRepository writeRepository;
    private final IClientDeviceValidator deviceValidator;

    @Override
    public Result<Created> handle(CreateOrderCommand command) {
        Result<Success> alignmentResult = deviceValidator.validateOwnership(
            command.clientId(), command.deviceId()
        );

        if (!alignmentResult.isSuccess()) {
            return Result.failure(alignmentResult.getErrors());
        }

        Result<Order> newOrder = new Order.Builder(
            command.deviceId(),
            command.issueDescription(),
            command.estimatedCost()
        ).technicalDiagnosis(command.technicalObservations())
            .assignedUserId(command.assignedUserId())
            .build();

        if (!newOrder.isSuccess()) {
            return Result.failure(newOrder.getErrors());
        }

        writeRepository.save(newOrder.getValue());
        return Created.result();
    }
}

