package com.devsoft.orders_api.repository;

import com.devsoft.orders_api.entities.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
}
