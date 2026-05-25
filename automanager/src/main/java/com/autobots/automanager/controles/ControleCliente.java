package com.autobots.automanager.controles;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.RepositorioCliente;

@RestController
public class ControleCliente {

	@Autowired
	private RepositorioCliente repositorioCliente;

	@GetMapping("/obter-clientes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR')")
	public ResponseEntity<List<Cliente>> obterClientes() {
		return ResponseEntity.ok(repositorioCliente.findAll());
	}

	@GetMapping("/obter-cliente/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR', 'ROLE_CLIENTE')")
	public ResponseEntity<?> obterCliente(@PathVariable Long id) {
		return repositorioCliente.findById(id)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/cadastrar-cliente")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR')")
	public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repositorioCliente.save(cliente));
	}

	@PutMapping("/atualizar-cliente/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE', 'ROLE_VENDEDOR')")
	public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
		return repositorioCliente.findById(id).map(cliente -> {
			cliente.setNome(clienteAtualizado.getNome());
			cliente.setEmail(clienteAtualizado.getEmail());
			cliente.setTelefone(clienteAtualizado.getTelefone());
			return ResponseEntity.ok(repositorioCliente.save(cliente));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/deletar-cliente/{id}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERENTE')")
	public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
		if (!repositorioCliente.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repositorioCliente.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
