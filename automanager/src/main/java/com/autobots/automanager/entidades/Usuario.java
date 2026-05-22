package com.autobots.automanager.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.autobots.automanager.modelos.Perfil;

import lombok.Data;

@Data
@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String nome;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Credencial credencial;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<Perfil> Perfis = new ArrayList<>();
}