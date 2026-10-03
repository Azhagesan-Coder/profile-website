# Azhagesan — Developer Portfolio

Dark developer portfolio built with Angular 21 and a Java 21 / Spring Boot 4.1 REST API. Includes an accessible responsive layout, light/dark themes, skill filters, career timeline, education, resume download and email contact API.

## Local development

Requirements: Node 22.12+ or Node 24, Java 21+, Maven 3.6.3+.

1. In `backend`, run `mvn spring-boot:run` (port 8080).
2. In `frontend`, run `npm ci`, then `npm start` (port 4200).
3. Open http://localhost:4200. The dev proxy forwards `/api` to Spring Boot.

The UI falls back to `frontend/public/profile.json` if the profile API is offline. Update that file and `backend/src/main/resources/profile.json` together. Employment dates follow the provided resume; the latest role ends June 2026. No personal projects or current employment have been invented.

## API

- `GET /api/profile`: resume-based profile JSON.
- `GET /api/health`: readiness check.
- `POST /api/contact`: `{ "name": "Visitor", "email": "visitor@example.com", "message": "A message of at least 10 characters", "website": "" }`.

Contact returns success only after SMTP accepts delivery. It is disabled until explicitly configured; HTTP 503 is returned while disabled or when delivery fails. The API does not store messages.

## Deploy the Angular frontend to GitHub Pages

GitHub Pages cannot run Java. Build the frontend separately:

1. In `frontend`, run `npm ci` then `npm run build:pages`.
2. Upload **the contents of** `frontend/dist/portfolio/browser` to your Pages publishing branch root (include `Azhagesan.pdf`, `profile.json` and `config.json`). Alternatively, use GitHub's Static HTML Actions workflow and set its artifact path to `frontend/dist/portfolio/browser`, after adding Node setup, `npm ci` and `npm run build:pages` steps.
3. Enable the matching publishing source in repository Settings → Pages.
4. Expected URL: https://azhagesan-coder.github.io/profile-website/ . Confirm successful deployment before sharing it.

## Deploy the Java backend

Use any Java/container host. Build the included Dockerfile with `backend` as its build context, or run `mvn package` and launch `java -jar target/portfolio-api-1.0.0.jar`.

Set `CORS_ORIGINS=https://azhagesan-coder.github.io` (origin only, no path). Set the frontend's `public/config.json` `apiBaseUrl` to the backend HTTPS origin, e.g. `https://your-api-host.example`, and rebuild/redeploy the frontend. No credentials belong in frontend configuration.

To enable contact delivery, configure these **backend environment variables**:

| Variable | Purpose |
| --- | --- |
| `CONTACT_ENABLED` | `true` after SMTP is configured |
| `SMTP_HOST`, `SMTP_PORT` | Mail provider host and port; default port 587 |
| `SMTP_USERNAME`, `SMTP_PASSWORD` | Mail provider credentials |
| `SMTP_AUTH`, `SMTP_STARTTLS` | Default `true`; adjust for your provider |
| `CONTACT_FROM` | Provider-approved sender address |
| `CONTACT_TO` | Recipient; defaults to resume email |
| `CORS_ORIGINS` | Comma-separated frontend origins |
| `PORT` | API port; defaults to 8080 |

Keep SMTP secrets in the hosting provider's secret settings. For public deployments enable your host's rate limiting for `/api/contact`; the form includes a honeypot, but it is not a substitute for rate limiting.

## Verification

Frontend: `npm run build`. Backend: `mvn test` / `mvn package`. Contact service tests cover disabled delivery, recipient/reply-to behavior and SMTP failure propagation.

The new application source is under `frontend/` and `backend/`. The compiled Angular browser files are also committed in root `dist/` so the existing GitHub Pages workflow can deploy them. After frontend edits, rebuild with `npm run build:pages`, replace the root `dist/` contents with `frontend/dist/portfolio/browser/`, and commit the updated source and build together.
