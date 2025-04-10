﻿# List - Sistema Avançado de Gerenciamento de Listas de Games

[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Coverage](https://img.shields.io/badge/coverage-100%25-green.svg)]()

O DSList é um sistema robusto e escalável desenvolvido para o gerenciamento avançado de listas de games. Este projeto serve como uma demonstração abrangente de arquitetura de microsserviços, segurança, e boas práticas de desenvolvimento em Java e Spring Boot.

## Visão Geral do Projeto

O DSList é uma aplicação back-end que implementa uma API REST completa para gerenciar listas de jogos. O projeto foi construído utilizando Java 21 com Spring Boot 3.4.1, incorporando uma ampla gama de tecnologias modernas e práticas de desenvolvimento.

## Principais Características e Tecnologias

1. **Spring Boot 3.4.1**: Framework base para o desenvolvimento rápido de aplicações Java.
2. **API REST com HATEOAS**: Implementação de uma API REST seguindo o princípio HATEOAS para melhor navegabilidade.
3. **Persistência de Dados**:
    - JPA/Hibernate para ORM
    - PostgreSQL como banco de dados principal
    - Flyway para gerenciamento de migrações de banco de dados
4. **Segurança**:
    - Spring Security para autenticação e autorização
    - OAuth2 Resource Server para segurança baseada em tokens
5. **Documentação da API**:
    - Springdoc OpenAPI (Swagger) para documentação automática da API
6. **Testes**:
    - JUnit 5 para testes unitários
    - Testcontainers para testes de integração com containers Docker
    - Rest Assured para testes de API
7. **Desenvolvimento e Produtividade**:
    - Lombok para redução de boilerplate
    - MapStruct para mapeamento objeto-objeto
    - Docker e Docker Compose para containerização
8. **Qualidade de Código**:
    - JaCoCo para cobertura de testes
9. **Auditoria**:
   - Spring Data JPA Auditing para rastreamento automático de criação e modificação de entidades
10. **Outros**:
    - Spring Mail para funcionalidades de e-mail
    - Validação de dados com Bean Validation

## Auditoria

O Projeto implementa um sistema de auditoria robusto utilizando o Spring Data JPA Auditing. Esta funcionalidade permite o rastreamento automático de quem criou ou modificou cada entidade, bem como quando essas ações ocorreram.

- Rastreamento automático de criação e modificação de entidades
- Campos de auditoria: `createdBy`, `createdAt`, `modifiedBy`, `modifiedAt`
- Integração com Spring Security para capturar o usuário atual
- Suporte a JWT para extração do nome de usuário em ambientes OAuth2


## Modelo de Domínio
![Modelo de domínio](https://raw.githubusercontent.com/fabioallima/dslist/refs/heads/main/src/main/resources/assets/dslist-model.png)

## Requisitos de Sistema

- Java Development Kit (JDK) 21
- Maven 3.6+
- Docker e Docker Compose (para ambiente de desenvolvimento e testes)
- PostgreSQL (pode ser executado via Docker)

## Como Executar o Projeto

```bash
# Clonar o repositório
git clone https://github.com/fabioallima/dslist

# Entrar no diretório do projeto
cd dslist

# Construir o projeto
mvn clean install

# Executar os testes
mvn test

# Iniciar a aplicação
mvn spring-boot:run
```

## Para executar com Docker:

```bash
# Construir a imagem Docker
docker build -t dslist .

# Executar o container
docker-compose up
```

## Documentação da API:
#### A documentação da API está disponível através do Swagger UI. Após iniciar a aplicação, acesse:

```text
http://localhost:8080/swagger-ui.html
```

## Cobertura de Testes:
#### Para gerar o relatório de cobertura de testes:

```bash
mvn clean test jacoco:report
```

- O relatório será gerado em target/jacoco-report/index.html.

## Autor

Fabio Almeida Lima

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Fabio%20Almeida%20Lima-blue.svg)](https://www.linkedin.com/in/fabio-alima/)

