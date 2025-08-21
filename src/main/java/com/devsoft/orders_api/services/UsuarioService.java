package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.RoleDTO;
import com.devsoft.orders_api.dto.UsuarioDTO;
import com.devsoft.orders_api.entities.Usuario;
import com.devsoft.orders_api.interfaces.IUsuarioService;
import com.devsoft.orders_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        Usuario user = userRepository.findById(id).orElse(null);
        if (user== null) return null;
        return convertToDTO(user);
    }

    private UsuarioDTO convertToDTO(Usuario user){
        return new UsuarioDTO(user.getId(),
                user.getNombre(),
                user.getUsername(),
                user.isActivo(),
                new RoleDTO(
                        user.getRole().getId(),
                        user.getRole().getNombre()
                ));
    }
}
