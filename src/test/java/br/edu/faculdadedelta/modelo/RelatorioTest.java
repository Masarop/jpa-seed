package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
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
			Produto produto = criarProduto("NoteBook", "SONY");
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
	
	@Test
	public void deveConsultarProdutosContendoParteDoNome(){
		criarProdutos(3);
		
		Criteria criteria = createCriteria(Produto.class,"p")
				//where ILIKE '%string%'
				.add(Restrictions.ilike("p.nome", "book",MatchMode.ANYWHERE))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<Produto> produtos = criteria.list();
		
		assertTrue("Verifica se a quantidade de produtos é pelo menos 3",produtos.size()>=3);
		
		produtos.forEach(produto -> assertFalse(produto.isTransient()));
	}
	@Test
	public void deveConsultarNoteBooksDellouSamsung(){
		criarProdutos(3);
		Criteria criteria = createCriteria(Produto.class,"p");
		//where or
		
		//criteria.add(Restrictions.eq("p.nome", "book"));
		criteria.add(
				Restrictions.or(
				Restrictions.eq("p.fabricante", "Dell"),
				Restrictions.eq("p.fabricante", "SONY")
				));
		
		List<Produto> produtos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		assertTrue("veridica se a quantidade de notebooks é pelo menos 3", produtos.size()>=3);
		produtos.forEach(produto -> assertFalse(produto.isTransient()));
	}
	
	@Test
	public void deveConsultarVendasENomeClienteCasoExista(){
		criarVendas(1);
		Criteria criteria = createCriteria(Venda.class,"v")
				//left join
				.createAlias("v.cliente", "c",JoinType.LEFT_OUTER_JOIN)
				//where ILIKE 'string%'
				.add(Restrictions.ilike("c.nome", "Nirso", MatchMode.START))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<Venda> vendas = criteria.list();
		
		assertTrue("verifica se a quantidade de vendas é pelo menos 1", vendas.size()>=1);
		vendas.forEach(venda -> assertFalse("trouxe os itens corretamente",venda.isTransient()));
	}
	
	@Test
	public void deveConsultarIdENomeProduto(){
		criarProdutos(1);
		
		ProjectionList projectionList = Projections.projectionList()
					//SELECT field_a, field_b, field_c
				.add(Projections.property("p.id").as("id"))
				.add(Projections.property("p.nome").as("nome"));
		
		Criteria criteria = createCriteria(Produto.class,"p")
				.setProjection(projectionList);
		
		List<Object[]> produtos = criteria
				.setResultTransformer(Criteria.PROJECTION)
				.list();
	
		assertTrue("verifica se a quantidade de produtos é pelo menos 1", produtos.size()>=1);
		produtos.forEach(produto ->{
			assertTrue("Primeiro item deve ser o ID",produto[0] instanceof Long);
			assertTrue("primeiro item deve ser o nome", produto[1] instanceof String);
		});
		
	}
	
	@Test
	public void deveConsultarClientesChaveValor(){
		criarClientes(5);
		
		ProjectionList projectionList = Projections.projectionList()
				//SELECT field_a, field_b, field_c
				.add(Projections.property("c.id").as("id"))
				.add(Projections.property("c.nome").as("nome"));
		
		Criteria criteria = createCriteria(Cliente.class,"c")
				.setProjection(projectionList);
		
		List<Map<String, Object>> clientes = criteria
				.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
				.list();
		
		assertTrue("verifica se a quantidade de cliente é pelo menos 5", clientes.size()>=5);
		
		clientes.forEach(clienteMap -> {
			clienteMap.forEach((chave, valor)->{
				assertTrue("chave deve ser String", chave instanceof String);
				assertTrue("valor deve ser String ou Long", valor instanceof String || valor instanceof Long);
			});
		});
				
	}
	
	@Test
	public void deveConsultarIdENomeConverterCliente(){
		criarClientes(3);
		ProjectionList projectionList = Projections.projectionList()
				//SELECT field_a, field_b, field_c
				.add(Projections.property("c.id").as("id"))
				.add(Projections.property("c.nome").as("nome"));
		
		Criteria criteria = createCriteria(Cliente.class,"c")
				.setProjection(projectionList);
		List<Cliente> clientes = criteria
				.setResultTransformer(Transformers.aliasToBean(Cliente.class))
				.list();
		
		assertTrue("verifica se a quantidade de clientes é pelo menos 3",clientes.size()>=3);
		clientes.forEach(cliente -> {
			assertTrue("ID deve estar preenchido", cliente.getId()!=null);
			assertTrue("Nome deve estar preenchido", cliente.getNome()!=null);
			assertTrue("CPF não deve estar preenchido", cliente.getCpf()==null);
		});
	}
	
	@Test
	public void deveConsultarVendasPorNomeClienteUsandoSubquery(){
		criarVendas(1);
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Cliente.class,"c")
				//where in
				.add(Restrictions.in("c.id", 1L,2L,3L,4L,5L,6L,7L,8L,9L,10L))
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
