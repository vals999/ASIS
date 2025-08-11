package dao_interfaces;

import java.util.List;

public interface I_GenericDAO<T, ID> {	
	void crear(T entidad);
	T obtenerPorId(Long id);
	List<T> obtenerTodos();
	List<T> obtenerNoBorrados();
	void actualizar(T entidad);
	void eliminar(Long id);
	void recuperar(Long id);
}
