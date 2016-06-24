package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.Comentario;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class ComentarioSegundaViaDAO extends DAO<Comentario> {

	private static ComentarioSegundaViaDAO dao = null;

	public static ComentarioSegundaViaDAO ComentarioSegundaViaDAO() {
		if (getDao() == null) {
			setDao(new ComentarioSegundaViaDAO());
		}
		return getDao();

	}

	public static ComentarioSegundaViaDAO getDao() {
		return dao;
	}

	public static void setDao(ComentarioSegundaViaDAO dao) {
		ComentarioSegundaViaDAO.dao = dao;
	}

	/**
	 * Persistem um comentário
	 */
	@Override
	public Comentario add(Comentario entity) throws InsertException {
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
		logdao.add(new LogAlteracaoBanco("INSERT",
				"TB_COMENTARIOS_SEGUNDA_VIA",
				entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um comentário pelo id
	 */
	@Override
	public Comentario get(Object id) throws EntityNotFoundException {
		if (id.getClass() == Comentario.class) {
			return getById(((Comentario) id).getId());
		}
		return null;
	}

	/**
	 * Seleciona um comentário pelo id
	 */
	public Comentario getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Comentario> typedQuery = 
					entityManager.createNamedQuery(Comentario
							.COMENTARIO_FIND_BY_ID,
					Comentario.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitacao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

}
