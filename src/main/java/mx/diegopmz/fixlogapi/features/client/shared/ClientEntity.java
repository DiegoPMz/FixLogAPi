package mx.diegopmz.fixlogapi.features.client.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@NoArgsConstructor
@Getter
@Setter
public class ClientEntity {
    @Id
    private UUID id;

    @Column(length = 250, nullable = false)
    private String name;

    @Column(name = "phone_number", length = 15, unique = true, nullable = false)
    private String phoneNumber;

    @Column(length = 250, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private List<DeviceEntity> devices = new ArrayList<>();
}
