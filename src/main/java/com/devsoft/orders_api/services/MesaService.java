package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.MesaDTO;
import com.devsoft.orders_api.entities.Mesa;
import com.devsoft.orders_api.interfaces.IMesaService;
import com.devsoft.orders_api.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesaService implements IMesaService {
    @Autowired
    private MesaRepository mesaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MesaDTO> findAll() {
        return mesaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MesaDTO findById(Long id) {
        Mesa mesa = mesaRepository.findById(id).orElse(null);
        if (mesa == null) return null;
        return convertToDTO(mesa);
    }

    private MesaDTO convertToDTO(Mesa m){
        return new MesaDTO(m.getId(),
                m.getNumero(),
                m.getUbicacion());
    }

}
