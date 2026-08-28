package com.buccodev.adm_soler.infra.config;

import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
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
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.AccessTokenProvider;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.RefreshTokenCodec;
import com.buccodev.adm_soler.infra.security.jwt.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AddressUseCase addressUseCase(AddressRepository addressRepository) {
        return new AddressUseCase(addressRepository);
    }

    @Bean
    public UserUseCase userUseCase(UserRepository userRepository,
                                   RefreshTokenRepository refreshTokenRepository,
                                   PasswordHasher passwordHasher) {
        return new UserUseCase(userRepository, refreshTokenRepository, passwordHasher);
    }

    @Bean
    public AuthUseCase authUseCase(UserRepository userRepository,
                                   RefreshTokenRepository refreshTokenRepository,
                                   PasswordHasher passwordHasher,
                                   AccessTokenProvider accessTokenProvider,
                                   RefreshTokenCodec refreshTokenCodec,
                                   JwtProperties jwtProperties) {
        return new AuthUseCase(userRepository, refreshTokenRepository, passwordHasher,
                accessTokenProvider, refreshTokenCodec, jwtProperties.getRefreshTokenTtl());
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
