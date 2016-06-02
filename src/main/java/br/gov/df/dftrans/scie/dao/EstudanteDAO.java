package br.gov.df.dftrans.scie.dao;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


import br.gov.df.dftrans.scie.domain.Estudante;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class EstudanteDAO extends DAO<Estudante> implements Serializable {
	private static EstudanteDAO dao;

	public static EstudanteDAO EstudanteDAO() {
		if (dao == null) {
			setDao(new EstudanteDAO());
		}
		return getDao();
	}

	public static EstudanteDAO getDao() {
		return dao;
	}

	public static void setDao(EstudanteDAO dao) {
		EstudanteDAO.dao = dao;
	}

	/**
	 * Seleciona todos os Estudantes persistidos no banco
	 * 
	 * @return uma list de Estudante
	 */
	public List<Estudante> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Estudante> typedQuery = entityManager.createNamedQuery(Estudante.ESTUDANTE_GET_ALL,
					Estudante.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Estudante por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma lista de Estudante
	 * 
	 * @param list
	 * @return a lista de Estudante
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public List<Estudante> add(List<Estudante> list) throws InsertException, EntityNotFoundException {
		List<Estudante> retorno = new ArrayList<Estudante>();
		for (Estudante est : list) {
			if (getByCPF(est.getCpf()) == null) {
				est = add(est);
			}
			retorno.add(est);
		}
		return retorno;
	}

	/**
	 * Persiste um Estudante
	 */
	@Override
	public Estudante add(Estudante entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			Estudante est = getByCPF(entity.getCpf());
			if (est == null) {

				entityManager.getTransaction().begin();
				entity = entityManager.merge(entity);
				entityManager.getTransaction().commit();
			} else {
				entity = est;
			}
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getInstituicao().toString() + entity.getCpf().toString()
					+ getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			e.printStackTrace();
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
	 * Seleciona um Estudante pelo id
	 */
	@Override
	public Estudante get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Estudante> typedQuery = entityManager.createNamedQuery(Estudante.ESTUDANTE_FIND_BY_ID,
					Estudante.class);
			return typedQuery.setParameter("id", ((Estudante) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Estudante por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um estudante pelo CPF
	 * 
	 * @param CPF
	 * @return se encontrar o Estudante se não null
	 * @throws EntityNotFoundException
	 */
	public Estudante getByCPF(String CPF) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Estudante> typedQuery = entityManager.createNamedQuery(Estudante.ESTUDANTE_FIND_BY_CPF,
					Estudante.class);
			return typedQuery.setParameter("cpf", CPF).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Estudante por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
