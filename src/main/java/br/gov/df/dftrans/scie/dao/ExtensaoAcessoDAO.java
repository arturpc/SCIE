package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.ExtensaoAcesso;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class ExtensaoAcessoDAO extends DAO<ExtensaoAcesso> {
	private static ExtensaoAcessoDAO dao = null;

	public static ExtensaoAcessoDAO ExtensaoAcessoDAO() {
		if (getDao() == null) {
			setDao(new ExtensaoAcessoDAO());
		}
		return getDao();

	}

	public static ExtensaoAcessoDAO getDao() {
		return dao;
	}

	public static void setDao(ExtensaoAcessoDAO dao) {
		ExtensaoAcessoDAO.dao = dao;
	}

	/**
	 * Persiste uma ExtensaoAcesso
	 */
	@Override
	public ExtensaoAcesso add(ExtensaoAcesso entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			if (entityManager.isOpen()){
				entityManager.getTransaction().rollback();
			}
			throw new DAOExcpetion("Erro ao persistir Extensao de Acesso");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_EXTENSAO_ACESSO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona uma ExtensaoAcesso pelo id
	 */
	@Override
	public ExtensaoAcesso get(Object id) throws EntityNotFoundException {
		if (id.getClass() == ExtensaoAcesso.class) {
			return getById(((ExtensaoAcesso) id).getId());
		}
		return null;
	}

	/**
	 * Adiciona uma lista de ExtensaoAcesso
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<ExtensaoAcesso> list) throws InsertException, EntityNotFoundException {
		for (ExtensaoAcesso ext : list) {
			if (get(ext) == null) {
				add(ext);
			}
		}
	}

	/**
	 * Seleciona uma ExtensaoAcesso pelo id
	 * 
	 * @param id
	 * @return se encontrar ExtensaoAcesso encontrada se não null
	 * @throws EntityNotFoundException
	 */
	public ExtensaoAcesso getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_FIND_BY_ID,
							ExtensaoAcesso.class);
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

	/**
	 * Seleciona todas as Extensões acesso que foram solicitadas
	 * 
	 * @return se encontrar a ExtensaoAcesso encontrada se não null
	 * @throws EntityNotFoundException
	 */
	public List<ExtensaoAcesso> getSolicitado() throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_GET_SOLICITADO, 
							ExtensaoAcesso.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações "
					+ "de Extensão de Acesso abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de ExtensaoAcesso pelo usuario se tiver status como 1
	 * (analise)
	 * 
	 * @param usuario
	 * @return uma Lista de ExtensaoAcesso se encontrado ou null
	 * @throws EntityNotFoundException
	 */
	public List<ExtensaoAcesso> getAnalise(Usuario usuario) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_GET_ANALISYS,
							ExtensaoAcesso.class);
			return typedQuery.setParameter("usuario", usuario).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações de "
					+ "Extensão de Acessos abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de ExtensaoAcesso aprovados
	 * 
	 * @param cpf
	 * @return uma Lista de ExtensaoAcesso se encontrado ou null
	 * @throws EntityNotFoundException
	 */
	public List<ExtensaoAcesso> getAprovado(String cpf) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_GET_APROVED,
							ExtensaoAcesso.class);
			return typedQuery.setParameter("cpf", cpf).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações de "
					+ "Extensão de Acessos aprovadas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de ExtensaoAcesso dos rejeitados
	 * 
	 * @param cpf
	 * @return uma Lista de ExtensaoAcesso se encontrado ou null
	 * @throws EntityNotFoundException
	 */
	public List<ExtensaoAcesso> getRejeitado(String cpf) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_GET_REJECTED, 
							ExtensaoAcesso.class);
			return typedQuery.setParameter("cpf", cpf).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações "
					+ "Extensão de Acessos rejeitadas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Atualiza uma extensaoAcesso
	 * 
	 * @param entity
	 * @param status
	 * @return a ExtensaoAcesso atualizada
	 */
	public ExtensaoAcesso update(ExtensaoAcesso entity, int status) {
		entity.setStatus(status);
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar Status da "
					+ "Solicitação de Extensão de Acessos");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("UPDATE", 
					"TB_EXTENSAO_ACESSO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * Seleciona todas as Extensões Acesso persistidas
	 * 
	 * @return uma lista de ExtensaoAcesso ou null
	 */
	public List<ExtensaoAcesso> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<ExtensaoAcesso> typedQuery = entityManager
					.createNamedQuery(ExtensaoAcesso
							.EXTENSAO_ACESSO_GET_ALL, 
							ExtensaoAcesso.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações de "
					+ "Extensão de Acesso!");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
}
