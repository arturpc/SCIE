package br.gov.df.dftrans.scie.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.DocumentoPendencia;
import br.gov.df.dftrans.scie.domain.InstituicaoEnsino;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.dao.DocumentoPendenciaDAO;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.LogValidacaoCadastro;
import br.gov.df.dftrans.scie.domain.Representante;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class LogDAO extends DAO<LogValidacaoCadastro> implements Serializable{
	private static LogDAO dao = null;

	public static LogDAO LogDAO() {
		if (getDao() == null) {
			setDao(new LogDAO());
		}
		return getDao();

	}

	public static LogDAO getDao() {
		return dao;
	}

	public static void setDao(LogDAO dao) {
		LogDAO.dao = dao;
	}

	/**
	 * Persiste um LogValidacaoCadastro
	 */
	@Override
	public LogValidacaoCadastro add(LogValidacaoCadastro entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			if(entityManager.getTransaction().isActive()){
				entityManager.getTransaction().rollback();
			}
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao persistir Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", 
				"TB_LOG_VALIDACAO_CADASTRO", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um LogValidacaoCadastro pelo id
	 */
	@Override
	public LogValidacaoCadastro get(Object id) throws EntityNotFoundException {
		if (id.getClass() == LogValidacaoCadastro.class) {
			return getById(((LogValidacaoCadastro) id).getId());
		}
		return null;
	}

	/**
	 * Persist uma lista de LogValidacaoCadastro
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<LogValidacaoCadastro> list) 
			throws InsertException, EntityNotFoundException {
		for (LogValidacaoCadastro log : list) {
			if (get(log) == null) {
				add(log);
			}
		}
	}

	/**
	 * Seleciona um LogValidacaoCadastro pelo id
	 * 
	 * @param id
	 * @return um LogValidacaoCadastro se encontrar ou null
	 * @throws EntityNotFoundException
	 */
	public LogValidacaoCadastro getById(int id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_FIND_BY_ID, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Log por Código");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma list de LogValidacao em que esteja pendente
	 * 
	 * @return uma List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getOpens() throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_OPEN, 
							LogValidacaoCadastro.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs abertos");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma list de LogValidacaoCadastro em que se esteja pendente e
	 * seja da instituicao
	 * 
	 * @param instituicao
	 * @return Uma List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getOpensRep(Representante representante) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_OPEN_REP, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("rep", representante).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs abertos "
					+ "para o representante " + representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * \ Seleciona uma lista de LogValidacaoCadastro, onde a validação está em
	 * análise usando como parâmentro de pesquisa uma instituição
	 * 
	 * @param instituicao
	 * @return List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getAnalisysRep(Representante representante) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_ANALISYS_REP,
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("rep", representante).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs abertos "
					+ "para o representante " + representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, onde a validação está
	 * aprovado usando como parâmentro de pesquisa uma instituição
	 * 
	 * @param instituicao
	 * @return List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getAprovedRep(Representante representante) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_APROVED_REP, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("rep", representante).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs abertos "
					+ "para o representante " + representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, onde a validação está como
	 * não aprovado usando como parâmentro de pesquisa uma instituição
	 * 
	 * @param instituicao
	 * @return List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */

	public List<LogValidacaoCadastro> getRejectRep(Representante representante) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_REJECT_REP,
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("rep", representante).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs abertos "
					+ "para o Representante " + representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, onde a validação está como
	 * em análise usando como parâmentro de pesquisa uma instituição e um
	 * usuario e um documento
	 * 
	 * @param usuario
	 * @param instituicao
	 * @param documento
	 * @return um object LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */

	public LogValidacaoCadastro getAnalisys(Usuario usuario, int id,
			DocumentoPendencia documento) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
			Representante representante = repdao.get(id);
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ANALISYS, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("usuario", usuario)
					.setParameter("rep", representante)
					.setParameter("documento", documento).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs em "
					+ "analise pelo usuário " + usuario.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, onde a validação está como
	 * em análise usando como parâmentro de pesquisa um usuario
	 * 
	 * @param usuario
	 * @return um object LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public LogValidacaoCadastro getAnalisysUser(Usuario usuario) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_ANALISYS_USER, 
							LogValidacaoCadastro.class);
			List<LogValidacaoCadastro> list = typedQuery
					.setParameter("usuario", usuario).getResultList();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			} else {
				return null;
			}

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs em "
					+ "analise pelo usuário " + usuario.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, aprovados por instituição
	 * 
	 * @param instituicao
	 * @return List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getAproved(Representante representante)
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_APROVED, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("id", representante.getId()).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Logs aprovados "
					+ "do Representante " + representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona uma lista de LogValidacaoCadastro, rejeitados por instituição
	 * 
	 * @param instituicao
	 * @return List de LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public List<LogValidacaoCadastro> getRejected(Representante representante) 
			throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL_REJECTED, 
							LogValidacaoCadastro.class);
			return typedQuery.setParameter("id", representante.getId()).getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion(
					"Erro ao coletar Logs rejeitados do representante "
			+ representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Seleciona um um objeto filtrados pelo documento e instituicao
	 * 
	 * @param instituicao
	 * @param documento
	 * @return um object LogValidacaoCadastro ou null
	 * @throws EntityNotFoundException
	 */
	public LogValidacaoCadastro get(int id, DocumentoPendencia documento)
			throws EntityNotFoundException {
		RepresentanteDAO repdao = RepresentanteDAO.RepresentanteDAO();
		Representante representante = repdao.get(id);
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_FIND_BY_DADOS, 
							LogValidacaoCadastro.class);
			LogValidacaoCadastro log = typedQuery
					.setParameter("representante", representante)
					.setParameter("documento", documento)
					.getSingleResult();
			return log;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Log do Representante " 
			+ representante.getNome());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Autalizaa um LogValidacaoCadastro
	 * 
	 * @param entity
	 * @param validacao
	 * @return true ou false
	 */
	public boolean update(LogValidacaoCadastro entity, int validacao) {
		entity.setValidacao(validacao);
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
			throw new DAOExcpetion("Erro ao modificar Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("UPDATE", 
					"TB_LOG_VALIDACAO_CADASTRO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * seleciona todos os registros de LogValidacaoCadastro no banco
	 * 
	 * @return um list de LogValidacaoCadastro ou null
	 */
	public List<LogValidacaoCadastro> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<LogValidacaoCadastro> typedQuery = entityManager
					.createNamedQuery(LogValidacaoCadastro
							.LOG_GET_ALL, LogValidacaoCadastro.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar Log");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Verifica se um log existe
	 * 
	 * @param instituicao
	 * @param documento
	 * @return true ou false
	 */
	public boolean existsLog(Representante representante, DocumentoPendencia documento) {
		try {
			if (get(representante.getId(), documento) != null) {
				return true;
			}
		} catch (EntityNotFoundException e) {
			return false;
		}
		return false;
	}
}
