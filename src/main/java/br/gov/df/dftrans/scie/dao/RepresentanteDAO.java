package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.List;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class RepresentanteDAO extends DAO<Representante> {
	private static RepresentanteDAO dao = null;

	public static RepresentanteDAO representanteDAO() {
		if (dao == null) {
			setDao(new RepresentanteDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona um Representando por CPF
	 * 
	 * @param chave
	 * @return um Representante ou null
	 */
	public Representante getByCPF(String chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Representante> typedQuery = entityManager
					.createNamedQuery(Representante.REPRESENTANTE_FIND_BY_CPF, Representante.class);
			return typedQuery.setParameter("cpf", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Autorizacao por CPF");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}

	}

	/**
	 * persiste um Representante
	 */
	@Override
	public Representante add(Representante entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getNome() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_REPRESENTANTE_INSTITUICAO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um Representante usando como parâmetro de pesquisa um id
	 */
	public Representante get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Representante> typedQuery = entityManager
					.createNamedQuery(Representante.REPRESENTANTE_FIND_BY_ID, Representante.class);
			return typedQuery.setParameter("id", ((Representante) id).getId()).getSingleResult();
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
	 * Seleciona todos os Representantes persestidos no banco
	 * 
	 * @return List de Representante ou null
	 */
	public List<Representante> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Representante> typedQuery = entityManager.createNamedQuery(Representante.REPRESENTANTE_GET_ALL,
					Representante.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Representante por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Persiste uma lista de Representante
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Representante> list) throws InsertException, EntityNotFoundException {
		for (Representante rep : list) {
			if (get(rep) == null) {
				add(rep);
			}
		}
	}

	/**
	 * Altera um Representante existente na base
	 * 
	 * @param entity
	 * @return o Representante alterado
	 */
	public Representante update(Representante entity) {
		EntityManager entityManager = factory.createEntityManager();
		String operacao = entity.getId() != 0 ? "UPDATE" : "INSERT";
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar Representante");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco(operacao, "TB_REPRESENTANTE_INSTITUICAO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;

	}

	public static RepresentanteDAO getDao() {
		return dao;
	}

	public static void setDao(RepresentanteDAO dao) {
		RepresentanteDAO.dao = dao;
	}

}