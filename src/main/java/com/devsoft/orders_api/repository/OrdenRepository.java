package com.devsoft.orders_api.repository;

import com.devsoft.orders_api.entities.Orden;
import com.devsoft.orders_api.utils.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden,Long> {
    List<Orden> findByEstado(EstadoOrden estado);

    //Método para obtener el correlativo maximo por mes y año
    @Query("SELECT MAX(CAST(SUBSTRING(o.correlativo,7) AS Long)) FROM Orden o WHERE SUBSTRING(o.correlativo,1,6) = :yearMonth")
    Long findMaxCorrelativoByYearAndMonth(@Param("yearMonth") String yearMonth);
}
