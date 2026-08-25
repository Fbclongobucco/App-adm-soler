package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Excluir tem de remover a linha, e nao apenas responder sem erro.
 *
 * As entidades implementam {@code Persistable} e o {@code delete} do Spring Data
 * desiste em silencio quando {@code isNew()} responde {@code true}. Sem o
 * {@code @PostLoad} que marca a entidade carregada como existente, todo DELETE
 * da API devolvia 204 sem apagar nada - e nenhum teste percebia, porque todos
 * conferiam o status da resposta em vez do estado do banco.
 */
@SpringBootTest
class RepositoryDeletionTest {

    @Autowired private AddressRepository addressRepository;
    @Autowired private EquipmentRepository equipmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private AccommodationRepository accommodationRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    /** Salva, exclui e cobra que o registro realmente tenha sumido. */
    private <T> void assertDeletionRemoves(
            String label,
            Supplier<UUID> save,
            Consumer<UUID> delete,
            java.util.function.Predicate<UUID> exists) {
        UUID id = save.get();
        assertThat(exists.test(id)).as("%s deveria existir apos salvar", label).isTrue();

        delete.accept(id);

        assertThat(exists.test(id)).as("%s deveria ter sido removido", label).isFalse();
    }

    private Address newAddress() {
        return Address.create("Rua das Flores", "123", null, "Centro",
                "Sao Paulo", "SP", "01234-567", "Brasil");
    }

    private Address savedAddress() {
        return addressRepository.save(newAddress());
    }

    private Client savedClient() {
        return clientRepository.save(
                Client.create("Cliente " + UUID.randomUUID(), null, null, null, savedAddress()));
    }

    private Project savedProject() {
        return projectRepository.save(Project.create("OS-" + UUID.randomUUID(), "Servico",
                savedClient(), LocalDateTime.now(), LocalDateTime.now().plusDays(30)));
    }

    @Test
    void deleteRemovesAddress() {
        assertDeletionRemoves("endereco",
                () -> addressRepository.save(newAddress()).getId(),
                addressRepository::deleteById,
                addressRepository::existsById);
    }

    @Test
    void deleteRemovesEquipment() {
        assertDeletionRemoves("equipamento",
                () -> equipmentRepository.save(Equipment.create("Betoneira", "400L")).getId(),
                equipmentRepository::deleteById,
                equipmentRepository::existsById);
    }

    @Test
    void deleteRemovesUser() {
        assertDeletionRemoves("usuario",
                () -> userRepository.save(User.create("Fulano",
                        "fulano-" + UUID.randomUUID() + "@teste.com", "senha123", null)).getId(),
                userRepository::deleteById,
                userRepository::existsById);
    }

    @Test
    void deleteRemovesClient() {
        assertDeletionRemoves("cliente",
                () -> savedClient().getId(),
                clientRepository::deleteById,
                clientRepository::existsById);
    }

    @Test
    void deleteRemovesEmployee() {
        assertDeletionRemoves("colaborador",
                () -> employeeRepository.save(
                        Employee.create("Carlos", null, null, savedAddress(), "Pedreiro")).getId(),
                employeeRepository::deleteById,
                employeeRepository::existsById);
    }

    @Test
    void deleteRemovesProject() {
        assertDeletionRemoves("obra",
                () -> savedProject().getId(),
                projectRepository::deleteById,
                projectRepository::existsById);
    }

    @Test
    void deleteRemovesRestaurant() {
        assertDeletionRemoves("restaurante",
                () -> restaurantRepository.save(Restaurant.create("Cantina", null, null, null,
                        savedProject(), Boolean.FALSE, BigDecimal.TEN, BigDecimal.TEN,
                        BigDecimal.ZERO, 10, savedAddress())).getId(),
                restaurantRepository::deleteById,
                restaurantRepository::existsById);
    }

    @Test
    void deleteRemovesAccommodation() {
        assertDeletionRemoves("alojamento",
                () -> accommodationRepository.save(Accommodation.create(savedAddress(),
                        savedProject(), 10, LocalDateTime.now(), LocalDateTime.now().plusDays(30))).getId(),
                accommodationRepository::deleteById,
                accommodationRepository::existsById);
    }

    @Test
    void deleteByUserIdRemovesRefreshTokens() {
        UUID userId = UUID.randomUUID();
        refreshTokenRepository.save(RefreshToken.issue(userId, "hash-" + UUID.randomUUID(),
                LocalDateTime.now().plusDays(7)));

        refreshTokenRepository.deleteByUserId(userId);

        assertThat(refreshTokenRepository.findActiveByUserId(userId)).isEmpty();
    }

    /**
     * A causa raiz: uma entidade vinda do banco precisa se declarar existente.
     * Este e o invariante que o {@code @PostLoad} restabelece.
     */
    @Test
    void loadedEntityIsNotConsideredNew() {
        UUID id = addressRepository.save(newAddress()).getId();

        assertThat(addressRepository.findById(id)).isPresent();
        assertThat(addressRepository.existsById(id)).isTrue();

        addressRepository.deleteById(id);

        assertThat(addressRepository.findById(id)).isEmpty();
    }
}
