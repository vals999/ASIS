package dto;

import java.util.Date;

public class Filtros {
    private String pregunta;
    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
    private String categoria;
    private String zona;
    private String barrio;
    private String campania;
    private Date fechaDesde;
    private Date fechaHasta;
    private String sexo;
    private Integer edadDesde;
    private Integer edadHasta;
    private String organizacionSocial;
    private String tipoRespuesta;
    private String perfil;
    private String jornada;
    private String encuestador;

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getCampania() { return campania; }
    public void setCampania(String campania) { this.campania = campania; }

    public Date getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(Date fechaDesde) { this.fechaDesde = fechaDesde; }

    public Date getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(Date fechaHasta) { this.fechaHasta = fechaHasta; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Integer getEdadDesde() { return edadDesde; }
    public void setEdadDesde(Integer edadDesde) { this.edadDesde = edadDesde; }

    public Integer getEdadHasta() { return edadHasta; }
    public void setEdadHasta(Integer edadHasta) { this.edadHasta = edadHasta; }

    public String getOrganizacionSocial() { return organizacionSocial; }
    public void setOrganizacionSocial(String organizacionSocial) { this.organizacionSocial = organizacionSocial; }

    public String getTipoRespuesta() { return tipoRespuesta; }
    public void setTipoRespuesta(String tipoRespuesta) { this.tipoRespuesta = tipoRespuesta; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public String getEncuestador() { return encuestador; }
    public void setEncuestador(String encuestador) { this.encuestador = encuestador; }
}
