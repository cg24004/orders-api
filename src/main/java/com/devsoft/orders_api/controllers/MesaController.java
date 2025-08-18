package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.MesaDTO;
import com.devsoft.orders_api.interfaces.IMesaService;
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
public class MesaController {

    @Autowired
    private IMesaService mesaService;

    //Endpoint para obtener todos las mesas
    @GetMapping("/mesas")
    public ResponseEntity<?> getAll(){
        List<MesaDTO> mDTO = mesaService.findAll();
        return ResponseEntity.ok(mDTO);
    }

    //Endpoint para obtener un cliente por ID
    @GetMapping("/mesas/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        MesaDTO mDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            mDTO = mesaService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (mDTO == null) {
            response.put("message", "La mesa con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<MesaDTO>(mDTO, HttpStatus.OK);
    }
}
