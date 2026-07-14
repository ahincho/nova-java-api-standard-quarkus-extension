# Contributing to nova-java-api-standard-quarkus-extension

Gracias por tu interes en contribuir.

## Convenciones

- **Commits**: [Conventional Commits](https://www.conventionalcommits.org/).
  Validado automaticamente por `commitlint` via `lefthook` pre-commit hook.
  Ejemplos:
  - `feat(mapper): add NotFoundException -> 404 mapping`
  - `test(jackson): add LocalDate serialization test`
  - `fix(deps): bump quarkus-bom to 3.37.2`
  - `docs(readme): document ObjectMapperCustomizer behavior`

- **Versionado**: [SemVer 2.0.0](https://semver.org/) coordinado por
  [`release-please`](https://github.com/googleapis/release-please).
  El PR de release se abre automaticamente al pushear a `main`.

- **Codigo**: sigue la configuracion `config/checkstyle/checkstyle.xml`.
  `LineLength` maximo 140 caracteres.

## Setup local

```bash
# Instalar pre-commit hooks
npm install

# Compilar y testear
./gradlew test
```

## Pull Requests

1. Crea una branch desde `main`: `git checkout -b feat/<descripcion-corta>`.
2. Haz commits atomicos siguiendo Conventional Commits.
3. Push y abre PR contra `main`.
4. Espera a que los workflows de CI pasen (build, matrix, OWASP, SBOM).
5. Merge cuando el PR de release-please se apruebe (o espera a que se genere).

## Estructura del repo

```
.
├── build.gradle.kts                          # Configuracion principal (Quarkus + OWASP + CycloneDX)
├── settings.gradle.kts                       # Root project
├── gradle.properties                         # version + quarkusPlatformVersion + caching
├── config/checkstyle/checkstyle.xml          # Reglas de estilo
├── .github/workflows/
│   ├── ci.yml                                # Pipeline en PRs
│   ├── release-please.yml                    # Genera PRs de release automaticos
│   └── publish-on-tag.yml                    # Publica a GitHub Packages al pushear tag vX.Y.Z
├── src/main/java/pe/edu/nova/java/starters/api/standard/quarkus/
│   ├── mapper/ApiExceptionMapper.java        # @Provider, mapea Throwable -> ApiResponse
│   └── jackson/ApiObjectMapperCustomizer.java # @Singleton, configura ObjectMapper
└── src/test/java/pe/edu/nova/java/starters/api/standard/quarkus/
    ├── resource/TestApiResource.java         # JAX-RS resource de prueba
    ├── ApiResponseEnvelopeTest.java          # Tests del envelope JSON
    └── ApiExceptionMapperTest.java           # Tests del mapper
```

## Que hacer antes de abrir PR

- [ ] `./gradlew test` pasa localmente
- [ ] `./gradlew dependencyCheckAnalyze` no reporta CVEs >= 7 (excepto FPs documentados en `nova-devops/docs/owasp-suppressions.json`)
- [ ] El codigo nuevo tiene tests
- [ ] El commit sigue Conventional Commits
- [ ] Si agregaste un archivo nuevo, actualiza el README si aplica