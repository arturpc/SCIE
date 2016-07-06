package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class InstituicaoEnsinoDAO extends DAO<InstituicaoEnsino> implements Serializable {
	private static InstituicaoEnsinoDAO dao = null;

	public static InstituicaoEnsinoDAO InstituicaoEnsinoDAO() {
		if (dao == null) {
			setDao(new InstituicaoEnsinoDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona uma InstituicaoEnsino pela chave
	 * 
	 * @param chave
	 * @return uma InstituicaoEnsino se encontrado ou null se não
	 */
	public InstituicaoEnsino getByInepEmec(String chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoEnsino> typedQuery = entityManager
					.createNamedQuery(InstituicaoEnsino
							.INSTITUICAO_FIND_BY_INEP_EMEC,
							InstituicaoEnsino.class);
			return typedQuery.setParameter("codInepEmec", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Instituicao por Inep/E-mec");
		} finally {
			entityManager.close();
		}

	}

	/**
	 * Persiste uma InstituicaoEnsino
	 */
	@Override
	public InstituicaoEnsino add(InstituicaoEnsino entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		Endereco end = entity.getEndereco();
		EnderecoDAO enddao = EnderecoDAO.EnderecoDAO();
		RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
		entity.setEndereco(enddao.update(end));
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			throw new InsertException(entity.getNomeInstituicao() 
					+ getString(ALREADY_EXISTS_EXCEPTION_KEY));
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
		logdao.add(new LogAlteracaoBanco("INSERT",
				"TB_INSTITUICAO_ENSINO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona uma InstituicaoEnsino pelo id
	 * 
	 * @param id
	 * @return se encontrado uma InstituicaoEnsino se não null
	 * @throws EntityNotFoundException
	 */
	public InstituicaoEnsino getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoEnsino> typedQuery = entityManager
					.createNamedQuery(InstituicaoEnsino
							.INSTITUICAO_FIND_BY_ID, 
							InstituicaoEnsino.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Instituicao por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma InstituicaoEnsino pelo id
	 * 
	 * @param id
	 * @return se encontrado uma InstituicaoEnsino se não null
	 * @throws EntityNotFoundException
	 */
	public InstituicaoEnsino get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoEnsino> typedQuery = entityManager
					.createNamedQuery(InstituicaoEnsino
							.INSTITUICAO_FIND_BY_OBJECT,
							InstituicaoEnsino.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Instituicao por Objeto");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todas as Instituições Ensino persistidas no banco
	 * 
	 * @return um list de InstituicaoEnsino ou null
	 */
	public List<InstituicaoEnsino> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<InstituicaoEnsino> typedQuery = entityManager
					.createNamedQuery(InstituicaoEnsino
							.INSTITUICAO_GET_ALL, 
							InstituicaoEnsino.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Instituicoes");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma lista de InstituicaoEnsino
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<InstituicaoEnsino> list)
			throws InsertException, EntityNotFoundException {
		for (InstituicaoEnsino inst : list) {
			if (get(inst) == null) {
				add(inst);
			}
		}
	}

	/**
	 * Altera uma InstituicaoEnsino
	 * 
	 * @param entity
	 * @return a InstituicaoEnsino atualizada
	 */
	public InstituicaoEnsino update(InstituicaoEnsino entity) {
		String operacao = entity.getId() != 0 ? "UPDATE" : "INSERT";
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
			logdao.add(new LogAlteracaoBanco(operacao, 
					"TB_INSTITUICAO_ENSINO", entity.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			if(entityManager.getTransaction().isActive()){
				entityManager.getTransaction().rollback();
			}
			throw new DAOExcpetion("Erro ao modificar Instituicao");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		return entity;
	}

	public static InstituicaoEnsinoDAO getDao() {
		return dao;
	}

	public static void setDao(InstituicaoEnsinoDAO dao) {
		InstituicaoEnsinoDAO.dao = dao;
	}

}