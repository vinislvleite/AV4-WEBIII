package com.autobots.automanager.filtros;

import javax.servlet.http.HttpServletRequest;

public class AnalisadorCabecalho {
	private static final String CABECALHO = "Authorization";
	private static final String PREFIXO = "Bearer ";

	public String obterJwt(HttpServletRequest requisicao) {
		String cabecalho = requisicao.getHeader(CABECALHO);
		if (cabecalho != null && cabecalho.startsWith(PREFIXO)) {
			return cabecalho.substring(PREFIXO.length());
		}
		return null;
	}
}