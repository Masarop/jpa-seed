package br.edu.faculdadedelta.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="tb_venda")
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
	
	@ManyToMany(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY)
	@JoinTable(name="venda_produto", joinColumns=@JoinColumn(name="id_venda"),
							  inverseJoinColumns=@JoinColumn(name="id_produto"))
	private List<Produto> produtos;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dh_venda",nullable=false)
	private Date dataHora;

	///////////////////////////////////////////////
	
	@Override
	public Long getId() {
		return id;
	}

/////////////

	public Venda() {
	}

//////////

	public Date getDataHora() {
		return dataHora;
	}



	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}



	public List<Produto> getProdutos() {
		if(produtos==null){
			produtos=new ArrayList<>();
		}
		return produtos;
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
