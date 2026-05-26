package mx.diegopmz.fixlogapi.features.order.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Mapper Unit Tests")
class OrderMapperTest {

    @Nested
    @DisplayName("Domain Model to Entity Mapping")
    class ModelToEntityTests {

        @Test
        @DisplayName("shouldMapAllFieldsToEntity_whenDomainModelIsValid")
        void shouldMapAllFieldsToEntity_whenDomainModelIsValid() {
            // Arrange
            Order domain = new Order.Builder(UUID.randomUUID(), "Pantalla rota de iPhone 13", 150.00)
                .assignedUserId(UUID.randomUUID())
                .technicalDiagnosis("Requiere cambio de módulo completo")
                .status(OrderStatus.RECIBIDO)
                .priority(OrderPriority.MEDIA)
                .finalCost(0.0)
                .build()
                .getValue();

            // Act
            OrderEntity entity = OrderMapper.toEntity(domain);

            // Assert
            assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals(domain.getId(), entity.getId()),
                () -> assertEquals(domain.getTicketNumber(), entity.getTicketNumber()),
                () -> assertEquals(domain.getDeviceId(), entity.getDeviceId()),
                () -> assertEquals(domain.getAssignedUserId(), entity.getAssignedUserId()),
                () -> assertEquals(domain.getIssueDescription(), entity.getIssueDescription()),
                () -> assertEquals(domain.getTechnicalDiagnosis(), entity.getTechnicalDiagnosis()),
                () -> assertEquals(domain.getStatus(), entity.getStatus()),
                () -> assertEquals(domain.getPriority(), entity.getPriority()),
                () -> assertEquals(domain.getEstimatedCost(), entity.getEstimatedCost()),
                () -> assertEquals(domain.getFinalCost(), entity.getFinalCost()),
                () -> assertEquals(domain.isWarranty(), entity.isWarranty()),
                () -> assertEquals(domain.getCreatedAt(), entity.getCreatedAt()),
                () -> assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt()),
                () -> assertEquals(domain.getWarrantyUntil(), entity.getWarrantyUntil())
            );
        }

        @Test
        @DisplayName("shouldReturnNull_whenDomainModelIsNull")
        void shouldReturnNull_whenDomainModelIsNull() {
            assertNull(OrderMapper.toEntity(null));
        }
    }

    @Nested
    @DisplayName("Entity to Domain Model Mapping")
    class EntityToModelTests {

        @Test
        @DisplayName("shouldRehydrateDomainModel_whenEntityContainsData")
        void shouldRehydrateDomainModel_whenEntityContainsData() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            UUID deviceId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            LocalDateTime created = LocalDateTime.now().minusDays(5);
            LocalDateTime updated = LocalDateTime.now().minusDays(1);
            LocalDateTime warranty = created.plusDays(30);

            OrderEntity entity = new OrderEntity();
            entity.setId(orderId);
            entity.setTicketNumber("TKT-2026-0042");
            entity.setDeviceId(deviceId);
            entity.setAssignedUserId(userId);
            entity.setIssueDescription("Mantenimiento preventivo PC");
            entity.setTechnicalDiagnosis("Limpieza de polvo y cambio de pasta térmica");
            entity.setStatus(OrderStatus.ESPERANDO_REPUESTOS);
            entity.setPriority(OrderPriority.ALTA);
            entity.setEstimatedCost(45.50);
            entity.setFinalCost(45.50);
            entity.setWarranty(true);
            entity.setCreatedAt(created);
            entity.setUpdatedAt(updated);
            entity.setWarrantyUntil(warranty);

            // Act
            Order domain = OrderMapper.toDomain(entity);

            // Assert
            assertAll(
                () -> assertNotNull(domain),
                () -> assertEquals(orderId, domain.getId()),
                () -> assertEquals("TKT-2026-0042", domain.getTicketNumber()),
                () -> assertEquals(deviceId, domain.getDeviceId()),
                () -> assertEquals(userId, domain.getAssignedUserId()),
                () -> assertEquals("Mantenimiento preventivo PC", domain.getIssueDescription()),
                () -> assertEquals(OrderStatus.ESPERANDO_REPUESTOS, domain.getStatus()),
                () -> assertTrue(domain.isWarranty()),
                () -> assertEquals(created, domain.getCreatedAt()),
                () -> assertEquals(updated, domain.getUpdatedAt()),
                () -> assertEquals(warranty, domain.getWarrantyUntil())
            );
        }

        @Test
        @DisplayName("shouldReturnNull_whenEntityIsNull")
        void shouldReturnNull_whenEntityIsNull() {
            assertNull(OrderMapper.toDomain(null));
        }
    }
}
