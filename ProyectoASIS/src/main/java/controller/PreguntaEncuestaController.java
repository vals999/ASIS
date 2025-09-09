// PreguntaEncuestaController.java
package controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import dao_interfaces.I_PreguntaEncuestaDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.PreguntaEncuesta;

@Path("/preguntas-encuesta")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pregunta Encuesta", description = "Operaciones ABML para la gestión de preguntas encuestas")
public class PreguntaEncuestaController {

    @Inject
    private I_PreguntaEncuestaDAO preguntaEncuestaDAO;

    @GET
    public Response obtenerTodasLasPreguntas() {
        try {
            List<PreguntaEncuesta> preguntas = preguntaEncuestaDAO.obtenerTodos();
            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerPreguntasActivas() {
        try {
            List<PreguntaEncuesta> preguntas = preguntaEncuestaDAO.obtenerNoBorrados();
            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerPreguntaPorId(@PathParam("id") Long id) {
        try {
            PreguntaEncuesta pregunta = preguntaEncuestaDAO.obtenerPorId(id);
            if (pregunta == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearPregunta(PreguntaEncuesta pregunta) {
        try {
            preguntaEncuestaDAO.crear(pregunta);
            return Response.status(Status.CREATED).entity(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarPregunta(@PathParam("id") Long id, PreguntaEncuesta pregunta) {
        try {
            pregunta.setId(id);
            preguntaEncuestaDAO.actualizar(pregunta);
            return Response.ok(pregunta).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarPregunta(@PathParam("id") Long id) {
        try {
            preguntaEncuestaDAO.eliminar(id);
            return Response.ok().entity("Pregunta eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarPregunta(@PathParam("id") Long id) {
        try {
            preguntaEncuestaDAO.recuperar(id);
            return Response.ok().entity("Pregunta recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/preguntas-hardcodeadas")
    public Response obtenerPreguntasHardcodeadas() {
        try {
            // Obtener todas las preguntas del ImportCsvService
            List<Map<String, String>> preguntas = new ArrayList<>();
            
            // Agregar preguntas de persona (MAPEO_PERSONA) - sin numeración automática
            String[] preguntasPersonaTextos = {
                "2. Número de persona",
                "3. Nombre",
                "4. Edad",
                "5. ¿Con cuál de las siguientes se identifica? (identidad de género)",
                "6. ¿En qué país nació?",
                "6.a Otro país: ¿cuál?",
                "7. ¿Sabe leer y escribir? (mayores de 10 años)",
                "8. ¿Cuál es el máximo nivel educativo que alcanzó? (mayores de 4 años)",
                "9. ¿Trabajó al menos una hora la semana pasada? (mayores de 14 años)",
                "10. ¿Está registrado/a en ese trabajo?",
                "11. ¿En cuál de las siguientes ramas ubica la actividad principal?",
                "12. ¿Realiza tareas domésticas y/o crianza de niñxs?",
                "13. ¿Cobra jubilación o pensión?",
                "14. ¿Recibió notificación para auditoría de pensión no contributiva?",
                "15. ¿Recibe algún programa, subsidio del Estado?",
                "15.a. ¿Cuál programa/subsidio recibe?",
                "16. ¿Tiene alguna cobertura de salud?",
                "17. ¿Realizó un control de salud en el último año?",
                "18. ¿Cuenta con el calendario de vacunación completo?",
                "19. ¿Pudo acceder a la vacunación?",
                "20. ¿Le han hablado sobre el calendario de vacunación?",
                "21. ¿Sabe si tiene o tuvo alguna de estas enfermedades o situaciones de salud?",
                "22. ¿Considera que tiene o tuvo problemas de consumo de sustancias?",
                "22.a. ¿Qué sustancias consume?",
                "23. ¿Sufre/ha sufrido situaciones de violencia?",
                "23.a. ¿Qué tipo de violencia?",
                "24. ¿Presenta alguna discapacidad?",
                "24.a. ¿De qué tipo?",
                "25. ¿Tiene certificado único de discapacidad (CUD)?",
                "25.a. ¿Conoce la fecha de vencimiento del CUD?",
                "25.b. Fecha de vencimiento del CUD"
            };

            // Agregar preguntas de vivienda (MAPEO_VIVIENDA) - sin numeración automática
            String[] preguntasViviendaTextos = {
                "0.a. Latitud de la ubicación",
                "0.b. Longitud de la ubicación",
                "0.c. Dirección (en caso de no tener numeración, describirlo, por ej. 'casa cerca de la esquina con puerta roja')",
                "0.d. Marque según la posibilidad de realizar la entrevista",
                "1. ¿Cuántas personas viven la mayor parte del tiempo en esta vivienda?",
                "0.e. Complete esta sección por cada persona de la vivienda",
                "26. ¿Con qué material está construida la vivienda, en su mayoría?",
                "27. ¿Tiene acceso a agua?",
                "28. ¿Cómo es el acceso al agua?",
                "29. ¿El agua que se usa para beber y cocinar es potable?",
                "30. Esta vivienda, ¿tiene (baño, letrina, no tiene, no sabe o no contesta)?",
                "31. El desagüe del inodoro, ¿es a … (red pública, cámara séptica, pozo ciego, etc.)?",
                "32. ¿Qué usa principalmente para cocinar?",
                "33. ¿Cómo es la calefacción en esta vivienda?",
                "34. La conexión de electricidad, ¿es…?",
                "35. Con respecto a internet, ¿Tiene…?",
                "36. ¿Cuántos ambientes, habitaciones o piezas para dormir tiene en total?",
                "37. La vivienda, ¿es…?",
                "38. Sumando todos los ingresos económicos de la vivienda, considera que:",
                "39. En esta vivienda, ¿reciben alguna asistencia alimentaria?",
                "40. Si respondió SÍ, ¿De qué tipo?",
                "41. Ante un problema de salud, ¿a dónde concurren?",
                "42. Cuando tuvieron que atenderse en un efector de salud, ¿tuvieron alguna dificultad?",
                "43 Si respondió SÍ, ¿podría seleccionar qué tipo de dificultades?",
                "44. ¿A qué disciplinas consultaron en el último año?",
                "45. ¿Hay personas embarazadas?",
                "45.a. ¿Cuántas personas embarazadas?",
                "46. ¿Sabe en qué etapa del embarazo se encuentran?",
                "46.a. ¿Cuántas se encuentran en el 1° trimestre?",
                "46.b. ¿Cuántas se encuentran en el 2° trimestre?",
                "46.c. ¿Cuántas se encuentran en el 3° trimestre?",
                "47. ¿Sabe si han realizado algún control del embarazo?",
                "47.a. ¿Cuántas realizaron un solo control?",
                "47.b. Cuántas realizaron más de un control?",
                "47.c. ¿Cuántas NO han realizado controles?",
                "48. ¿Dónde los realizan?",
                "49. ¿Alguien realiza tratamiento en Salud Mental (psicológico/psiquiátrico)?",
                "50. En caso de responder SÍ, ¿dónde?",
                "51. ¿Alguien lo necesitó y no lo obtuvo?",
                "51.a. ¿Cuántos?",
                "52. ¿Cuál fue el motivo por el que no pudo acceder?",
                "53. ¿En algún caso fue por derivación de juzgado/escuela/organismo de niñez?",
                "53.a. ¿Cuántos?",
                "54. En esta vivienda, ¿participan en alguna institución u organización en tu barrio?",
                "55. Si respondió SÍ: ¿Podría identificar cuál/es?",
                "55.a. ¿Cuál?",
                "56. ¿Cuál o cuáles de los siguiente servicios llegan a tu barrio?",
                "57. ¿Identificás algunos de estos problemas en tu barrio?",
                "57.a. ¿Cuáles (otros)?",
                "58. ¿Fuiste atendido/a por una situación o problema de salud en el último año?",
                "59. ¿Te indicaron tomar alguna medicación?",
                "60. ¿Pudiste acceder a esa medicación?",
                "61. ¿Cómo conseguís la medicación?",
                "62. ¿En el último año tuviste que interrumpir algún tratamiento por dificultades en el acceso a la medicación?",
                "63. ¿Hace cuánto que te realizaste un estudio de papanicolau o PAP para rastreo de cáncer de cuello de útero?",
                "64. ¿Ha tenido relaciones sexuales alguna vez?",
                "65. ¿Actualmente usa algún método anticonceptivo?",
                "66. ¿Qué método anticonceptivo usa?",
                "67. ¿Cómo lo consigue?",
                "68. ¿En el último año ha tenido dificultad para conseguirlo?",
                "69. En caso de responder SÍ, ¿Le ofrecieron otras opciones?",
                "70. ¿Hay algún método que te gustaría usar y no conseguís?",
                "70.a. ¿Cuál?",
                "71. ¿Utiliza preservativo/campo látex como prevención de infecciones de transmisión?",
                "72. Pregunta de cierre: Luego de terminar el trabajo pensamos compartir con los barrios lo que fuimos recolectando. ¿Te gustaría participar?",
                "0.e. Observaciones",
                "0.f. Nombre y contacto opcional"
            };

            // Agregar preguntas de persona (filtrar las que NO empiecen con "0.[letra]")
            for (String texto : preguntasPersonaTextos) {
                if (!esPatronIgnorado(texto)) {
                    Map<String, String> pregunta = new HashMap<>();
                    pregunta.put("numero", extraerNumero(texto));
                    pregunta.put("texto", texto);
                    pregunta.put("categoria", "Persona");
                    preguntas.add(pregunta);
                }
            }

            // Agregar preguntas de vivienda (filtrar las que NO empiecen con "0.[letra]")
            for (String texto : preguntasViviendaTextos) {
                if (!esPatronIgnorado(texto)) {
                    Map<String, String> pregunta = new HashMap<>();
                    pregunta.put("numero", extraerNumero(texto));
                    pregunta.put("texto", texto);
                    pregunta.put("categoria", "Vivienda");
                    preguntas.add(pregunta);
                }
            }

            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    /**
     * Verifica si una pregunta tiene el patrón "0.[letra]" que debe ser ignorado
     */
    private boolean esPatronIgnorado(String texto) {
        // Buscar patrón "0." seguido de una letra minúscula al inicio del texto
        return texto.matches("^0\\.[a-z]\\..*");
    }

    /**
     * Extrae el número de la pregunta del texto
     */
    private String extraerNumero(String texto) {
        // Buscar el patrón de número al inicio del texto
        if (texto.matches("^\\d+\\..*")) {
            return texto.substring(0, texto.indexOf('.'));
        } else if (texto.matches("^\\d+\\.[a-z]\\..*")) {
            return texto.substring(0, texto.indexOf('.', texto.indexOf('.') + 1));
        }
        return ""; // Si no encuentra patrón, devuelve vacío
    }
}