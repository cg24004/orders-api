package com.devsoft.orders_api.repository;

import com.devsoft.orders_api.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    //Este método servirá para no insertar registros duplicados
    //Optional<Categoria> findByNombreIgnoreCase(String nombre);

    // Método para buscar una categoría por su nombre almacenado en la base de datos a traves de un DTO
    Categoria findByNombre(String nombre);
}
