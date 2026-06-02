package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_summary_mview")
@Immutable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientSummaryViewEntity {
    @Id
    private UUID clientId;
    private String name;
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "total_orders")
    private long totalOrders;

    @Column(name = "total_spent")
    private double totalSpent;
}
