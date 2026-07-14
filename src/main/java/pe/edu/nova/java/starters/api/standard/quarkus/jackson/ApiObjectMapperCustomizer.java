package pe.edu.nova.java.starters.api.standard.quarkus.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.inject.Singleton;

/**
 * SmallRye {@link ObjectMapperCustomizer} que configura el {@link ObjectMapper}
 * de Quarkus para serializar correctamente los tipos de {@code nova-java-api-standard}.
 * <p>
 * Aplicado:
 * <ul>
 *   <li>{@link JavaTimeModule} para serializar {@link java.time.Instant},
 *       {@link java.time.LocalDateTime}, etc. que aparecen en
 *       {@code ApiMetadata.timestamp}.</li>
 *   <li>Sin timestamps como numero (ISO-8601 en su lugar) — consistente con
 *       la convencion de Jackson y con lo que hace el starter Spring Boot.</li>
 *   <li>Sin FAIL_ON_EMPTY_BEANS para que records vacios (e.g.,
 *       {@code new MetadataRequestContext()}) se serialicen como {@code {}}.</li>
 * </ul>
 * <p>
 * Se registra como CDI bean ({@code @Singleton}); SmallRye lo descubre y lo
 * aplica sobre el ObjectMapper global. No requiere codigo de build-time
 * (ver doc 07 §4.4.3 - "extension coloquial").
 */
@Singleton
public class ApiObjectMapperCustomizer implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}

