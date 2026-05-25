package com.autobots.automanager.filtros;

import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.autobots.automanager.jwt.ProvedorJwt;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Autenticador extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager gerenciadorAutenticacao;
	private ProvedorJwt provedorJwt;

	public Autenticador(AuthenticationManager gerenciadorAutenticacao, ProvedorJwt provedorJwt) {
		this.gerenciadorAutenticacao = gerenciadorAutenticacao;
		this.provedorJwt = provedorJwt;
		setFilterProcessesUrl("/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws AuthenticationException {
		try {
			Map<?, ?> credenciais = new ObjectMapper().readValue(requisicao.getInputStream(), Map.class);
			String nomeUsuario = (String) credenciais.get("nomeUsuario");
			String senha = (String) credenciais.get("senha");
			UsernamePasswordAuthenticationToken autenticacao =
					new UsernamePasswordAuthenticationToken(nomeUsuario, senha);
			return gerenciadorAutenticacao.authenticate(autenticacao);
		} catch (IOException e) {
			throw new RuntimeException("Falha ao autenticar usuario", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest requisicao, HttpServletResponse resposta,
			FilterChain cadeia, Authentication autenticacao) throws IOException, ServletException {
		UserDetails usuario = (UserDetails) autenticacao.getPrincipal();
		String jwt = provedorJwt.gerarJwt(usuario.getUsername());
		resposta.addHeader("Authorization", "Bearer " + jwt);
		resposta.addHeader("Access-Control-Expose-Headers", "Authorization");
	}
}
