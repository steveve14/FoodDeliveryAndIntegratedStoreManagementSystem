Manual DB Changes

This folder is intended for manual SQL files or documents describing manual database changes.

Policy:
- Automatic Flyway migrations are disabled in application.yml. Apply DB structure changes manually to the database.
- Recommended location for manual SQL scripts: store under this folder and execute them against the target DB using psql or a DB client.

Example (psql):

```bat
psql -h <host> -p <port> -U <user> -d <database> -f path/to/script.sql
```

Security:
- Use appropriate DB user with limited privileges when applying migrations.
