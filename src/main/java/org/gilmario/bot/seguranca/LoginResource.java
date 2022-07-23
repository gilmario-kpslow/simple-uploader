package org.gilmario.bot.seguranca;

import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

/**
 *
 * @author gilmario
 */
@Path("login")
public class LoginResource {

    @ConfigProperty(name = "login.username", defaultValue = "upadmin")
    private String defaulUsername;
    @ConfigProperty(name = "login.password", defaultValue = "123456789")
    private String defaulPassword;

    @POST
    @Produces(value = MediaType.TEXT_PLAIN)
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    public Response getToken(@FormParam("username") String username, @FormParam("password") String password) {

        if (!Objects.equals(username, defaulUsername) || !Objects.equals(password, defaulPassword)) {
            return Response.status(403).entity("Username OR Passsword is incorrect").build();
        }

        return Response.status(200).entity(Jwt.issuer("http://localhost")
                .upn("jdoe@quarkus.io")
                .groups(new HashSet<>(Arrays.asList("ADMIN")))
                .claim(Claims.full_name.name(), "Admin")
                .expiresIn(Duration.ofDays(1))
                .sign()).build();

    }
}
