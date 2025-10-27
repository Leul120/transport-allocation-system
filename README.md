
# Transport Allocation System

A small monorepo containing two Spring Boot services used for transport allocation and station camera features.

Services
 - `stationCamera` — camera / image-processing microservice (default port: 8082)
 - `tas` — transport allocation system (APIs for allocations, vehicles, stations) (default port: 8081)

This README explains repository layout, how to build and run each service locally, required environment variables, and notes for contributors.

## Repository layout

Top-level folders:

- `stationCamera/` — Spring Boot Maven project for station camera features.
- `tas/` — Spring Boot Maven project for the transport allocation system.
- `.gitignore` — ignores `**/target/` and common IDE files (prevents build artifacts from being committed).

Each subproject is a standalone Maven project with its own `pom.xml` and `src/` tree.

## Requirements

- Java 17 or later (project logs show Java 21 was used; Java 17+ is recommended)
- Maven or the included Maven Wrapper (`mvnw` / `mvnw.cmd`)
- A running database (MySQL is used by default in application properties, or change datasource settings for other DBs)

## Quick start — build & run

From the repo root on Windows PowerShell (adjust for Linux/macOS by using the POSIX `./mvnw` script):

Build both modules (skip tests for faster local builds):

```powershell
cd 'c:\Users\PC\Downloads\transport-allocation-system'
.\stationCamera\mvnw.cmd -f stationCamera\pom.xml clean package -DskipTests
.\tas\mvnw.cmd -f tas\pom.xml clean package -DskipTests
```

Run a module via Maven (convenient during development):

```powershell
cd tas
.\mvnw.cmd spring-boot:run

cd ..\stationCamera
.\mvnw.cmd spring-boot:run
```

Or run a packaged JAR:

```powershell
java -jar stationCamera\target\stationCamera-0.0.1-SNAPSHOT.jar
java -jar tas\target\tas-0.0.1-SNAPSHOT.jar
```

Run tests:

```powershell
cd stationCamera
.\mvnw.cmd test

cd ..\tas
.\mvnw.cmd test
```

## Configuration & Environment variables

Both services read configuration from `application.properties`/`application.yml`. The projects reference common environment variables for database, OAuth and cloud storage. Example variables you should set in your environment or provide via an external config provider:

- Database
	- `URL` / `spring.datasource.url` (e.g. `jdbc:mysql://localhost:3306/dbname`)
	- `USERNAME` / `spring.datasource.username`
	- `PASSWORD` / `spring.datasource.password`

- Google OAuth
	- `GOOGLE_CLIENT_ID`
	- `GOOGLE_CLIENT_SECRET`

- AWS
	- `AWS_ACCESS_ID` (or `AWS_ACCESS_KEY`)
	- `AWS_SECRET_KEY`
	- `AWS_REGION`
	- `AWS_BUCKET_NAME`

- Mapbox
	- `MAPBOX_KEY`

Set env vars on PowerShell for a session:

```powershell
$env:URL = 'jdbc:mysql://localhost:3306/dbname'
$env:USERNAME = 'root'
$env:PASSWORD = 's3cret'
# then run app in same shell
.\tas\mvnw.cmd spring-boot:run
```

Security note: never commit real secrets to Git. If secrets were accidentally committed, rotate them immediately and consider rewriting repository history to remove them.

## Why some folders looked empty on GitHub

This repository previously had `stationCamera` and `tas` tracked as gitlinks (mode `160000`), so GitHub displayed them as empty links rather than the projects' files. Those gitlinks were converted to normal tracked directories and their sources are now pushed to the `main` branch.

Also: compiled outputs under `target/` were previously tracked; these were removed from the index and a `.gitignore` was added to prevent future commits of build artifacts.

## Developer notes and contribution guidelines

- Fork, create a feature branch, and open a PR against `main`.
- Ensure unit tests pass and no secrets or large binaries are included with commits.
- Keep commits focused and add meaningful commit messages.

Suggested PR checklist:

- Code builds and unit tests pass
- No secrets or large artifacts committed
- Documentation updated as needed

## Rewriting history to remove secrets / large files

If sensitive values or very large files exist in repository history you can remove them with tools such as `git filter-repo` or the BFG Repo-Cleaner. This rewrites history and requires a force-push and coordination with collaborators. Ask maintainers before performing this operation.

## Troubleshooting

- Port conflicts: change `server.port` in `application.properties` or set `SERVER_PORT` env var.
- DB connection failures: check `spring.datasource.url`, credentials and that the DB is reachable.
- Push rejected by GitHub: you may hit pre-receive hooks (large-file limits, secret scanning). Fix locally (remove the offending files) and push cleaned commits.

## License

Add a `LICENSE` file for your preferred license.

---

If you'd like, I can also:

- remove local `target/` directories to reclaim disk space, or
- run a local secret scan and help remove secrets from history (requires force-push and coordination).
