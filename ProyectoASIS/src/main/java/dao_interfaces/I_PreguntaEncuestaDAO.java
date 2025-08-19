package dao_interfaces;

import model.PreguntaEncuesta;

public interface I_PreguntaEncuestaDAO extends I_GenericDAO<PreguntaEncuesta, Long> {
    PreguntaEncuesta findByPreguntaCsv(String preguntaCsv);
}
