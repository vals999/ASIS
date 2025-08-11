package dao_impl;

import dao_interfaces.I_RespuestaEncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.RespuestaEncuesta;
@RequestScoped
public class RespuestaEncuestaDAO_IMPL extends GenericDAO_IMPL<RespuestaEncuesta, Long> implements I_RespuestaEncuestaDAO{
	
	public RespuestaEncuestaDAO_IMPL() {
		super(RespuestaEncuesta.class);
	}
	
}
