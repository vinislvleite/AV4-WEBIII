package com.autobots.automanager.adaptadores;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.autobots.automanager.entidades.Usuario;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String nomeUsuario;
	private String senha;
	private Collection<? extends GrantedAuthority> permissoes;

	public UserDetailsImpl(Usuario usuario) {
		this.nomeUsuario = usuario.getCredencial().getNomeUsuario();
		this.senha = usuario.getCredencial().getSenha();
		this.permissoes = usuario.getPerfis().stream()
				.map(perfil -> new SimpleGrantedAuthority(perfil.name()))
				.collect(Collectors.toList());
	}

	@Override public Collection<? extends GrantedAuthority> getAuthorities() { return permissoes; }
	@Override public String getPassword() { return senha; }
	@Override public String getUsername() { return nomeUsuario; }
	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return true; }
}
