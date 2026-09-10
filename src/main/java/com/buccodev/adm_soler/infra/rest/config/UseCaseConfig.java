package com.buccodev.adm_soler.infra.rest.config;

import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import com.buccodev.adm_soler.application.usecase.AddressUseCase;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
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
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registro dos casos de uso como beans. As classes de {@code application.usecase}
 * sao POJOs puros: a amarracao com o container fica confinada aqui.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public AddressUseCase addressUseCase(AddressRepository addressRepository) {
        return new AddressUseCase(addressRepository);
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
    public AccommodationUseCase accommodationUseCase(AccommodationRepository accommodationRepository,
                                                     AddressRepository addressRepository,
                                                     ProjectRepository projectRepository) {
        return new AccommodationUseCase(accommodationRepository, addressRepository, projectRepository);
    }

    @Bean
    public RestaurantUseCase restaurantUseCase(RestaurantRepository restaurantRepository,
                                               AddressRepository addressRepository,
                                               ProjectRepository projectRepository) {
        return new RestaurantUseCase(restaurantRepository, addressRepository, projectRepository);
    }

    @Bean
    public UserUseCase userUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new UserUseCase(userRepository, passwordHasher);
    }

    @Bean
    public AuthUseCase authUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                                   TokenService tokenService) {
        return new AuthUseCase(userRepository, passwordHasher, tokenService);
    }
}
