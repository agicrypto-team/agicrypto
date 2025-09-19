package br.com.agicrypto.agicrypto.service;

import br.com.agicrypto.agicrypto.model.Usuarios;
import br.com.agicrypto.agicrypto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    public UsuarioService (UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    public List<Usuarios> listarTodos() { return usuarioRepository.findAll(); }

    public Optional<Usuarios> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuarios salvar(Usuarios usuario) {
        return usuarioRepository.save(usuario);
    }

    public void remover(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
