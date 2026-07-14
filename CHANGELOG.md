# Changelog

## [1.1.1](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/compare/v1.1.0...v1.1.1) (2026-07-14)


### Bug Fixes

* **ci:** apply checkstyle plugin (was missing, made checkstyleMain task fail) ([a694b60](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/a694b6016bc84ab1a8a2c258c2a13fb3e305571f))
* **ci:** read nova-api-standard from its own repo + accept NOVA_PACKAGES_READ_TOKEN ([756e4a7](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/756e4a77b1cc7317d5787fbfecd81a60157009db))
* **quarkus:** use @ServerExceptionMapper instead of ExceptionMapper&lt;Throwable&gt; ([37bd083](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/37bd083e4f6e711868217ae71088a4f431b0f5f7))

## [1.1.0](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/compare/v1.0.0...v1.1.0) (2026-07-14)


### Features

* initial implementation of Quarkus extension for nova-api-standard ([a3e5bc2](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/a3e5bc2621b0de9a77b2d9aa8337756c8bd94985))


### Bug Fixes

* **build:** propagate nova-api-standard as api dep + suppress enforcedPlatform warning ([7721d3e](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/7721d3e49532f00b93952b087d8b392c9a268517))
* **ci:** grant contents:write to sbom job ([71bfe42](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/71bfe42086970023e236fb5399f9277b9f2a76d6))


### Documentation

* add SECRETS_SETUP.md checklist for GitHub Actions secrets ([e5e5dac](https://github.com/ahincho/nova-java-api-standard-quarkus-extension/commit/e5e5dac3a01099c1cb8e3242477c16e7a2bef57d))
