package com.autobots.automanager.adaptadores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@Override
	public UserDetails loadUserByUsername(String nomeUsuario) throws UsernameNotFoundException {
		Usuario usuario = repositorioUsuario.findByCredencialNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + nomeUsuario));
		return new UserDetailsImpl(usuario);
	}
}
