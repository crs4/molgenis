package org.molgenis.api.ejprd.validators;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import org.molgenis.api.ejprd.model.InternalResourceRequest;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {OrphaCodeOrNameNotNullValidator.class})
public @interface OrphaCodeOrNameNotNull {

  String message() default "At least one parameter between orphaCode and Name is mandatory";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

class OrphaCodeOrNameNotNullValidator
    implements ConstraintValidator<OrphaCodeOrNameNotNull, InternalResourceRequest> {

  @Override
  public void initialize(OrphaCodeOrNameNotNull orphaCodeOrNameNotNullValid) {}

  @Override
  public boolean isValid(InternalResourceRequest request, ConstraintValidatorContext context) {

    List<String> orphaCode = request.getOrphaCode();
    String name = request.getName();

    return orphaCode != null || name != null;
  }
}
