package com.devsoft.orders_api.controllers;

import com.devsoft.orders_api.dto.MenuDTO;
import com.devsoft.orders_api.interfaces.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Anotación que indica que esta clase es un controlador REST
@CrossOrigin // Permite solicitudes desde otros dominios (CORS)/cruzadas
@RequestMapping("/api") // Define la ruta base para las solicitudes a este controlador
public class MenuController {
    @Autowired
    private IMenuService menuService;

    //Endpoint para obtener todas las categorías
    //Ruta para obtener todas las categorías
    @GetMapping("/menus")
    public ResponseEntity<?> getAll() {
        List<MenuDTO> menuList = menuService.findAll();
        return ResponseEntity.ok(menuList);
    }

    // Endpoint para obtener un MenuDTO por su ID
    @GetMapping("/menus/{id}") // Ruta para obtener una categoría por su ID
    public ResponseEntity<?> getById(@PathVariable Long id) {
        MenuDTO menuDTO = null;
        Map<String, Object> response = new HashMap<>();
        try {
            menuDTO = menuService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (menuDTO == null) {
            response.put("message", "El menú con ID: "
                    .concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<MenuDTO>(menuDTO, HttpStatus.OK);
    }

    // Método guardar un menú
    @PostMapping("/menus")
    public ResponseEntity<?> save(@RequestPart MenuDTO dto,
                                  @RequestPart(value = "imagen", required = false) MultipartFile imageFile) {
        MenuDTO menuPersisted = new MenuDTO();
        Map<String, Object> response = new HashMap<>();

        try {
            MenuDTO menuExiste = menuService.findByNombre(dto.getNombre());
            if (menuExiste != null && dto.getId() == null) {
                response.put("message", "Ya existe un menú o producto con este nombre, digite otro");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            menuPersisted = menuService.save(dto, imageFile);
            response.put("message", "Menú insertado correctamente...!");
            response.put("menu", menuPersisted);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            response.put("message", "Error al insertar el registro, intente nuevamente");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Retorna una respuesta con el estado 500 (INTERNAL_SERVER_ERROR) y el mensaje de error
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //endpoint para actualizar un menú por su ID
    @PutMapping("/menus/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestPart MenuDTO dto, // Usamos @RequestPart para recibir el DTO y la imagen
                                    // Se usa MultipartFile para recibir archivos
                                    @RequestPart(value = "imagen", required = false) MultipartFile imageFile) {
        Map<String, Object> response = new HashMap<>(); // Mapa para almacenar la respuesta
        MenuDTO menuActual = menuService.findById(id); // Obtenemos el menú actual por ID
        MenuDTO menuActualizado; // Variable para almacenar el menú actualizado

        // Verificamos si el menú actual es nulo (no existe en la base de datos)
        if (menuActual == null) {
            response.put("message", "No existe un menú con el ID: " + id + " en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Retorna una respuesta con el estado 404 (NOT FOUND) si no se encuentra el menú
        }

        // Verificamos si ya existe un menú con el mismo nombre
        try {
            MenuDTO menuExiste = menuService.findByNombre(dto.getNombre());
            if (menuExiste != null && !menuExiste.getId().equals(id)) { // Verificamos si el menú existe y si su ID es diferente al que estamos actualizando
                response.put("message", "Ya existe un menú o producto con este nombre, digite otro");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT); // Retorna una respuesta con el estado 409 (CONFLICT) si ya existe un menú con el mismo nombre
            }
            dto.setId(id); // Establecemos el ID del DTO para que se actualice el menú existente
            menuActualizado = menuService.save(dto, imageFile); // Llamamos al servicio para guardar el menú actualizado
            response.put("message", "Menú actualizado correctamente");
            response.put("menu", menuActualizado); // Agregamos el menú actualizado a la respuesta
            return new ResponseEntity<>(response, HttpStatus.OK); // Retorna una respuesta con el estado 200 (OK) y el menú actualizado

            // Si se produce una excepción al acceder a la base de datos o al procesar la imagen, capturamos la excepción y retornamos un mensaje de error
        } catch (DataAccessException | IOException e) {
            response.put("message", "Error al actualizar el registro, intente nuevamente");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/menus/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        MenuDTO menuActual = menuService.findById(id);
        if (menuActual == null) {
            response.put("message", "No se puede eliminar el menú o producto con ID: "
                    .concat(id.toString().concat(" ya que no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            menuService.delete(id);
            response.put("message", "Menú eliminado correctamente");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "No se puede eliminar el menú " + menuActual.getNombre() + ", porque está en uso");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch(DataAccessException e) {
            response.put("message", "No se puede eliminar el menú, ya que tiene ordenes asociadas");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
