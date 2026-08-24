package com.buccodev.adm_soler.infra.config;

import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import com.buccodev.adm_soler.application.usecase.AddressUseCase;
import com.buccodev.adm_soler.application.usecase.ClientUseCase;
import com.buccodev.adm_soler.application.usecase.EmployeeUseCase;
import com.buccodev.adm_soler.application.usecase.EquipmentUseCase;
import com.buccodev.adm_soler.application.usecase.ProjectUseCase;
import com.buccodev.adm_soler.application.usecase.RestaurantUseCase;
import com.buccodev.adm_soler.application.usecase.UserUseCase;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root dos casos de uso.
 *
 * Os use cases sao POJOs, sem nenhuma anotacao de framework: quem os conhece
 * e o Spring e esta classe, que vive na camada de infraestrutura. A dependencia
 * aponta de infra para application, nunca o contrario.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public AddressUseCase addressUseCase(AddressRepository addressRepository) {
        return new AddressUseCase(addressRepository);
    }

    @Bean
    public UserUseCase userUseCase(UserRepository userRepository) {
        return new UserUseCase(userRepository);
    }

    @Bean
    public EquipmentUseCase equipmentUseCase(EquipmentRepository equipmentRepository) {
        return new EquipmentUseCase(equipmentRepository);
    }

    @Bean
    public EmployeeUseCase employeeUseCase(EmployeeRepository employeeRepository,
                                           AddressRepository addressRepository) {
        return new EmployeeUseCase(employeeRepository, addressRepository);
    }

    @Bean
    public ClientUseCase clientUseCase(ClientRepository clientRepository,
                                       AddressRepository addressRepository) {
        return new ClientUseCase(clientRepository, addressRepository);
    }

    @Bean
    public ProjectUseCase projectUseCase(ProjectRepository projectRepository,
                                         ClientRepository clientRepository) {
        return new ProjectUseCase(projectRepository, clientRepository);
    }

    @Bean
    public RestaurantUseCase restaurantUseCase(RestaurantRepository restaurantRepository,
                                               AddressRepository addressRepository,
                                               ProjectRepository projectRepository) {
        return new RestaurantUseCase(restaurantRepository, addressRepository, projectRepository);
    }

    @Bean
    public AccommodationUseCase accommodationUseCase(AccommodationRepository accommodationRepository,
                                                     AddressRepository addressRepository,
                                                     ProjectRepository projectRepository) {
        return new AccommodationUseCase(accommodationRepository, addressRepository, projectRepository);
    }
}
