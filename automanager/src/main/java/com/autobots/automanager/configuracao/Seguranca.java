package com.autobots.automanager.configuracao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.autobots.automanager.adaptadores.UserDetailsServiceImpl;
import com.autobots.automanager.filtros.Autenticador;
import com.autobots.automanager.filtros.Autorizador;
import com.autobots.automanager.jwt.ProvedorJwt;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Seguranca {

	@Autowired
	private UserDetailsServiceImpl servico;

	@Autowired
	private ProvedorJwt provedorJwt;

	private static final String[] rotasPublicas = { "/", "/cadastrar-usuario", "/obter-usuarios" };

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
		http.cors().and().csrf().disable();

		http.authorizeHttpRequests()
				.antMatchers(rotasPublicas).permitAll()
				.anyRequest().authenticated();

		http.addFilter(new Autenticador(authManager, provedorJwt));
		http.addFilter(new Autorizador(authManager, provedorJwt, servico));

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
		fonte.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return fonte;
	}
}