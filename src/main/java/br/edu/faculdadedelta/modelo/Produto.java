package br.edu.faculdadedelta.modelo;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_produto")
public class Produto extends BaseEntity<Long>{

	
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
@Column(name="id_produto",nullable=false)
private Long id;

@Column(name="nm_produto",nullable=false,length=100)
private String nome;


	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public void setId(Long id) {
		this.id = id;
	}

	
	
}
