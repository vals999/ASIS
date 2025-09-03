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
            
            // Agregar preguntas de persona (MAPEO_PERSONA) con numeración secuencial
            String[] preguntasPersonaTextos = {
                "Número de persona",
                "Nombre",
                "Edad",
                "¿Con cuál de las siguientes se identifica? (identidad de género)",
                "¿En qué país nació?",
                "Otro país: ¿cuál?",
                "¿Sabe leer y escribir? (mayores de 10 años)",
                "¿Cuál es el máximo nivel educativo que alcanzó? (mayores de 4 años)",
                "¿Trabajó al menos una hora la semana pasada? (mayores de 14 años)",
                "¿Está registrado/a en ese trabajo?",
                "¿En cuál de las siguientes ramas ubica la actividad principal?",
                "¿Realiza tareas domésticas y/o crianza de niñxs?",
                "¿Cobra jubilación o pensión?",
                "¿Recibió notificación para auditoría de pensión no contributiva?",
                "¿Recibe algún programa, subsidio del Estado?",
                "¿Cuál programa/subsidio recibe?",
                "¿Tiene alguna cobertura de salud?",
                "¿Realizó un control de salud en el último año?",
                "¿Cuenta con el calendario de vacunación completo?",
                "¿Pudo acceder a la vacunación?",
                "¿Le han hablado sobre el calendario de vacunación?",
                "¿Sabe si tiene o tuvo alguna de estas enfermedades o situaciones de salud?",
                "¿Considera que tiene o tuvo problemas de consumo de sustancias?",
                "¿Qué sustancias consume?",
                "¿Sufre/ha sufrido situaciones de violencia?",
                "¿Qué tipo de violencia?",
                "¿Presenta alguna discapacidad?",
                "¿De qué tipo?",
                "¿Tiene certificado único de discapacidad (CUD)?",
                "¿Conoce la fecha de vencimiento del CUD?",
                "Fecha de vencimiento del CUD"
            };

            // Agregar preguntas de vivienda (MAPEO_VIVIENDA) con numeración secuencial
            String[] preguntasViviendaTextos = {
                "Latitud de la ubicación",
                "Longitud de la ubicación",
                "Dirección (en caso de no tener numeración, describirlo, por ej. 'casa cerca de la esquina con puerta roja')",
                "Marque según la posibilidad de realizar la entrevista",
                "¿Cuántas personas viven la mayor parte del tiempo en esta vivienda?",
                "Complete esta sección por cada persona de la vivienda",
                "¿Con qué material está construida la vivienda, en su mayoría?",
                "¿Tiene acceso a agua?",
                "¿Cómo es el acceso al agua?",
                "¿El agua que se usa para beber y cocinar es potable?",
                "Esta vivienda, ¿tiene (baño, letrina, no tiene, no sabe o no contesta)?",
                "El desagüe del inodoro, ¿es a … (red pública, cámara séptica, pozo ciego, etc.)?",
                "¿Qué usa principalmente para cocinar?",
                "¿Cómo es la calefacción en esta vivienda?",
                "La conexión de electricidad, ¿es…?",
                "Con respecto a internet, ¿Tiene…?",
                "¿Cuántos ambientes, habitaciones o piezas para dormir tiene en total?",
                "La vivienda, ¿es…?",
                "Sumando todos los ingresos económicos de la vivienda, considera que:",
                "En esta vivienda, ¿reciben alguna asistencia alimentaria?",
                "Si respondió SÍ, ¿De qué tipo?",
                "Ante un problema de salud, ¿a dónde concurren?",
                "Cuando tuvieron que atenderse en un efector de salud, ¿tuvieron alguna dificultad?",
                "Si respondió SÍ, ¿podría seleccionar qué tipo de dificultades?",
                "¿A qué disciplinas consultaron en el último año?",
                "¿Hay personas embarazadas?",
                "¿Cuántas personas embarazadas?",
                "¿Sabe en qué etapa del embarazo se encuentran?",
                "¿Cuántas se encuentran en esa etapa?",
                "¿Cuántas se encuentran en esa etapa?",
                "¿Cuántas se encuentran en esa etapa?",
                "¿Sabe si han realizado algún control del embarazo?",
                "¿Cuántas realizaron controles?",
                "¿Cuántas realizaron controles?",
                "¿Cuántas NO han realizado controles?",
                "¿Dónde los realizan?",
                "¿Alguien realiza tratamiento en Salud Mental (psicológico/psiquiátrico)?",
                "En caso de responder SÍ, ¿dónde?",
                "¿Alguien lo necesitó y no lo obtuvo?",
                "¿Cuántos?",
                "¿Cuál fue el motivo por el que no pudo acceder?",
                "¿En algún caso fue por derivación de juzgado/escuela/organismo de niñez?",
                "¿Cuántos?",
                "En esta vivienda, ¿participan en alguna institución u organización en tu barrio?",
                "Si respondió SÍ: ¿Podría identificar cuál/es?",
                "¿Cuál?",
                "¿Cuál o cuáles de los siguiente servicios llegan a tu barrio?",
                "¿Identificás algunos de estos problemas en tu barrio?",
                "¿Cuáles?",
                "¿Fuiste atendido/a por una situación o problema de salud en el último año?",
                "¿Te indicaron tomar alguna medicación?",
                "¿Pudiste acceder a esa medicación?",
                "¿Cómo conseguís la medicación?",
                "¿En el último año tuviste que interrumpir algún tratamiento por dificultades en el acceso a la medicación?",
                "¿Hace cuánto que te realizaste un estudio de papanicolau o PAP para rastreo de cáncer de cuello de útero?",
                "¿Ha tenido relaciones sexuales alguna vez?",
                "¿Actualmente usa algún método anticonceptivo?",
                "¿Qué método anticonceptivo usa?",
                "¿Cómo lo consigue?",
                "¿En el último año ha tenido dificultad para conseguirlo?",
                "En caso de responder SÍ, ¿Le ofrecieron otras opciones?",
                "¿Hay algún método que te gustaría usar y no conseguís?",
                "¿Cuál?",
                "¿Utiliza preservativo/campo látex como prevención de infecciones de transmisión?",
                "Pregunta de cierre: Luego de terminar el trabajo pensamos compartir con los...",
                "Observaciones",
                "Nombre y contacto opcional"
            };

            // Convertir a formato de lista con numeración secuencial
            for (int i = 0; i < preguntasPersonaTextos.length; i++) {
                Map<String, String> pregunta = new HashMap<>();
                pregunta.put("numero", String.valueOf(i + 1));
                pregunta.put("texto", preguntasPersonaTextos[i]);
                pregunta.put("categoria", "Persona");
                preguntas.add(pregunta);
            }

            for (int i = 0; i < preguntasViviendaTextos.length; i++) {
                Map<String, String> pregunta = new HashMap<>();
                pregunta.put("numero", String.valueOf(i + 1));
                pregunta.put("texto", preguntasViviendaTextos[i]);
                pregunta.put("categoria", "Vivienda");
                preguntas.add(pregunta);
            }

            return Response.ok(preguntas).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}