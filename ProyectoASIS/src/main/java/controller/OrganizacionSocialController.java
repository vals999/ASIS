// OrganizacionSocialController.java
package controller;

import java.util.List;
import dao_interfaces.I_OrganizacionSocialDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.OrganizacionSocial;

@Path("/organizaciones-sociales")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Organizaciones Sociales", description = "Operaciones ABML para la gestión de organizaciones sociales")
public class OrganizacionSocialController {

    @Inject
    private I_OrganizacionSocialDAO organizacionSocialDAO;

    @GET
    public Response obtenerTodasLasOrganizaciones() {
        try {
            List<OrganizacionSocial> organizaciones = organizacionSocialDAO.obtenerTodos();
            return Response.ok(organizaciones).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activas")
    public Response obtenerOrganizacionesActivas() {
        try {
            List<OrganizacionSocial> organizaciones = organizacionSocialDAO.obtenerNoBorrados();
            return Response.ok(organizaciones).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerOrganizacionPorId(@PathParam("id") Long id) {
        try {
            OrganizacionSocial organizacion = organizacionSocialDAO.obtenerPorId(id);
            if (organizacion == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(organizacion).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearOrganizacion(OrganizacionSocial organizacion) {
        try {
            organizacionSocialDAO.crear(organizacion);
            return Response.status(Status.CREATED).entity(organizacion).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarOrganizacion(@PathParam("id") Long id, OrganizacionSocial organizacion) {
        try {
            organizacion.setId(id);
            organizacionSocialDAO.actualizar(organizacion);
            return Response.ok(organizacion).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarOrganizacion(@PathParam("id") Long id) {
        try {
            organizacionSocialDAO.eliminar(id);
            return Response.ok().entity("Organización social eliminada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarOrganizacion(@PathParam("id") Long id) {
        try {
            organizacionSocialDAO.recuperar(id);
            return Response.ok().entity("Organización social recuperada correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}