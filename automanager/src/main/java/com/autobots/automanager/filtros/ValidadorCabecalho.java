package com.autobots.automanager.filtros;

import javax.servlet.http.HttpServletRequest;

public class ValidadorCabecalho {
	private static final String CABECALHO = "Authorization";
	private static final String PREFIXO = "Bearer ";

	public boolean validar(HttpServletRequest requisicao) {
		String cabecalho = requisicao.getHeader(CABECALHO);
		return cabecalho != null && cabecalho.startsWith(PREFIXO);
	}
}