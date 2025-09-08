package controller;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import dao_interfaces.I_UsuarioDAO;
import dao_interfaces.I_PersonaDAO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Usuario;
import model.Perfil;
import model.DatosPersonales;

@Path("/auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticación", description = "Operaciones de autenticación y autorización")
public class AuthController {

    @Inject
    private I_UsuarioDAO usuarioDAO;
    
    @Inject
    private I_PersonaDAO personaDAO;
    
    @Inject
    private EntityManager em;

    // Clave secreta para JWT (obtenida de variables de entorno)
    private static final String JWT_SECRET = System.getenv("JWT_SECRET") != null 
        ? System.getenv("JWT_SECRET") 
        : "default-dev-key-change-in-production";
    private static final long JWT_EXPIRATION = 1800000; // 30 minutos

    @POST
    @Path("/login")
    @Operation(summary = "Iniciar sesión", 
        description = "Autentica un usuario y retorna un token JWT",
        requestBody = @RequestBody(
            description = "Credenciales de usuario",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"nombreUsuario\":\"admin\",\"contrasena\":\"password123\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    public Response login(LoginRequest loginRequest) {
        try {
            // Buscar usuario por nombre de usuario
            Usuario usuario = usuarioDAO.obtenerPorNombreUsuario(loginRequest.getNombreUsuario());
            
            if (usuario == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Usuario no encontrado"))
                    .build();
            }

            // Verificar contraseña (aquí deberías usar hash, por ahora comparación directa)
            if (!usuario.getContrasena().equals(loginRequest.getContrasena())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Contraseña incorrecta"))
                    .build();
            }

            // Verificar que el usuario esté habilitado
            if (!usuario.isHabilitado()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Usuario deshabilitado"))
                    .build();
            }

            // Generar token JWT
            String token = generateJWT(usuario);

            // Preparar respuesta con datos personales
            Map<String, Object> usuarioInfo = new HashMap<>();
            usuarioInfo.put("id", usuario.getId());
            usuarioInfo.put("nombreUsuario", usuario.getNombreUsuario());
            usuarioInfo.put("email", usuario.getEmail());
            usuarioInfo.put("perfil", usuario.getPerfil().name());
            
            // Incluir datos personales si existen
            if (usuario.getDatosPersonales() != null) {
                usuarioInfo.put("nombre", usuario.getDatosPersonales().getNombre());
                usuarioInfo.put("apellido", usuario.getDatosPersonales().getApellido());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuarioInfo);
            response.put("message", "Login exitoso");

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("message", "Error interno: " + e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/register")
    @Operation(summary = "Registrar nuevo usuario", 
        description = "Registra un nuevo usuario en el sistema",
        requestBody = @RequestBody(
            description = "Datos del nuevo usuario",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"nombreUsuario\":\"nuevouser\",\"email\":\"user@ejemplo.com\",\"contrasena\":\"password123\",\"perfil\":\"PERSONAL_SALUD\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    public Response register(RegisterRequest registerRequest) {
        EntityTransaction tx = em.getTransaction();
        try {
            // Validar que no exista un usuario con el mismo nombre de usuario
            Usuario usuarioExistentePorNombre = usuarioDAO.obtenerPorNombreUsuario(registerRequest.getNombreUsuario());
            if (usuarioExistentePorNombre != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "El nombre de usuario ya existe"))
                    .build();
            }

            // Validar que no exista un usuario con el mismo email
            Usuario usuarioExistentePorEmail = usuarioDAO.obtenerPorEmail(registerRequest.getEmail());
            if (usuarioExistentePorEmail != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "El email ya está registrado"))
                    .build();
            }

            // Convertir String a enum Perfil
            Perfil perfilEnum;
            try {
                perfilEnum = Perfil.valueOf(registerRequest.getPerfil().toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Perfil inválido"))
                    .build();
            }

            // Validar que el perfil no sea ADMINISTRADOR
            if (perfilEnum == Perfil.ADMINISTRADOR) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "No se puede registrar con perfil de administrador"))
                    .build();
            }

            // Iniciar transacción manual
            tx.begin();

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(registerRequest.getNombreUsuario());
            nuevoUsuario.setEmail(registerRequest.getEmail());
            nuevoUsuario.setContrasena(registerRequest.getContrasena()); // En producción debería ser hasheada
            nuevoUsuario.setPerfil(perfilEnum);
            nuevoUsuario.setHabilitado(false); // Por defecto deshabilitado

            // Persistir usuario
            em.persist(nuevoUsuario);
            em.flush(); // Forzar el ID antes de crear datos personales

            // Crear automáticamente los datos personales del usuario
            DatosPersonales datosPersonales = new DatosPersonales();
            datosPersonales.setUsuario(nuevoUsuario);
            datosPersonales.setNombre(registerRequest.getNombre());
            datosPersonales.setApellido(registerRequest.getApellido());
            datosPersonales.setDni(registerRequest.getDni());
            datosPersonales.setEdad(registerRequest.getEdad());
            datosPersonales.setGenero(registerRequest.getGenero());
            
            // Persistir datos personales
            em.persist(datosPersonales);

            // Confirmar transacción
            tx.commit();

            // Preparar respuesta (sin incluir la contraseña)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente. Pendiente de habilitación por administrador.");
            response.put("usuario", Map.of(
                "nombreUsuario", nuevoUsuario.getNombreUsuario(),
                "email", nuevoUsuario.getEmail(),
                "perfil", nuevoUsuario.getPerfil().name(),
                "habilitado", nuevoUsuario.isHabilitado()
            ));

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            // Rollback en caso de error
            if (tx.isActive()) {
                tx.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("message", "Error interno: " + e.getMessage()))
                .build();
        }
    }

    private String generateJWT(Usuario usuario) {
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_EXPIRATION);

        return Jwts.builder()
            .setSubject(usuario.getNombreUsuario())
            .claim("userId", usuario.getId())
            .claim("email", usuario.getEmail())
            .claim("perfil", usuario.getPerfil().name())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    // Clase interna para el request de login
    public static class LoginRequest {
        private String nombreUsuario;
        private String contrasena;

        public LoginRequest() {}

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public void setNombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }

    // Clase interna para el request de registro
    public static class RegisterRequest {
        private String nombreUsuario;
        private String email;
        private String contrasena;
        private String perfil;
        // Nuevos campos de datos personales
        private String nombre;
        private String apellido;
        private int edad;
        private String dni;
        private String genero;

        public RegisterRequest() {}

        // Getters y setters existentes
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

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }

        public String getPerfil() {
            return perfil;
        }

        public void setPerfil(String perfil) {
            this.perfil = perfil;
        }

        // Nuevos getters y setters para datos personales
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public int getEdad() {
            return edad;
        }

        public void setEdad(int edad) {
            this.edad = edad;
        }

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }

        public String getGenero() {
            return genero;
        }

        public void setGenero(String genero) {
            this.genero = genero;
        }
    }
}
