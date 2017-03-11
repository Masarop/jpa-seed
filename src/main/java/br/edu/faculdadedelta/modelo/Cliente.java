package br.edu.faculdadedelta.modelo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Cliente extends BaseEntity<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_cliente")
	private Long id;
	
	@Column(length=60,nullable=false)
	private String nome;
	
	@Column(length=20)
	private String cpf;
	
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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente() {
	}

}
