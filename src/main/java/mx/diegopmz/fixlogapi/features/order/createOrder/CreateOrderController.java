package mx.diegopmz.fixlogapi.features.order.createOrder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.http.HttpErrorTranslator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class CreateOrderController {
    private final ICreateOrderCommandHandler commandHandler;

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        var result = commandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.noContent().build();
        }

        AppError error = result.firstError().get();
        return HttpErrorTranslator.handle(error);
    }
}
