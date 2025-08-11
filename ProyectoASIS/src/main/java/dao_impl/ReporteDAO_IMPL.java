package dao_impl;

import dao_interfaces.I_ReporteDAO;
import jakarta.enterprise.context.RequestScoped;
import model.Reporte;
@RequestScoped
public class ReporteDAO_IMPL extends GenericDAO_IMPL<Reporte, Long> implements I_ReporteDAO  {

	public ReporteDAO_IMPL() {
		super(Reporte.class);
		// TODO Auto-generated constructor stub
	}

	

}
