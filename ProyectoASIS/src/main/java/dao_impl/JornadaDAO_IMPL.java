package dao_impl;

import dao_interfaces.I_JornadaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Jornada;
@RequestScoped
public class JornadaDAO_IMPL extends GenericDAO_IMPL<Jornada, Long> implements I_JornadaDAO  {

	public JornadaDAO_IMPL() {
		super(Jornada.class);
		// TODO Auto-generated constructor stub
	}

	

}
