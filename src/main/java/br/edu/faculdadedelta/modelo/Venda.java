package br.edu.faculdadedelta.modelo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


public class Venda extends BaseEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id_venda")
	private Long id;
	
	@ManyToOne(cascade = {CascadeType.PERSIST},fetch=FetchType.LAZY)
	@JoinColumn(name="id_cliente",referencedColumnName="id_cliente",nullable=false,insertable=true,updatable=false)
	private Cliente cliente;
	
	
	
	@Override
	public Long getId() {
		return null;
	}



	public Cliente getCliente() {
		return cliente;
	}



	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}



	public void setId(Long id) {
		this.id = id;
	}
	

}
