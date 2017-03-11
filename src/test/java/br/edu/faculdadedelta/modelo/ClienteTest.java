package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import br.edu.faculdadedelta.util.JPAUtil;

public class ClienteTest {
	private EntityManager em;
	
	
	
	
	
	
	
	
	
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
		Query query = entityManager.createQuery("DELETE FROM Cliente c");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
	}
}
