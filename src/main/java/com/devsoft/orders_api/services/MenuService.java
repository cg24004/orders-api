package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.MenuDTO;
import com.devsoft.orders_api.entities.Categoria;
import com.devsoft.orders_api.entities.Menu;
import com.devsoft.orders_api.interfaces.IMenuService;
import com.devsoft.orders_api.repository.MenuRepository;
import com.devsoft.orders_api.utils.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenuService implements IMenuService {
    @Autowired
    private MenuRepository menuRepository;

    //Obtenemos el valor de la propiedad del directorio de images
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> findAll() {
        return menuRepository.findAll()
                .stream().map(MenuMapper::toDTO).toList();
    }

    // Método para obtener para un objeto MenuDTO por su ID
    @Override
    @Transactional(readOnly = true)
    public MenuDTO findById(Long id) {
        return menuRepository.findById(id)
                .map(MenuMapper::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDTO findByNombre(String nombre) {
        return menuRepository.findByNombre(nombre)
                .map(MenuMapper::toDTO).orElse(null);
    }

    @Override
    // Método para guardar un objeto MenuDTO, ya sea creando uno nuevo o actualizando uno existente
    public MenuDTO save(MenuDTO dto, MultipartFile imageFile) throws IOException {
        Menu menu;
        if (dto.getId() == null) {
            //se está creando un nuevo registro de Menú
            menu = MenuMapper.toEntity(dto);
            String nombreImagen = procesarImagen(imageFile);
            if (nombreImagen != null) {
                menu.setUrlImagen(nombreImagen);
            }
        } else {
            //Se está actualizando un registro existente
            Optional<Menu> menuActual = menuRepository.findById(dto.getId());
            if (menuActual.isEmpty()) {
                return null;
            }
            menu = menuActual.get();
            //seteamos atributos con dto
            menu.setNombre(dto.getNombre());
            menu.setDescripcion(dto.getDescripcion());
            menu.setTipo(dto.getTipo());
            menu.setPrecioUnitario(dto.getPrecioUnitario());
            menu.setCategoria(new Categoria(dto.getCategoriaDTO().getId(),
                    dto.getCategoriaDTO().getNombre()));
            //Si viene una imgaen nueva, eliminar la imagen anterior
            if (imageFile != null && !imageFile.isEmpty()) {
                if (menu.getUrlImagen() != null && !menu.getUrlImagen().isEmpty()) {
                    //obtenemos la ruta completa de la imagen anterior
                    Path rutaAnterior = Paths.get(uploadDir, "menus", menu.getUrlImagen());
                    //eliminamos la imagen anterior
                    Files.deleteIfExists(rutaAnterior);
                }
                String nombreArchivo = procesarImagen(imageFile);
                menu.setUrlImagen(nombreArchivo);
            }
        }
        Menu menuSave = menuRepository.save(menu);
        return MenuMapper.toDTO(menuSave);
    }

    @Override
    public void delete(Long id) {
        Menu menuActual = menuRepository.findById(id).orElse(null);
        try {
            menuRepository.deleteById(id);
            if (menuActual != null && menuActual.getUrlImagen() != null) {
                // Obtenemos la ruta completa de la imagen anterior para eliminarla
                Path rutaAnterior = Paths.get(uploadDir, "menus", menuActual.getUrlImagen());
                Files.deleteIfExists(rutaAnterior);
            }
        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String procesarImagen (MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            //le anteponemos un id al nombre original de la imagen}
            // Generamos un nombre único para la imagen
            String nombreArchivo = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path rutaArchivo = Paths.get(uploadDir, "menus", nombreArchivo);
            // Guardamos la imagen en el directorio especificado
            Files.createDirectories(rutaArchivo.getParent());
            //subimos o reemplazamos la imagen
            Files.copy(imageFile.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            return nombreArchivo;
        }
        return null;
    }
}
