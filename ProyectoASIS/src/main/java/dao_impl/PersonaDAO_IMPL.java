package dao_impl;

import dao_interfaces.I_PersonaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.DatosPersonales;

@RequestScoped
public class PersonaDAO_IMPL extends GenericDAO_IMPL<DatosPersonales, Long> implements I_PersonaDAO{
	
	public PersonaDAO_IMPL() {
		super(DatosPersonales.class);
		// TODO Auto-generated constructor stub
	}
	
}
