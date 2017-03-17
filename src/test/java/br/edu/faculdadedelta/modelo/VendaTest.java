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

public class VendaTest {
	private static final String CPF_PADRAO = "001.001.100-10";
	private EntityManager em;
	
	private Produto criarProduto(String nome, String marca){
		Produto produto = new Produto();
		produto.setNome("NoteBook");
		produto.setFabricante("Dell");
		return produto;
				
	}
	
	private Venda criarVenda(String cpf){
		Cliente cliente = new Cliente();
		cliente.setNome("Nirso");
		cliente.setCpf(cpf == null ? CPF_PADRAO : cpf);
		assertTrue("Não deve ter ID definido", cliente.isTransient());
		Venda venda = new Venda();
		venda.setDataHora(new Date());
		venda.setCliente(cliente);
		return venda;
	}
	
	private Venda criarVenda(){
		return criarVenda(null);
	}
	
	
	
	@Test
	public void deveSalvarVendaComrelacionamentosEmCascata(){
		Venda venda = criarVenda();
		
		venda.getProdutos().add(criarProduto("NoteBook", "Sony"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));
		
		assertTrue("não deve ter ID definido",venda.isTransient());
		
		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();
		
		assertFalse("deve ter ID definido",venda.isTransient());
		assertFalse("deve ter ID definido",venda.getCliente().isTransient());
		
		for(Produto produto : venda.getProdutos()){
			assertFalse("Deve ter id definido", produto.isTransient());
		}
	}
	
	@Test(expected = IllegalStateException.class)
	public void naoDeveFazerMergeEmObjetosTransient(){
		Venda venda = criarVenda();
		
		venda.getProdutos().add(criarProduto("NoteBook", "Sony"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));
		
		assertTrue("não deve ter ID definido",venda.isTransient());
		
		em.getTransaction().begin();
		venda = em.merge(venda);
		em.getTransaction().commit();
		
		fail("não deveria ter salvo (merge) uma venda nova com relacionamentos trasient");
	}
	
	@Test
	public void deveConsultarQuantidadeProdutosVendidos(){
		Venda venda = criarVenda("001.001.001-01");
		
		for (int i=0;i<10;i++){
			venda.getProdutos().add(criarProduto("Produto "+i, "Marca "+i));
		}
		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();
		
		assertFalse("deve ter ID definido",venda.isTransient());
		
		int qtdProdutosAdicionados = venda.getProdutos().size();
		
		assertFalse("Lista de produtos deve ter itens",qtdProdutosAdicionados>0);
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append("   FROM Venda v ");
		jpql.append("   INNER JOIN v.produtos p ");
		jpql.append("   INNER JOIN v.cliente c ");
		jpql.append("   WHERE c.cpf = :cpf ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("cpf", "001.001.001-01");
		
		Long qtdProdutosDavenda = (Long) query.getSingleResult();
		
		assertEquals("quantidade de produtos deve ser igual a quantidade da lista de produtos", qtdProdutosDavenda, qtdProdutosAdicionados);
		
	}
	@Before
	public void instanciarEntityManager(){
		em=JPAUtil.ISNTANCE.getEntityManager();
	}
	
	@After
	public void fecharEntityManagaer(){
		if(em.isOpen()){
			em.close();
		}
	}
	@AfterClass
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Venda v");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
	}
	
	
}
