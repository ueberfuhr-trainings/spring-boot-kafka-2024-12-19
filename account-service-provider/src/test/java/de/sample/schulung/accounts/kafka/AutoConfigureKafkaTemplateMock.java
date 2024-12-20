package de.sample.schulung.accounts.kafka;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto-configures a {@link KafkaTemplate} mock in the test context.
 * You can get the mock injected by simply using
 * <pre>
 * \u0040Autowired
 * KafkaTemplate&lt;String, CustomerDto&gt; templateMock;
 * </pre>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@MockBean(KafkaTemplate.class)
public @interface AutoConfigureKafkaTemplateMock {

}
