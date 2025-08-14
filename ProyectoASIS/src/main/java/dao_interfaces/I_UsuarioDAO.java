package dao_interfaces;


import java.util.List;
import model.Usuario;

public interface I_UsuarioDAO extends I_GenericDAO<Usuario, Long>{

	// Métodos específicos solo para Usuario
	Usuario obtenerPorNombreUsuario(String nombreUsuario);
	Usuario obtenerPorEmail(String email);
	List<Usuario> obtenerUsuariosPendientes();
	
}
