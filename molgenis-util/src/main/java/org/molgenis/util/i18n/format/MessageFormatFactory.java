package org.molgenis.util.i18n.format;

import com.google.common.collect.ImmutableMap;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.apache.commons.lang3.text.FormatFactory;

public class MessageFormatFactory {
  public static final String LABEL = "label";
  public static final String ID = "id";

  private Map<String, FormatFactory> formatRegistry =
      ImmutableMap.of(LABEL, new LabelFormatFactory(), ID, new IdFormatFactory());

  public MessageFormat createMessageFormat(String msg, Locale locale) {
    return new ExtendedMessageFormat((msg != null ? msg : ""), locale, formatRegistry);
  }
}
