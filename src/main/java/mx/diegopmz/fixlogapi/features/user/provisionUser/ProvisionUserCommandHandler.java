package mx.diegopmz.fixlogapi.features.user.provisionUser;

import jakarta.transaction.Transactional;
import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;
import mx.diegopmz.fixlogapi.features.user.shared.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class ProvisionUserCommandHandler implements IProvisionUserCommandHandler {
    private final IUserWriteRepository userWriteRepository;
    private final IUserReadRepository userReadRepository;

    public ProvisionUserCommandHandler(IUserWriteRepository userWriteRepository, IUserReadRepository userReadRepository1) {
        this.userWriteRepository = userWriteRepository;
        this.userReadRepository = userReadRepository1;
    }

    @Override
    public Result<Created> handle(ProvisionUserCommand command) {
        Optional<User> existingUserOpt = userReadRepository.findByEmail(command.email());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (!existingUser.getExternalId().equals(command.externalId())) {
                return Result.failure(UserErrors.emailAlreadyInUse());
            }

            return Created.result();
        }

        Result<User> newUser = User.create(
            command.externalId(),
            command.name(),
            command.lastName(),
            command.email(),
            UserRoles.TECHNICIAN
        );

        if (!newUser.isSuccess()) {
            return Result.failure(newUser.getErrors());
        }

        userWriteRepository.save(newUser.getValue());
        return Created.result();
    }
}


