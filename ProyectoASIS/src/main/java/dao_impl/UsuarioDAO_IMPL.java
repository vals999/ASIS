package dao_impl;

import dao_interfaces.I_UsuarioDAO;
import jakarta.enterprise.context.ApplicationScoped;
import model.Usuario;
@ApplicationScoped 
public class UsuarioDAO_IMPL extends GenericDAO_IMPL<Usuario, Long> implements I_UsuarioDAO{

	// Implemento los métodos específicos de Usuario
    // Los métodos genéricos ya están implementados en la clase padre
	
	public UsuarioDAO_IMPL() {
		super(Usuario.class);
		// TODO Auto-generated constructor stub
	}
	
}
