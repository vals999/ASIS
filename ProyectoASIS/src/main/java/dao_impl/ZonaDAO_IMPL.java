package dao_impl;

import dao_interfaces.I_ZonaDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Zona;
@RequestScoped
public class ZonaDAO_IMPL extends GenericDAO_IMPL<Zona, Long> implements I_ZonaDAO  {

	public ZonaDAO_IMPL() {
		super(Zona.class);
		// TODO Auto-generated constructor stub
	}


}
