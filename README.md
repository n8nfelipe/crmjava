# Nexus CRM

CRM SaaS full stack com Java 21/Spring Boot 3.5, Angular 20 e PostgreSQL. O backend mantém regras na camada de aplicação, usa DTOs na fronteira HTTP, UUIDs, locking otimista, paginação e auditoria. Access tokens expiram em 15 minutos; refresh tokens são rotacionados, persistidos somente como SHA-256 e podem ser revogados.

## Executar tudo com Docker

```bash
git clone <repository>
cd crm
cp .env.example .env
# troque todas as senhas e gere JWT_SECRET com: openssl rand -base64 48
docker compose up -d --build
```

- Frontend: http://localhost:4200
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui/index.html
- Health: http://localhost:8080/actuator/health
- Caixa de e-mail local para recuperação de senha: http://localhost:8025

O primeiro administrador é criado uma única vez a partir de `APP_ADMIN_NAME`, `APP_ADMIN_EMAIL` e `APP_ADMIN_PASSWORD`. Nenhuma credencial padrão é embutida no sistema.

## Desenvolvimento local

Suba as dependências:

```bash
docker compose up -d postgres mailpit
```

Backend (JDK 21 e Maven 3.9+):

```bash
cd backend
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/crm
export SPRING_DATASOURCE_USERNAME=crm
export SPRING_DATASOURCE_PASSWORD=crm
export APP_JWT_SECRET="$(openssl rand -base64 48)"
mvn spring-boot:run
```

Frontend (Node 22+):

```bash
cd frontend
npm install
npm start
```

Para desenvolvimento, configure um proxy para `/api` ou execute o frontend pelo Compose. Em produção, o Nginx incluído encaminha `/api` ao serviço backend.

## Testes e qualidade

```bash
cd backend && mvn test
cd frontend && npm test
docker compose config
```

O teste de integração do backend exige Docker disponível para o Testcontainers. O banco é construído exclusivamente pelas migrations em `backend/src/main/resources/db/migration` e o Hibernate roda em modo `validate`.

## Módulos e APIs principais

- Autenticação: login, logout, rotação de refresh token e recuperação de senha por e-mail.
- Usuários: administração de usuários e RBAC (`ADMIN`, `MANAGER`, `SALES`, `USER`).
- Clientes e contatos: CRUD, busca, filtro, ordenação e paginação.
- Leads: cadastro, qualificação, status e conversão transacional em cliente/oportunidade.
- Pipeline: Kanban persistente e movimentação protegida contra negócios encerrados.
- Atividades, produtos, dashboard, relatório CSV e trilha de auditoria.

Swagger descreve os contratos completos. Datas são ISO-8601; valores monetários usam decimal; paginação aceita `page`, `size` e `sort`.

## Segurança operacional

Nunca versione `.env`. Use segredos de orquestrador em produção, TLS no proxy, origem CORS explícita e SMTP real. Tokens ficam em `sessionStorage` no cliente para limitar persistência; a defesa principal contra XSS continua sendo CSP, dependências auditadas e ausência de HTML não confiável. Restrinja Actuator à rede interna quando publicar além de `health` e aplique retenção aos logs de auditoria.
