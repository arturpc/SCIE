<<<<<<< HEAD
package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class LogAlteracaoBancoDAO extends DAO<LogAlteracaoBanco> {

	public static LogAlteracaoBancoDAO LogAlteracaoBancoDAO() {
		if (getDao() == null) {
			setDao(new LogAlteracaoBancoDAO());
		}
		return getDao();

	}

	public static LogAlteracaoBancoDAO getDao() {
		return logdao;
	}

	public static void setDao(LogAlteracaoBancoDAO dao) {
		LogAlteracaoBancoDAO.logdao = dao;
	}

	/**
	 * Persiste um LogAlteracaoBanco
	 */
	@Override
	public LogAlteracaoBanco add(LogAlteracaoBanco entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
			return entity;

		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new DAOExcpetion("Erro ao persistir Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um LogAlteracaoBanco pelo id
	 */
	@Override
	public LogAlteracaoBanco get(Object id) throws EntityNotFoundException {
		if (id.getClass() == LogAlteracaoBanco.class) {
			return getById(((LogAlteracaoBanco) id).getId());
		}
		return null;
	}

	/**
	 * Seleciona um LogAlteracaoBanco pelo id
	 */
	public LogAlteracaoBanco getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogAlteracaoBanco> typedQuery = entityManager.createNamedQuery(LogAlteracaoBanco.LOG_FIND_BY_ID,
					LogAlteracaoBanco.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Log por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
=======
package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class LogAlteracaoBancoDAO extends DAO<LogAlteracaoBanco> {

	public static LogAlteracaoBancoDAO logAlteracaoBancoDAO() {
		if (getDao() == null) {
			setDao(new LogAlteracaoBancoDAO());
		}
		return getDao();

	}

	public static LogAlteracaoBancoDAO getDao() {
		return logdao;
	}

	public static void setDao(LogAlteracaoBancoDAO dao) {
		LogAlteracaoBancoDAO.logdao = dao;
	}

	/**
	 * Persiste um LogAlteracaoBanco
	 */
	@Override
	public LogAlteracaoBanco add(LogAlteracaoBanco entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
			return entity;

		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new DAOExcpetion("Erro ao persistir Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um LogAlteracaoBanco pelo id
	 */
	@Override
	public LogAlteracaoBanco get(Object id) throws EntityNotFoundException {
		if (id.getClass() == LogAlteracaoBanco.class) {
			return getById(((LogAlteracaoBanco) id).getId());
		}
		return null;
	}

	/**
	 * Seleciona um LogAlteracaoBanco pelo id
	 */
	public LogAlteracaoBanco getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogAlteracaoBanco> typedQuery = entityManager.createNamedQuery(LogAlteracaoBanco.LOG_FIND_BY_ID,
					LogAlteracaoBanco.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Log por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
>>>>>>> origin/master
