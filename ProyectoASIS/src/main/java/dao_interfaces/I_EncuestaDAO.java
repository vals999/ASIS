package dao_interfaces;


import model.Encuesta;

public interface I_EncuestaDAO extends I_GenericDAO<Encuesta, Long> {
    
    /**
     * Busca una encuesta por su identificador externo (primera columna del CSV)
     * @param idExterno el identificador externo
     * @return la encuesta si existe, null si no existe
     */
    Encuesta findByIdExterno(String idExterno);

}
