package dao_impl;

import dao_interfaces.I_PreguntaEncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.PreguntaEncuesta;
@RequestScoped
public class PreguntaEncuestaDAO_IMPL extends GenericDAO_IMPL<PreguntaEncuesta, Long> implements I_PreguntaEncuestaDAO{
	
	public PreguntaEncuestaDAO_IMPL() {
		super(PreguntaEncuesta.class);
	}
	
}
