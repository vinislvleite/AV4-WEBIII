package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
public class ControleUsuario {

	@Autowired
	private RepositorioUsuario repositorio;

	@PostMapping("/cadastrar-usuario")
	public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
		BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
		try {
			Credencial credencial = new Credencial();
			credencial.setNomeUsuario(usuario.getCredencial().getNomeUsuario());
			String senha = codificador.encode(usuario.getCredencial().getSenha());
			credencial.setSenha(senha);
			usuario.setCredencial(credencial);
			repositorio.save(usuario);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/obter-usuarios")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		List<Usuario> usuarios = repositorio.findAll();
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.FOUND);
	}
}