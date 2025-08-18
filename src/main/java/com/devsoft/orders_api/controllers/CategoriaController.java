package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.CategoriaDTO;
import com.devsoft.orders_api.dto.MenuDTO;
import com.devsoft.orders_api.entities.Categoria;
import com.devsoft.orders_api.interfaces.ICategoriaService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController // Anotación que indica que esta clase es un controlador REST
@CrossOrigin // Permite solicitudes desde otros dominios (CORS)/cruzadas
@RequestMapping("/api") // Define la ruta base para las solicitudes a este controlador
public class CategoriaController { // Controlador para manejar las operaciones relacionadas con las categorías

    //Anotación de inyección de dependencias
    @Autowired
    private ICategoriaService categoriaService; // Inyecta el servicio de categorías

    //Endpoint para obtener todas las categorías
    //Ruta para obtener todas las categorías
    @GetMapping("/categorias")
    public ResponseEntity<?> getAll() {
        List<CategoriaDTO> categoriaDTOList = categoriaService.findAll();
        return ResponseEntity.ok(categoriaDTOList);
    }

    // Endpoint para obtener un MenuDTO por su ID
    @GetMapping("/categorias/{id}") // Ruta para obtener una categoría por su ID
    public ResponseEntity<?> getById(@PathVariable Long id) {
        CategoriaDTO categoriaDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            categoriaDTO = categoriaService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (categoriaDTO == null) {
            response.put("message", "La categoria con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CategoriaDTO>(categoriaDTO, HttpStatus.OK);
    }

    // Método guardar una categoría por su ID
    @PostMapping("/categorias")
    public ResponseEntity<?> save(@RequestBody CategoriaDTO dto) {
        CategoriaDTO catPersisted = new CategoriaDTO();
        Map<String, Object> response = new HashMap<>();
        try {
            // Validar que el nombre de la categoría no sea nulo o vacío
            CategoriaDTO catExiste = categoriaService.findByNombre(dto.getNombre());
            if (catExiste != null && dto.getId() == null) {
                response.put("message", "Ya existe una categoria con este nombre, digite otro");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT); // Retorna una respuesta con el estado 409 (CONFLICT) si ya existe una categoría con el mismo nombre
            }
            catPersisted = categoriaService.save(dto);
            response.put("message", "Categoria guardada correctamente");
            response.put("categoria", catPersisted);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); // Retorna una respuesta con el estado 201 (CREATED) y el objeto guardado
        } catch (DataAccessException e) {
            response.put("message", "Error al insertar el registro, intente nuevamente");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna una respuesta con el estado 500 (INTERNAL_SERVER_ERROR) y el mensaje de error
        }
    }

    //endpoint para actualizar una categoría por su ID
    @PutMapping("/categorias/{id}") // Ruta para actualizar una categoría por su ID
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoriaDTO dto) {
        CategoriaDTO catActual = categoriaService.findById(id);
        CategoriaDTO catUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (catActual == null) {
            response.put("message", "No existe una categoria con el ID: "
                    .concat(id.toString().concat("no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // Retorna una respuesta con el estado 404 (NOT FOUND) si no se encuentra la categoría
        }
        try {
            CategoriaDTO catExiste = categoriaService.findByNombre(dto.getNombre());
            if (catExiste != null && !Objects.requireNonNull(catExiste).getId().equals(id)) {
                response.put("message", "Ya existe una categoria con este nombre, digite otro");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT); // Retorna una respuesta con el estado 409 (CONFLICT) si ya existe una categoría con el mismo nombre
            }
            catActual.setNombre(dto.getNombre());
            catUpdated = categoriaService.save(catActual);
            response.put("message", "Categoria actualizada correctamente");
            response.put("categoria", catUpdated);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK); // Retorna
        } catch (DataAccessException e) {
            response.put("message", "Error al actualizar el registro, intente nuevamente");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna una respuesta con el estado 500 (INTERNAL_SERVER_ERROR) y el mensaje de error
        }
    }

    //endpoint para eliminar una categoría por su ID
    @DeleteMapping("/categorias/{id}") // Ruta para eliminar una categoría por su ID
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        CategoriaDTO catActual = categoriaService.findById(id);
        if (catActual == null) {
            response.put("message", "No existe una categoria con el ID: "
                    .concat(id.toString().concat("no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // Retorna una respuesta con el estado 404 (NOT FOUND) si no se encuentra la categoría
        }
        try {
            categoriaService.delete(id);
        }catch (DataAccessException e){
            response.put("message", "Error al eliminar el registro, intente nuevamente");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna una respuesta con el estado 500 (INTERNAL_SERVER_ERROR) y el mensaje de error
        }
        response.put("message", "Categoria eliminada correctamente");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK); // Retorna una respuesta con el estado 200 (OK) y el mensaje de éxito
    }
}
