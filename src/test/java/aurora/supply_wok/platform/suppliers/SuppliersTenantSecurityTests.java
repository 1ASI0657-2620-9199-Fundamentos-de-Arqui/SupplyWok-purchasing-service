package aurora.supply_wok.platform.suppliers;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SuppliersTenantSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CatalogItemRepository catalogItemRepository;

    @Value("${authorization.jwt.secret:local-dev-secret-key-change-me-12345678901234567890}")
    private String jwtSecret;

    private Long supplier1Id;
    private Long supplier2Id;

    @BeforeEach
    void setUp() {
        Supplier sup1 = supplierRepository.findByEmailIgnoreCase("owner.supplier1@test.com")
                .orElseGet(() -> supplierRepository.save(new Supplier(
                        UUID.randomUUID(), "Supplier Alpha", "Alice", "owner.supplier1@test.com",
                        "+51911111111", "Verduras", "2026-01-01", "99%", "24h")));
        supplier1Id = sup1.getId();

        Supplier sup2 = supplierRepository.findByEmailIgnoreCase("owner.supplier2@test.com")
                .orElseGet(() -> supplierRepository.save(new Supplier(
                        UUID.randomUUID(), "Supplier Beta", "Bob", "owner.supplier2@test.com",
                        "+51922222222", "Carnes", "2026-01-01", "98%", "24h")));
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
    void createCatalogItem_whenNotOwningSupplier_returnsForbidden() throws Exception {
        // Arrange
        String tokenSup1 = generateToken(301L, "owner.supplier1@test.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "name": "Tomate Rojo",
                    "category": "Verduras",
                    "price": 3.50,
                    "unit": "KG",
                    "deliveryConditions": "Entrega inmediata"
                }
                """;

        // Act & Assert
        // Supplier 1 attempts to create a catalog item under Supplier 2's id -> 403
        mockMvc.perform(post("/api/v1/suppliers/" + supplier2Id + "/catalog-items")
                        .header("Authorization", "Bearer " + tokenSup1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCatalogItem_whenOwningSupplier_createsSuccessfully() throws Exception {
        // Arrange
        String tokenSup1 = generateToken(301L, "owner.supplier1@test.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "name": "Tomate Rojo",
                    "category": "Verduras",
                    "price": 3.50,
                    "unit": "KG",
                    "deliveryConditions": "Entrega inmediata"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/suppliers/" + supplier1Id + "/catalog-items")
                        .header("Authorization", "Bearer " + tokenSup1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tomate Rojo"));
    }

    @Test
    void updateCatalogItem_whenNotOwningSupplier_returnsForbidden() throws Exception {
        // Arrange
        var item = catalogItemRepository.save(new CatalogItem(
                supplier2Id, "Carne de Res", "Carnes", BigDecimal.valueOf(25), ECatalogUnit.KG, "Cadena de frio"
        ));

        String tokenSup1 = generateToken(301L, "owner.supplier1@test.com", List.of("SUPPLIER"));
        String payload = """
                {
                    "name": "Carne Modificada",
                    "category": "Carnes",
                    "price": 28.00,
                    "unit": "KG",
                    "deliveryConditions": "Cadena de frio"
                }
                """;

        // Act & Assert
        // Supplier 1 attempts to modify Supplier 2's catalog item -> 403
        mockMvc.perform(put("/api/v1/suppliers/" + supplier2Id + "/catalog-items/" + item.getId())
                        .header("Authorization", "Bearer " + tokenSup1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCatalogItem_whenNotOwningSupplier_returnsForbidden() throws Exception {
        // Arrange
        var item = catalogItemRepository.save(new CatalogItem(
                supplier2Id, "Lomo Fino", "Carnes", BigDecimal.valueOf(40), ECatalogUnit.KG, "Cadena de frio"
        ));

        String tokenSup1 = generateToken(301L, "owner.supplier1@test.com", List.of("SUPPLIER"));

        // Act & Assert
        // Supplier 1 attempts to delete Supplier 2's catalog item -> 403
        mockMvc.perform(delete("/api/v1/suppliers/" + supplier2Id + "/catalog-items/" + item.getId())
                        .header("Authorization", "Bearer " + tokenSup1))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllClientsBySupplierId_whenNotOwningSupplier_returnsForbidden() throws Exception {
        // Arrange
        String tokenSup1 = generateToken(301L, "owner.supplier1@test.com", List.of("SUPPLIER"));

        // Act & Assert
        // Supplier 1 attempts to view Supplier 2's clients -> 403
        mockMvc.perform(get("/api/v1/suppliers/" + supplier2Id + "/clients")
                        .header("Authorization", "Bearer " + tokenSup1))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllClientsBySupplierId_whenRestaurantRole_returnsForbidden() throws Exception {
        // Arrange
        String tokenRest = generateToken(501L, "restaurante@ejemplo.com", List.of("RESTAURANT"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/suppliers/" + supplier1Id + "/clients")
                        .header("Authorization", "Bearer " + tokenRest))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllCatalogItemsBySupplierId_whenAnyAuthenticated_returnsOk() throws Exception {
        // Arrange
        String tokenRest = generateToken(501L, "restaurante@ejemplo.com", List.of("RESTAURANT"));

        // Act & Assert
        // Restaurants are allowed to browse supplier catalog items
        mockMvc.perform(get("/api/v1/suppliers/" + supplier1Id + "/catalog-items")
                        .header("Authorization", "Bearer " + tokenRest))
                .andExpect(status().isOk());
    }
}
