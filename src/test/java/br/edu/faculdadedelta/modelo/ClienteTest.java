package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.LazyInitializationException;
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
	public void deveConsultarCpf(){
		deveSalvarCliente();
		
		
		Query query = em.createQuery("SELECT c.cpf FROM Cliente c WHERE c.nome LIKE :nome");
		query.setParameter("nome", "%Nirson%");
		List<String> lisCPF = query.getResultList();
		
		assertFalse("verifica se há registros na lista", lisCPF.isEmpty());
	}
	
	
	@Test
	public void deveConsultarClienteComIdNome(){
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		@SuppressWarnings("unchecked")
		List<Cliente> clientes = query.getResultList();
	
		assertFalse("verifica se há registros na lista", clientes.isEmpty());
		
		for(Cliente cliente: clientes){
			assertNull("Verifica que o cpf deve estar null", cliente.getCpf());
			cliente.setCpf(CPF_PADRAO);
		}
	}
	
	@Test
	public void deveConsultaIdNome(){
		deveSalvarCliente();
		Query query = em.createQuery("SELECT c.id, c.nome FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = query.getResultList();
		
		assertFalse("verifica se há registros na lista", resultado.isEmpty());
		
		for(Object[] linha : resultado){
			assertTrue("Verifica que o primeiro item é o ID",linha[0] instanceof Long);
			assertTrue("Verifica que o primeiro item é o ID",linha[1] instanceof String);
			
			Cliente cliente = new Cliente((Long) linha[0],(String)linha[1]);
			
			assertNotNull(cliente);
		}
	}
	@Test
	public void deveVerificarExistenciaCliente(){
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT COUNT(c.id) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		Long qtdResultados = (Long) query.getSingleResult();
		
		assertTrue("Verifica se há registros na lista",qtdResultados>0L);
		
	}
	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResultComMuitosRegistros(){
		deveSalvarCliente();
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
	
		query.getSingleResult();
		
		fail("Método getSingleResult deve disparar exception Non UniqueResultException");
	}
	
	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistro(){
		deveSalvarCliente();
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", "000.000.000-00");
	
		query.getSingleResult();
		
		fail("Método getSingleResult deve disparar exception NoResultException");
	}
	
	@Test
	public void deveAcessarAtributoLazy(){
		deveSalvarCliente();
		
		Cliente cliente = em.find(Cliente.class, 1L);
		
		assertNotNull("Verifica se encontrou um registro", cliente);
		
		assertNotNull("lista lazy não deve ser null", cliente.getCompras());
	}
	
	@Test(expected = LazyInitializationException.class)
	public void naoDeveAcessarAtributoLazyforaEscopoEntityManager(){
	deveSalvarCliente();
	Cliente cliente = em.find(Cliente.class, 1L);
	assertNotNull("Verifica se encontrou um registro",cliente);
	em.detach(cliente);
	cliente.getCompras().size();
	
	fail("Deve disparar LazyInitializationException ao acessar atributo lazy de um objeto fora de escopo do EntityManager");
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
