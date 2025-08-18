package com.devsoft.orders_api.repository;

import com.devsoft.orders_api.dto.ClienteDTO;
import com.devsoft.orders_api.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByNombre(String nombre);
}
