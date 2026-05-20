package mx.diegopmz.fixlogapi.features.client.shared;

import lombok.AccessLevel;
import lombok.Getter;
import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Client {
    private final UUID id;
    private final LocalDateTime createdAt;

    @Getter(AccessLevel.NONE)
    private final List<Device> internalDevices;
    @Getter
    private final List<Device> devices;

    private String phoneNumber;
    private String email;
    private String name;
    private LocalDateTime updatedAt;

    private Client(String name, String phoneNumber, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;

        this.internalDevices = new ArrayList<>();
        this.devices = Collections.unmodifiableList(this.internalDevices);

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    Client(UUID id, String name, String phoneNumber, String email,
           List<Device> historicalDevices, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.internalDevices = new ArrayList<>(historicalDevices != null ? historicalDevices : Collections.emptyList());
        this.devices = Collections.unmodifiableList(this.internalDevices);
    }

    public static Result<Client> create(String name, String phoneNumber, String email) {
        if (name == null || name.trim().isEmpty()) {
            return Result.failure(ClientErrors.nameRequired());
        }

        if (phoneNumber == null) {
            return Result.failure(ClientErrors.phoneNumberRequired());
        }

        if (phoneNumber.length() != 10) {
            return Result.failure(ClientErrors.invalidPhoneNumberLength(phoneNumber));
        }

        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Result.failure(ClientErrors.invalidEmail(email));
        }

        if (email != null) {
            email = email.toLowerCase().trim();
        }

        return Result.ok(new Client(name.trim(), phoneNumber, email));
    }

    public Result<Created> registerDevice(String brand, String model, String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            return Result.failure(DeviceErrors.serialNumberRequired());
        }

        boolean isDuplicate = this.internalDevices.stream()
            .anyMatch(d -> serialNumber.equals(d.getSerialNumber()));

        if (isDuplicate) {
            return Result.failure(ClientErrors.deviceAlreadyRegistered(serialNumber));
        }

        Result<Device> deviceResult = Device.create(brand, model, serialNumber);
        if (!deviceResult.isSuccess()) {
            return Result.failure(deviceResult.getErrors());
        }

        this.internalDevices.add(deviceResult.getValue());
        this.updatedAt = LocalDateTime.now();

        return Created.result();
    }
}