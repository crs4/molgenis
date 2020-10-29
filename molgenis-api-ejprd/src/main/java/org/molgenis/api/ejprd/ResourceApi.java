package org.molgenis.api.ejprd;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.molgenis.api.ejprd.model.InternalResourceRequest;
import org.molgenis.api.ejprd.model.ResourceRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "resource")
public interface ResourceApi {

  @ApiOperation(
      value = "",
      nickname = "getResourceRequest",
      notes = "Get response to a query for a certain rare disease (resources).",
      response = DataResponse.class,
      responseContainer = "List",
      tags = {})
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "successful operation",
            response = DataResponse.class,
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
        @ApiResponse(code = 404, message = "not found", response = ErrorResponse.class),
        @ApiResponse(code = 200, message = "unexpected error", response = ErrorResponse.class)
      })
  @RequestMapping(
      value = "/resource",
      produces = {"application/json"},
      method = RequestMethod.GET)
  DataResponse getResourceRequest(@Valid InternalResourceRequest resourceRequest);
}
