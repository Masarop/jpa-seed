package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

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
			Produto produto = criarProduto("NoteBook", "Sony");
			//produto.setNome("Notebook");
			//produto.setFabricante("Sony");
			em.persist(produto);
		}
		em.getTransaction().commit();
	}

	public void criarVendas(int qtd) {
		
		em.getTransaction().begin();
		
		for (int i = 0; i < qtd; i++) {

			Venda venda = criarVenda();
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
	private Session getSession(){
		return (Session) em.getDelegate();
	}
	
	private Criteria createCriteria(Class<?> clazz){
		return getSession().createCriteria(clazz);
		}
	private Criteria createCriteria(Class<?> clazz, String alias){
		return getSession().createCriteria(clazz, alias);
	}
	
	@SuppressWarnings("unused")
	@Test
	public void deveConsultarTodosClientes(){
		criarClientes(3);
		
		Criteria criteria =createCriteria(Cliente.class, "C");
		
		List<Cliente> clientes = criteria
							.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
							.list();
		assertTrue("Verifica se a quantidade e clientes é pelo menos 3", clientes.size()>=3);
		clientes.forEach(cliente -> assertFalse(cliente.isTransient()));
		
	}
	
	@Test
	public void deveConsultarMaiorIdCliente(){
		criarClientes(3);
		Criteria criteria = createCriteria(Cliente.class,"c").setProjection(Projections.max("c.id"));
		Long maiorId = (Long) criteria .setResultTransformer(Criteria.PROJECTION).uniqueResult();
		assertTrue("Verifica se o ID é maior que 2 (Salvou 3 clientes",maiorId >=3);
	}
	
	@Test
		public void deveConsultarVendasDaUltimaSemana(){
		criarVendas(3);
		Calendar ultimaSemana = Calendar.getInstance();
		ultimaSemana.add(Calendar.WEEK_OF_YEAR, -1);
		
		Criteria criteria = createCriteria(Venda.class,"v")
				.add(Restrictions.between("v.dataHora",  ultimaSemana.getTime(), new Date()))
				.setProjection(Projections.rowCount());
				
		Long qtdVendas = (Long) criteria
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.uniqueResult();
		assertTrue("Verifica se a quantidade de vendas é pelo menos 3", qtdVendas >=3);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarNoteBooks(){
		criarProdutos(3);
		Criteria criteria = createCriteria(Produto.class, "p")
					.add(Restrictions.in("p.nome", "NoteBook","NetBook","MacBook"))
					.addOrder(Order.asc("p.fabricante"));
		
		List<Produto> noteBooks = criteria
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
		assertTrue("Verifica se a quantidade de notebooks é pelo menos 3", noteBooks.size()>=3);
		
		noteBooks.forEach(noteBook -> assertFalse(noteBook.isTransient()));
	}
	@Test
	public void deveConsultarDezPrimeirosProdutos(){
		criarProdutos(20);
		
		Criteria criteria = createCriteria(Produto.class, "p")
				.setFirstResult(1)
				.setMaxResults(10)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<Produto> produtos = criteria.list();
		assertTrue("deve ter produtos",produtos.size() == 10);
		
		produtos.forEach(produto -> assertFalse(produto.isTransient()));
	}
	
	@Test
	public void deveConsultarQuantidadeVendasPorCliente(){
		criarVendas(3);
		Criteria criteria = createCriteria(Venda.class, "v")
		//Join
		.createAlias("v.cliente", "c")
		//where =
		.add(Restrictions.eq("c.cpf", CPF_PADRAO))
		//COUNT(*)
		.setProjection(Projections.rowCount())
		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		Long qtdRegistros = (Long) criteria.uniqueResult();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 3",qtdRegistros >=3);
		
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
