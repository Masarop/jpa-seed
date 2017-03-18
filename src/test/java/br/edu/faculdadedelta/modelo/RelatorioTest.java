package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class RelatorioTest {
	private static final String CPF_PADRAO = "001.001.100-10";
	private EntityManager em;

	private Produto criarProduto(String nome, String marca) {
		Produto produto = new Produto();
		produto.setNome(nome);
		produto.setFabricante(marca);
		return produto;

	}

	private Venda criarVenda(String cpf) {
		Cliente cliente = new Cliente();
		cliente.setNome("Nirso");
		cliente.setCpf(cpf == null ? CPF_PADRAO : cpf);
		assertTrue("Não deve ter ID definido", cliente.isTransient());
		Venda venda = new Venda();
		venda.setDataHora(new Date());
		venda.setCliente(cliente);
		return venda;
	}

	private Venda criarVenda() {
		return criarVenda(null);
	}

	public void criarProdutos(int qtd) {
		em.getTransaction().begin();
		for (int i = 0; i < qtd; i++) {
			Produto produto = new Produto();
			produto.setNome("Notebook");
			produto.setFabricante("Sony");
			em.persist(produto);
		}
		em.getTransaction().commit();
	}

	public void criarVendas(int qtd) {
		
		em.getTransaction().begin();
		
		for (int i = 0; i < qtd; i++) {

			Venda venda = new Venda();
			venda.getProdutos().add(criarProduto("Notebook", "SONY"));
			venda.getProdutos().add(criarProduto("Monitor", "AOC"));

			em.persist(venda);
		}
		em.getTransaction().commit();
	}

	public void criarClientes(int qtd) {
		em.getTransaction().begin();
		for (int i = 0; i < qtd; i++) {
			Cliente cliente = new Cliente();
			cliente.setNome("Nirso");
			cliente.setCpf(CPF_PADRAO);
			em.persist(cliente);
		}
		em.getTransaction().commit();
	}

	@Before
	public void instanciarEntityManager() {
		em = JPAUtil.ISNTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManagaer() {
		if (em.isOpen()) {
			em.close();
		}
	}

}
