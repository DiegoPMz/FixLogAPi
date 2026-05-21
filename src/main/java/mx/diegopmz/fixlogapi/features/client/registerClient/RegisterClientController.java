package mx.diegopmz.fixlogapi.features.client.registerClient;

import jakarta.validation.Valid;
import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.common.http.HttpErrorTranslator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/clients")
public class RegisterClientController {
    private final IRegisterClientCommandHandler commandHandler;

    public RegisterClientController(IRegisterClientCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping
    public ResponseEntity<?> registerClient(@Valid @RequestBody RegisterClientCommand command) {
        Result<Created> result = commandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.noContent().build();
        }

        AppError error = result.firstError().get();
        return HttpErrorTranslator.handle(error);
    }
}

