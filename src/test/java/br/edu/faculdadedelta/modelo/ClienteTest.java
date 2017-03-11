package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class ClienteTest {
	private static final String CPF_PADRAO = "001.001.100-10";
	private EntityManager em;
	
	
	@Test
	public void deveSalvarCliente(){
		Cliente cli = new Cliente();
		cli.setNome("Nirson");
		cli.setCpf(CPF_PADRAO);
		assertTrue("Não deve ter ID definido", cli.isTransient());
		
		em.getTransaction().begin();
		em.persist(cli);
		em.getTransaction().commit();
		
		assertFalse("entidade agora tem id ainda", cli.isTransient());
	}
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultar(){
		deveSalvarCliente();
		
		
		Query query = em.createQuery("SELECT c.cpf FROM Cliente c WHERE c.nome LIKE :nome");
		query.setParameter("nome", "%Nirson%");
		List<String> lisCPF = query.getResultList();
		
		assertFalse("verifica se há registros na lista", lisCPF.isEmpty());
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
		Query query = entityManager.createQuery("DELETE FROM Cliente c");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
	}
}
