package com.autobots.automanager.filtros;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.autobots.automanager.adaptadores.UserDetailsServiceImpl;
import com.autobots.automanager.jwt.ProvedorJwt;

public class Autorizador extends BasicAuthenticationFilter {

	private ProvedorJwt provedorJwt;
	private UserDetailsServiceImpl servico;

	public Autorizador(AuthenticationManager gerenciadorAutenticacao, ProvedorJwt provedorJwt,
			UserDetailsServiceImpl servico) {
		super(gerenciadorAutenticacao);
		this.provedorJwt = provedorJwt;
		this.servico = servico;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta, FilterChain cadeia)
			throws IOException, ServletException {
		ValidadorCabecalho validador = new ValidadorCabecalho();
		if (validador.validar(requisicao)) {
			AnalisadorCabecalho analisador = new AnalisadorCabecalho();
			String jwt = analisador.obterJwt(requisicao);
			AutenticadorJwt autenticadorJwt = new AutenticadorJwt(jwt, provedorJwt, servico);
			UsernamePasswordAuthenticationToken autenticacao = autenticadorJwt.obterAutenticacao();
			if (autenticacao != null) {
				SecurityContextHolder.getContext().setAuthentication(autenticacao);
			}
		}
		cadeia.doFilter(requisicao, resposta);
	}
}
