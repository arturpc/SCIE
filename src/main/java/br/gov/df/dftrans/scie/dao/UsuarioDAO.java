package br.gov.df.dftrans.scie.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.df.dftrans.scie.domain.LogAlteracaoBanco;
import br.gov.df.dftrans.scie.domain.Usuario;
import br.gov.df.dftrans.scie.exceptions.DAOExcpetion;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;
import br.gov.df.dftrans.scie.utils.MD5;
import br.gov.df.dftrans.scie.utils.StringUtils;

public class UsuarioDAO extends DAO<Usuario> {
	private static UsuarioDAO dao = null;

	public static UsuarioDAO UsuarioDAO() {
		if (getDao() == null) {
			setDao(new UsuarioDAO());
		}
		return getDao();

	}

	public static UsuarioDAO getDao() {
		return dao;
	}

	public static void setDao(UsuarioDAO dao) {
		UsuarioDAO.dao = dao;
	}

	/**
	 * Persiste um usuario
	 */
	@Override
	public Usuario add(Usuario entity) throws InsertException {
		EntityManager entityManager = factory.createEntityManager();
		try {
			StringUtils.parserObject(entity);
			entity.setSenha(MD5.md5(entity.getSenha()));
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw new DAOExcpetion("Erro ao persistir Usuário");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		logdao.add(new LogAlteracaoBanco("INSERT", "TB_USUARIO", entity.getId()));
		return entity;
	}

	@Override
	public Usuario get(Object id) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Seleciona um usuário utilizando como parâmetro de pesquisa o seu login e
	 * sua senha
	 * 
	 * @param login
	 * @param senha
	 * @return o usuário se encontrado ou null
	 */
	@SuppressWarnings("finally") // ignora erros no último bloco
	public Usuario getByAutenticacao(String login, String senha) {
		EntityManager entityManager = factory.createEntityManager();
		Usuario user = null;
		try {
			user = entityManager.createNamedQuery(Usuario.USUARIO_FIND_BY_AUTENTICACAO, Usuario.class)
					.setParameter("login", login).setParameter("senha", MD5.md5(senha)).getSingleResult();
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new DAOExcpetion("Erro na autenticaçao do usuario");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
			return user;
		}
	}

	/**
	 * Seleciona um Usuário utilizando como parâmetro de pesquisa um email
	 * 
	 * @param email
	 * @return se encontrar o Usuário se não null
	 */
	@SuppressWarnings("finally") // ignora erros no último bloco
	public Usuario getByEmail(String email) {
		EntityManager entityManager = factory.createEntityManager();
		Usuario user = null;
		try {
			user = entityManager.createNamedQuery(Usuario.USUARIO_FIND_BY_EMAIL, Usuario.class)
					.setParameter("email", email).getSingleResult();
		} catch (Exception e) {
			throw new DAOExcpetion("Erro ao buscar usuário por Email");
		} finally {
			return user;
		}
	}

	/**
	 * Seleciona um usuário pelo seu login
	 * 
	 * @param login
	 * @return se encontrar o Usuário se não null
	 */
	@SuppressWarnings("finally")
	public Usuario getByLogin(String login) {
		EntityManager entityManager = factory.createEntityManager();
		Usuario user = null;
		try {
			user = entityManager.createNamedQuery(Usuario.USUARIO_FIND_BY_LOGIN, Usuario.class)
					.setParameter("login", login).getSingleResult();
		} catch (Exception e) {
			throw new DAOExcpetion("Erro ao buscar usuário por Email");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
			return user;
		}
	}

	/**
	 * Persiste uma list de Usuario
	 * 
	 * @param list
	 * @throws InsertException
	 * @throws EntityNotFoundException
	 */
	public void add(List<Usuario> list) throws InsertException, EntityNotFoundException {
		for (Usuario usr : list) {
			if (get(usr) == null) {
				add(usr);
			}
		}
	}

	/**
	 * Seleciona todos os Usuários persestidos na base
	 * 
	 * @retur uma list de Usuario ou null
	 */
	public List<Usuario> get() {
		EntityManager entityManager = factory.createEntityManager();
		try {
			TypedQuery<Usuario> typedQuery = entityManager.createNamedQuery(Usuario.USUARIO_GET_ALL, Usuario.class);
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao coletar UFs");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
	}

	/**
	 * Atualiza um Usuario
	 * 
	 * @param entity
	 * @return o usuário atualizado
	 */
	public boolean update(Usuario entity) {
		EntityManager entityManager = factory.createEntityManager();
		entity.setSenha(MD5.md5(entity.getSenha()));
		String operacao = entity.getId() != 0 ? "UPDATE" : "INSERT";
		boolean flag = false;
		try {
			entityManager.getTransaction().begin();
			entity = entityManager.merge(entity);
			entityManager.getTransaction().commit();
			flag = true;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw new DAOExcpetion("Erro ao modificar Usuario");
		} finally {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		}
		try {
			logdao.add(new LogAlteracaoBanco(operacao, "TB_USUARIO", entity.getId()));
		} catch (InsertException e) {
			e.printStackTrace();
		}
		return flag;
	}

}
