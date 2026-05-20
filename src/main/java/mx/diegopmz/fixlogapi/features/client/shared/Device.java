package mx.diegopmz.fixlogapi.features.client.shared;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Device {
    private final UUID id;
    private final String brand;
    private final String model;
    private final String serialNumber;

    private final LocalDateTime createdAt;
    
    private Device(String brand, String model, String serialNumber) {
        this.id = UUID.randomUUID();

        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;

        this.createdAt = LocalDateTime.now();
    }

    static Result<Device> create(String brand, String model, String serialNumber) {
        if (brand == null || brand.trim().isEmpty()) {
            return Result.failure(DeviceErrors.brandRequired());
        }

        if (model == null || model.trim().isEmpty()) {
            return Result.failure(DeviceErrors.modelRequired());
        }

        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            return Result.failure(DeviceErrors.serialNumberRequired());
        }

        String cleanedSerialNumber = serialNumber.trim();
        if (cleanedSerialNumber.length() < 4) {
            return Result.failure(DeviceErrors.serialNumberTooShort());
        }

        return Result.ok(new Device(
            brand.trim(),
            model.trim(),
            cleanedSerialNumber
        ));
    }
}
