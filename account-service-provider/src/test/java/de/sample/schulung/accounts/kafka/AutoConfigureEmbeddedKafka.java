package de.sample.schulung.accounts.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Autoconfigures an {@link EmbeddedKafka}
 * and provides an extension to run, reset and stop the container.<br/>
 * We can get the following beans injected into our test class
 * <pre>
 * \u0040Autowired
 * EmbeddedKafkaBroker kafka;
 * \u0040Autowired
 * KafkaTestContext&lt;Key,Value&gt; context;
 * </pre>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// Kafka Configuration
@EmbeddedKafka(
  partitions = 1
)
@TestPropertySource(
  properties = """
    spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}
    # we disable this in production, but need this for the tests
    spring.kafka.producer.properties."[spring.json.add.type.headers]"=true
    application.kafka.customer-events-partitions=1
    application.kafka.customer-events-topic=test-customer-events
    """
)
@Import({
  AutoConfigureEmbeddedKafka.EmbeddedKafkaConfiguration.class,
  AutoConfigureEmbeddedKafka.KafkaMessageListenerContainerLifecycleHandler.class
})
@ExtendWith(AutoConfigureEmbeddedKafka.EmbeddedKafkaExtension.class)
public @interface AutoConfigureEmbeddedKafka {

  @RequiredArgsConstructor
  @Getter(AccessLevel.PRIVATE)
  class KafkaTestContext<K, V> {

    private final BlockingQueue<ConsumerRecord<K, V>> records;
    private final KafkaMessageListenerContainer<K, V> container;

    @SneakyThrows
    public Optional<ConsumerRecord<K, V>> poll(long timeout, TimeUnit unit) {
      return Optional.ofNullable(this.records.poll(timeout, unit));
    }

  }


  @TestConfiguration
  class EmbeddedKafkaConfiguration {

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    KafkaTestContext<?, ?> createKafkaTestContext(EmbeddedKafkaBroker kafka, List<NewTopic> topics) {
      final var consumerFactory = new DefaultKafkaConsumerFactory<>(
        Map.of(
          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokersAsString(),
          ConsumerConfig.GROUP_ID_CONFIG, "consumer",
          ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
          ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10",
          ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
          // not needed, but must not be null
          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class,
          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
          ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomJsonDeserializer.class.getName(),
          JsonDeserializer.TRUSTED_PACKAGES, "*",
          ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        )
      );
      final var containerProperties = new ContainerProperties(
        topics
          .stream()
          .map(NewTopic::name)
          .toArray(String[]::new)
      );
      final var container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
      final var records = new LinkedBlockingQueue<ConsumerRecord<Object, Object>>();
      container.setupMessageListener((MessageListener<?, ?>) records::add);
      return new KafkaTestContext<>(records, container);
    }

  }

  @Component
  @RequiredArgsConstructor
  class KafkaMessageListenerContainerLifecycleHandler {

    private final KafkaTestContext<?, ?> context;
    private final EmbeddedKafkaBroker kafka;

    @EventListener(ContextRefreshedEvent.class)
    public void startup() {
      context.getContainer().start();
      ContainerTestUtils.waitForAssignment(
        context.getContainer(),
        kafka.getPartitionsPerTopic()
      );
    }

    @EventListener(ContextClosedEvent.class)
    public void shutdown() {
      context.getContainer().stop();
    }

  }

  // we need to reset the records between the tests
  class EmbeddedKafkaExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
      SpringExtension
        .getApplicationContext(context)
        .getBean(KafkaTestContext.class)
        .getRecords()
        .clear();
    }
  }

}
