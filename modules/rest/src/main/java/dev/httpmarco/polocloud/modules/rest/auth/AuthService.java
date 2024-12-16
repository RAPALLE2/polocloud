package dev.httpmarco.polocloud.modules.rest.auth;

import dev.httpmarco.polocloud.modules.rest.RestModule;
import dev.httpmarco.polocloud.modules.rest.auth.user.User;
import dev.httpmarco.polocloud.modules.rest.controller.ControllerService;
import dev.httpmarco.polocloud.modules.rest.controller.methods.RequestMethodData;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@AllArgsConstructor
public class AuthService {

    private final RestModule restModule;
    private final RequestMethodData requestMethodData;

    public void handle(Context context) {
        if (!isUserCreationAllowed(context)) {

            if (!isLogin(context)) {

                var user = userByContext(context);
                if (user == null) {
                    context.status(401).result("Unauthorized");
                    return;
                }

                if (!isPermitted(user)) {
                    context.status(403).result("Forbidden");
                    return;
                }
            }
        }

        this.restModule.controllerService().processRequest(requestMethodData.method(), requestMethodData.controller(), context);
    }

    private boolean isLogin(Context context) {
        return context.path().replaceAll("/$", "").equals(ControllerService.API_PATH + "/auth/login");
    }

    private boolean isUserCreationAllowed(Context context) {
        return context.path().replaceAll("/$", "").equals(ControllerService.API_PATH + "/user") && context.method().equals(HandlerType.POST)
                && this.restModule.config().usersConfiguration().users().isEmpty();
    }

    private User userByContext(Context context) {
        var decodedToken = Optional.ofNullable(context.cookie("token")).flatMap(this.restModule.jwtProvider().provider()::validateToken);

        if (decodedToken.isEmpty()) {
            context.status(401).result("Missing or invalid token");
            return null;
        }

        var token = decodedToken.get();
        var uuid = token.getClaim("uuid").asString();

        return this.restModule.userService().userByUUID(UUID.fromString(uuid));
    }

    private boolean isPermitted(User user) {
        return !requestMethodData.permission().isEmpty() || user.hasPermission(requestMethodData.permission());
    }

}