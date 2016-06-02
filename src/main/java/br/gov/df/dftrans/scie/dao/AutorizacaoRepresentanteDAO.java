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
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

public class AutorizacaoRepresentanteDAO extends DAO<AutorizacaoRepresentante> implements Serializable {
	private static AutorizacaoRepresentanteDAO dao = null;

	public static AutorizacaoRepresentanteDAO autorizacaoRepresentanteDAO() {
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
			e.printStackTrace();
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
		final EntityManager entityManager = factory.createEntityManager();
		try {
			final TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager
					.createNamedQuery(AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_ID, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("id", ((AutorizacaoRepresentante) id).getId()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
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
	public AutorizacaoRepresentante getByCpf(String cpf, int instituicao) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<AutorizacaoRepresentante> typedQuery = entityManager
					.createNamedQuery(AutorizacaoRepresentante.AUTORIZACAO_FIND_BY_CPF, AutorizacaoRepresentante.class);
			return typedQuery.setParameter("cpf", cpf).setParameter("id_instituicao", instituicao).getSingleResult();
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
		for (AutorizacaoRepresentante autRep : list) {
			if (get(autRep) == null) {
				add(autRep);
			}
		}
	}

}