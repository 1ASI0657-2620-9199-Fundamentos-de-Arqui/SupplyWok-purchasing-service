package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierClientCommandService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.SupplierClient;
import aurora.supply_wok.platform.suppliers.domain.model.commands.LinkClientToSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.repositories.ClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierClientRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service that creates the supplier-client relationship when a restaurant buys from a supplier.
 */
@Service
public class SupplierClientCommandServiceImpl implements SupplierClientCommandService {

    private static final String DEFAULT_CLIENT_DISTRICT = "Unknown";
    private static final String ACTIVE_STATUS = "active";

    private final SupplierRepository supplierRepository;
    private final ClientRepository clientRepository;
    private final SupplierClientRepository supplierClientRepository;

    public SupplierClientCommandServiceImpl(SupplierRepository supplierRepository,
                                            ClientRepository clientRepository,
                                            SupplierClientRepository supplierClientRepository) {
        this.supplierRepository = supplierRepository;
        this.clientRepository = clientRepository;
        this.supplierClientRepository = supplierClientRepository;
    }

    @Override
    @Transactional
    public Optional<Client> handle(LinkClientToSupplierCommand command) {
        if (command.supplierId() == null || command.supplierId() <= 0) {
            return Optional.empty();
        }
        if (command.clientName() == null || command.clientName().isBlank()) {
            return Optional.empty();
        }
        if (!supplierRepository.existsById(command.supplierId())) {
            return Optional.empty();
        }

        var normalizedClientName = command.clientName().trim();
        var client = clientRepository.findByNameIgnoreCase(normalizedClientName)
                .orElseGet(() -> clientRepository.save(new Client(normalizedClientName, DEFAULT_CLIENT_DISTRICT, ACTIVE_STATUS)));

        if (!supplierClientRepository.existsBySupplierIdAndClientId(command.supplierId(), client.getId())) {
            supplierClientRepository.save(new SupplierClient(command.supplierId(), client.getId()));
        }

        return Optional.of(client);
    }
}
