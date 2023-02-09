package it.snapoli.services.insurance.config;

import lombok.extern.jbosslog.JBossLog;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;

@JBossLog
public class ErrorPageResponseExceptionMapper  {

  @ServerExceptionMapper
  public RestResponse<String> mapException(Exception x) {
    log.warn(x.getMessage(), x);
    return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "error: " + x.getMessage());
  }

}
