package aurora.supply_wok.platform.purchasing;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PurchaseOrdersTenantIsolationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Value("${authorization.jwt.secret:local-dev-secret-key-change-me-12345678901234567890}")
    private String jwtSecret;

    private Long supplier1Id;
    private Long supplier2Id;

    @BeforeEach
    void setUp() {
        Supplier sup1 = supplierRepository.findByEmailIgnoreCase("proveedor1@ejemplo.com")
                .orElseGet(() -> supplierRepository.save(new Supplier(
                        UUID.randomUUID(), "Supplier Uno", "Contact 1", "proveedor1@ejemplo.com",
                        "+51900000001", "Insumos", "2026-01-01", "99%", "24h")));
        supplier1Id = sup1.getId();

        Supplier sup2 = supplierRepository.findByEmailIgnoreCase("proveedor2@ejemplo.com")
                .orElseGet(() -> supplierRepository.save(new Supplier(
                        UUID.randomUUID(), "Supplier Dos", "Contact 2", "proveedor2@ejemplo.com",
                        "+51900000002", "Carnes", "2026-01-01", "95%", "48h")));
        supplier2Id = sup2.getId();
    }

    private String generateToken(Long userId, String email, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void createPurchaseOrder_whenTenantIdInToken_assignsTenantFromTokenIgnoringBody() throws Exception {
        // Arrange
        Long tokenRestaurantId = 101L;
        String token = generateToken(tokenRestaurantId, "restaurante101@ejemplo.com", List.of("RESTAURANT"));
        String payload = """
                {
                    "code": "PO-TENANT-TOKEN-1",
                    "supplierId": %d,
                    "supplierName": "Supplier Uno",
                    "restaurantName": "Restaurante Central",
                    "orderDate": "2026-05-10",
                    "estimatedDate": "2026-05-12",
                    "priority": "High",
                    "status": "Pending",
                    "restaurantId": 9999,
                    "items": [
                        {
                            "inventoryItemId": 1,
                            "productName": "Arroz Especial",
                            "quantity": 10.0,
                            "unitPrice": 3.5,
                            "unitType": "KG"
                        }
                    ]
                }
                """.formatted(supplier1Id);

        // Act & Assert
        mockMvc.perform(post("/api/v1/purchase-orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("PO-TENANT-TOKEN-1"));

        var created = purchaseOrderRepository.findAllByRestaurantId(tokenRestaurantId);
        assertThat(created).anyMatch(po -> "PO-TENANT-TOKEN-1".equals(po.getCode())
                && tokenRestaurantId.equals(po.getRestaurantId()));
    }

    @Test
    void createPurchaseOrder_whenCallerIsSupplier_returnsForbidden() throws Exception {
        // Arrange
        String token = generateToken(201L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "code": "PO-WRONG-ROLE-1",
                    "supplierId": %d,
                    "supplierName": "Supplier Uno",
                    "restaurantName": "Restaurante Central",
                    "orderDate": "2026-05-10",
                    "priority": "Medium",
                    "status": "Pending",
                    "items": [
                        {
                            "inventoryItemId": 1,
                            "productName": "Sillao",
                            "quantity": 5.0,
                            "unitPrice": 12.0,
                            "unitType": "L"
                        }
                    ]
                }
                """.formatted(supplier1Id);

        // Act & Assert
        mockMvc.perform(post("/api/v1/purchase-orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPurchaseOrderById_whenBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantAId = 10L;
        Long restaurantBId = 20L;

        var item = new PurchaseOrderItem(1L, "Fideo Wantan", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "KG");
        var order = new PurchaseOrder("PO-RESTA-1", supplier1Id, "Supplier Uno", "Rest A",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(item), restaurantAId);
        var saved = purchaseOrderRepository.save(order);

        String tokenB = generateToken(restaurantBId, "restb@ejemplo.com", List.of("RESTAURANT"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/purchase-orders/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePurchaseOrder_whenBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantAId = 10L;
        Long restaurantBId = 20L;

        var item = new PurchaseOrderItem(1L, "Fideo Wantan", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "KG");
        var order = new PurchaseOrder("PO-RESTA-UPDATE", supplier1Id, "Supplier Uno", "Rest A",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(item), restaurantAId);
        var saved = purchaseOrderRepository.save(order);

        String tokenB = generateToken(restaurantBId, "restb@ejemplo.com", List.of("RESTAURANT"));
        String updatePayload = """
                {
                    "code": "PO-RESTA-UPDATE",
                    "supplierId": %d,
                    "supplierName": "Supplier Uno",
                    "restaurantName": "Hacked Rest",
                    "orderDate": "2026-05-10",
                    "priority": "High",
                    "status": "Confirmed",
                    "items": [
                        {
                            "id": %d,
                            "inventoryItemId": 1,
                            "productName": "Fideo Wantan",
                            "quantity": 20.0,
                            "unitPrice": 5.0,
                            "unitType": "KG"
                        }
                    ]
                }
                """.formatted(supplier1Id, saved.getItems().getFirst().getId());

        // Act & Assert
        mockMvc.perform(put("/api/v1/purchase-orders/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePurchaseOrder_whenBelongsToAnotherRestaurant_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantAId = 10L;
        Long restaurantBId = 20L;

        var item = new PurchaseOrderItem(1L, "Fideo", BigDecimal.valueOf(5), BigDecimal.valueOf(5), "KG");
        var order = new PurchaseOrder("PO-RESTA-DEL", supplier1Id, "Supplier Uno", "Rest A",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(item), restaurantAId);
        var saved = purchaseOrderRepository.save(order);

        String tokenB = generateToken(restaurantBId, "restb@ejemplo.com", List.of("RESTAURANT"));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/purchase-orders/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPurchaseOrders_whenRestaurantTenant_returnsOnlyCallerResources() throws Exception {
        // Arrange
        Long restaurantAId = 30L;
        Long restaurantBId = 40L;

        var itemA = new PurchaseOrderItem(1L, "Arroz", BigDecimal.valueOf(10), BigDecimal.valueOf(4), "KG");
        var orderA = new PurchaseOrder("PO-REST-LIST-A", supplier1Id, "Supplier Uno", "Rest A",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(itemA), restaurantAId);
        purchaseOrderRepository.save(orderA);

        var itemB = new PurchaseOrderItem(2L, "Aceite", BigDecimal.valueOf(5), BigDecimal.valueOf(10), "L");
        var orderB = new PurchaseOrder("PO-REST-LIST-B", supplier1Id, "Supplier Uno", "Rest B",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(itemB), restaurantBId);
        purchaseOrderRepository.save(orderB);

        String tokenA = generateToken(restaurantAId, "resta30@ejemplo.com", List.of("RESTAURANT"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/purchase-orders")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].code").value(org.hamcrest.Matchers.hasItem("PO-REST-LIST-A")))
                .andExpect(jsonPath("$[*].code").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("PO-REST-LIST-B"))));
    }

    @Test
    void getAllPurchaseOrders_whenSupplierTenant_returnsOnlyOrdersForItsSupplier() throws Exception {
        // Arrange
        Long restaurantId = 50L;

        var item1 = new PurchaseOrderItem(1L, "Pollo", BigDecimal.valueOf(10), BigDecimal.valueOf(8), "KG");
        var order1 = new PurchaseOrder("PO-SUP1-ORDER", supplier1Id, "Supplier Uno", "Rest C",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(item1), restaurantId);
        purchaseOrderRepository.save(order1);

        var item2 = new PurchaseOrderItem(2L, "Carne", BigDecimal.valueOf(5), BigDecimal.valueOf(25), "KG");
        var order2 = new PurchaseOrder("PO-SUP2-ORDER", supplier2Id, "Supplier Dos", "Rest C",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Pending, List.of(item2), restaurantId);
        purchaseOrderRepository.save(order2);

        String tokenSupplier1 = generateToken(901L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/purchase-orders")
                        .header("Authorization", "Bearer " + tokenSupplier1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].code").value(org.hamcrest.Matchers.hasItem("PO-SUP1-ORDER")))
                .andExpect(jsonPath("$[*].code").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("PO-SUP2-ORDER"))));
    }

    @Test
    void getPurchaseOrderById_whenOwningSupplier_returnsOrder() throws Exception {
        // Arrange
        Long restaurantId = 60L;
        var item = new PurchaseOrderItem(1L, "Cebolla", BigDecimal.valueOf(10), BigDecimal.valueOf(2), "KG");
        var order = new PurchaseOrder("PO-SUP-GET-OK", supplier1Id, "Supplier Uno", "Rest D",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Low, EPurchaseOrderStatus.Pending, List.of(item), restaurantId);
        var saved = purchaseOrderRepository.save(order);

        String tokenSupplier1 = generateToken(902L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/purchase-orders/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenSupplier1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PO-SUP-GET-OK"));
    }

    @Test
    void getPurchaseOrderById_whenOtherSupplier_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantId = 60L;
        var item = new PurchaseOrderItem(1L, "Cebolla", BigDecimal.valueOf(10), BigDecimal.valueOf(2), "KG");
        var order = new PurchaseOrder("PO-SUP-GET-OTHER", supplier2Id, "Supplier Dos", "Rest D",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Low, EPurchaseOrderStatus.Pending, List.of(item), restaurantId);
        var saved = purchaseOrderRepository.save(order);

        String tokenSupplier1 = generateToken(902L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/purchase-orders/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenSupplier1))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePurchaseOrderStatus_whenOwningSupplier_updatesSuccessfully() throws Exception {
        // Arrange
        Long restaurantId = 70L;
        var item = new PurchaseOrderItem(1L, "Ajo", BigDecimal.valueOf(5), BigDecimal.valueOf(6), "KG");
        var order = new PurchaseOrder("PO-STATUS-SUP-OK", supplier1Id, "Supplier Uno", "Rest E",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.High, EPurchaseOrderStatus.Pending, List.of(item), restaurantId);
        var saved = purchaseOrderRepository.save(order);

        String tokenSupplier1 = generateToken(903L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "status": "Confirmed"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/v1/purchase-orders/" + saved.getId() + "/status")
                        .header("Authorization", "Bearer " + tokenSupplier1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Confirmed"));
    }

    @Test
    void updatePurchaseOrderStatus_whenOtherSupplier_returnsNotFound() throws Exception {
        // Arrange
        Long restaurantId = 70L;
        var item = new PurchaseOrderItem(1L, "Ajo", BigDecimal.valueOf(5), BigDecimal.valueOf(6), "KG");
        var order = new PurchaseOrder("PO-STATUS-SUP-ERR", supplier2Id, "Supplier Dos", "Rest E",
                LocalDate.now(), LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.High, EPurchaseOrderStatus.Pending, List.of(item), restaurantId);
        var saved = purchaseOrderRepository.save(order);

        String tokenSupplier1 = generateToken(903L, "proveedor1@ejemplo.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "status": "Confirmed"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/v1/purchase-orders/" + saved.getId() + "/status")
                        .header("Authorization", "Bearer " + tokenSupplier1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }
}
