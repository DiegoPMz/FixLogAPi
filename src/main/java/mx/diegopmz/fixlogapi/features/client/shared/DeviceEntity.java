package mx.diegopmz.fixlogapi.features.client.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "devices")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
public class DeviceEntity {
    @Id
    private UUID id;

    @Column(length = 100, nullable = false, updatable = false)
    private String brand;

    @Column(length = 100, nullable = false, updatable = false)
    private String model;

    @Column(name = "serial_number", length = 150, nullable = false, unique = true, updatable = false)
    private String serialNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
