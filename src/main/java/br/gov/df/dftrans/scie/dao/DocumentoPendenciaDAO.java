package br.gov.df.dftrans.scie.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.DocumentoPendencia;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class DocumentoPendenciaDAO extends DAO<DocumentoPendencia> implements Serializable {
	private static DocumentoPendenciaDAO dao = null;

	public static DocumentoPendenciaDAO DocumentoPendenciaDAO() {
		if (dao == null) {
			setDao(new DocumentoPendenciaDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona um DocumentoPendencia pela sua descrição
	 * 
	 * @param chave
	 * @return se encontrado o DocumentoPendencia se não null
	 */
	public DocumentoPendencia getByDesc(String chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<DocumentoPendencia> typedQuery = entityManager
					.createNamedQuery(DocumentoPendencia
							.DOCUMENTO_FIND_BY_DESC,
							DocumentoPendencia.class);
			return typedQuery.setParameter("descricao", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Documento por descricao");
		} finally {
			entityManager.close();
		}

	}

	/**
	 * Seleciona um DocumentoPendencia pelo numero
	 * 
	 * @param documento
	 * @return se encontrado o DocumentoPendencia se não null
	 */
	public DocumentoPendencia getByNro(int documento) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<DocumentoPendencia> typedQuery = entityManager
					.createNamedQuery(DocumentoPendencia
							.DOCUMENTO_FIND_BY_NUMBER, 
							DocumentoPendencia.class);
			return typedQuery.setParameter("documento", documento).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Documento por descricao");
		} finally {
			entityManager.close();
		}

	}

	/**
	 * persiste um DocumentoPendencia
	 */
	@Override
	public DocumentoPendencia add(DocumentoPendencia entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();

		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getDescricao() 
					+ getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT",
				"TB_DOCUMENTO_PENDENCIA", entity.getId()));
		return entity;
	}

	/**
	 * Persiste uma lista de DocumentoPendencia
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<DocumentoPendencia> list) 
			throws InsertException, EntityNotFoundException {
		for (DocumentoPendencia doc : list) {
			if (get(doc) == null) {
				add(doc);
			}
		}
	}

	/**
	 * Seleciona um documentoPendencia pelo id
	 */
	@Override
	public DocumentoPendencia get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<DocumentoPendencia> typedQuery = entityManager
					.createNamedQuery(DocumentoPendencia
							.DOCUMENTO_FIND_BY_ID, 
							DocumentoPendencia.class);
			return typedQuery.setParameter("id", ((DocumentoPendencia) id)
					.getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Documento por código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
	
	/**
	 * Seleciona um documentoPendencia pelo id
	 */
	public DocumentoPendencia get(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<DocumentoPendencia> typedQuery = entityManager
					.createNamedQuery(DocumentoPendencia
							.DOCUMENTO_FIND_BY_ID,
							DocumentoPendencia.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Documento por código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de DocumentoPendencia
	 * 
	 * @return uma lista de DocumentoPendencia ou null
	 */
	public List<DocumentoPendencia> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<DocumentoPendencia> typedQuery = entityManager
					.createNamedQuery(DocumentoPendencia
							.DOCUMENTO_GET_ALL, 
							DocumentoPendencia.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Tipos de Documentos Pendentes");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	public static DocumentoPendenciaDAO getDao() {
		return dao;
	}

	public static void setDao(DocumentoPendenciaDAO dao) {
		DocumentoPendenciaDAO.dao = dao;
	}

}