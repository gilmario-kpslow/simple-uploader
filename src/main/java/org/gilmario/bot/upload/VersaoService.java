package org.gilmario.bot.upload;

import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.gilmario.bot.Mensagem;
import org.gilmario.bot.arquivo.PathObject;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author gilmario
 */
@RequestScoped
@Startup
public class VersaoService {

    @ConfigProperty(name = "paths.arquivo")
    private String BASE;
    @Inject
    protected JsonWebToken jwt;

    Mensagem upload(MultipartFormDataInput input) throws IOException {

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        List<InputPart> inputParts = uploadForm.get("file");

        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> header
                    = inputPart.getHeaders();
            String fileName = getFileName(header);
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Files.write(Paths.get(BASE, fileName), bytes);
        }
        return new Mensagem("Upload realizado com sucesso");
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.
                getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }

    public List<PathObject> listarArquivos() throws IOException {
        return Files.list(Paths.get(BASE)).map(f -> {
            PathObject list = new PathObject();
            list.setNome(f.getFileName().toString());
            list.setDiretorio(Files.isDirectory(f));
            list.setTamanho(f.toFile().length());
            return list;
        }).toList();
    }

    public Mensagem deletarArquivo(String fileName) throws IOException {
        Files.delete(Paths.get(BASE, fileName));
        return new Mensagem("Arquivo excluído", "");
    }

    public List<PathObject> listarArquivos(Optional<String> arvore) throws IOException {
        return Files.list(Paths.get(BASE, arvore.orElse(""))).map(f -> {
            PathObject list = new PathObject();
            list.setNome(f.getFileName().toString());
            list.setDiretorio(Files.isDirectory(f));
            try {
                list.setTamanho(Files.size(f));
                list.setUltimaModificacao(LocalDateTime.ofInstant(Files.getLastModifiedTime(f).toInstant(), ZoneId.systemDefault()));
            } catch (IOException ex) {
                Logger.getLogger(VersaoService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return list;
        }).toList();
    }

    public Mensagem info(SecurityContext ctx) {
        String secureInfo = getResponseString(ctx);
        return new Mensagem("Sistema no ar", secureInfo);
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello + %s,"
                + " isHttps: %s,"
                + " authScheme: %s,"
                + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }

    byte[] download(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(BASE, fileName));
    }

}
