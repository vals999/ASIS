package dao_impl;

import dao_interfaces.I_EncuestadorDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Encuestador;
@RequestScoped
public class EncuestadorDAO_IMPL extends GenericDAO_IMPL<Encuestador, Long> implements I_EncuestadorDAO  {

	public EncuestadorDAO_IMPL() {
		super(Encuestador.class);
		// TODO Auto-generated constructor stub
	}

	

}
