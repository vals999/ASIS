package dao_impl;

import dao_interfaces.I_PersonaEncuestadaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.PersonaEncuestada;
@RequestScoped
public class PersonaEncuestadaDAO_IMPL extends GenericDAO_IMPL<PersonaEncuestada, Long> implements I_PersonaEncuestadaDAO{

	public PersonaEncuestadaDAO_IMPL() {
		super(PersonaEncuestada.class);
		// TODO Auto-generated constructor stub
	}
	
}
