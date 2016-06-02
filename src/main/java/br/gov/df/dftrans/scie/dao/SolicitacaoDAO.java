package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Solicitacao;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class SolicitacaoDAO extends DAO<Solicitacao> {
	private static SolicitacaoDAO dao = null;

	public static SolicitacaoDAO SolicitacaoDAO() {
		if (getDao() == null) {
			setDao(new SolicitacaoDAO());
		}
		return getDao();

	}

	public static SolicitacaoDAO getDao() {
		return dao;
	}

	public static void setDao(SolicitacaoDAO dao) {
		SolicitacaoDAO.dao = dao;
	}

	/**
	 * Persiste um solicitação
	 */
	@Override
	public Solicitacao add(Solicitacao entity) throws InsertException {
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
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_SOLICITACAO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona uma solicitação usando como parâmetro de busca um id
	 */
	@Override
	public Solicitacao get(Object id) throws EntityNotFoundException {
		if (id.getClass() == Solicitacao.class) {
			return getById(((Solicitacao) id).getId());
		}
		return null;
	}

	/**
	 * Persiste uma lista de Solicitacao
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Solicitacao> list) throws InsertException, EntityNotFoundException {
		for (Solicitacao sol : list) {
			if (get(sol) == null) {
				add(sol);
			}
		}
	}

	/**
	 * Seleciona uma solicitaçao usando como parâmetro de busca um id
	 * 
	 * @param id
	 * @return a solicitação encontrada
	 * @throws EntityNotFoundException
	 */
	public Solicitacao getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_FIND_BY_ID,
					Solicitacao.class);
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
	 * seleciona uma lista de Solicitacao onde o status está como solicitado
	 * 
	 * @return uma list de Solicitacao ou null
	 * @throws EntityNotFoundException
	 */
	public List<Solicitacao> getSolicitado() throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_GET_SOLICITADO,
					Solicitacao.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * 
	 * @param usuario
	 * @return Seleciona uma list de Solicitacao onde o status está como em
	 *         análise usando como parâmetro de pesquisa um usuário
	 * @throws EntityNotFoundException
	 */
	public List<Solicitacao> getAnalise(Usuario usuario) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_GET_ANALISYS,
					Solicitacao.class);
			return typedQuery.setParameter("usuario", usuario).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um list de Solicitacao onde o status esteja como aprovado,
	 * usando como parâmetro de pesquisa um CPF
	 * 
	 * @param cpf
	 * @return um list de Solicitacao ou null
	 * @throws EntityNotFoundException
	 */
	public List<Solicitacao> getAprovado(String cpf) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_GET_APROVED,
					Solicitacao.class);
			return typedQuery.setParameter("cpf", cpf).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * 
	 * @param cpf
	 * @return Seleciona um list de Solicitacao onde o status está como
	 *         rejeitado, utilizando como parâmetro de pesquisa um CPF
	 * @throws EntityNotFoundException
	 */
	public List<Solicitacao> getRejeitado(String cpf) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_GET_REJECTED,
					Solicitacao.class);
			return typedQuery.setParameter("cpf", cpf).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações abertas");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Autaliza uma solicitacao
	 * 
	 * @param entity
	 * @param status
	 * @return a solicitacao atualizada
	 */
	public Solicitacao update(Solicitacao entity, int status) {
		entity.setStatus(status);
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			;
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar Status da Solicitação");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("UPDATE", "TB_SOLICITACAO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * Seleciona todas as Solicitacoes persesitidas na base
	 * 
	 * @return um List de Solicitacao ou null
	 */
	public List<Solicitacao> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Solicitacao> typedQuery = entityManager.createNamedQuery(Solicitacao.SOLICITACAO_GET_ALL,
					Solicitacao.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Solicitações!");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}
}
