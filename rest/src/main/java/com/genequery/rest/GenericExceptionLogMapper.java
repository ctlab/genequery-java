package com.genequery.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionLogMapper implements ExceptionMapper<Throwable> {
  private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionLogMapper.class);

  public Response toResponse(Throwable t) {
    LOG.error("Uncaught exception thrown by REST service", t);

    if (t instanceof BadRequestException) {
      return Response.status(Response.Status.BAD_REQUEST).entity(t.getMessage()).build();
    } else if (t instanceof WebApplicationException) {
      return ((WebApplicationException)t).getResponse();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}