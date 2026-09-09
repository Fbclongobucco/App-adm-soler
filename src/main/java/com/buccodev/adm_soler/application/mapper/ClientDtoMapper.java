package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ClientDtoMapper {

    private ClientDtoMapper() {
    }

    public static Client toDomain(ClientRequest request, Address address) {
        return Client.create(
                request.name(),
                request.email(),
                request.phone(),
                request.cnpj(),
                address
        );
    }

    public static void applyTo(Client client, ClientRequest request, Address address) {
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setCnpj(request.cnpj());
        client.setAddress(address);
    }

    public static ClientResponse toResponse(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCnpj(),
                AddressDtoMapper.toResponse(client.getAddress()),
                projectIds(client.getProjects()),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    private static Set<UUID> projectIds(Set<Project> projects) {
        if (projects == null) {
            return Collections.emptySet();
        }
        return projects.stream().map(Project::getId).collect(Collectors.toSet());
    }
}
