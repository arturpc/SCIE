package br.gov.df.dftrans.scie.dao;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.Declaracao;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class DeclaracaoDAO extends DAO<Declaracao> implements Serializable {
	private static DeclaracaoDAO dao;

	public static DeclaracaoDAO declaracaoDAO() {
		if (dao == null) {
			setDao(new DeclaracaoDAO());
		}
		return getDao();
	}

	public static DeclaracaoDAO getDao() {
		return dao;
	}

	public static void setDao(DeclaracaoDAO dao) {
		DeclaracaoDAO.dao = dao;
	}

	/**
	 * seleciona todas as declarações persistidas no banco
	 * 
	 * @return List de Declaracao
	 */
	public List<Declaracao> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Declaracao> typedQuery = entityManager.createNamedQuery(Declaracao.DECLARACAO_GET_ALL,
					Declaracao.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Declaracao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma lista de Declaracao
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Declaracao> list) throws InsertException, EntityNotFoundException {
		for (Declaracao dec : list) {
			if (get(dec) == null) {
				add(dec);
			}
		}
	}

	/**
	 * persiste uma declaracao
	 */
	@Override
	public Declaracao add(Declaracao entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		EstudanteDAO estdao = EstudanteDAO.estudanteDAO();
		entity.setEstudante(estdao.add(entity.getEstudante()));
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getInstituicao().toString() + entity.getEstudante().toString()
					+ getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_DECLARACAO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona uma declaração pelo id
	 */
	@Override
	public Declaracao get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Declaracao> typedQuery = entityManager.createNamedQuery(Declaracao.DECLARACAO_FIND_BY_ID,
					Declaracao.class);
			return typedQuery.setParameter("id", ((Declaracao) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Declaracao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de Declaracao pela instituicaoEnsino
	 * 
	 * @param id
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<Declaracao> getInst(InstituicaoEnsino id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Declaracao> typedQuery = entityManager.createNamedQuery(Declaracao.DECLARACAO_FIND_BY_INST,
					Declaracao.class);
			return typedQuery.setParameter("instituicao", id).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Declaracao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
