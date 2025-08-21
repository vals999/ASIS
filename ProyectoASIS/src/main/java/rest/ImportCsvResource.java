package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.*;
import service.ImportCsvService;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@jakarta.ws.rs.Path("/import-csv")
public class ImportCsvResource {

    @Inject
    private ImportCsvService importCsvService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadCsv(@FormDataParam("file") InputStream uploadedInputStream,
                             @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            // Guardar temporalmente el archivo
            Path tempFile = Files.createTempFile("import_", ".csv");
            Files.copy(uploadedInputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Llamar al servicio de importación
            importCsvService.importar(tempFile.toString());

            // Borrar el archivo temporal
            Files.deleteIfExists(tempFile);

            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity("Error al importar el archivo: " + e.getMessage()).build();
        }
    }
}