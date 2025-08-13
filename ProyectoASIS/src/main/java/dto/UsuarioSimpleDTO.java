package dto;

import model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simple para representar un Usuario con información básica")
public class UsuarioSimpleDTO {
    
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    private String nombreUsuario;
    
    @Schema(description = "Email del usuario", example = "juan.perez@example.com")
    private String email;
    
    @Schema(description = "Indica si el usuario está habilitado", example = "true")
    private boolean habilitado;
    
    @Schema(description = "Perfil del usuario")
    private Perfil perfil;

    // Constructores
    public UsuarioSimpleDTO() {}

    public UsuarioSimpleDTO(Long id, String nombreUsuario, String email, 
                           boolean habilitado, Perfil perfil) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.habilitado = habilitado;
        this.perfil = perfil;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}
