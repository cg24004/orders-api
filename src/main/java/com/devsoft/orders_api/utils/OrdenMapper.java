package com.devsoft.orders_api.utils;

import com.devsoft.orders_api.dto.*;
import com.devsoft.orders_api.entities.Orden;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

public class OrdenMapper {

    public static OrdenDTO toDTO(Orden orden){
        if(orden == null) return null;
        OrdenDTO dto = new OrdenDTO();
        dto.setId(orden.getId());
        dto.setCorrelativo(orden.getCorrelativo());
        dto.setFecha(orden.getFecha());
        dto.setHora(orden.getHora());
        dto.setEstado(orden.getEstado());
        dto.setTotal(orden.getTotal());
        dto.setClienteDTO(new ClienteDTO(
                orden.getCliente().getId(),
                orden.getCliente().getNombre(),
                orden.getCliente().getDireccion(),
                orden.getCliente().getTelefono(),
                orden.getCliente().getEmail(),
                orden.getCliente().getTipoCliente()
        ));
        dto.setMesaDTO(new MesaDTO(
                orden.getMesa().getId(),
                orden.getMesa().getNumero(),
                orden.getMesa().getUbicacion()
        ));
        dto.setUsuarioDTO(new UsuarioDTO(
                orden.getUsuario().getId(),
                orden.getUsuario().getNombre(),
                orden.getUsuario().getUsername(),
                orden.getUsuario().isActivo(),
                new RoleDTO(
                        orden.getUsuario().getRole().getId(),
                        orden.getUsuario().getRole().getNombre()
                )
        ));
        //mapeamos el detalle de la orden
        if(orden.getDetalleOrden() != null){
            dto.setDetalle(orden.getDetalleOrden().stream().map(d ->{
                DetalleOrdenDTO doDTO = new DetalleOrdenDTO();
                doDTO.setId(d.getId());
                doDTO.setCantidad(d.getCantidad());
                doDTO.setPrecio(d.getPrecio());
                doDTO.setSubtotal(
                        BigDecimal.valueOf(d.getCantidad())
                        .multiply(d.getPrecio())
                        .setScale(2, RoundingMode.HALF_UP)
                );
                doDTO.setMenuDTO(new MenuDTO(
                        d.getMenu().getId(),
                        d.getMenu().getNombre(),
                        d.getMenu().getDescripcion(),
                        d.getMenu().getTipo(),
                        d.getMenu().getPrecioUnitario(),
                        d.getMenu().getUrlImagen(),
                        d.getMenu().isDisponible(),
                        new CategoriaDTO(
                                d.getMenu().getCategoria().getId(),
                                d.getMenu().getCategoria().getNombre()
                        )
                ));
                return doDTO;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
