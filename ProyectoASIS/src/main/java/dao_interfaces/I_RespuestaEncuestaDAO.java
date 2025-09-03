package dao_interfaces;

import java.util.List;
import model.RespuestaEncuesta;

public interface I_RespuestaEncuestaDAO extends I_GenericDAO<RespuestaEncuesta, Long>{
    List<RespuestaEncuesta> obtenerNoBorrados();
    List<String> obtenerRespuestasUnicasPorPreguntaCodigo(String preguntaCodigo);
    List<RespuestaEncuesta> obtenerRespuestasPorPreguntaYValor(String preguntaCodigo, String valor);
    List<RespuestaEncuesta> obtenerRespuestasPorPreguntaCodigo(String preguntaCodigo);
    RespuestaEncuesta obtenerRespuestaPorEncuestaYPregunta(Long encuestaId, Long preguntaId);
    RespuestaEncuesta obtenerRespuestaPorEncuestaYPreguntaCsv(Long encuestaId, String preguntaCsv);
}
