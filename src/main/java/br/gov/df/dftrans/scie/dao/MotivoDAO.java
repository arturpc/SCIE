<<<<<<< HEAD
package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Motivo;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class MotivoDAO extends DAO<Motivo> {

	private static MotivoDAO dao = null;

	public static MotivoDAO MotivoDAO() {
		if (getDao() == null) {
			setDao(new MotivoDAO());
		}
		return getDao();

	}

	public static MotivoDAO getDao() {
		return dao;
	}

	public static void setDao(MotivoDAO dao) {
		MotivoDAO.dao = dao;
	}

	/**
	 * Persiste um motivo
	 */
	@Override
	public Motivo add(Motivo entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new DAOExcpetion("Erro ao persistir Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_MOTIVO_VAL_SEGUNDA_VIA", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um motivo passando como parâmetro um id
	 */
	@Override
	public Motivo get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Motivo> typedQuery = entityManager.createNamedQuery(Motivo.MOTIVO_FIND_BY_ID, Motivo.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Motivo por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todos os motivos persestidos no banco de dados
	 * 
	 * @return um list de Motivo ou null
	 * @throws EntityNotFoundException
	 */
	public List<Motivo> getAll() throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Motivo> typedQuery = entityManager.createNamedQuery(Motivo.MOTIVO_GET_ALL, Motivo.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar todos os Motivo");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
=======
package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Motivo;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class MotivoDAO extends DAO<Motivo> {

	private static MotivoDAO dao = null;

	public static MotivoDAO motivoDAO() {
		if (getDao() == null) {
			setDao(new MotivoDAO());
		}
		return getDao();

	}

	public static MotivoDAO getDao() {
		return dao;
	}

	public static void setDao(MotivoDAO dao) {
		MotivoDAO.dao = dao;
	}

	/**
	 * Persiste um motivo
	 */
	@Override
	public Motivo add(Motivo entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new DAOExcpetion("Erro ao persistir Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_MOTIVO_VAL_SEGUNDA_VIA", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um motivo passando como parâmetro um id
	 */
	@Override
	public Motivo get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Motivo> typedQuery = entityManager.createNamedQuery(Motivo.MOTIVO_FIND_BY_ID, Motivo.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Motivo por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todos os motivos persestidos no banco de dados
	 * 
	 * @return um list de Motivo ou null
	 * @throws EntityNotFoundException
	 */
	public List<Motivo> getAll() throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Motivo> typedQuery = entityManager.createNamedQuery(Motivo.MOTIVO_GET_ALL, Motivo.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar todos os Motivo");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
>>>>>>> origin/master
