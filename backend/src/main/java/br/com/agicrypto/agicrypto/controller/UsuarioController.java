package br.com.agicrypto.agicrypto.controller;

import br.com.agicrypto.agicrypto.model.Usuarios;
import br.com.agicrypto.agicrypto.repository.UsuarioRepository;
import br.com.agicrypto.agicrypto.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @GetMapping("/listar")
    public List<Usuarios> listar() { return usuarioService.listarTodos(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> buscarPorId(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/salvar")
    public Usuarios salvar(@RequestBody Usuarios usuario) {
        return usuarioService.salvar(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> atualizar(@PathVariable Integer id, @RequestBody Usuarios usuario) {
        return usuarioService.buscarPorId(id)
                .map(u -> {
                    usuario.setId(id);
                    return ResponseEntity.ok(usuarioService.salvar(usuario));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuarios> remover(@PathVariable Integer id) {

        if (usuarioService.buscarPorId(id).isPresent()) {
            usuarioService.remover(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
