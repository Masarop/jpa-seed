package br.edu.faculdadedelta.modelo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import br.edu.faculdadedelta.util.JPAUtil;

public class ProdutoTest {
	private EntityManager em;
	
	
	@Test
	public void deveSalvarProduto(){
		Produto produto = new Produto();
		produto.setNome("NoteBook");
		produto.setFabricante("Dell");
		assertTrue("Não deve ter ID definido", produto.isTransient());
		
		em.getTransaction().begin();
		em.persist(produto);
		em.getTransaction().commit();
		
		assertFalse("entidade agora tem id ainda", produto.isTransient());
	}
	@Test
	public void devePesquisarProdutos(){
		for(int i=0;i<10;i++){
			deveSalvarProduto();
		}
		
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class);
		List<Produto> produtos = query.getResultList();
		
		assertFalse("deve ter encontrado um produto", produtos.isEmpty());
		assertTrue("deve ter encontrado vários produtos", produtos.size()>=10);
	}
	@Test
	public void deveAlterarProduto(){
		deveSalvarProduto();
		
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p",Produto.class).setMaxResults(1);
		Produto prod = query.getSingleResult();
		
		assertNotNull("Deve ter encontrado um produto", prod);
		
		Integer versao = prod.getVersion();
		
		em.getTransaction().begin();
		
		prod.setFabricante("Sony");
		
		prod = em.merge(prod);

		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.intValue(), prod.getVersion().intValue());
	}
	
	@Test
	public void deveExcluirProduto(){
		deveSalvarProduto();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(p.id) FROM Produto p",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Produto prod = em.find(Produto.class, id);
		em.remove(prod);
		em.getTransaction().commit();
		Produto prodExcluido = em.find(Produto.class, id);
		
		assertNull("Não deve encontrar o produto",prodExcluido);
	}

	@AfterClass
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Produto p");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
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
	
}
