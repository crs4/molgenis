package org.molgenis.metrics;

import static java.util.Objects.requireNonNull;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterBindersConfiguration {

  private final HikariDataSource dataSource;
  private MeterRegistry meterRegistry;

  public MeterBindersConfiguration(MeterRegistry meterRegistry, HikariDataSource dataSource) {
    this.meterRegistry = requireNonNull(meterRegistry);
    this.dataSource = requireNonNull(dataSource);
  }

  @Bean
  public ClassLoaderMetrics classLoaderMetrics() {
    return new ClassLoaderMetrics();
  }

  @Bean
  public JvmMemoryMetrics jvmMemoryMetrics() {
    return new JvmMemoryMetrics();
  }

  @Bean
  public JvmGcMetrics jvmGcMetrics() {
    return new JvmGcMetrics();
  }

  @Bean
  public ProcessorMetrics processorMetrics() {
    return new ProcessorMetrics();
  }

  @Bean
  public JvmThreadMetrics jvmThreadMetrics() {
    return new JvmThreadMetrics();
  }

  @Bean
  LogbackMetrics logbackMetrics() {
    return new LogbackMetrics();
  }

  @Bean
  HikariMetrics hikariMetrics() {
    return new HikariMetrics(dataSource);
  }

  @PostConstruct
  private void registerBeans() {
    classLoaderMetrics().bindTo(meterRegistry);
    jvmMemoryMetrics().bindTo(meterRegistry);
    jvmGcMetrics().bindTo(meterRegistry);
    processorMetrics().bindTo(meterRegistry);
    jvmThreadMetrics().bindTo(meterRegistry);
    logbackMetrics().bindTo(meterRegistry);
    hikariMetrics().bindTo(meterRegistry);
  }
}
