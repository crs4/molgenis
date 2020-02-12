package org.molgenis.api.identities;

import com.google.auto.value.AutoValue;
import org.molgenis.data.security.auth.Role;

@AutoValue
@SuppressWarnings("java:S1610") // Abstract classes without fields should be converted to interfaces
public abstract class RoleResponse {
  public abstract String getRoleName();

  public abstract String getRoleLabel();

  static RoleResponse fromEntity(Role role) {
    return new AutoValue_RoleResponse(role.getName(), role.getLabel());
  }
}
