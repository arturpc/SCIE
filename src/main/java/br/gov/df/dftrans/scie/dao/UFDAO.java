package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.List;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.UF;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class UFDAO extends DAO<UF> {
	private static UFDAO dao = null;

	public static UFDAO UFDAO() {
		if (dao == null) {
			setDao(new UFDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona um UF por UF
	 * 
	 * @param chave
	 * @return se encontrar o UF ou null
	 */
	public UF getByUF(String chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<UF> typedQuery = entityManager.createNamedQuery(UF.UF_FIND_BY_UF, UF.class);
			return typedQuery.setParameter("uf", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por sigla");
		} finally {
			entityManager.close();
		}

	}

	/**
	 * Persiste um UF
	 */
	public UF add(UF entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getUf() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_UF", entity.getId()));
		return entity;
	}

	/**
	 * Persiste uma lista de UF
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<UF> list) throws InsertException, EntityNotFoundException {
		for (UF uf : list) {
			if (get(uf) == null) {
				add(uf);
			}
		}
	}

	/**
	 * Seleciona um UF usando como parâmetro de pesquisa um id
	 */
	public UF get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<UF> typedQuery = entityManager.createNamedQuery(UF.UF_FIND_BY_ID, UF.class);
			return typedQuery.setParameter("id", ((UF) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todos os UFs persestidos na base
	 * 
	 * @return um list de UF ou null
	 */
	public List<UF> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<UF> typedQuery = entityManager.createNamedQuery(UF.UF_GET_ALL, UF.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UFs");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	public static UFDAO getDao() {
		return dao;
	}

	public static void setDao(UFDAO dao) {
		UFDAO.dao = dao;
	}
}