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

    // Mapeo para preguntas de personas (branch)
    private static final Map<String, String> MAPEO_PERSONA = new HashMap<>();
    // Mapeo para preguntas generales de la vivienda (form)
    private static final Map<String, String> MAPEO_VIVIENDA = new HashMap<>();

    static {
        // Mapeo branch (por persona)
        MAPEO_PERSONA.put("6_2_Numero_de_person", "Número de persona");
        MAPEO_PERSONA.put("7_Nombre", "Nombre");
        MAPEO_PERSONA.put("8_3_Edad", "Edad");
        MAPEO_PERSONA.put("9_4_De_acuerdo_a_la_", "¿Con cuál de las siguientes se identifica? (identidad de género)");
        MAPEO_PERSONA.put("10_5_En_qu_pas_naci", "¿En qué país nació?");
        MAPEO_PERSONA.put("11_5a_Otro_pas_cul", "Otro país: ¿cuál?");
        MAPEO_PERSONA.put("12_6_Para_los_mayore", "¿Sabe leer y escribir? (mayores de 10 años)");
        MAPEO_PERSONA.put("13_7_Para_mayores_de", "¿Cuál es el máximo nivel educativo que alcanzó? (mayores de 4 años)");
        MAPEO_PERSONA.put("14_8_Para_mayores_de", "¿Trabajó al menos una hora la semana pasada? (mayores de 14 años)");
        MAPEO_PERSONA.put("15_9_En_ese_trabajo_", "¿Está registrado/a en ese trabajo?");
        MAPEO_PERSONA.put("16_10_En_cules_de_la", "¿En cuál de las siguientes ramas ubica la actividad principal?");
        MAPEO_PERSONA.put("17_11_Realiza_tareas", "¿Realiza tareas domésticas y/o crianza de niñxs?");
        MAPEO_PERSONA.put("18_12_Cobra_jubilaci", "¿Cobra jubilación o pensión?");
        MAPEO_PERSONA.put("19_13_En_caso_de_que", "¿Recibió notificación para auditoría de pensión no contributiva?");
        MAPEO_PERSONA.put("20_14_Recibe_algn_pr", "¿Recibe algún programa, subsidio del Estado?");
        MAPEO_PERSONA.put("21_14a_Marque_si_con", "¿Cuál programa/subsidio recibe?");
        MAPEO_PERSONA.put("22_15_Tiene_alguna_d", "¿Tiene alguna cobertura de salud?");
        MAPEO_PERSONA.put("23_16_Realizo_un_con", "¿Realizó un control de salud en el último año?");
        MAPEO_PERSONA.put("24_17_Cuenta_con_el_", "¿Cuenta con el calendario de vacunación completo?");
        MAPEO_PERSONA.put("25_18_Cuando_le_ha_t", "¿Pudo acceder a la vacunación?");
        MAPEO_PERSONA.put("26_19_En_las_consult", "¿Le han hablado sobre el calendario de vacunación?");
        MAPEO_PERSONA.put("27_20_Tiene_o_tuvo_e", "¿Sabe si tiene o tuvo alguna de estas enfermedades o situaciones de salud?");
        MAPEO_PERSONA.put("28_21_Considera_que_", "¿Considera que tiene o tuvo problemas de consumo de sustancias?");
        MAPEO_PERSONA.put("29_21a_Podra_identif", "¿Qué sustancias consume?");
        MAPEO_PERSONA.put("30_22_Sufreha_sufrid", "¿Sufre/ha sufrido situaciones de violencia?");
        MAPEO_PERSONA.put("31_22a_Podra_identif", "¿Qué tipo de violencia?");
        MAPEO_PERSONA.put("32_23_Presenta_algun", "¿Presenta alguna discapacidad?");
        MAPEO_PERSONA.put("33_23a_Podra_identif", "¿De qué tipo?");
        MAPEO_PERSONA.put("34_24_Tiene_certific", "¿Tiene certificado único de discapacidad (CUD)?");
        MAPEO_PERSONA.put("35_24a_Conoce_su_fec", "¿Conoce la fecha de vencimiento del CUD?");
        MAPEO_PERSONA.put("36_Fecha_de_vencimie", "Fecha de vencimiento del CUD");
    }

    static {
        // Mapeo form (por vivienda)
        MAPEO_VIVIENDA.put("lat_1_Presione_actualiza", "Latitud de la ubicación");
        MAPEO_VIVIENDA.put("long_1_Presione_actualiza", "Longitud de la ubicación");
        MAPEO_VIVIENDA.put("2_Direccin_escribir_", "Dirección (en caso de no tener numeración, describirlo, por ej. 'casa cerca de la esquina con puerta roja')");
        MAPEO_VIVIENDA.put("3_Marque_segn_la_pos", "Marque según la posibilidad de realizar la entrevista");
        MAPEO_VIVIENDA.put("4_1_Cuntas_personas_", "¿Cuántas personas viven la mayor parte del tiempo en esta vivienda?");
        MAPEO_VIVIENDA.put("5_Complete_esta_secc", "Complete esta sección por cada persona de la vivienda");
        MAPEO_VIVIENDA.put("37_25_Con_qu_materia", "¿Con qué material está construida la vivienda, en su mayoría?");
        MAPEO_VIVIENDA.put("38_26_Tiene_acceso_a", "¿Tiene acceso a agua?");
        MAPEO_VIVIENDA.put("39_27_Cmo_es_el_acce", "¿Cómo es el acceso al agua?");
        MAPEO_VIVIENDA.put("40_28_El_agua_que_se", "¿El agua que se usa para beber y cocinar es potable?");
        MAPEO_VIVIENDA.put("41_29_Esta_vivienda_", "Esta vivienda, ¿tiene (baño, letrina, no tiene, no sabe o no contesta)?");
        MAPEO_VIVIENDA.put("42_30_Cmo_es_el_desa", "El desagüe del inodoro, ¿es a … (red pública, cámara séptica, pozo ciego, etc.)?");
        MAPEO_VIVIENDA.put("43_31_Qu_usa_princip", "¿Qué usa principalmente para cocinar?");
        MAPEO_VIVIENDA.put("44_32_Cmo_es_la_cale", "¿Cómo es la calefacción en esta vivienda?");
        MAPEO_VIVIENDA.put("45_33_La_conexion_de", "La conexión de electricidad, ¿es…?");
        MAPEO_VIVIENDA.put("46_34_Con_respecto_a", "Con respecto a internet, ¿Tiene…?");
        MAPEO_VIVIENDA.put("47_35_Cuntos_ambient", "¿Cuántos ambientes, habitaciones o piezas para dormir tiene en total?");
        MAPEO_VIVIENDA.put("48_36_La_vivienda_es", "La vivienda, ¿es…?");
        MAPEO_VIVIENDA.put("49_37_Sumando_todos_", "Sumando todos los ingresos económicos de la vivienda, considera que:");
        MAPEO_VIVIENDA.put("50_38_En_esta_vivien", "En esta vivienda, ¿reciben alguna asistencia alimentaria?");
        MAPEO_VIVIENDA.put("51_39_Podra_identifi", "Si respondió SÍ, ¿De qué tipo?");
        MAPEO_VIVIENDA.put("52_40_Ante_un_proble", "Ante un problema de salud, ¿a dónde concurren?");
        MAPEO_VIVIENDA.put("53_41_Cuando_tuviero", "Cuando tuvieron que atenderse en un efector de salud, ¿tuvieron alguna dificultad?");
        MAPEO_VIVIENDA.put("54_42_podra_seleccio", "Si respondió SÍ, ¿podría seleccionar qué tipo de dificultades?");
        MAPEO_VIVIENDA.put("55_43_A_qu_disciplin", "¿A qué disciplinas consultaron en el último año?");
        MAPEO_VIVIENDA.put("56_44_Hay_personas_e", "¿Hay personas embarazadas?");
        MAPEO_VIVIENDA.put("57_44a_Cuntas", "¿Cuántas personas embarazadas?");
        MAPEO_VIVIENDA.put("58_45_Sabe_en_qu_eta", "¿Sabe en qué etapa del embarazo se encuentran?");
        MAPEO_VIVIENDA.put("59_45a_Cuntas_se_enc", "¿Cuántas se encuentran en esa etapa?");
        MAPEO_VIVIENDA.put("60_45b_Cuntas_se_enc", "¿Cuántas se encuentran en esa etapa?");
        MAPEO_VIVIENDA.put("61_45c_Cuntas_se_enc", "¿Cuántas se encuentran en esa etapa?");
        MAPEO_VIVIENDA.put("62_46_Sabe_si_han_re", "¿Sabe si han realizado algún control del embarazo?");
        MAPEO_VIVIENDA.put("63_46a_Cuntas_realiz", "¿Cuántas realizaron controles?");
        MAPEO_VIVIENDA.put("64_46b_Cuntas_realiz", "¿Cuántas realizaron controles?");
        MAPEO_VIVIENDA.put("65_46c_Cuntas_NO_han", "¿Cuántas NO han realizado controles?");
        MAPEO_VIVIENDA.put("66_47_Dnde_realizan_", "¿Dónde los realizan?");
        MAPEO_VIVIENDA.put("67_48_Alguien_realiz", "¿Alguien realiza tratamiento en Salud Mental (psicológico/psiquiátrico)?");
        MAPEO_VIVIENDA.put("68_49_Dnde_lo_realiz", "En caso de responder SÍ, ¿dónde?");
        MAPEO_VIVIENDA.put("69_50_Alguien_lo_nec", "¿Alguien lo necesitó y no lo obtuvo?");
        MAPEO_VIVIENDA.put("70_50a_Cuntos", "¿Cuántos?");
        MAPEO_VIVIENDA.put("71_51_Cul_fue_el_mot", "¿Cuál fue el motivo por el que no pudo acceder?");
        MAPEO_VIVIENDA.put("72_52_En_algn_caso_f", "¿En algún caso fue por derivación de juzgado/escuela/organismo de niñez?");
        MAPEO_VIVIENDA.put("73_52a_Cuntos", "¿Cuántos?");
        MAPEO_VIVIENDA.put("74_53_En_esta_vivien", "En esta vivienda, ¿participan en alguna institución u organización en tu barrio?");
        MAPEO_VIVIENDA.put("75_54_Podra_identifi", "Si respondió SÍ: ¿Podría identificar cuál/es?");
        MAPEO_VIVIENDA.put("76_54a_Cul", "¿Cuál?");
        MAPEO_VIVIENDA.put("77_55_Cul_o_cules_de", "¿Cuál o cuáles de los siguiente servicios llegan a tu barrio?");
        MAPEO_VIVIENDA.put("78_56_Identifics_alg", "¿Identificás algunos de estos problemas en tu barrio?");
        MAPEO_VIVIENDA.put("79_56a_Cules", "¿Cuáles?");
        MAPEO_VIVIENDA.put("80_57_Fuiste_atendid", "¿Fuiste atendido/a por una situación o problema de salud en el último año?");
        MAPEO_VIVIENDA.put("81_58_Te_indicaron_t", "¿Te indicaron tomar alguna medicación?");
        MAPEO_VIVIENDA.put("82_59_Pudiste_accede", "¿Pudiste acceder a esa medicación?");
        MAPEO_VIVIENDA.put("83_60_Cmo_consegus_l", "¿Cómo conseguís la medicación?");
        MAPEO_VIVIENDA.put("84_61_En_el_ltimo_ao", "¿En el último año tuviste que interrumpir algún tratamiento por dificultades en el acceso a la medicación?");
        MAPEO_VIVIENDA.put("85_62_Pregunta_para_", "¿Hace cuánto que te realizaste un estudio de papanicolau o PAP para rastreo de cáncer de cuello de útero?");
        MAPEO_VIVIENDA.put("86_63_Consultar_si_h", "¿Ha tenido relaciones sexuales alguna vez?");
        MAPEO_VIVIENDA.put("87_64_Actualmente_us", "¿Actualmente usa algún método anticonceptivo?");
        MAPEO_VIVIENDA.put("88_65_Qu_mtodo_antic", "¿Qué método anticonceptivo usa?");
        MAPEO_VIVIENDA.put("89_66_Cmo_lo_consigu", "¿Cómo lo consigue?");
        MAPEO_VIVIENDA.put("90_67_En_el_ltimo_ao", "¿En el último año ha tenido dificultad para conseguirlo?");
        MAPEO_VIVIENDA.put("91_68_Le_ofrecieron_", "En caso de responder SÍ, ¿Le ofrecieron otras opciones?");
        MAPEO_VIVIENDA.put("92_69_Hay_algn_mtodo", "¿Hay algún método que te gustaría usar y no conseguís?");
        MAPEO_VIVIENDA.put("93_69a_Cul", "¿Cuál?");
        MAPEO_VIVIENDA.put("94_70_Utiliza_preser", "¿Utiliza preservativo/campo látex como prevención de infecciones de transmisión?");
        MAPEO_VIVIENDA.put("95_71_Luego_de_termi", "Pregunta de cierre: Luego de terminar el trabajo pensamos compartir con los...");
        MAPEO_VIVIENDA.put("96_Observaciones_agr", "Observaciones");
        MAPEO_VIVIENDA.put("97_OPCIONAL_Nombre_a", "Nombre y contacto opcional");
    }

    // Método para detectar tipo de CSV según headers (branch/persona o form/vivienda)
    private static Map<String, String> detectarMapeo(String[] headers) {
        int scorePersona = 0;
        int scoreVivienda = 0;
        for (String h : headers) {
            if (MAPEO_PERSONA.containsKey(h)) scorePersona++;
            if (MAPEO_VIVIENDA.containsKey(h)) scoreVivienda++;
        }
        // Si hay más coincidencias con persona, es branch; si no, vivienda
        return (scorePersona >= scoreVivienda) ? MAPEO_PERSONA : MAPEO_VIVIENDA;
    }

    @Transactional
    public void importar(String rutaCsv) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(rutaCsv))) {
            String[] header = reader.readNext();
            if (header == null) return;

            Map<String, String> mapeoPreguntas = detectarMapeo(header);

            // Solo considerar columnas cuyo header esté en el mapeo
            Map<Integer, PreguntaEncuesta> preguntasMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                String preguntaCsv = header[i];
                if (preguntaCsv == null || preguntaCsv.trim().isEmpty()) continue;
                if (!mapeoPreguntas.containsKey(preguntaCsv)) continue; // ignorar columnas no mapeadas

                PreguntaEncuesta pregunta = preguntaDao.findByPreguntaCsv(preguntaCsv);
                if (pregunta == null) {
                    pregunta = new PreguntaEncuesta();
                    pregunta.setPreguntaCsv(preguntaCsv);
                    String preguntaTexto = mapeoPreguntas.getOrDefault(preguntaCsv, preguntaCsv);
                    pregunta.setTexto(preguntaTexto);
                    preguntaDao.crear(pregunta);
                }
                preguntasMap.put(i, pregunta);
            }

            // Guardar solo respuestas de columnas mapeadas
            String[] fila;
            while ((fila = reader.readNext()) != null) {
                for (int i = 0; i < fila.length; i++) {
                    if (!preguntasMap.containsKey(i)) continue; // ignorar columnas no mapeadas
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