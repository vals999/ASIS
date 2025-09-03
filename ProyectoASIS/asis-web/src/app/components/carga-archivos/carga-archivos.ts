import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { EventService } from '../../services/event.service';
import { MapaService } from '../../services/mapa.service';

@Component({
  selector: 'app-carga-archivos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './carga-archivos.html',
  styleUrls: ['./carga-archivos.css']
})
export class CargaArchivosComponent implements OnInit {
  selectedFile = signal<File | null>(null);
  uploadMessage = signal<string>('');
  uploading = signal<boolean>(false);
  csvCargadoExitosamente = signal<boolean>(false);

  // Propiedades para el dropdown de preguntas
  preguntasPersona: any[] = [];
  preguntasVivienda: any[] = [];
  preguntaSeleccionada: string = '';

  // Propiedades para el dropdown de respuestas dinámico
  respuestasDisponibles: string[] = [];
  respuestaSeleccionada: string = '';
  cargandoRespuestas: boolean = false;

  // Mapeo de códigos originales para usar en las consultas SQL
  private codigosOriginales: Map<string, string> = new Map();

  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private eventService: EventService,
    private mapaService: MapaService
  ) {}

  ngOnInit(): void {
    // Verificar si ya existen datos al cargar el componente
    this.verificarDatosExistentes();
    
    // Cargar preguntas hardcodeadas (siempre disponibles)
    this.cargarPreguntasHardcodeadas();
  }

  private verificarDatosExistentes(): void {
    this.mapaService.verificarSiExistenDatos().subscribe({
      next: (response) => {
        if (response.existenDatos) {
          console.log(`Se encontraron ${response.totalRespuestas} respuestas en la base de datos`);
          this.csvCargadoExitosamente.set(true);
          // Los dropdowns ya están disponibles desde el inicio - sin mensaje al usuario
        } else {
          console.log('No se encontraron datos en la base de datos');
          this.csvCargadoExitosamente.set(false);
        }
      },
      error: (error) => {
        console.error('Error al verificar datos existentes:', error);
        this.csvCargadoExitosamente.set(false);
      }
    });
  }

  private cargarPreguntasHardcodeadas(): void {
    this.mapaService.obtenerPreguntasHardcodeadas().subscribe({
      next: (preguntas) => {
        // Separar por categoría y ordenar por número
        this.preguntasPersona = preguntas
          .filter(p => p.categoria === 'Persona')
          .sort((a, b) => parseInt(a.numero) - parseInt(b.numero));
          
        this.preguntasVivienda = preguntas
          .filter(p => p.categoria === 'Vivienda')
          .sort((a, b) => parseInt(a.numero) - parseInt(b.numero));

        // Crear mapeo de códigos para las consultas SQL
        this.crearMapeoCodigosOriginales();
          
        console.log('Preguntas cargadas:', preguntas.length);
        console.log('Preguntas Persona ordenadas:', this.preguntasPersona.length);
        console.log('Preguntas Vivienda ordenadas:', this.preguntasVivienda.length);
      },
      error: (error) => {
        console.error('Error al cargar preguntas hardcodeadas:', error);
      }
    });
  }

  private crearMapeoCodigosOriginales(): void {
    // Mapeo de preguntas de persona con sus códigos CSV originales
    const codigosPersona = [
      "6_2_Numero_de_person", "7_Nombre", "8_3_Edad", "9_4_De_acuerdo_a_la_",
      "10_5_En_qu_pas_naci", "11_5a_Otro_pas_cul", "12_6_Para_los_mayore",
      "13_7_Para_mayores_de", "14_8_Para_mayores_de", "15_9_En_ese_trabajo_",
      "16_10_En_cules_de_la", "17_11_Realiza_tareas", "18_12_Cobra_jubilaci",
      "19_13_En_caso_de_que", "20_14_Recibe_algn_pr", "21_14a_Marque_si_con",
      "22_15_Tiene_alguna_d", "23_16_Realizo_un_con", "24_17_Cuenta_con_el_",
      "25_18_Cuando_le_ha_t", "26_19_En_las_consult", "27_20_Tiene_o_tuvo_e",
      "28_21_Considera_que_", "29_21a_Podra_identif", "30_22_Sufreha_sufrid",
      "31_22a_Podra_identif", "32_23_Presenta_algun", "33_23a_Podra_identif",
      "34_24_Tiene_certific", "35_24a_Conoce_su_fec", "36_Fecha_de_vencimie"
    ];

    // Mapeo de preguntas de vivienda con sus códigos CSV originales
    const codigosVivienda = [
      "lat_1_Presione_actualiza", "long_1_Presione_actualiza", "2_Direccin_escribir_",
      "3_Marque_segn_la_pos", "4_1_Cuntas_personas_", "5_Complete_esta_secc",
      "37_25_Con_qu_materia", "38_26_Tiene_acceso_a", "39_27_Cmo_es_el_acce",
      "40_28_El_agua_que_se", "41_29_Esta_vivienda_", "42_30_Cmo_es_el_desa",
      "43_31_Qu_usa_princip", "44_32_Cmo_es_la_cale", "45_33_La_conexion_de",
      "46_34_Con_respecto_a", "47_35_Cuntos_ambient", "48_36_La_vivienda_es",
      "49_37_Sumando_todos_", "50_38_En_esta_vivien", "51_39_Podra_identifi",
      "52_40_Ante_un_proble", "53_41_Cuando_tuviero", "54_42_podra_seleccio",
      "55_43_A_qu_disciplin", "56_44_Hay_personas_e", "57_44a_Cuntas",
      "58_45_Sabe_en_qu_eta", "59_45a_Cuntas_se_enc", "60_45b_Cuntas_se_enc",
      "61_45c_Cuntas_se_enc", "62_46_Sabe_si_han_re", "63_46a_Cuntas_realiz",
      "64_46b_Cuntas_realiz", "65_46c_Cuntas_NO_han", "66_47_Dnde_realizan_",
      "67_48_Alguien_realiz", "68_49_Dnde_lo_realiz", "69_50_Alguien_lo_nec",
      "70_50a_Cuntos", "71_51_Cul_fue_el_mot", "72_52_En_algn_caso_f",
      "73_52a_Cuntos", "74_53_En_esta_vivien", "75_54_Podra_identifi",
      "76_54a_Cul", "77_55_Cul_o_cules_de", "78_56_Identifics_alg",
      "79_56a_Cules", "80_57_Fuiste_atendid", "81_58_Te_indicaron_t",
      "82_59_Pudiste_accede", "83_60_Cmo_consegus_l", "84_61_En_el_ltimo_ao",
      "85_62_Pregunta_para_", "86_63_Consultar_si_h", "87_64_Actualmente_us",
      "88_65_Qu_mtodo_antic", "89_66_Cmo_lo_consigu", "90_67_En_el_ltimo_ao",
      "91_68_Le_ofrecieron_", "92_69_Hay_algn_mtodo", "93_69a_Cul",
      "94_70_Utiliza_preser", "95_71_Luego_de_termi", "96_Observaciones_agr",
      "97_OPCIONAL_Nombre_a"
    ];

    // Crear mapeo para preguntas de persona
    for (let i = 0; i < codigosPersona.length && i < this.preguntasPersona.length; i++) {
      this.codigosOriginales.set(`persona-${i + 1}`, codigosPersona[i]);
    }

    // Crear mapeo para preguntas de vivienda
    for (let i = 0; i < codigosVivienda.length && i < this.preguntasVivienda.length; i++) {
      this.codigosOriginales.set(`vivienda-${i + 1}`, codigosVivienda[i]);
    }
  }

  get perfil(): string | undefined {
    return this.authService.currentUser()?.perfil;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    this.selectedFile.set(file || null);
    
    // Limpiar mensaje anterior y estado de CSV cargado
    this.uploadMessage.set('');
    this.csvCargadoExitosamente.set(false);
  }

  onCsvUpload(event: Event) {
    event.preventDefault();
    
    const file = this.selectedFile();
    if (!file) {
      this.uploadMessage.set('Error: No se ha seleccionado ningún archivo');
      return;
    }

    // Validar que sea un archivo CSV
    if (!file.name.toLowerCase().endsWith('.csv')) {
      this.uploadMessage.set('Error: El archivo debe tener extensión .csv');
      return;
    }

    // Validar tamaño del archivo (máximo 10MB)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      this.uploadMessage.set('Error: El archivo es demasiado grande. Máximo 10MB.');
      return;
    }

    this.uploading.set(true);
    this.uploadMessage.set('');
    this.csvCargadoExitosamente.set(false);

    const formData = new FormData();
    formData.append('file', file);

    this.http.post('http://localhost:8080/ProyectoASIS/api/import-csv', formData).subscribe({
      next: (response) => {
        this.uploading.set(false);
        this.uploadMessage.set(`Archivo "${file.name}" importado correctamente.`);
        this.csvCargadoExitosamente.set(true);
        
        // Limpiar el archivo seleccionado
        this.selectedFile.set(null);
        
        // Limpiar el input file
        const fileInput = document.getElementById('csv-file') as HTMLInputElement;
        if (fileInput) {
          fileInput.value = '';
        }

        // Notificar que se completó la carga del CSV
        this.eventService.notifyCsvUploaded();

        // Auto-limpiar mensaje después de 5 segundos (el botón permanece habilitado)
        setTimeout(() => {
          this.uploadMessage.set('');
        }, 5000);
      },
      error: (error) => {
        this.uploading.set(false);
        this.csvCargadoExitosamente.set(false);
        console.error('Error al importar archivo CSV:', error);
        
        let errorMessage = 'Error desconocido al importar el archivo.';
        
        if (error.status === 400) {
          errorMessage = 'Error: Formato de archivo inválido o datos incorrectos.';
        } else if (error.status === 500) {
          errorMessage = 'Error del servidor al procesar el archivo.';
        } else if (error.error && typeof error.error === 'string') {
          errorMessage = `Error: ${error.error}`;
        }
        
        this.uploadMessage.set(errorMessage);
      }
    });
  }

  onPreguntaSeleccionada(): void {
    if (this.preguntaSeleccionada) {
      // Obtener el código original de la pregunta para la consulta SQL
      const codigoOriginal = this.codigosOriginales.get(this.preguntaSeleccionada);
      
      if (codigoOriginal) {
        this.cargarRespuestasParaPregunta(codigoOriginal);
      }
    } else {
      // Limpiar respuestas si no hay pregunta seleccionada
      this.respuestasDisponibles = [];
      this.respuestaSeleccionada = '';
    }
  }

  private cargarRespuestasParaPregunta(codigoPregunta: string): void {
    this.cargandoRespuestas = true;
    this.respuestasDisponibles = [];
    this.respuestaSeleccionada = '';

    this.mapaService.obtenerRespuestasUnicasPorPregunta(codigoPregunta).subscribe({
      next: (respuestas) => {
        this.respuestasDisponibles = respuestas;
        this.cargandoRespuestas = false;
        console.log(`Respuestas únicas para ${codigoPregunta}:`, respuestas.length);
      },
      error: (error) => {
        console.error('Error al cargar respuestas:', error);
        this.cargandoRespuestas = false;
        this.respuestasDisponibles = [];
      }
    });
  }

  filtrarCoordenadasEnMapa(): void {
    if (!this.preguntaSeleccionada || !this.respuestaSeleccionada) {
      return;
    }

    // Obtener el código original de la pregunta para la consulta
    const codigoOriginal = this.codigosOriginales.get(this.preguntaSeleccionada);
    
    if (codigoOriginal) {
      console.log(`Filtrando mapa - Pregunta: ${codigoOriginal}, Respuesta: ${this.respuestaSeleccionada}`);
      
      // Notificar al mapa que debe mostrar coordenadas filtradas
      this.eventService.notifyShowFilteredMap(codigoOriginal, this.respuestaSeleccionada);
    }
  }
}