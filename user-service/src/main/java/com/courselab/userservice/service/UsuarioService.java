package com.courselab.userservice.service;

import com.courselab.userservice.domain.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario registerUsuario(Usuario usuario);
    Optional<Usuario> getUsuario(Long id);
}
