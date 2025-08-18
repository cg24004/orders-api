package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.CategoriaDTO;
import com.devsoft.orders_api.entities.Categoria;
import com.devsoft.orders_api.interfaces.ICategoriaService;
import com.devsoft.orders_api.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService implements ICategoriaService {

    @Autowired //Inyectar por dependencia el repositorio
    private CategoriaRepository categoriaRepository; //Esto es un repositorio que maneja las operaciones CRUD de la entidad Categoria

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() { // Este método obtiene todas las categorías y las convierte a CategoriaDTO
        return categoriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener todas una lista de entidades Categoria
    /*public List<Categoria> getAll(){
        return categoriaRepository.findAll();
    }*/

    @Override
    public CategoriaDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (categoria == null) return null;
        return convertToDTO(categoria);
    }

    @Override
    public CategoriaDTO save(CategoriaDTO dto) { // Este método guarda una categoría y devuelve un CategoriaDTO
        Categoria catNueva = new Categoria();
        if(dto.getId() == null) {
            //nuevo registro
            catNueva.setNombre(dto.getNombre());
        }else{
            //actualizar registro
            catNueva.setId(dto.getId());
            catNueva.setNombre(dto.getNombre());
        }
        return convertToDTO(categoriaRepository.save(catNueva));
    }

    // Este método busca una categoría por su nombre y devuelve un CategoriaDTO
    @Override
    public CategoriaDTO findByNombre(String nombre) {
        Categoria categoria = categoriaRepository.findByNombre(nombre);
        if (categoria == null) return null;
        return convertToDTO(categoria);
    }

    @Override
    public void delete(Long id) { // Este método elimina una categoría por su ID
        categoriaRepository.deleteById(id);
    }

    // Método privado para convertir una entidad Categoria a CategoriaDTO
    private CategoriaDTO convertToDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(),categoria.getNombre());
    }
}
