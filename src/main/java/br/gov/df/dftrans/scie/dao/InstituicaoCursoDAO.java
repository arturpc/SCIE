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
import br.gov.df.dftrans.scie.domain.InstituicaoCurso;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class InstituicaoCursoDAO extends DAO<InstituicaoCurso> implements Serializable {
	private static InstituicaoCursoDAO dao = null;

	public static InstituicaoCursoDAO instituicaoCursoDAO() {
		if (dao == null) {
			setDao(new InstituicaoCursoDAO());
		}
		return getDao();
	}

	public static InstituicaoCursoDAO getDao() {
		return dao;
	}

	public static void setDao(InstituicaoCursoDAO dao) {
		InstituicaoCursoDAO.dao = dao;
	}

	/**
	 * Persiste uma InstituicaoCurso
	 */
	@Override
	public InstituicaoCurso add(InstituicaoCurso entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getCurso() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
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
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_INSTITUICAO_CURSO", entity.getId()));
		return entity;
	}

	/**
	 * Altera uma InstituicaoCurso
	 * 
	 * @param entity
	 * @return a InstituicaoCurso atualizada
	 */
	public InstituicaoCurso update(InstituicaoCurso entity) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar InstituicaoCurso");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("UPDATE", "TB_INSTITUICAO_CURSO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;

	}

	/**
	 * Deleta uma InstituicaoCurso
	 * 
	 * @param entity
	 */
	public void delete(InstituicaoCurso entity) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(entityManager.getReference(InstituicaoCurso.class, entity.getId()));
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao excluir InstituicaoCurso");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("DELETE", "TB_INSTITUICAO_CURSO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Seleciona uma InstituicaoCurso pelo id
	 */
	@Override
	public InstituicaoCurso get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_ID, InstituicaoCurso.class);
			return typedQuery.setParameter("id", ((InstituicaoCurso) id).getId()).getSingleResult();
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
	 * Seleciona todas Cursos persistidos no banco onde o ano da
	 * instituicaoEnsino seja = ao maior ano
	 * 
	 * @param inst
	 * @param curso
	 * @return uma lista de InstituicaoCurso ou null
	 */
	public InstituicaoCurso get(InstituicaoEnsino inst, Curso curso) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_CURSO, InstituicaoCurso.class);
			return typedQuery.setParameter("idInstituicao", inst.getId()).setParameter("idCurso", curso.getId())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar InstituicaoCurso por Instituicao e Curso");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todas as Instituições Cursos persistidas no banco ond o ano da
	 * instituicaoEnsino seja = ao maior ano
	 * 
	 * @param inst
	 * @param curso
	 * @return uma lista de InstituicaoCurso ou null
	 */
	public List<InstituicaoCurso> getAll(InstituicaoEnsino inst, Curso curso) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_FIND_ALL_BY_CURSO, InstituicaoCurso.class);
			return typedQuery.setParameter("idInstituicao", inst.getId()).setParameter("idCurso", curso.getId())
					.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar InstituicaoCurso por Instituicao e Curso");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de Curso pelo id da Instituicao
	 * 
	 * @param idInstituicao
	 * @return uma Lista de Curso ou null
	 * @throws EntityNotFoundException
	 */
	public List<Curso> getCursos(int idInstituicao) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_INSTITUICAO, InstituicaoCurso.class);
			List<InstituicaoCurso> list = typedQuery.setParameter("id", idInstituicao).getResultList();
			List<Curso> cursos = new ArrayList<Curso>();
			CursoDAO cursodao = CursoDAO.cursoDAO();
			for (InstituicaoCurso i : list) {
				cursos.add(cursodao.get(i.getCurso()));
			}
			return cursos;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Cursos de Instituição por Instituição");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de InstituicaoCurso pela InstituicaoEnsino
	 * 
	 * @param instituicao
	 * @return uma lista de InstituicaoCurso ou null
	 * @throws EntityNotFoundException
	 */
	public List<InstituicaoCurso> getCursos(InstituicaoEnsino instituicao) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_FIND_BY_INSTITUICAO, InstituicaoCurso.class);
			List<InstituicaoCurso> list = typedQuery.setParameter("id", instituicao.getId()).getResultList();
			return list;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Cursos de Instituição por Instituição");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de InstituicaoCurso pela Instituicoa ensino e curso
	 * 
	 * @param instituicao
	 * @param curso
	 * @return uma lista de InstituicaoCurso ou null
	 * @throws EntityNotFoundException
	 */
	public List<InstituicaoCurso> getAllCursos(InstituicaoEnsino instituicao, Curso curso)
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager.createNamedQuery(
					InstituicaoCurso.INSTITUICAOCURSO_FIND_ALL_BY_INSTITUICAO, InstituicaoCurso.class);
			List<InstituicaoCurso> list = typedQuery.setParameter("inst", instituicao).setParameter("curso", curso)
					.getResultList();
			return list;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Cursos de Instituição por Instituição");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todas as instituicoes Curso persistidas no banco
	 * 
	 * @return uma list de InstituicaoCurso ou null
	 */
	public List<InstituicaoCurso> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoCurso> typedQuery = entityManager
					.createNamedQuery(InstituicaoCurso.INSTITUICAOCURSO_GET_ALL, InstituicaoCurso.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Curso por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma list de InstituicaoCurso
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<InstituicaoCurso> list) throws InsertException, EntityNotFoundException {
		for (InstituicaoCurso instCurso : list) {
			if (get(instCurso) == null) {
				add(instCurso);
			}
		}
	}
}
