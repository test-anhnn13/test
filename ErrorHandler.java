/**
 * Created by hung on 8/6/2015.
 */

import play.*;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.i18n.Messages;
import play.libs.F.*;
import play.mvc.Http.*;
import play.mvc.*;
import services.LogService;
import views.html.errors.*;

import javax.inject.*;

public class ErrorHandler extends DefaultHttpErrorHandler {
    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    protected Promise<Result> onProdServerError(RequestHeader requestHeader, UsefulException exception) {
        String err = String.format("Error on server: client info: %s ", LogService.getClientInfo(requestHeader));
        LogService.logger.error(err, exception);
        return Promise.<Result>pure(Results.internalServerError(client_error.render(Messages.get("message_server_error"))));
    }

    @Override
    public Promise<Result> onServerError(RequestHeader requestHeader, Throwable throwable) {
        String err = String.format("Error on server: client info: %s ", LogService.getClientInfo(requestHeader));
        LogService.logger.error(err, throwable);
        return Promise.<Result>pure(Results.internalServerError(client_error.render(Messages.get("message_server_error"))));
    }

    protected Promise<Result> onForbidden(RequestHeader requestHeader, String message) {
        String err = String.format("Forbidden: %s client info: %s ", message, LogService.getClientInfo(requestHeader));
        LogService.logger.error(err);
        return Promise.<Result>pure(Results.forbidden("You're not allowed to access this resource."));
    }

    @Override
    protected Promise<Result> onBadRequest(RequestHeader requestHeader, String message) {
        String err = String.format("Bad Request: %s client info: %s ", message, LogService.getClientInfo(requestHeader));
        LogService.logger.error(err);
        return Promise.<Result>pure(Results.badRequest(client_error.render(Messages.get("message_client_error"))));
    }

    @Override
    protected Promise<Result> onNotFound(RequestHeader requestHeader, String message) {
        String err = String.format("Resource Not Found: %s client info: %s ", message, LogService.getClientInfo(requestHeader));
        LogService.logger.error(err);
        return Promise.<Result>pure(Results.notFound(client_error.render(Messages.get("message_resource_not_found_error"))));
    }


    /*Returns all clent errors inform of 4xx code
    * */
    @Override
    public Promise<Result> onClientError(RequestHeader requestHeader, int statusCode, String message) {
        String err = String.format("Client Error [%s]: %s client info: %s ", statusCode, message, LogService.getClientInfo(requestHeader));
        LogService.logger.error(err);
        return Promise.<Result>pure(Results.status(statusCode, client_error.render(Messages.get("message_client_error"))));
    }
}
