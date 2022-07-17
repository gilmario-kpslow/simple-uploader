package org.gilmario.bot;

import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author gilmario
 */
@RequestScoped
@Startup
public class VersaoService {

    @ConfigProperty(name = "paths.base", defaultValue = "versoes")
    private String BASE;
    @ConfigProperty(name = "paths.site", defaultValue = "site")
    private String PUBLICAR;
    @Inject
    protected JsonWebToken jwt;

    @PostConstruct
    public void teste() {
        System.err.println(BASE);
        System.err.println(PUBLICAR);
    }

    public Mensagem upload(UploadRequest request) throws IOException {
        Files.write(Paths.get(BASE, request.getVersao(), request.getNome()), Base64.getDecoder().decode(request.getConteudo()));
        return new Mensagem("Upload realizado com sucesso", "", "SUCESSO");
    }

    public Mensagem gerarVersao() throws IOException {
        Long total = Files.list(Paths.get(BASE)).count();
        String versao = ("Versao-" + (total + 1L));
        Files.createDirectories(Paths.get(BASE, versao));
        return new Mensagem("Upload realizado com sucesso", versao, "SUCESSO");
    }

    public List<PathObject> listarArquivosVersoes(String versao) throws IOException {
        return Files.list(Paths.get(BASE, versao)).map(f -> {
            PathObject list = new PathObject();
            list.setNome(f.getFileName().toString());
            list.setTipo(Files.isDirectory(f) ? Tipo.PASTA : Tipo.ARQUIVO);
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
        return new Mensagem("Versão excluída", "", "SUCESSO");
    }

    public Mensagem deletarArquivoVersao(String versao, String fileName) throws IOException {
        Files.delete(Paths.get(BASE, versao, fileName));
        return new Mensagem("Arquivo excluído", "", "SUCESSO");
    }

    public Mensagem publicarVersao(String versao) throws IOException {

        Path fromPath = Paths.get(BASE, versao);
        Path destPath = Paths.get(PUBLICAR);
        Files.list(destPath).forEach(f -> {
            try {
                Files.deleteIfExists(f);
            } catch (IOException ex) {
                Logger.getLogger(UploaderResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

//        Files.copy(fromPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        Files.walk(fromPath).forEach(a -> {
            Path b = Paths.get(PUBLICAR, a.toString().substring(fromPath.toString().length()));
            try {
                if (!a.toString().equals(fromPath.toString())) {
                    Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return new Mensagem("Publicado com sucesso", "", "SUCESSO");
    }

    private void copySourceToDest(Path fromPath, Path source) throws IOException {
        Path destination = Paths.get(PUBLICAR, source.toString().substring(fromPath.toString().length()));
        Files.copy(fromPath, destination);

    }

    public List<PathObject> listarArquivos(Optional<String> arvore) throws IOException {
        return Files.list(Paths.get(BASE, arvore.orElse(""))).map(f -> {
            PathObject list = new PathObject();
            list.setNome(f.getFileName().toString());
            list.setTipo(f.toFile().isDirectory() ? Tipo.PASTA : Tipo.ARQUIVO);
            return list;
        }).toList();
    }

    public Mensagem info(SecurityContext ctx) {
        String secureInfo = getResponseString(ctx);
        return new Mensagem("Sistema no ARR", secureInfo, "SUCESSO");
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
