package org.molgenis.api.ejprd.validators;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.molgenis.api.ejprd.model.InternalResourceRequest;

public class OrphaCodeOrNameNotNullValidator
    implements ConstraintValidator<OrphaCodeOrNameNotNull, InternalResourceRequest> {

  @Override
  public void initialize(OrphaCodeOrNameNotNull orphaCodeOrNameNotNullValid) {}

  @Override
  public boolean isValid(InternalResourceRequest request, ConstraintValidatorContext context) {

    List<String> orphaCode = request.getOrphaCode();
    String name = request.getName();

    if (orphaCode == null && name == null) {
      return false;
    } else if (orphaCode != null && name != null) {
      if (!orphaCode.isEmpty()) {
        return !name.isEmpty();
      } else {
        return false;
      }
    } else if (orphaCode != null) {
      return !orphaCode.isEmpty();
    } else {
      return !name.isEmpty();
    }
  }
}
