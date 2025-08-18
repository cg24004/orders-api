package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.UsuarioDTO;
import com.devsoft.orders_api.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    //Endpoint para obtener todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<?> getAll(){
        List<UsuarioDTO> userDTO = usuarioService.findAll();
        return ResponseEntity.ok(userDTO);
    }

    //Endpoint para obtener un usuario por ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        UsuarioDTO userDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            userDTO = usuarioService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (userDTO == null) {
            response.put("message", "El usuario con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<UsuarioDTO>(userDTO, HttpStatus.OK);
    }
}
