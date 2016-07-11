package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;

import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;

import br.gov.df.dftrans.scie.domain.Cidade;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class CidadeDAO extends DAO<Cidade> implements Serializable {
	private static CidadeDAO dao = null;

	public static CidadeDAO CidadeDAO() {
		if (dao == null) {
			setDao(new CidadeDAO());
		}
		return getDao();
	}

	/**
	 * Seleciona uma cidade pelo nome
	 * 
	 * @param chave
	 * @return cidade se entrada ou null
	 */
	public Cidade getByNome(String chave) {
		EntityManager entityManager = factory.createEntityManager();
        try {
			TypedQuery<Cidade> typedQuery = entityManager
					.createNamedQuery(Cidade
							.CIDADE_FIND_BY_NOME, 
							Cidade.class);
			return typedQuery.setParameter("nome", chave).getSingleResult();
        } catch (NoResultException e) {
			return null;
        } catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por sigla");
        } finally {
			entityManager.close();
        }

	}

	/**
	 * persiste uma cidade
	 */
	public Cidade add(Cidade entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
        try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getUf() 
					+ getString(ALREADY_EXISTS_EXCEPTION_KEY));
        } catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new InsertException();
        } finally {
			if (entityManager.isOpen()){
				entityManager.close();
			}
        }
        logdao.add(new LogAlteracaoBanco("INSERT", "TB_CIDADE", entity.getId()));
		return entity;
	}

	/**
	 * Persiste uma lista de cidades
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Cidade> list) throws InsertException, EntityNotFoundException {
		for (Cidade cid : list) {
			if (get(cid) == null) {
				add(cid);
			}
		}
	}

	/**
	 * seleciona uma cidade pelo id
	 */
	public Cidade get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
        try {
			TypedQuery<Cidade> typedQuery = entityManager
					.createNamedQuery(Cidade
							.CIDADE_FIND_BY_ID, 
							Cidade.class);
			return typedQuery.setParameter("id", 
					((Cidade) id).getId()).getSingleResult();
        } catch (NoResultException e) {
			return null;
        } catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por código");
        } finally {
			entityManager.close();
        }
	}

	/**
	 * Seleciona todos as cidades persistidas no banco
	 * 
	 * @return lista de cidades
	 */
	public List<Cidade> get() {
		EntityManager entityManager = factory.createEntityManager();
        try {
			TypedQuery<Cidade> typedQuery = entityManager
					.createNamedQuery(Cidade
							.CIDADE_GET_ALL, 
							Cidade.class);
			return typedQuery.getResultList();
        } catch (NoResultException e) {
			return null;
        } catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por código");
        } finally {
			entityManager.close();
        }
	}
	
	/**
	 * Seleciona todos as cidades persistidas no banco
	 * 
	 * @return lista de cidades
	 */
	public Cidade get(String nome, String uf) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Cidade> typedQuery = entityManager
					.createNamedQuery(Cidade
							.CIDADE_FIND_BY_NOME_UF, 
							Cidade.class);
			List<Cidade> cid = typedQuery.setParameter("nome", nome)
					.setParameter("uf", uf).getResultList();
			if(cid.isEmpty()){
				return null;
			}
			return cid.get(0);
        } catch (NoResultException e) {
			return null;
        } catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UF por código");
        } finally {
			entityManager.close();
        }
	}

	/**
	 * Atualiza uma cidade
	 * 
	 * @param entity
	 * @return
	 */
	public boolean update(Cidade entity) {
        EntityManager entityManager = factory.createEntityManager();
        boolean flag = false;
        try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
			flag = true;
        } catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar Cidade");
        } finally {
			entityManager.close();
        }
        try {
			logdao.add(new LogAlteracaoBanco("UPDATE", "TB_CIDADE", entity.getId()));
        } catch (InsertException e) {
			e.printStackTrace();
        }
        return flag;

	}

	public static CidadeDAO getDao() {
		return dao;
	}

	public static void setDao(CidadeDAO dao) {
		CidadeDAO.dao = dao;
	}

}
