package org.molgenis.api.ejprd.validators;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {OrphaCodeOrNameNotNullValidator.class})
public @interface OrphaCodeOrNameNotNull {

  String message() default
      "At least one parameter between orphaCode and Name is mandatory, and noone of both can be empty";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
