package dao_factory;

import dao_impl.BarrioDAO_IMPL;
import dao_impl.CampañaDAO_IMPL;
import dao_impl.EncuestaDAO_IMPL;
import dao_impl.EncuestadorDAO_IMPL;
import dao_impl.JornadaDAO_IMPL;
import dao_impl.OrganizacionSocialDAO_IMPL;
import dao_impl.PersonaDAO_IMPL;
import dao_impl.PersonaEncuestadaDAO_IMPL;
import dao_impl.PreguntaEncuestaDAO_IMPL;
import dao_impl.ReporteDAO_IMPL;
import dao_impl.RespuestaEncuestaDAO_IMPL;
import dao_impl.UsuarioDAO_IMPL;
import dao_impl.ZonaDAO_IMPL;
import dao_interfaces.I_BarrioDAO;
import dao_interfaces.I_CampañaDAO;
import dao_interfaces.I_EncuestaDAO;
import dao_interfaces.I_EncuestadorDAO;
import dao_interfaces.I_JornadaDAO;
import dao_interfaces.I_OrganizacionSocialDAO;
import dao_interfaces.I_PersonaDAO;
import dao_interfaces.I_PersonaEncuestadaDAO;
import dao_interfaces.I_PreguntaEncuestaDAO;
import dao_interfaces.I_ReporteDAO;
import dao_interfaces.I_RespuestaEncuestaDAO;
import dao_interfaces.I_UsuarioDAO;
import dao_interfaces.I_ZonaDAO;

public class FactoryDAO {
	public static I_UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAO_IMPL();
    }
	
	public static I_PersonaDAO getPersonaDAO() {
        return new PersonaDAO_IMPL();
    }
	
	public static I_BarrioDAO getBarrioDAO() {
		return new BarrioDAO_IMPL();
	}
	
	public static I_EncuestaDAO getEncuestaDAO() {
		return new EncuestaDAO_IMPL();
	}
	
	public static I_PreguntaEncuestaDAO getPreguntaEncuestaDAO() {
		return new PreguntaEncuestaDAO_IMPL();
	}
	
	public static I_RespuestaEncuestaDAO getRespuestaEncuestaDAO() {
		return new RespuestaEncuestaDAO_IMPL();
	}
	
	public static I_PersonaEncuestadaDAO getPersonaEncuestadaDAO() {
		return new PersonaEncuestadaDAO_IMPL();
	}
	
	public static I_CampañaDAO getCampañaDAO () {
		return new CampañaDAO_IMPL(); 
	}
	
	public static I_ZonaDAO getZonaDAO () {
		return new ZonaDAO_IMPL(); 
	}
	
	public static I_ReporteDAO getReporteDAO () {
		return new ReporteDAO_IMPL(); 
	}
	
	public static I_JornadaDAO getJornadaDAO () {
		return new JornadaDAO_IMPL(); 
	}
	
	public static I_EncuestadorDAO getEncuestadorDAO () {
		return new EncuestadorDAO_IMPL(); 
	}
	
	public static I_OrganizacionSocialDAO getOrganizacionSocialDAO () {
		return new OrganizacionSocialDAO_IMPL(); 
	}

}
