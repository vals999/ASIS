package dao_interfaces;

import java.util.List;
import model.Reporte;
import model.TipoVisibilidad;

public interface I_ReporteDAO extends I_GenericDAO <Reporte, Long >{
    
    /**
     * Obtiene todos los reportes públicos (visibles en la landing page)
     * @return Lista de reportes públicos
     */
    List<Reporte> obtenerReportesPublicos();
    
    /**
     * Obtiene todos los reportes privados (solo visibles para usuarios autenticados)
     * @return Lista de reportes privados
     */
    List<Reporte> obtenerReportesPrivados();
    
    /**
     * Obtiene reportes filtrados por visibilidad
     * @param visibilidad Tipo de visibilidad (PUBLICO o PRIVADO)
     * @return Lista de reportes con la visibilidad especificada
     */
    List<Reporte> obtenerPorVisibilidad(TipoVisibilidad visibilidad);
}
