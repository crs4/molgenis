package org.molgenis.api.identities;

import com.google.auto.value.AutoValue;
import org.molgenis.util.AutoGson;

@AutoValue
@AutoGson(autoValueClass = AutoValue_UpdateGroupMemberCommand.class)
@SuppressWarnings("java:S1610")
public abstract class UpdateGroupMemberCommand {
  public abstract String getRoleName();

  public static UpdateGroupMemberCommand updateGroupMember(String roleName) {
    return new AutoValue_UpdateGroupMemberCommand(roleName);
  }
}
