package com.devsoft.orders_api.interfaces;

import com.devsoft.orders_api.dto.MesaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IMesaService {
    List<MesaDTO> findAll();
    MesaDTO findById (Long id);
}
