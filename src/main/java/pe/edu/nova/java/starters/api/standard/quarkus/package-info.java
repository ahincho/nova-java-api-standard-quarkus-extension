/**
 * Nova Platform API Standard Quarkus Extension.
 * <p>
 * Esta extension Quarkus (coloquial, sin {@code @BuildStep}) bridgea los
 * tipos de {@code nova-java-api-standard} (framework-agnostic) con el mundo
 * Quarkus, especificamente:
 * <ul>
 *   <li>{@link pe.edu.nova.java.starters.api.standard.quarkus.mapper.ApiExceptionMapper}
 *       mapea excepciones no controladas a {@code ApiResponse} JSON.</li>
 *   <li>{@link pe.edu.nova.java.starters.api.standard.quarkus.jackson.ApiObjectMapperCustomizer}
 *       configura el {@code ObjectMapper} para serializar correctamente
 *       {@code java.time.*} y records vacios.</li>
 * </ul>
 * <p>
 * Discovery: CDI + Jandex encuentran estos beans en build-time, sin requerir
 * registro manual en {@code META-INF/services/}.
 * <p>
 * Ver {@code docs/java/07-quarkus-analisis-adopcion.md} seccion 4 para la
 * justificacion del diseno.
 */
package pe.edu.nova.java.starters.api.standard.quarkus;

