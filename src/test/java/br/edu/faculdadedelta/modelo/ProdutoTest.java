package br.edu.faculdadedelta.modelo;

import javax.persistence.EntityManager;

import org.junit.After;
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
