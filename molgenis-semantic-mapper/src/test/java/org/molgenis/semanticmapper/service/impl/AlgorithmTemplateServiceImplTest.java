package org.molgenis.semanticmapper.service.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.script.core.ScriptMetadata.SCRIPT;
import static org.molgenis.script.core.ScriptMetadata.TYPE;
import static org.molgenis.script.core.ScriptParameterMetadata.SCRIPT_PARAMETER;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.data.AbstractMolgenisSpringTest;
import org.molgenis.data.DataService;
import org.molgenis.data.Query;
import org.molgenis.data.file.FileStore;
import org.molgenis.data.meta.model.Attribute;
import org.molgenis.data.meta.model.AttributeFactory;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.meta.model.EntityTypeFactory;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.js.magma.JsMagmaScriptRunner;
import org.molgenis.script.core.Script;
import org.molgenis.script.core.ScriptFactory;
import org.molgenis.script.core.ScriptMetadata;
import org.molgenis.script.core.ScriptParameter;
import org.molgenis.script.core.ScriptParameterFactory;
import org.molgenis.script.core.config.ScriptTestConfig;
import org.molgenis.security.core.token.TokenService;
import org.molgenis.semanticsearch.explain.bean.ExplainedAttribute;
import org.molgenis.semanticsearch.explain.bean.ExplainedQueryString;
import org.molgenis.semanticsearch.semantic.Hit;
import org.molgenis.semanticsearch.semantic.Hits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = AlgorithmTemplateServiceImplTest.Config.class)
public class AlgorithmTemplateServiceImplTest extends AbstractMolgenisSpringTest {
  @Autowired private ScriptFactory scriptFactory;

  @Autowired private ScriptParameterFactory scriptParameterFactory;

  @Autowired private EntityTypeFactory entityTypeFactory;

  @Autowired private AttributeFactory attrMetaFactory;

  @Autowired private AlgorithmTemplateServiceImpl algorithmTemplateServiceImpl;

  @Autowired private DataService dataService;

  private Script script0;
  private String param0Name = "param0", param1Name = "param1";

  @BeforeEach
  public void setUpBeforeMethod() {
    ScriptParameter param0 = scriptParameterFactory.create();
    param0.setName(param0Name);

    ScriptParameter param1 = scriptParameterFactory.create();
    param1.setName(param1Name);

    script0 = scriptFactory.create();
    script0.setName("name");
    script0.setContent(String.format("$('%s'),$('%s')", param0, param1));
    script0.set(ScriptMetadata.PARAMETERS, asList(param0, param1));

    Query<Script> q = new QueryImpl<Script>().eq(TYPE, JsMagmaScriptRunner.NAME);
    when(dataService.findAll(SCRIPT, q, Script.class)).thenReturn(Stream.of(script0));
    when(dataService.findOneById(SCRIPT_PARAMETER, param0Name)).thenReturn(param0);
    when(dataService.findOneById(SCRIPT_PARAMETER, param1Name)).thenReturn(param1);
  }

  @Test
  public void find() {
    String sourceAttr0Name = "sourceAttr0";
    String sourceAttr1Name = "sourceAttr1";
    EntityType sourceEntityMeta = entityTypeFactory.create("source");
    Attribute sourceAttr0 = attrMetaFactory.create().setName(sourceAttr0Name);
    Attribute sourceAttr1 = attrMetaFactory.create().setName(sourceAttr1Name);
    sourceEntityMeta.addAttribute(sourceAttr0);
    sourceEntityMeta.addAttribute(sourceAttr1);
    ExplainedQueryString sourceAttr0Explain =
        ExplainedQueryString.create("a", "b", param0Name, 1.0);
    ExplainedQueryString sourceAttr1Explain =
        ExplainedQueryString.create("a", "b", param1Name, 0.5);
    Hits<ExplainedAttribute> attrResults =
        Hits.create(
            Hit.create(
                ExplainedAttribute.create(sourceAttr0, singleton(sourceAttr0Explain), false), 1f),
            Hit.create(
                ExplainedAttribute.create(sourceAttr1, singleton(sourceAttr1Explain), false), 1f));

    Stream<AlgorithmTemplate> templateStream = algorithmTemplateServiceImpl.find(attrResults);

    Map<String, String> model = Maps.newHashMap();
    model.put(param0Name, sourceAttr0Name);
    model.put(param1Name, sourceAttr1Name);
    AlgorithmTemplate expectedAlgorithmTemplate = new AlgorithmTemplate(script0, model);
    assertEquals(of(expectedAlgorithmTemplate).collect(toList()), templateStream.collect(toList()));
  }

  @Configuration
  @Import(ScriptTestConfig.class)
  public static class Config {
    @Autowired private DataService dataService;

    @Bean
    public FileStore fileStore() {
      return mock(FileStore.class);
    }

    @Bean
    public TokenService tokenService() {
      return mock(TokenService.class);
    }

    @Bean
    public AlgorithmTemplateServiceImpl algorithmTemplateServiceImpl() {
      return new AlgorithmTemplateServiceImpl(dataService);
    }
  }
}
