package service;

import com.opencsv.CSVReader;
import dao_interfaces.I_PreguntaEncuestaDAO;
import dao_interfaces.I_RespuestaEncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.PreguntaEncuesta;
import model.RespuestaEncuesta;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class ImportCsvService {

    @Inject
    private I_PreguntaEncuestaDAO preguntaDao;

    @Inject
    private I_RespuestaEncuestaDAO respuestaDao;

    @Transactional
    public void importar(String rutaCsv) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(rutaCsv))) {
            String[] header = reader.readNext();
            if (header == null) return;

            // 1. Guardar preguntas si no existen
            Map<Integer, PreguntaEncuesta> preguntasMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                String preguntaCsv = header[i];
                if (preguntaCsv == null || preguntaCsv.trim().isEmpty()) continue;

                PreguntaEncuesta pregunta = preguntaDao.findByPreguntaCsv(preguntaCsv);
                if (pregunta == null) {
                    pregunta = new PreguntaEncuesta();
                    pregunta.setPreguntaCsv(preguntaCsv);
                    pregunta.setTexto(preguntaCsv); // Puedes mapear a un texto más legible si lo deseas
                    preguntaDao.crear(pregunta);
                }
                preguntasMap.put(i, pregunta);
            }

            // 2. Guardar respuestas
            String[] fila;
            while ((fila = reader.readNext()) != null) {
                for (int i = 0; i < fila.length; i++) {
                    String respuesta = fila[i];
                    PreguntaEncuesta pregunta = preguntasMap.get(i);
                    if (pregunta != null && respuesta != null && !respuesta.trim().isEmpty()) {
                        RespuestaEncuesta resp = new RespuestaEncuesta();
                        resp.setPregunta(pregunta);
                        resp.setValor(respuesta);
                        respuestaDao.crear(resp);
                    }
                }
            }
        }
    }
}