import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("java-library")
    id("maven-publish")
    id("org.owasp.dependencycheck") version "12.2.2"
    id("org.cyclonedx.bom") version "3.2.4"
}

group = findProperty("group") as String
version = findProperty("version") as String

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenLocal()
    mavenCentral()
    // GitHub Packages (Nova Platform) — necesario para resolver nova-java-api-standard
    // y futuras libs Nova. GitHub Packages requiere autenticacion incluso para paquetes
    // public (modelo maven.pkg.github.com/<owner>/<repo> scoped al repo dueno).
    // En CI: GITHUB_TOKEN esta disponible. Local: exportar GITHUB_TOKEN=ghp_xxx antes
    // de correr Gradle, o usar Maven Local (publishToMavenLocal) como fallback.
    maven {
        name = "GitHubPackages-Nova"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-api-standard-quarkus-extension")
        val token = System.getenv("GITHUB_TOKEN")
        if (!token.isNullOrBlank()) {
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
                password = token
            }
        }
    }
}

val junitVersion = "6.0.0"

dependencies {
    // Quarkus BOM: alinea todas las versiones de extensiones Quarkus (REST, ARC, etc.)
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    // Quarkus REST (JAX-RS reactivo) - necesario para @Path, @Provider, @ServerExceptionMapper
    implementation("io.quarkus:quarkus-rest")
    // Quarkus ARC (CDI) - necesario para @ApplicationScoped, @Singleton, @Inject
    implementation("io.quarkus:quarkus-arc")
    // Quarkus Jackson - aporta jackson-databind + la API ObjectMapperCustomizer.
    // quarkus-rest NO expone jackson en compileClasspath (solo en test), por eso lo declaramos explicito.
    implementation("io.quarkus:quarkus-jackson")

    // Libreria pura Nova - los tipos ApiResponse, ApiError, PageInfo, etc.
    // ArtifactId es `nova-api-standard` (NO `nova-java-api-standard`) — convencion Nova:
    // solo el groupId incluye `java`, el artifactId es `nova-<rol>`.
    // Usamos `api` (no `implementation`) porque los tipos Nova son parte del API
    // publico del extension: el usuario hace `import pe.edu.nova.java.libs...`
    // en su codigo. Con `implementation` no estaria disponible en compileClasspath.
    api("pe.edu.nova.java.libs:nova-api-standard:1.0.0")

    // Tests
    // Tests unitarios solo: JUnit puro. Los tests de integracion con @QuarkusTest
    // viven en examples/code-with-quarkus (ahi SI se aplica el plugin io.quarkus).
    // Ver seccion "Testing" del README para justificacion.
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
    }
}

tasks.test {
    useJUnitPlatform()
    // @QuarkusTest requiere que el contexto Quarkus arranque por test class.
    // Forks=1 es seguro y suficiente para una libreria sin state global mutable.
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Quarkus extensions usan `enforcedPlatform` para el quarkus-bom (es el patron
// recomendado por Quarkus para alinear todas las versiones). Gradle 9.x marca
// esto como warning porque enforcedPlatform "leak" a los consumers. Es
// exactamente lo que queremos: forzar a quien use el extension a usar la misma
// version del Quarkus platform. Suprimimos la validacion.
tasks.withType<GenerateModuleMetadata>().configureEach {
    suppressedValidationErrors.add("enforced-platform")
}

dependencyCheck {
    // NVD_API_KEY / NOVA_OWASP_FAIL_ON_CVSS son inyectados por reusable-owasp-check.yml.
    // Localmente (sin env vars) defaults a "never fail" (CVSS 11) + sin NVD key.
    failBuildOnCVSS = (System.getenv("NOVA_OWASP_FAIL_ON_CVSS") ?: "11").toFloat()
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""
    // Skip test deps (mismas reglas que los repos Spring Boot Nova).
    skipConfigurations = listOf("testCompileClasspath", "testRuntimeClasspath")
    formats = listOf("HTML", "JSON")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Nova Platform API Standard Quarkus Extension")
                description.set(
                    "Nova Platform Quarkus extension that bridges nova-api-standard " +
                    "(framework-agnostic, pe.edu.nova.java.libs) with Quarkus via " +
                    "JAX-RS @ServerExceptionMapper and SmallRye ObjectMapperCustomizer. " +
                    "Enables ApiResponse/ApiError JSON serialization for quarkus-rest apps."
                )
                url.set("https://github.com/ahincho/nova-java-api-standard-quarkus-extension")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("ahincho")
                        name.set("ahincho")
                        email.set("ahincho@users.noreply.github.com")
                    }
                }
                scm {
                    url.set("https://github.com/ahincho/nova-java-api-standard-quarkus-extension")
                    connection.set("scm:git:git@github.com:ahincho/nova-java-api-standard-quarkus-extension.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-api-standard-quarkus-extension")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}