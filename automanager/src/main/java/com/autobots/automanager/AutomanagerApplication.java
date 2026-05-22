package com.autobots.automanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.Perfil;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@SpringBootApplication
public class AutomanagerApplication implements CommandLineRunner {

	@Autowired
	private RepositorioUsuario repositorio;

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
		Usuario usuario = new Usuario();
		usuario.setNome("administrador");
		usuario.getPerfis().add(Perfil.ROLE_ADMIN);
		Credencial credencial = new Credencial();
		credencial.setNomeUsuario("admin");
		String senha  = "123456";
		credencial.setSenha(codificador.encode(senha));
		usuario.setCredencial(credencial);
		repositorio.save(usuario);
	}
}