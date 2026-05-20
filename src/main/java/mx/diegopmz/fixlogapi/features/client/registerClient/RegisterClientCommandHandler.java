package mx.diegopmz.fixlogapi.features.client.registerClient;

import jakarta.transaction.Transactional;
import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.features.client.shared.Client;
import mx.diegopmz.fixlogapi.features.client.shared.IClientReadRepository;
import mx.diegopmz.fixlogapi.features.client.shared.IClientWriteRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RegisterClientCommandHandler implements IRegisterClientCommandHandler {
    private final IClientWriteRepository writeRepository;
    private final IClientReadRepository readRepository;

    public RegisterClientCommandHandler(IClientWriteRepository writeRepository, IClientReadRepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public Result<Created> handle(RegisterClientCommand command) {
        if (readRepository.existsByPhoneNumber(command.phoneNumber())) {
            return Created.result();
        }

        Result<Client> clientResult = Client.create(
            command.name(),
            command.phoneNumber(),
            command.email()
        );

        if (!clientResult.isSuccess()) {
            return Result.failure(clientResult.getErrors());
        }

        writeRepository.save(clientResult.getValue());
        return Created.result();
    }
}
