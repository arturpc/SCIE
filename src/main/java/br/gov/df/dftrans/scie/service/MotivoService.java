<<<<<<< HEAD
package br.gov.df.dftrans.scie.service;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.df.dftrans.scie.dao.MotivoDAO;
import br.gov.df.dftrans.scie.domain.Motivo;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;

@ManagedBean(name = "MotivoService", eager = true)
@SessionScoped
public class MotivoService {

	private List<Motivo> motivos;

	public MotivoService() {
		refresh();
	}

	/**
	 * Inicia motivos com todos os dados da base
	 */
	public void refresh() {
		motivos = new ArrayList<Motivo>();
		MotivoDAO dao = MotivoDAO.MotivoDAO();
		try {
			motivos.addAll(dao.getAll());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * pesquisa um motivo por nome
	 * 
	 * @param nome
	 * @return se encontrar o Motivo se não null
	 */
	public Motivo getMotivoPorNome(String nome) {
		for (Motivo mot : getMotivos()) {
			if (mot.getMotivo().equals(nome)) {
				return mot;
			}
		}
		return null;
	}

	// getteres and setteres
	public List<Motivo> getMotivos() {
		return motivos;
	}

	public void setMotivos(List<Motivo> motivos) {
		this.motivos = motivos;
	}

}
=======
package br.gov.df.dftrans.scie.service;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.df.dftrans.scie.dao.MotivoDAO;
import br.gov.df.dftrans.scie.domain.Motivo;
import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;

@ManagedBean(name = "MotivoService", eager = true)
@SessionScoped
public class MotivoService {

	private List<Motivo> motivos;

	public MotivoService() {
		refresh();
	}

	/**
	 * Inicia motivos com todos os dados da base
	 */
	public void refresh() {
		motivos = new ArrayList<Motivo>();
		MotivoDAO dao = MotivoDAO.motivoDAO();
		try {
			motivos.addAll(dao.getAll());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * pesquisa um motivo por nome
	 * 
	 * @param nome
	 * @return se encontrar o Motivo se não null
	 */
	public Motivo getMotivoPorNome(String nome) {
		for (Motivo mot : getMotivos()) {
			if (mot.getMotivo().equals(nome)) {
				return mot;
			}
		}
		return null;
	}

	// getteres and setteres
	public List<Motivo> getMotivos() {
		return motivos;
	}

	public void setMotivos(List<Motivo> motivos) {
		this.motivos = motivos;
	}

}
>>>>>>> origin/master
