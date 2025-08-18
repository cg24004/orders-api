package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.CategoriaDTO;
import com.devsoft.orders_api.dto.ClienteDTO;
import com.devsoft.orders_api.interfaces.IClienteService;
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
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    //Endpoint para obtener todos los clientes
    @GetMapping("/clientes")
    public ResponseEntity<?> getAll() {
        List<ClienteDTO> clDtoList = clienteService.findAll();
        return ResponseEntity.ok(clDtoList);
    }

    //Endpoint para obtener un cliente por ID
    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ClienteDTO clDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            clDTO = clienteService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (clDTO == null) {
            response.put("message", "La cliente con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ClienteDTO>(clDTO, HttpStatus.OK);
    }

    //Endpoint para obtener un cliente por nombre
    @GetMapping("/clientes/nombre/{nombre}")
    public ResponseEntity<?> getByNombre(@PathVariable String nombre) {
        ClienteDTO clDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            clDTO = clienteService.finByNombre(nombre);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (clDTO == null) {
            response.put("message", "El cliente con nombre: "
                    .concat(nombre.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ClienteDTO>(clDTO, HttpStatus.OK);
    }
}
