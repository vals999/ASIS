package service;

import com.opencsv.CSVReader;
import dao_interfaces.I_PreguntaEncuestaDAO;
import dao_interfaces.I_RespuestaEncuestaDAO;
import dao_interfaces.I_EncuestaDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.PreguntaEncuesta;
import model.RespuestaEncuesta;
import model.Encuesta;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class ImportCsvService {

    @Inject
    private I_PreguntaEncuestaDAO preguntaDao;

    @Inject
    private I_RespuestaEncuestaDAO respuestaDao;

    @Inject
    private I_EncuestaDAO encuestaDao;

    // Clase auxiliar para almacenar texto y categoría
    private static class PreguntaMapeada {
        String texto;
        model.TipoCategoria categoria;
        model.TipoRespuesta tipoRespuesta;
        PreguntaMapeada(String texto, model.TipoCategoria categoria, model.TipoRespuesta tipoRespuesta) {
            this.texto = texto;
            this.categoria = categoria;
            this.tipoRespuesta = tipoRespuesta;
        }
    }
    // Mapeo para preguntas de personas (branch)
    private static final Map<String, PreguntaMapeada> MAPEO_PERSONA = new HashMap<>();
    // Mapeo para preguntas generales de la vivienda (form)
    private static final Map<String, PreguntaMapeada> MAPEO_VIVIENDA = new HashMap<>();

    static {
        // Mapeo branch (por persona)
    MAPEO_PERSONA.put("6_2_Numero_de_person", new PreguntaMapeada("Número de persona", model.TipoCategoria.PERSONAL, model.TipoRespuesta.NUMERO));
    MAPEO_PERSONA.put("7_Nombre", new PreguntaMapeada("Nombre", model.TipoCategoria.PERSONAL, model.TipoRespuesta.TEXTO));
    MAPEO_PERSONA.put("8_3_Edad", new PreguntaMapeada("Edad", model.TipoCategoria.PERSONAL, model.TipoRespuesta.NUMERO));
    MAPEO_PERSONA.put("9_4_De_acuerdo_a_la_", new PreguntaMapeada("¿Con cuál de las siguientes se identifica? (identidad de género)", model.TipoCategoria.PERSONAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("10_5_En_qu_pas_naci", new PreguntaMapeada("¿En qué país nació?", model.TipoCategoria.PERSONAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("11_5a_Otro_pas_cul", new PreguntaMapeada("Otro país: ¿cuál?", model.TipoCategoria.PERSONAL, model.TipoRespuesta.TEXTO));
    MAPEO_PERSONA.put("12_6_Para_los_mayore", new PreguntaMapeada("¿Sabe leer y escribir? (mayores de 10 años)", model.TipoCategoria.EDUCACION, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("13_7_Para_mayores_de", new PreguntaMapeada("¿Cuál es el máximo nivel educativo que alcanzó? (mayores de 4 años)", model.TipoCategoria.EDUCACION, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("14_8_Para_mayores_de", new PreguntaMapeada("¿Trabajó al menos una hora la semana pasada? (mayores de 14 años)", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("15_9_En_ese_trabajo_", new PreguntaMapeada("¿Está registrado/a en ese trabajo?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("16_10_En_cules_de_la", new PreguntaMapeada("¿En cuál de las siguientes ramas ubica la actividad principal?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("17_11_Realiza_tareas", new PreguntaMapeada("¿Realiza tareas domésticas y/o crianza de niñxs?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("18_12_Cobra_jubilaci", new PreguntaMapeada("¿Cobra jubilación o pensión?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("19_13_En_caso_de_que", new PreguntaMapeada("¿Recibió notificación para auditoría de pensión no contributiva?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("20_14_Recibe_algn_pr", new PreguntaMapeada("¿Recibe algún programa, subsidio del Estado?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("21_14a_Marque_si_con", new PreguntaMapeada("¿Cuál programa/subsidio recibe?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("22_15_Tiene_alguna_d", new PreguntaMapeada("¿Tiene alguna cobertura de salud?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("23_16_Realizo_un_con", new PreguntaMapeada("¿Realizó un control de salud en el último año?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("24_17_Cuenta_con_el_", new PreguntaMapeada("¿Cuenta con el calendario de vacunación completo?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("25_18_Cuando_le_ha_t", new PreguntaMapeada("¿Pudo acceder a la vacunación?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("26_19_En_las_consult", new PreguntaMapeada("¿Le han hablado sobre el calendario de vacunación?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("27_20_Tiene_o_tuvo_e", new PreguntaMapeada("¿Sabe si tiene o tuvo alguna de estas enfermedades o situaciones de salud?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("28_21_Considera_que_", new PreguntaMapeada("¿Considera que tiene o tuvo problemas de consumo de sustancias?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("29_21a_Podra_identif", new PreguntaMapeada("¿Qué sustancias consume?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_PERSONA.put("30_22_Sufreha_sufrid", new PreguntaMapeada("¿Sufre/ha sufrido situaciones de violencia?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("31_22a_Podra_identif", new PreguntaMapeada("¿Qué tipo de violencia?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    MAPEO_PERSONA.put("32_23_Presenta_algun", new PreguntaMapeada("¿Presenta alguna discapacidad?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("33_23a_Podra_identif", new PreguntaMapeada("¿De qué tipo?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_PERSONA.put("34_24_Tiene_certific", new PreguntaMapeada("¿Tiene certificado único de discapacidad (CUD)?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("35_24a_Conoce_su_fec", new PreguntaMapeada("¿Conoce la fecha de vencimiento del CUD?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_PERSONA.put("36_Fecha_de_vencimie", new PreguntaMapeada("Fecha de vencimiento del CUD", model.TipoCategoria.SALUD, model.TipoRespuesta.FECHA));
    }

    static {
        // Mapeo form (por vivienda)
    MAPEO_VIVIENDA.put("lat_1_Presione_actualiza", new PreguntaMapeada("Latitud de la ubicación", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("long_1_Presione_actualiza", new PreguntaMapeada("Longitud de la ubicación", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("2_Direccin_escribir_", new PreguntaMapeada("Dirección (en caso de no tener numeración, describirlo, por ej. 'casa cerca de la esquina con puerta roja')", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("3_Marque_segn_la_pos", new PreguntaMapeada("Marque según la posibilidad de realizar la entrevista", model.TipoCategoria.SOCIAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("4_1_Cuntas_personas_", new PreguntaMapeada("¿Cuántas personas viven la mayor parte del tiempo en esta vivienda?", model.TipoCategoria.PERSONAL, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("5_Complete_esta_secc", new PreguntaMapeada("Complete esta sección por cada persona de la vivienda", model.TipoCategoria.PERSONAL, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("37_25_Con_qu_materia", new PreguntaMapeada("¿Con qué material está construida la vivienda, en su mayoría?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("38_26_Tiene_acceso_a", new PreguntaMapeada("¿Tiene acceso a agua?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("39_27_Cmo_es_el_acce", new PreguntaMapeada("¿Cómo es el acceso al agua?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("40_28_El_agua_que_se", new PreguntaMapeada("¿El agua que se usa para beber y cocinar es potable?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("41_29_Esta_vivienda_", new PreguntaMapeada("Esta vivienda, ¿tiene (baño, letrina, no tiene, no sabe o no contesta)?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("42_30_Cmo_es_el_desa", new PreguntaMapeada("El desagüe del inodoro, ¿es a … (red pública, cámara séptica, pozo ciego, etc.)?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("43_31_Qu_usa_princip", new PreguntaMapeada("¿Qué usa principalmente para cocinar?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("44_32_Cmo_es_la_cale", new PreguntaMapeada("¿Cómo es la calefacción en esta vivienda?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("45_33_La_conexion_de", new PreguntaMapeada("La conexión de electricidad, ¿es…?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("46_34_Con_respecto_a", new PreguntaMapeada("Con respecto a internet, ¿Tiene…?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("47_35_Cuntos_ambient", new PreguntaMapeada("¿Cuántos ambientes, habitaciones o piezas para dormir tiene en total?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("48_36_La_vivienda_es", new PreguntaMapeada("La vivienda, ¿es…?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("49_37_Sumando_todos_", new PreguntaMapeada("Sumando todos los ingresos económicos de la vivienda, considera que:", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("50_38_En_esta_vivien", new PreguntaMapeada("En esta vivienda, ¿reciben alguna asistencia alimentaria?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("51_39_Podra_identifi", new PreguntaMapeada("Si respondió SÍ, ¿De qué tipo?", model.TipoCategoria.ECONOMICA, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("52_40_Ante_un_proble", new PreguntaMapeada("Ante un problema de salud, ¿a dónde concurren?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("53_41_Cuando_tuviero", new PreguntaMapeada("Cuando tuvieron que atenderse en un efector de salud, ¿tuvieron alguna dificultad?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("54_42_podra_seleccio", new PreguntaMapeada("Si respondió SÍ, ¿podría seleccionar qué tipo de dificultades?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("55_43_A_qu_disciplin", new PreguntaMapeada("¿A qué disciplinas consultaron en el último año?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("56_44_Hay_personas_e", new PreguntaMapeada("¿Hay personas embarazadas?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("57_44a_Cuntas", new PreguntaMapeada("¿Cuántas personas embarazadas?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("58_45_Sabe_en_qu_eta", new PreguntaMapeada("¿Sabe en qué etapa del embarazo se encuentran?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("59_45a_Cuntas_se_enc", new PreguntaMapeada("¿Cuántas se encuentran en esa etapa?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("60_45b_Cuntas_se_enc", new PreguntaMapeada("¿Cuántas se encuentran en esa etapa?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("61_45c_Cuntas_se_enc", new PreguntaMapeada("¿Cuántas se encuentran en esa etapa?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("62_46_Sabe_si_han_re", new PreguntaMapeada("¿Sabe si han realizado algún control del embarazo?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("63_46a_Cuntas_realiz", new PreguntaMapeada("¿Cuántas realizaron controles?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("64_46b_Cuntas_realiz", new PreguntaMapeada("¿Cuántas realizaron controles?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("65_46c_Cuntas_NO_han", new PreguntaMapeada("¿Cuántas NO han realizado controles?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("66_47_Dnde_realizan_", new PreguntaMapeada("¿Dónde los realizan?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("67_48_Alguien_realiz", new PreguntaMapeada("¿Alguien realiza tratamiento en Salud Mental (psicológico/psiquiátrico)?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("68_49_Dnde_lo_realiz", new PreguntaMapeada("En caso de responder SÍ, ¿dónde?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("69_50_Alguien_lo_nec", new PreguntaMapeada("¿Alguien lo necesitó y no lo obtuvo?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("70_50a_Cuntos", new PreguntaMapeada("¿Cuántos?", model.TipoCategoria.SALUD, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("71_51_Cul_fue_el_mot", new PreguntaMapeada("¿Cuál fue el motivo por el que no pudo acceder?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("72_52_En_algn_caso_f", new PreguntaMapeada("¿En algún caso fue por derivación de juzgado/escuela/organismo de niñez?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("73_52a_Cuntos", new PreguntaMapeada("¿Cuántos?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.NUMERO));
    MAPEO_VIVIENDA.put("74_53_En_esta_vivien", new PreguntaMapeada("En esta vivienda, ¿participan en alguna institución u organización en tu barrio?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("75_54_Podra_identifi", new PreguntaMapeada("Si respondió SÍ: ¿Podría identificar cuál/es?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("76_54a_Cul", new PreguntaMapeada("¿Cuál?", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("77_55_Cul_o_cules_de", new PreguntaMapeada("¿Cuál o cuáles de los siguiente servicios llegan a tu barrio?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("78_56_Identifics_alg", new PreguntaMapeada("¿Identificás algunos de estos problemas en tu barrio?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("79_56a_Cules", new PreguntaMapeada("¿Cuáles?", model.TipoCategoria.VIVIENDA, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("80_57_Fuiste_atendid", new PreguntaMapeada("¿Fuiste atendido/a por una situación o problema de salud en el último año?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("81_58_Te_indicaron_t", new PreguntaMapeada("¿Te indicaron tomar alguna medicación?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("82_59_Pudiste_accede", new PreguntaMapeada("¿Pudiste acceder a esa medicación?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("83_60_Cmo_consegus_l", new PreguntaMapeada("¿Cómo conseguís la medicación?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("84_61_En_el_ltimo_ao", new PreguntaMapeada("¿En el último año tuviste que interrumpir algún tratamiento por dificultades en el acceso a la medicación?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("85_62_Pregunta_para_", new PreguntaMapeada("¿Hace cuánto que te realizaste un estudio de papanicolau o PAP para rastreo de cáncer de cuello de útero?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("86_63_Consultar_si_h", new PreguntaMapeada("¿Ha tenido relaciones sexuales alguna vez?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("87_64_Actualmente_us", new PreguntaMapeada("¿Actualmente usa algún método anticonceptivo?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("88_65_Qu_mtodo_antic", new PreguntaMapeada("¿Qué método anticonceptivo usa?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("89_66_Cmo_lo_consigu", new PreguntaMapeada("¿Cómo lo consigue?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("90_67_En_el_ltimo_ao", new PreguntaMapeada("¿En el último año ha tenido dificultad para conseguirlo?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("91_68_Le_ofrecieron_", new PreguntaMapeada("En caso de responder SÍ, ¿Le ofrecieron otras opciones?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("92_69_Hay_algn_mtodo", new PreguntaMapeada("¿Hay algún método que te gustaría usar y no conseguís?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("93_69a_Cul", new PreguntaMapeada("¿Cuál?", model.TipoCategoria.SALUD, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("94_70_Utiliza_preser", new PreguntaMapeada("¿Utiliza preservativo/campo látex como prevención de infecciones de transmisión?", model.TipoCategoria.SALUD, model.TipoRespuesta.OPCION_MULTIPLE));
    MAPEO_VIVIENDA.put("95_71_Luego_de_termi", new PreguntaMapeada("Pregunta de cierre: Luego de terminar el trabajo pensamos compartir con los...", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("96_Observaciones_agr", new PreguntaMapeada("Observaciones", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    MAPEO_VIVIENDA.put("97_OPCIONAL_Nombre_a", new PreguntaMapeada("Nombre y contacto opcional", model.TipoCategoria.SOCIAL, model.TipoRespuesta.TEXTO));
    }

    // Método para detectar tipo de CSV según headers (branch/persona o form/vivienda)
    private static Map<String, PreguntaMapeada> detectarMapeo(String[] headers) {
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

            Map<String, PreguntaMapeada> mapeoPreguntas = detectarMapeo(header);

            // Solo considerar columnas cuyo header esté en el mapeo
            Map<Integer, PreguntaEncuesta> preguntasMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                String preguntaCsv = header[i];
                if (preguntaCsv == null || preguntaCsv.trim().isEmpty()) continue;
                if (!mapeoPreguntas.containsKey(preguntaCsv)) continue; // ignorar columnas no mapeadas

                PreguntaEncuesta pregunta = preguntaDao.findByPreguntaCsv(preguntaCsv);
                PreguntaMapeada pm = mapeoPreguntas.get(preguntaCsv);
                if (pregunta == null) {
                    pregunta = new PreguntaEncuesta();
                    pregunta.setPreguntaCsv(preguntaCsv);
                    pregunta.setTexto(pm.texto);
                    pregunta.setCategoria(pm.categoria);
                    pregunta.setTipoRespuesta(pm.tipoRespuesta);
                    preguntaDao.crear(pregunta);
                } else {
                    // Si ya existe, actualizar texto, categoría y tipoRespuesta si cambiaron
                    if (!pm.texto.equals(pregunta.getTexto())) {
                        pregunta.setTexto(pm.texto);
                    }
                    if (pregunta.getCategoria() == null || !pm.categoria.equals(pregunta.getCategoria())) {
                        pregunta.setCategoria(pm.categoria);
                    }
                    if (pregunta.getTipoRespuesta() == null || !pm.tipoRespuesta.equals(pregunta.getTipoRespuesta())) {
                        pregunta.setTipoRespuesta(pm.tipoRespuesta);
                    }
                    preguntaDao.actualizar(pregunta);
                }
                preguntasMap.put(i, pregunta);
            }

            // Guardar solo respuestas de columnas mapeadas
            String[] fila;
            while ((fila = reader.readNext()) != null) {
                // Crear una nueva encuesta para la fila
                Encuesta encuesta = new Encuesta();
                // Si necesitas setear campos adicionales en Encuesta, hazlo aquí
                // Por ejemplo: encuesta.setFecha(...);
                // Si usas un DAO para persistir la encuesta, hazlo aquí:
                // encuestaDao.crear(encuesta);
                encuestaDao.crear(encuesta);

                for (int i = 0; i < fila.length; i++) {
                    if (!preguntasMap.containsKey(i)) continue; // ignorar columnas no mapeadas
                    String respuesta = fila[i];
                    PreguntaEncuesta pregunta = preguntasMap.get(i);
                    if (pregunta != null && respuesta != null && !respuesta.trim().isEmpty()) {
                        RespuestaEncuesta resp = new RespuestaEncuesta();
                        resp.setPregunta(pregunta);
                        resp.setValor(respuesta);
                        resp.setEncuesta(encuesta); // Asignar el id de encuesta aquí
                        respuestaDao.crear(resp);
                    }
                }
            }
        }
    }
}