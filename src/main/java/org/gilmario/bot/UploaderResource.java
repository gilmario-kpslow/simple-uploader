package org.gilmario.bot;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("uploader")
@RequestScoped
@RolesAllowed({"ADMIN"})
public class UploaderResource {

    @Inject
    VersaoService versaoService;

    @POST
    @Path("upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem uploadFile(UploadRequest request) throws IOException {
        return versaoService.upload(request);
    }

    @GET
    @Path("versao")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem gerarVersao() throws IOException {
        return versaoService.gerarVersao();
    }

    @GET
    @Path("versao/{versao}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<PathObject> listarArquivosVersao(@PathParam("versao") String versao) throws IOException {
        return versaoService.listarArquivosVersoes(versao);
    }

    @DELETE
    @Path("versao/{versao}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem delete(@PathParam("versao") String versao) throws IOException {
        return versaoService.deletarVersao(versao);
    }

    @DELETE
    @Path("versao/{versao}/{fileName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem deleteFile(@PathParam("versao") String versao, @PathParam("fileName") String fileName) throws IOException {
        return versaoService.deletarArquivoVersao(versao, fileName);
    }

    @PUT
    @Path("publicar/{versao}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Mensagem publicar(@PathParam("versao") String versao) throws IOException {
        return versaoService.publicarVersao(versao);
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
