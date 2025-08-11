# DTOs para el Sistema ASIS

## Descripción

Este paquete contiene los DTOs (Data Transfer Objects) creados para resolver el problema de bucles infinitos en las relaciones bidireccionales entre entidades como `Barrio` y `Zona`.

## Problema Resuelto

Cuando las entidades tienen relaciones bidireccionales (por ejemplo, `Barrio` tiene una lista de `Zona` y `Zona` tiene una referencia a `Barrio`), al serializar a JSON para el frontend se produce un bucle infinito.

## Solución

Se crearon dos tipos de DTOs para cada entidad:

### 1. DTOs Completos
- Contienen toda la información de la entidad
- Usan DTOs simples para las relaciones para evitar ciclos
- Ejemplos: `BarrioDTO`, `ZonaDTO`, `UsuarioDTO`, etc.

### 2. DTOs Simples  
- Contienen solo información básica
- Se usan dentro de otros DTOs para evitar referencias circulares
- Ejemplos: `BarrioSimpleDTO`, `ZonaSimpleDTO`, `UsuarioSimpleDTO`, etc.

## Estructura de Archivos

```
dto/
├── BarrioDTO.java              # DTO completo para Barrio
├── BarrioSimpleDTO.java        # DTO simple para Barrio
├── ZonaDTO.java                # DTO completo para Zona  
├── ZonaSimpleDTO.java          # DTO simple para Zona
├── UsuarioDTO.java             # DTO completo para Usuario
├── UsuarioSimpleDTO.java       # DTO simple para Usuario
├── DatosPersonalesDTO.java     # DTO para datos personales
├── CampañaDTO.java             # DTO completo para Campaña
├── CampañaSimpleDTO.java       # DTO simple para Campaña
├── EncuestadorDTO.java         # DTO completo para Encuestador
├── EncuestadorSimpleDTO.java   # DTO simple para Encuestador
├── JornadaDTO.java             # DTO completo para Jornada
├── JornadaSimpleDTO.java       # DTO simple para Jornada
├── EncuestaDTO.java            # DTO completo para Encuesta
├── EncuestaSimpleDTO.java      # DTO simple para Encuesta
├── OrganizacionSocialDTO.java      # DTO completo para OrganizacionSocial
├── OrganizacionSocialSimpleDTO.java # DTO simple para OrganizacionSocial
├── PersonaEncuestadaDTO.java       # DTO completo para PersonaEncuestada
├── PersonaEncuestadaSimpleDTO.java # DTO simple para PersonaEncuestada
├── PreguntaEncuestaDTO.java        # DTO completo para PreguntaEncuesta
├── PreguntaEncuestaSimpleDTO.java  # DTO simple para PreguntaEncuesta
├── RespuestaEncuestaDTO.java       # DTO completo para RespuestaEncuesta
├── RespuestaEncuestaSimpleDTO.java # DTO simple para RespuestaEncuesta
├── ReporteDTO.java                 # DTO completo para Reporte
├── ReporteSimpleDTO.java           # DTO simple para Reporte
├── DTOMapper.java              # Clase utilitaria para mapear entidades a DTOs
└── examples/
    └── ExampleController.java  # Ejemplo de uso en controladores
```

## Cómo Usar

### 1. En los Controladores

Reemplaza las devoluciones directas de entidades por DTOs:

```java
// ANTES (causaba bucles infinitos)
@GET
@Path("/barrios")
public Response getAllBarrios() {
    List<Barrio> barrios = barrioDAO.getAll();
    return Response.ok(barrios).build(); // ❌ Bucle infinito
}

// DESPUÉS (sin bucles infinitos)
@GET  
@Path("/barrios")
public Response getAllBarrios() {
    List<Barrio> barrios = barrioDAO.getAll();
    List<BarrioDTO> barriosDTO = DTOMapper.toBarriosDTOList(barrios);
    return Response.ok(barriosDTO).build(); // ✅ Sin bucles
}
```

### 2. Mapeo Individual

```java
// Convertir una entidad a DTO
Barrio barrio = barrioDAO.getById(1L);
BarrioDTO barrioDTO = DTOMapper.toBarrioDTO(barrio);

// Convertir a DTO simple (para usar dentro de otros DTOs)
BarrioSimpleDTO barrioSimple = DTOMapper.toBarrioSimpleDTO(barrio);
```

### 3. Mapeo de Listas

```java
// Convertir lista de entidades a lista de DTOs
List<Zona> zonas = zonaDAO.getAll();
List<ZonaDTO> zonasDTO = DTOMapper.toZonasDTOList(zonas);
```

## Ventajas

1. **Sin bucles infinitos**: Las relaciones circulares se rompen usando DTOs simples
2. **Control de datos**: Solo expones la información que necesitas
3. **Mantenibilidad**: Separación clara entre modelo de datos y transferencia
4. **Performance**: Reduces la cantidad de datos transferidos
5. **Seguridad**: No expones campos sensibles como contraseñas

## Modificación de Controladores Existentes

Para implementar estos DTOs en tus controladores existentes:

1. **Importa las clases DTO necesarias**:
   ```java
   import dto.*;
   ```

2. **Reemplaza las respuestas de entidades por DTOs**:
   ```java
   // En lugar de retornar la entidad directamente
   return Response.ok(entidad).build();
   
   // Usa el mapper para convertir a DTO
   return Response.ok(DTOMapper.toEntidadDTO(entidad)).build();
   ```

3. **Para listas usa los métodos de mapeo de listas**:
   ```java
   List<BarrioDTO> barriosDTO = DTOMapper.toBarriosDTOList(barrios);
   ```

## Extensión

Si necesitas agregar nuevas entidades:

1. Crea el DTO completo con todas las propiedades
2. Crea el DTO simple con solo información básica  
3. Agrega los métodos de mapeo en `DTOMapper`
4. Usa DTOs simples para las relaciones en otros DTOs

## Notas Importantes

- Los DTOs simples se usan dentro de otros DTOs para evitar ciclos
- El `DTOMapper` maneja automáticamente los valores null
- Se mantiene la documentación Swagger en todos los DTOs
- Las fechas y tipos especiales se conservan correctamente
