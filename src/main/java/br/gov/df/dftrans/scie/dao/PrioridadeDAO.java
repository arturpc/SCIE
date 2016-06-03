
package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.boot.model.source.spi.SubclassEntitySource;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Prioridade;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class PrioridadeDAO extends DAO<Prioridade> {

	private static PrioridadeDAO dao = null;
	List<Prioridade> list;

	public static PrioridadeDAO PrioridadeDAO() {
		if (getDao() == null) {
			setDao(new PrioridadeDAO());
		}
		return getDao();

	}

	public static PrioridadeDAO getDao() {
		return dao;
	}

	public static void setDao(PrioridadeDAO dao) {
		PrioridadeDAO.dao = dao;
	}

	/**
	 * Persistem uma solicitação de prioridade
	 * @throws InsertException 
	 */
	@Override
	public Prioridade add(Prioridade entity) throws InsertException {
		try{
		EntityManager entityManager = factory.createEntityManager();
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_PRIORIDADE", entity.getId()));
		} catch(Exception e){
			throw new InsertException();
		}
		return entity;
	}

	/**
	 * Seleciona um prioridade pelo id
	 */
	@Override
	public Prioridade get(Object id) throws EntityNotFoundException {
		if (id.getClass() == Prioridade.class) {
			return getById(((Prioridade) id).getId());
		}
		return null;
	}

	/**
	 * Seleciona um prioridade pelo id
	 */
	public Prioridade getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Prioridade> typedQuery = entityManager.createNamedQuery(Prioridade.PRIORIDADE_FIND_BY_ID,
					Prioridade.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Prioridade por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
	
	/**
	 * Seleciona todas as Solicitacoes de prioridade persistidas na base
	 * 
	 * @return um List de Prioridades ou null
	 */
	public List<Prioridade> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Prioridade> typedQuery = entityManager.createNamedQuery(Prioridade.PRIORIDADE_GET_ALL,
					Prioridade.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Prioridades!");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
	
	public boolean Exists(String cpf){
		list = get();
		for (Prioridade p : list){
			if(p.getCpf().equals(cpf)){
				return true;
			}
		}
		return false;
	}

}