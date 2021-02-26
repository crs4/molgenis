package org.molgenis.api.ejprd.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.molgenis.api.ejprd.model.CatalogResponse;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.molgenis.api.ejprd.model.ExternalResourceRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "/external_sources")
public interface ExternalSourcesController {
  @ApiOperation(
      value = "",
      nickname = "getExternalResources",
      notes =
          "Perform a REST request to the source with value 'sourceId' to get Resources corresponding to the query parameters",
      response = DataResponse.class,
      responseContainer = "List",
      tags = {})
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "successful operation",
            response = CatalogResponse.class,
            responseContainer = "List"),
        @ApiResponse(
            code = 400,
            message = "Bad request (e.g. missing mandatory parameter)",
            response = ErrorResponse.class),
        @ApiResponse(
            code = 401,
            message =
                "Unauthorised (e.g. when an unauthenticated user tries to access a protected resource)",
            response = ErrorResponse.class),
        @ApiResponse(
            code = 403,
            message =
                "Forbidden (e.g. the resource is protected for all users or the user is authenticated but s/he is not granted for this resource)",
            response = ErrorResponse.class),
        @ApiResponse(
            code = 404,
            message = "When the source is is not found",
            response = ErrorResponse.class),
      })
  @RequestMapping(
      value = "/external_resources/{sourceId}",
      produces = {"application/json"},
      method = RequestMethod.GET)
  CatalogResponse getExternalResource(
      @PathVariable("sourceId") String sourceId, @Valid ExternalResourceRequest request);
}
