package com.buccodev.adm_soler.infra.config;

import com.buccodev.adm_soler.application.gateway.AuthenticationGatewayPort;
import com.buccodev.adm_soler.application.gateway.PasswordEncoderPort;
import com.buccodev.adm_soler.application.gateway.TokenProviderPort;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registro dos casos de uso como beans. As classes de {@code application.usecase}
 * sao POJOs puros: toda a amarracao com o container fica confinada nesta camada.
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    AddressUseCase addressUseCase(AddressRepository addressRepository) {
        return new AddressUseCase(addressRepository);
    }

    @Bean
    EquipmentUseCase equipmentUseCase(EquipmentRepository equipmentRepository) {
        return new EquipmentUseCase(equipmentRepository);
    }

    @Bean
    EmployeeUseCase employeeUseCase(EmployeeRepository employeeRepository,
                                    AddressRepository addressRepository) {
        return new EmployeeUseCase(employeeRepository, addressRepository);
    }

    @Bean
    ClientUseCase clientUseCase(ClientRepository clientRepository,
                                AddressRepository addressRepository) {
        return new ClientUseCase(clientRepository, addressRepository);
    }

    @Bean
    ProjectUseCase projectUseCase(ProjectRepository projectRepository,
                                  ClientRepository clientRepository) {
        return new ProjectUseCase(projectRepository, clientRepository);
    }

    @Bean
    AccommodationUseCase accommodationUseCase(AccommodationRepository accommodationRepository,
                                              AddressRepository addressRepository,
                                              ProjectRepository projectRepository) {
        return new AccommodationUseCase(accommodationRepository, addressRepository, projectRepository);
    }

    @Bean
    RestaurantUseCase restaurantUseCase(RestaurantRepository restaurantRepository,
                                        AddressRepository addressRepository,
                                        ProjectRepository projectRepository) {
        return new RestaurantUseCase(restaurantRepository, addressRepository, projectRepository);
    }

    @Bean
    UserUseCase userUseCase(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        return new UserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    AuthUseCase authUseCase(AuthenticationGatewayPort authenticationGateway,
                            TokenProviderPort tokenProvider,
                            UserRepository userRepository,
                            PasswordEncoderPort passwordEncoder) {
        return new AuthUseCase(authenticationGateway, tokenProvider, userRepository, passwordEncoder);
    }
}
