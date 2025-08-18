package com.devsoft.orders_api.services;

import com.devsoft.orders_api.dto.DetalleOrdenDTO;
import com.devsoft.orders_api.dto.OrdenDTO;
import com.devsoft.orders_api.entities.*;
import com.devsoft.orders_api.interfaces.IOrdenService;
import com.devsoft.orders_api.repository.*;
import com.devsoft.orders_api.utils.EstadoOrden;
import com.devsoft.orders_api.utils.OrdenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService implements IOrdenService {
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> findAll() {
        return ordenRepository.findAll()
                .stream().map(OrdenMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> findByEstado(EstadoOrden estado) {
        return ordenRepository.findByEstado(estado)
                .stream().map(OrdenMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenDTO findById(Long id) {
        return ordenRepository.findById(id)
                .map(OrdenMapper::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public OrdenDTO registerOrUpdate(OrdenDTO ordenDTO) {
        //validamos referencias de objetos
        Optional<Cliente> clienteOpt = clienteRepository.findById(ordenDTO.getClienteDTO().getId());
        Optional<Mesa> mesaOpt = mesaRepository.findById(ordenDTO.getMesaDTO().getId());
        Optional<Usuario> userOpt = usuarioRepository.findById(ordenDTO.getUsuarioDTO().getId());
        if (clienteOpt.isEmpty() || mesaOpt.isEmpty() || userOpt.isEmpty()
                || ordenDTO.getDetalle() == null || ordenDTO.getDetalle().isEmpty()){
            return null; //gestionar en el controlador
        }
        Orden orden = null; //objeto a persistir
        if (ordenDTO.getId() == null){
            //Lógica para registrar una nueva orden
            orden = new Orden();
            //seteando los atributos
            orden.setFecha(LocalDate.now());
            orden.setHora(LocalTime.now());
            orden.setEstado(EstadoOrden.CREADA);
            orden.setCorrelativo(generarCorrelativo());
            orden.setTotal(ordenDTO.getTotal());
            orden.setDetalleOrden(new ArrayList<>());
        }else{
            //Lógica para actualizar una orden existente
            Optional<Orden> ordenOpt = ordenRepository.findById(ordenDTO.getId());
            if (ordenOpt.isEmpty()){
                return null;
            }
            orden = ordenOpt.get();
            //limpiamos el detalle previo
            orden.getDetalleOrden().clear();
        }
        //seteamos nuevas referencias
        orden.setCliente(clienteOpt.get());
        orden.setMesa(mesaOpt.get());
        orden.setUsuario(userOpt.get());

        //agregando el detalle de la orden
        for (DetalleOrdenDTO detalleDTO : ordenDTO.getDetalle()) {
            Optional<Menu> menuOpt = menuRepository.findById(detalleDTO.getMenuDTO().getId());
            if (menuOpt.isEmpty()) return null;
            Menu menu = menuOpt.get();
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setCantidad(detalleDTO.getCantidad());
            detalleOrden.setPrecio(detalleDTO.getPrecio());
            detalleOrden.setSubtotal(detalleDTO.getSubtotal());
            detalleOrden.setMenu(menu);
            detalleOrden.setOrden(orden);
            //agregamos el detalleOrden a la Orden
            orden.getDetalleOrden().add(detalleOrden);
        }
        Orden ordenPersisted = ordenRepository.save(orden);
        return OrdenMapper.toDTO(ordenPersisted);
    }

    @Override
    @Transactional
    public void anular(Long id) {
        Optional ordenOpt = ordenRepository.findById(id);
        if (ordenOpt.isEmpty()) return;
        Orden orden = (Orden) ordenOpt.get();
        orden.setEstado(EstadoOrden.ANULADA);
        ordenRepository.save(orden);
    }

    @Override
    @Transactional
    public OrdenDTO changeState(OrdenDTO dto) {
        Optional ordenOpt = ordenRepository.findById(dto.getId());
        if (ordenOpt.isEmpty()) return null;
        Orden orden = (Orden) ordenOpt.get();
        //cambiamos el estado de la orden
        orden.setEstado(dto.getEstado());
        return OrdenMapper.toDTO(ordenRepository.save(orden));
    }

    //métodos auxiliares
    private String generarCorrelativo() {
        LocalDate currentDate = LocalDate.now();
        String yearMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        Long maxCorrelativo = ordenRepository.findMaxCorrelativoByYearAndMonth(yearMonth);
        Long nextCorrelativo = (maxCorrelativo != null) ? maxCorrelativo + 1 : 1;
        return yearMonth + String.format("%04d", nextCorrelativo);
    }
}
