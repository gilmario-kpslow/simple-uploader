package org.gilmario.bot.upload;

import org.gilmario.bot.arquivo.PathObject;
import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.gilmario.bot.Mensagem;
import org.gilmario.bot.TipoMessagem;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author gilmario
 */
@RequestScoped
@Startup
public class VersaoService {

    @ConfigProperty(name = "paths.base", defaultValue = "versoes")
    private String BASE;
    @ConfigProperty(name = "paths.web", defaultValue = "web")
    private String WEB;
    @ConfigProperty(name = "paths.mobile", defaultValue = "mobile")
    private String MOBILE;
    @Inject
    protected JsonWebToken jwt;

    Mensagem upload(MultipartFormDataInput input, String versao) throws IOException {

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
//        List<String> fileNames = new ArrayList<>();
        List<InputPart> inputParts = uploadForm.get("file");
//        String fileName = null;
        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> header
                    = inputPart.getHeaders();
            String fileName = getFileName(header);
//            fileNames.add(fileName);
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Files.write(Paths.get(BASE, versao, fileName), bytes);
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

    public Mensagem gerarVersao(String tipo) throws IOException {
        Long total = Files.list(Paths.get(BASE)).count();
        String versao = ("Versao-" + (total + 1L) + "-" + tipo.toLowerCase());
        Files.createDirectories(Paths.get(BASE, versao));
        return new Mensagem("Upload realizado com sucesso", versao, TipoMessagem.SUCCESS);
    }

    public List<PathObject> listarArquivosVersoes(String versao) throws IOException {
        return Files.list(Paths.get(BASE, versao)).map(f -> {
            PathObject list = new PathObject();
            list.setNome(f.getFileName().toString());
            list.setDiretorio(Files.isDirectory(f));
            return list;
        }).toList();
    }

    public Mensagem deletarVersao(String versao) throws IOException {
        Files.list(Paths.get(BASE, versao)).forEach(f -> {
            try {
                Files.deleteIfExists(f);
            } catch (IOException ex) {
                Logger.getLogger(UploaderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        Files.delete(Paths.get(BASE, versao));
        return new Mensagem("Versão excluída", "", TipoMessagem.SUCCESS);
    }

    public Mensagem deletarArquivoVersao(String versao, String fileName) throws IOException {
        Files.delete(Paths.get(BASE, versao, fileName));
        return new Mensagem("Arquivo excluído", "");
    }

    public Mensagem publicarVersao(String versao) throws IOException {

        String publicar = versao.toLowerCase().contains("web") ? WEB : MOBILE;

        Path fromPath = Paths.get(BASE, versao);
        Path destPath = Paths.get(publicar);
        Files.list(destPath).forEach(f -> {
            try {
                Files.deleteIfExists(f);
            } catch (IOException ex) {
                Logger.getLogger(UploaderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Files.walk(fromPath).forEach(a -> {
            Path b = Paths.get(publicar, a.toString().substring(fromPath.toString().length()));
            try {
                if (!a.toString().equals(fromPath.toString())) {
                    Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return new Mensagem("Publicado com sucesso");
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

}
