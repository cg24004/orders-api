package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.ClienteDTO;
import com.devsoft.orders_api.entities.Cliente;
import com.devsoft.orders_api.interfaces.IClienteService;
import com.devsoft.orders_api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        Cliente cl = clienteRepository.findById(id).orElse(null);
        if (cl == null) return null;
        return convertToDTO(cl);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO finByNombre(String nombre) {
        Cliente cl = clienteRepository.findByNombre(nombre);
        if (cl == null) return null;
        return convertToDTO(cl);
    }

    private ClienteDTO convertToDTO(Cliente cl) {
        return new ClienteDTO(cl.getId(),
                cl.getNombre(),
                cl.getDireccion(),
                cl.getTelefono(),
                cl.getEmail(),
                cl.getTipoCliente());
    }
}
