package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.Endereco;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class EnderecoDAO extends DAO<Endereco> {

	private static EnderecoDAO dao = null;

	public static EnderecoDAO enderecoDAO() {
		if (dao == null) {
			setDao(new EnderecoDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona um Endereco pelo cep
	 * 
	 * @param chave
	 * @return se encontrar o Endereco se não null
	 */
	public Endereco getByCEP(String chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Endereco> typedQuery = entityManager.createNamedQuery(Endereco.ENDERECO_FIND_BY_CEP,
					Endereco.class);
			return typedQuery.setParameter("cep", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Endereco por CEP");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}

	}

	/**
	 * Seleciona um Endereco pelo id
	 * 
	 * @param chave
	 * @return se encontrar o Endereco se não null
	 */
	public Endereco getByCodigo(int chave) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Endereco> typedQuery = entityManager.createNamedQuery(Endereco.ENDERECO_FIND_BY_ID,
					Endereco.class);
			return typedQuery.setParameter("id", chave).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Endereco por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}

	}

	/**
	 * Persiste um Endereco
	 */
	@Override
	public Endereco add(Endereco entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getCep() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
		} catch (Exception e) {
			throw new InsertException();
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_ENDERECO", entity.getId()));
		return entity;
	}

	/**
	 * Persiste uma lista de Endereco
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Endereco> list) throws InsertException, EntityNotFoundException {
		for (Endereco end : list) {
			if (get(end) == null) {
				add(end);
			}
		}
	}

	/**
	 * Seleciona um Endereco pelo id
	 */
	@Override
	public Endereco get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Endereco> typedQuery = entityManager.createNamedQuery(Endereco.ENDERECO_FIND_BY_ID,
					Endereco.class);
			return typedQuery.setParameter("id", ((Endereco) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Instituicao por Inep/E-mec");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona todos os Enderecos persistidos no banco
	 * 
	 * @return lista de Endereco ou null
	 */
	public List<Endereco> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Endereco> typedQuery = entityManager.createNamedQuery(Endereco.ENDERECO_GET_ALL, Endereco.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Enderecos");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Atualiza um endereco
	 * 
	 * @param entity
	 * @return o Endereco atualizado
	 */
	public Endereco update(Endereco entity) {
		EntityManager entityManager = factory.createEntityManager();
		String operacao = entity.getId() != 0 ? "UPDATE" : "INSERT";
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			throw new DAOExcpetion("Erro ao modificar Endereco");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco(operacao, "TB_ENDERECO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static EnderecoDAO getDao() {
		return dao;
	}

	public static void setDao(EnderecoDAO dao) {
		EnderecoDAO.dao = dao;
	}

}
