package org.gilmario.bot.upload;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.gilmario.bot.Mensagem;
import org.gilmario.bot.arquivo.PathObject;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("uploader")
@RequestScoped
@RolesAllowed({"ADMIN"})
public class UploaderResource {

    @Inject
    protected VersaoService versaoService;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fileUpload(@MultipartForm MultipartFormDataInput input) throws Exception {
        return Response.ok().
                entity(versaoService.upload(input)).build();
    }

    @GET
    @Path("download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] fileUpload(@QueryParam("filename") String fileName) throws Exception {
        return versaoService.download(fileName);
    }

    @GET
    @Path("arquivos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<PathObject> listarArquivos() throws IOException {
        return versaoService.listarArquivos();
    }

    @DELETE
    @Path("arquivo/{fileName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem deleteFile(@PathParam("fileName") String fileName) throws IOException {
        return versaoService.deletarArquivo(fileName);
    }

    @GET
    @Path("listar")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PathObject> listFiles(@QueryParam(value = "arvore") Optional<String> arvore) throws IOException {
        return versaoService.listarArquivos(arvore);
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Mensagem info(@Context SecurityContext ctx) {
        return versaoService.info(ctx);
    }

}
