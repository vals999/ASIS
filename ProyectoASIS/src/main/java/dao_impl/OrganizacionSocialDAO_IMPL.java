package dao_impl;

import dao_interfaces.I_OrganizacionSocialDAO;
import jakarta.enterprise.context.RequestScoped;
import model.OrganizacionSocial;
@RequestScoped
public class OrganizacionSocialDAO_IMPL extends GenericDAO_IMPL<OrganizacionSocial, Long> implements I_OrganizacionSocialDAO  {

	public OrganizacionSocialDAO_IMPL() {
		super(OrganizacionSocial.class);
		// TODO Auto-generated constructor stub
	}

	

}