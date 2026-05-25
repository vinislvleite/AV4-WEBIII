package com.autobots.automanager.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

@Component
public class ProvedorJwt {

	@Value("${jwt.secret}")
	private String assinatura;

	@Value("${jwt.expiration}")
	private long duracao;

	public String gerarJwt(String nomeUsuario) {
		GeradorJwt gerador = new GeradorJwt(assinatura, duracao);
		return gerador.gerarJwt(nomeUsuario);
	}

	public Claims obterReivindicacoes(String jwt) {
		AnalisadorJwt analisador = new AnalisadorJwt(assinatura);
		return analisador.obterReivindicacoes(jwt);
	}

	public boolean validarJwt(String jwt) {
		Claims reivindicacoes = obterReivindicacoes(jwt);
		ValidadorJwt validador = new ValidadorJwt();
		return validador.validar(reivindicacoes);
	}

	public String obterNomeUsuario(String jwt) {
		Claims reivindicacoes = obterReivindicacoes(jwt);
		if (reivindicacoes != null) {
			return reivindicacoes.getSubject();
		}
		return null;
	}
}