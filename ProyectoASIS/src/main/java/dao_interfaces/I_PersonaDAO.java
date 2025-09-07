package dao_interfaces;


import model.DatosPersonales;

public interface I_PersonaDAO extends I_GenericDAO<DatosPersonales, Long>{
    // Método específico para obtener datos personales por usuario ID
    DatosPersonales obtenerPorUsuarioId(Long usuarioId);
}
