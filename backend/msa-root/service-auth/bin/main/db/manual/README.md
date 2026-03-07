Manual DB Changes

DB structure changes for authentication (users, tokens) must be applied manually.

Use psql or your DB client to run SQL scripts and coordinate with the operations team.

Example:

```bat
psql -h <host> -p <port> -U <user> -d <db> -f ./path/to/script.sql
```
