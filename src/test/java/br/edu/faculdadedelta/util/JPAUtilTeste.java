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
