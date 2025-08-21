package com.devsoft.orders_api.auth;

import com.devsoft.orders_api.auth.dto.LoginDTO;
import com.devsoft.orders_api.auth.dto.RegisterDTO;
import com.devsoft.orders_api.repository.RoleRepository;
import com.devsoft.orders_api.repository.UserRepository;
import com.devsoft.orders_api.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    //endpoint para registro de usuarios
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto){
        Map<String, Object> response = new HashMap<>();
        try{
            if(userRepository.existsByUsername(dto.getUsername()) ||
                    userRepository.existsByNombre(dto.getNombre())){
                response.put("message","Ya existe un usuario con este nombre y login");
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
            authService.register(dto);
            response.put("message","Usuario registrado correctamente...!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("message", "Error al guardar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //endpoint para autenticar usuarios
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto){
        try{
            return ResponseEntity.ok(authService.authenticate(dto));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }
}
