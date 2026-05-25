package com.autobots.automanager.controles;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
public class ControleUsuario {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@PostMapping("/cadastrar-usuario")
	public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
		if (usuario.getCredencial() != null) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			usuario.getCredencial().setSenha(encoder.encode(usuario.getCredencial().getSenha()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(repositorioUsuario.save(usuario));
	}

	@GetMapping("/obter-usuarios")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		return ResponseEntity.ok(repositorioUsuario.findAll());
	}

	@GetMapping("/obter-usuario/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR', 'ROLE_CLIENTE')")
	public ResponseEntity<?> obterUsuario(@PathVariable Long id) {
		return repositorioUsuario.findById(id)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/atualizar-usuario/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR')")
	public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
		return repositorioUsuario.findById(id).map(usuario -> {
			usuario.setNome(usuarioAtualizado.getNome());
			if (usuarioAtualizado.getPerfis() != null && !usuarioAtualizado.getPerfis().isEmpty()) {
				usuario.setPerfis(usuarioAtualizado.getPerfis());
			}
			if (usuarioAtualizado.getCredencial() != null
					&& usuarioAtualizado.getCredencial().getSenha() != null
					&& !usuarioAtualizado.getCredencial().getSenha().isEmpty()) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				Credencial credencial = usuario.getCredencial();
				credencial.setSenha(encoder.encode(usuarioAtualizado.getCredencial().getSenha()));
			}
			return ResponseEntity.ok(repositorioUsuario.save(usuario));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/deletar-usuario/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE')")
	public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
		if (!repositorioUsuario.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repositorioUsuario.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
