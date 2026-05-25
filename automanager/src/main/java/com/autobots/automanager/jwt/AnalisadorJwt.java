package com.autobots.automanager.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class AnalisadorJwt {
	private String assinatura;

	public AnalisadorJwt(String assinatura) {
		this.assinatura = assinatura;
	}

	public Claims obterReivindicacoes(String jwt) {
		try {
			return Jwts.parser()
					.setSigningKey(this.assinatura.getBytes())
					.parseClaimsJws(jwt)
					.getBody();
		} catch (Exception e) {
			return null;
		}
	}
}
