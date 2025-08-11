package dao_impl;

import dao_interfaces.I_CampañaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Campaña;
@RequestScoped
public class CampañaDAO_IMPL extends GenericDAO_IMPL<Campaña, Long> implements I_CampañaDAO  {

	public CampañaDAO_IMPL() {
		super(Campaña.class);
		// TODO Auto-generated constructor stub
	}

	

}
