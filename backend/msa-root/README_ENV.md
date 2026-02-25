# Environment setup (local development)

This repository contains multiple microservices. To run services locally, create a `.env` file in the repository root (not committed) or set equivalent environment variables per service.

Quick steps (Windows cmd)

1. Copy the example to create a project .env (optional):
   copy .env.example .env

2. Edit `.env` and fill secrets (DB_PASSWORD, TOKEN_SECRET, etc.).

3. Load `.env` into current cmd session and run a service:

```bat
call env-load.cmd
:: Run a service (example)
call gradlew :service-user:bootRun
```

PowerShell (Process scope):

```powershell
.\\env-load.ps1
./gradlew :service-user:bootRun
```

Docker Compose

Place `.env` next to `docker-compose.yml` and use `env_file` or `${VAR}` substitution in the compose file.

Security

- Never commit `.env`.
- Use `.env.example` files in each service to show required variables.
- Use secret stores (Vault, AWS Secrets Manager, Kubernetes Secrets) in production environments.
