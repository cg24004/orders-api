package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.OrdenDTO;
import com.devsoft.orders_api.interfaces.IOrdenService;
import com.devsoft.orders_api.utils.EstadoOrden;
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
public class OrdenController {
    @Autowired
    private IOrdenService ordenService;

    //Endpoint para listar todas las órdenes
    @GetMapping("/ordenes")
    public ResponseEntity<?> getAll() {
        List<OrdenDTO> ordenes = ordenService.findAll();
        return ResponseEntity.ok(ordenes);
    }

    //Endpoint para listar órdenes por estado
    @GetMapping("/ordenes/estado/{estado}")
    public ResponseEntity<?> getAllEstado(@PathVariable EstadoOrden estado) {
        List<OrdenDTO> lista = ordenService.findByEstado(estado);
        return ResponseEntity.ok(lista);
    }

    //Endpoint para obtener una orden por su ID
    @GetMapping("/ordenes/{id}") // Ruta para obtener una categoría por su ID
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrdenDTO dto = ordenService.findById(id);
            if (dto == null) {
                response.put("message", "La orden con ID: "
                        .concat(id.toString()
                                .concat(" no existe en la base de datos")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Endpoint para crear una nueva orden
    @PostMapping("/ordenes")
    public ResponseEntity<?> create(@RequestBody OrdenDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrdenDTO dtoCreated = ordenService.registerOrUpdate(dto);
            if (dtoCreated == null) {
                response.put("message", "No se pudo crear la orden, verifique los datos ingresados");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response.put("message", "La orden ha sido creada con éxito");
            response.put("orden", dtoCreated);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la órden a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Endpoint para actualizar una orden
    @PutMapping("/ordenes/{id}") // Ruta para actualizar una categoría por su ID
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody OrdenDTO dto) {
        OrdenDTO ordenActual = ordenService.findById(id);
        Map<String, Object> response = new HashMap<>();
        if (ordenActual == null) {
            response.put("message", "No se puede editar la órden con el ID: "
                    .concat(id.toString().concat("no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            ordenActual.setClienteDTO(dto.getClienteDTO());
            ordenActual.setMesaDTO(dto.getMesaDTO());
            ordenActual.setUsuarioDTO(dto.getUsuarioDTO());
            ordenActual.setTotal(dto.getTotal());
            ordenActual.setDetalle(dto.getDetalle());
            OrdenDTO dtoUptated = ordenService.registerOrUpdate(ordenActual);
            if (dtoUptated == null) {
                response.put("message", "No se pudo actualizar la orden, verifique los datos ingresados");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response.put("message", "La orden ha sido actualizada con éxito");
            response.put("orden", dtoUptated);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (DataAccessException e) {
            response.put("message", "Error al actualizar el registro, intente nuevamente");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna una respuesta con el estado 500 (INTERNAL_SERVER_ERROR) y el mensaje de error
        }
    }

    //Endpoint para cambiar el estado de una orden
    @PutMapping("/ordenes/cambiar-estado")
    public ResponseEntity<?> changeState(@RequestBody OrdenDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrdenDTO ordenUpdated = ordenService.changeState(dto);
            if (ordenUpdated == null) {
                response.put("message", "No se pudo cambiar el estado de la orden, verifique los datos ingresados");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response.put("message", "El estado de la orden ha cambiado a: " + ordenUpdated.getEstado());
            response.put("orden", ordenUpdated);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (DataAccessException e) {
            response.put("message", "Error al cambiar el estado de la orden");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Endpoint para anular una orden
    @PutMapping("/ordenes/anular/{id}")
    public ResponseEntity<?> anularOrden (@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        OrdenDTO ordenActual = ordenService.findById(id);
        if (ordenActual == null) {
            response.put("message", "No se puede anular la orden con el ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        try {
            ordenService.anular(id);
            response.put("message", "La orden ha sido anulada con éxito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Error al anular la orden");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
