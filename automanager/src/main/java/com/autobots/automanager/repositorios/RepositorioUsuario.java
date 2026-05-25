package com.autobots.automanager.repositorios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.autobots.automanager.entidades.Usuario;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

	@Query("SELECT u FROM Usuario u WHERE u.credencial.nomeUsuario = :nomeUsuario")
	Optional<Usuario> findByCredencialNomeUsuario(@Param("nomeUsuario") String nomeUsuario);
}