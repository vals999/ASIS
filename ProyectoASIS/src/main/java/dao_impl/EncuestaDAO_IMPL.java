package dao_impl;

import dao_interfaces.I_EncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Encuesta;
@RequestScoped
public class EncuestaDAO_IMPL extends GenericDAO_IMPL<Encuesta, Long> implements I_EncuestaDAO{

	public EncuestaDAO_IMPL() {
		super(Encuesta.class);
		// TODO Auto-generated constructor stub
	}
	
}
