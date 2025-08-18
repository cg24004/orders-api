package com.devsoft.orders_api.interfaces;

import com.devsoft.orders_api.dto.CategoriaDTO;

import java.util.List;

//Permite definir los métodos que serán implementados en la clase CategoriaService para manejar las categorías
public interface ICategoriaService {
    List<CategoriaDTO> findAll(); // Método para obtener todas las categorías
    CategoriaDTO findById(Long id); // Método para buscar una categoría por su ID
    CategoriaDTO save(CategoriaDTO categoriaDTO); // Método para guardar una categoría
    CategoriaDTO findByNombre(String nombre); // Método para buscar una categoría por su nombre
    void delete(Long id); // Método para eliminar una categoría por su ID
}