// ReporteController.java
package controller;

import java.util.List;
import dao_interfaces.I_ReporteDAO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import model.Reporte;

@Path("/reportes")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reportes", description = "Operaciones ABML para la gestión de reportes")
public class ReporteController {

    @Inject
    private I_ReporteDAO reporteDAO;

    @GET
    public Response obtenerTodosLosReportes() {
        try {
            List<Reporte> reportes = reporteDAO.obtenerTodos();
            return Response.ok(reportes).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/activos")
    public Response obtenerReportesActivos() {
        try {
            List<Reporte> reportes = reporteDAO.obtenerNoBorrados();
            return Response.ok(reportes).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerReportePorId(@PathParam("id") Long id) {
        try {
            Reporte reporte = reporteDAO.obtenerPorId(id);
            if (reporte == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(reporte).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    public Response crearReporte(Reporte reporte) {
        try {
            reporteDAO.crear(reporte);
            return Response.status(Status.CREATED).entity(reporte).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizarReporte(@PathParam("id") Long id, Reporte reporte) {
        try {
            reporte.setId(id);
            reporteDAO.actualizar(reporte);
            return Response.ok(reporte).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarReporte(@PathParam("id") Long id) {
        try {
            reporteDAO.eliminar(id);
            return Response.ok().entity("Reporte eliminado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/recuperar")
    public Response recuperarReporte(@PathParam("id") Long id) {
        try {
            reporteDAO.recuperar(id);
            return Response.ok().entity("Reporte recuperado correctamente").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}