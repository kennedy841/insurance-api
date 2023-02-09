package it.snapoli.services.insurance.config;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErrorPageResponseExceptionMapper implements ExceptionMapper<Exception> {

  @Inject
  javax.inject.Provider<ContainerRequestContext> containerRequestContextProvider;

  @Inject
  Logger logger;

  @Override
  public Response toResponse(Exception exception) {
    Response errorResponse = mapExceptionToResponse(exception);

    // Modify error response...

    return errorResponse;
  }

  private Response mapExceptionToResponse(Exception exception) {
    // Use response from WebApplicationException as they are
    if (exception instanceof WebApplicationException) {
      // Overwrite error message
      logger.warn(exception.getMessage());
      Response originalErrorResponse = ((WebApplicationException) exception).getResponse();
      return Response.fromResponse(originalErrorResponse)
          .entity(exception.getMessage())
          .build();
    }
    // Special mappings
    else if (exception instanceof IllegalArgumentException) {
      logger.warn(exception.getMessage());
      return Response.status(400).entity(exception.getMessage()).build();
    }
    // Use 500 (Internal Server Error) for all other
    else {
      logger.error(exception.getMessage(), exception);
      return Response.serverError().entity("Internal Server Error").build();
    }
  }
}
