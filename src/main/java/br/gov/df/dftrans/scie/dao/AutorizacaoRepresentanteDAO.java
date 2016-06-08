package br.gov.df.dftrans.scie.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import static br.gov.df.dftrans.scie.utils.MessageUtils.ALREADY_EXISTS_EXCEPTION_KEY;
import static br.gov.df.dftrans.scie.utils.MessageUtils.getString;
import java.io.Serializable;
import java.util.List;
import br.gov.df.dftrans.scie.domain.AutorizacaoRepresentante;
import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.dao.InstituicaoEnsinoDAO;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class AutorizacaoRepresentanteDAO extends DAO<AutorizacaoRepresentante> implements Serializable {
	private static AutorizacaoRepresentanteDAO dao = null;

	public static AutorizacaoRepresentanteDAO AutorizacaoRepresentanteDAO() {
		if (dao == null) {
			setDao(new AutorizacaoRepresentanteDAO());
		}
		return getDao();
	}

	public static AutorizacaoRepresentanteDAO getDao() {
		return dao;
	}

	public static void setDao(AutorizacaoRepresentanteDAO dao) {
		AutorizacaoRepresentanteDAO.dao = dao;
	}

	/**
	 * Persiste Objeto Autorização representante
	 */
	@Override
	public AutorizacaoRepresentante add(AutorizacaoRepresentante entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();

		} catch (EntityExistsException e) {
			entityManager.getTransaction().rollback();
			throw new InsertException(entity.getCpf() + getString(ALREADY_EXISTS_EXCEPTION_KEY));
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
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_AUTORIZACAO_REPRESENTANTE", entity.getId()));
		return entity;
	}

	/**
	 * Seleciona um objeto autorizaçãoRepresentante de acordo com o id informado
	 */
	@Override
	public AutorizacaoRepresentante get(Object id) throws EntityNotFoundException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager
					.createNamedQuery(AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_ID, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("id", ((AutorizacaoRepresentante) id).getId()).getSingleResult();
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
	 * Seleciona um objeto autorizaçãoRepresentante de acordo com o cpf
	 * informado e o id da instituição
	 */
	public AutorizacaoRepresentante getByCpf(String cpf, int id_instituicao) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager
					.createNamedQuery(AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("cpf", cpf).setParameter("id_instituicao", id_instituicao).getSingleResult();
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
	 * Seleciona um objeto autorizaçãoRepresentante pelo cpf informado
	 * 
	 * @param cpf
	 * @return Se a autorização existir a autorização caso não null
	 */
	public AutorizacaoRepresentante getByCpf(String cpf) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager.createNamedQuery(
					AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF_ONLY, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("cpf", cpf).getResultList().get(0);
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
	 * Seleciona um objeto autorizaçãoRepresentante pelo cpf informado sem instituicao associada
	 * 
	 * @param cpf
	 * @return Se a autorização existir a autorização caso não null
	 */
	public AutorizacaoRepresentante getByCpfInstNull(String cpf) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager.createNamedQuery(
					AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF_INST_NULL, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("cpf", cpf).getResultList().get(0);
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
	 * Autaliza uma autorizacao
	 * 
	 * @param entity
	 * @return a autorizacao atualizada
	 */
	public AutorizacaoRepresentante update(AutorizacaoRepresentante entity) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao atualizar AutorizacaoRepresentante cpf = "+ entity.getCpf());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco("UPDATE", "TB_AUTORIZACAO_REPRESENTANTE", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * Seleciona todos os registros de autorizaçãoRepresentante persistidos no
	 * banco
	 * 
	 * @return um list de AutorizaçãoRepresentante
	 */
	public List<AutorizacaoRepresentante> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager
					.createNamedQuery(AutorizacaoRepresentante.AUTORIZACAO_GET_ALL, AutorizacaoRepresentante.class);
			return typedQuery.getResultList();
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
	 * persiste uma lista de AutorizaçaoRepresentante
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<AutorizacaoRepresentante> list) throws InsertException, EntityNotFoundException {
		for (AutorizacaoRepresentante aut : list) {
			if (get(aut) == null) {
				add(aut);
			}
		}
	}

}