package br.gov.df.dftrans.scie.dao;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.Frequencia;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class FrequenciaDAO extends DAO<Frequencia> {
	private static FrequenciaDAO dao;

	public static FrequenciaDAO FrequenciaDAO() {
		if (dao == null) {
			setDao(new FrequenciaDAO());
		}
		return getDao();
	}

	public static FrequenciaDAO getDao() {
		return dao;
	}

	public static void setDao(FrequenciaDAO dao) {
		FrequenciaDAO.dao = dao;
	}

	/**
	 * Seleciona todas as Frequencias persistidas no banco
	 * 
	 * @return uma lista de Frequencia ou null
	 */
	public List<Frequencia> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Frequencia> typedQuery = entityManager
					.createNamedQuery(Frequencia
							.FREQUENCIA_GET_ALL,
					Frequencia.class);
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
	 * Persiste uma lista de Frequencia
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Frequencia> list) throws InsertException, EntityNotFoundException {
		for (Frequencia freq : list) {
			if (get(freq) == null) {
				add(freq);
			}
		}
	}

	/**
	 * Persiste uma Frequencia
	 */
	@Override
	public Frequencia add(Frequencia entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		EstudanteDAO estdao = EstudanteDAO.EstudanteDAO();
		entity.setEstudante(estdao.add(entity.getEstudante()));
		try {

			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getInstituicao().toString() 
					+ entity.getEstudante().toString()
					+ entity.getDataReferencia() + getString(
							ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			e.printStackTrace();
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_FREQUENCIA", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona uma Frequencia pelo id
	 */
	@Override
	public Frequencia get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Frequencia> typedQuery = entityManager
					.createNamedQuery(Frequencia
							.FREQUENCIA_FIND_BY_ID,
					Frequencia.class);
			return typedQuery.setParameter("id", ((Frequencia) id).getId())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Frequencia por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de Frequencia pela InstituicaoEnsino
	 * 
	 * @param id
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<Frequencia> getInst(InstituicaoEnsino id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Frequencia> typedQuery = entityManager
					.createNamedQuery(Frequencia
							.FREQUENCIA_FIND_BY_INST,
					Frequencia.class);
			return typedQuery.setParameter("instituicao", id).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Frequencias por Instituicão");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
