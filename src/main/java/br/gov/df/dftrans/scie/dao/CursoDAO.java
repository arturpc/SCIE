package br.gov.df.dftrans.scie.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.Curso;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class CursoDAO extends DAO<Curso> implements Serializable {
	private static CursoDAO dao = null;
	private LogAlteracaoBancoDAO logdao = LogAlteracaoBancoDAO.LogAlteracaoBancoDAO();

	public static CursoDAO CursoDAO() {
		if (dao == null) {
			setDao(new CursoDAO());
		}
		return getDao();
	}

	public static CursoDAO getDao() {
		return dao;
	}

	public static void setDao(CursoDAO dao) {
		CursoDAO.dao = dao;
	}

	/**
	 * Seleciona todos os cursos persistidos no banco
	 * 
	 * @return lista de curso
	 */
	public List<Curso> getAll() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Curso> typedQuery = entityManager.createNamedQuery(Curso.CURSO_GET_ALL, Curso.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar cursos");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste um curso
	 */
	@Override
	public Curso add(Curso entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();

		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getCurso() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
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
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_CURSO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um curso pelo id
	 */
	@Override
	public Curso get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Curso> typedQuery = entityManager.createNamedQuery(Curso.CURSO_FIND_BY_ID, Curso.class);
			return typedQuery.setParameter("id", ((Curso) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Autorizacao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma lista de cursos
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Curso> list) throws InsertException, EntityNotFoundException {
		for (Curso curso : list) {
			if (get(curso) == null) {
				add(curso);
			}
		}
	}

	/**
	 * Seleciona um curso pelo código emec
	 * 
	 * @param emec
	 * @return
	 */
	public Curso getByEmec(String emec) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Curso> typedQuery = entityManager.createNamedQuery(Curso.CURSO_FIND_BY_EMEC, Curso.class);
			List<Curso> list = new ArrayList<Curso>();
			list = typedQuery.setParameter("codEmec", emec).getResultList();
			if (!list.isEmpty()) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Curso por EMEC");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um curso pelo nome do curso
	 * 
	 * @param curso
	 * @return curso caso exista ou null
	 */
	public Curso getByCurso(String curso) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Curso> typedQuery = entityManager.createNamedQuery(Curso.CURSO_FIND_BY_CURSO, Curso.class);
			return typedQuery.setParameter("curso", curso).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Curso por Descrição");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
}
