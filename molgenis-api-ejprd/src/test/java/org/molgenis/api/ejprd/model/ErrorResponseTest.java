package org.molgenis.api.ejprd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

  @Test
  public void testErrorResponseComplete() {

    ErrorResponse errorResponse = ErrorResponse.create(100, "This is Error 100");

    assertEquals(errorResponse.getCode(), 100);
    assertEquals(errorResponse.getMessage(), "This is Error 100");
  }

  @Test
  public void testErrorResponseMissingMandatoryFields() {

    assertThrows(
        NullPointerException.class,
        () -> {
          ErrorResponse.create(100, null);
        });

    assertThrows(
        NullPointerException.class,
        () -> {
          ErrorResponse.create(null, "This is error 100");
        });
  }

  @Test
  public void testFromJsonComplete() {

    String jsonError = "{" + "  \"code\": 100," + "  \"message\": \"This is Error 100\" " + "}";

    ErrorResponse errorResponse = ErrorResponse.fromJson(jsonError);
    assertEquals(errorResponse.getCode(), 100);
    assertEquals(errorResponse.getMessage(), "This is Error 100");
  }

  @Test
  public void testFromJsonCompleteMissingCode() {

    String jsonError = "{" + "  \"message\": \"This is Error 100\" " + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          ErrorResponse.fromJson(jsonError);
        });
  }

  @Test
  public void testFromJsonCompleteMissingMessage() {

    String jsonError = "{" + "  \"code\": 100" + "}";
    assertThrows(
        NullPointerException.class,
        () -> {
          ErrorResponse.fromJson(jsonError);
        });
  }
}
