import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("java-library")
    id("maven-publish")
    // Genera META-INF/jandex.idx en el JAR para que Quarkus descubra
    // @ServerExceptionMapper y @Singleton en apps consumidoras sin necesidad
    // de extension processor ni @BuildStep. Plugin compatible con Gradle 9.x.
    id("org.kordamp.gradle.jandex") version "2.3.0"
}

group = findProperty("group") as String
version = findProperty("version") as String

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
    // GitHub Packages de nova-java-api-standard (la lib pura que consumimos).
    // NO usamos el repo URL de este extension porque nova-api-standard NO
    // esta publicado aqui — esta en su propio repo, como cualquier lib Maven.
    // NOVA_PACKAGES_READ_TOKEN es el secret que permite cross-repo reads
    // en CI (GITHUB_TOKEN no sirve para leer packages de otro repo).
    // Fallback automatico a GITHUB_TOKEN si NOVA_PACKAGES_READ_TOKEN no esta.
    maven {
        name = "GitHubPackages-Nova-ApiStandard"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-api-standard")
        val token = System.getenv("NOVA_PACKAGES_READ_TOKEN")
            ?: System.getenv("GITHUB_TOKEN")
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
    // Quarkus REST (JAX-RS reactivo) - necesario para @Path, @Provider, @ServerExceptionMapper
    implementation("io.quarkus:quarkus-rest:3.33.2.1")
    // Quarkus ARC (CDI) - necesario para @ApplicationScoped, @Singleton, @Inject
    implementation("io.quarkus:quarkus-arc:3.33.2.1")
    // Quarkus Jackson - aporta jackson-databind + la API ObjectMapperCustomizer.
    implementation("io.quarkus:quarkus-jackson:3.33.2.1")

    // Libreria pura Nova - los tipos ApiResponse, ApiError, PageInfo, etc.
    api("pe.edu.nova.java.libs:nova-api-standard:1.0.0")

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

// Quarkus platform block TEMPORALMENTE removido para aislar el publish fantasma.

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
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