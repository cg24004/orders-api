package com.devsoft.orders_api.repository;

import com.devsoft.orders_api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    /*metodos para verificar en el controlador que no se registre un usuario con
    mismo username y nombre */
    boolean existsByUsername(String username);
    boolean existsByNombre(String nombre);
}