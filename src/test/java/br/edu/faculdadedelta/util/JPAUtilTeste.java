package br.edu.faculdadedelta.util;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JPAUtilTeste {

	
	private EntityManager em;
	
	@Test
	public void deveTerInstaciaDoEntityManager(){
		assertNotNull("deve Ter Instaciado o EntityManager", em);
	}
	@Test
	public void devefecharEntityManager(){
		em.close();
		assertFalse("fecha o entityManager",em.isOpen());
		
	}
	@Test
	public void deveAbrirUmaTransacao(){
		
		assertFalse("Transação dever estar fechada",em.getTransaction().isActive());
		
		em.getTransaction().begin();
		
		assertTrue("Transação dever estar Aberta",em.getTransaction().isActive());
		
	}
	@Before
	public void instanciarEntityManager(){
		em= JPAUtil.ISNTANCE.getEntityManager();
	}
	@After
	public void fecharEntityManager(){
		if(em.isOpen()){
			em.close();
		}
	}
}
