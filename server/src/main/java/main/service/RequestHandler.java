package main.service;

import network.CommandType;
import network.Request;
import network.Response;
import main.collectionManager.Invoker;
import main.db.ConnectionManager;
import main.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final AuthService authService;
    private final Invoker invoker;

    public RequestHandler(AuthService authService, Invoker invoker) {
        this.authService = authService;
        this.invoker = invoker;
    }

    public Response handle(Request request) {
        try {
            if (request.getCommandType() == CommandType.REGISTER) {
                logger.info("Register request for login: {}", request.getLogin());
                User user = authService.register(request.getLogin(), request.getPassword());
                return new Response("User registered: " + user.getLogin());
            }

            logger.info("Authenticating request for login: {}", request.getLogin());
            Optional<User> user = authService.authenticate(
                    request.getLogin(),
                    request.getPassword()
            );

            if (user.isEmpty()) {
                return new Response("Authorization failed");
            }

            String result = invoker.execute(request, user.get());
            return new Response(result);
        } catch (IllegalArgumentException e) {
            return new Response("Error: " +  e.getMessage());
        } catch (SQLException e) {
            return new Response(ConnectionManager.getDatabaseErrorMessage(e));
        } catch (Exception e) {
            return new Response("Server error: " +  e.getMessage());
        }
    }
}
