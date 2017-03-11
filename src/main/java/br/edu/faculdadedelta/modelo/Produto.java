package br.edu.faculdadedelta.modelo;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_produto")
public class Produto extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_produto", nullable = false)
	private Long id;

	@Column(name = "nm_produto", nullable = false, length = 100)
	private String nome;

	@Basic(fetch = FetchType.LAZY, optional = false)
	@Column(name = "nm_fabricante", length = 50)
	private String fabricante;

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_validade", nullable = true)
	private Date validade;

	@Override
	public Long getId() {
		return id;
	}

	public Produto() {
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

	public String getFabricante() {
		return fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

}
