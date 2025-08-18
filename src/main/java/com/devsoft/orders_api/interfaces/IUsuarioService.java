package com.devsoft.orders_api.interfaces;

import com.devsoft.orders_api.dto.UsuarioDTO;
import java.util.List;

public interface IUsuarioService {
    List<UsuarioDTO> findAll();
    UsuarioDTO findById (Long id);
}
