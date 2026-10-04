# Free Render + Brevo setup

## Brevo

Complete account verification. In Settings, find **Senders, Domains & Dedicated IPs**, add and verify your sender email. Follow any account approval or domain authentication requirements shown by Brevo.

Open **SMTP & API → API Keys** and generate an **API key**, not an SMTP key. Enter the key only in Render's environment settings.

## Render

Choose **New → Blueprint**, connect GitHub, and select `Azhagesan-Coder/profile-website`. The root `render.yaml` defines one **Free Docker web service**, Dockerfile `./backend/Dockerfile`, Docker context `./backend`, and health check `/api/health`.

Review the Free plan and enter the two prompted environment values:

- `BREVO_API_KEY`: the Brevo API key.
- `CONTACT_FROM`: the exact sender email verified in Brevo.

The recipient defaults to `azhagesanr.gctcse@gmail.com`. CORS allows `https://azhagesan-coder.github.io`. Email uses HTTPS; no SMTP ports or database are needed.

Alternatively select **New → Web Service**, connect the same repository, choose Docker, set the Dockerfile and context above, select Free, and add the environment variables from `render.yaml`.

## Connect the frontend

Wait for Render to show **Live**, then check `https://YOUR-SERVICE.onrender.com/api/health` returns `{"status":"ok"}`. Share this public backend URL with Codex.

Set `apiBaseUrl` in `frontend/public/config.json` and `dist/config.json` to the backend HTTPS origin without an `/api` suffix, and commit both. GitHub Pages will deploy the configuration change; rebuilding Angular is unnecessary.

## Test

Submit the contact form and check your inbox and Brevo transactional logs. Success means Brevo accepted the message, not proof of inbox delivery. Free Render services sleep after inactivity; wait for the API to wake before testing. Email requests are not automatically retried to avoid duplicates after ambiguous timeouts.

`mvn package` compiles the API and runs four contact tests against a local HTTP stub. No real mail is sent by tests. Locally contact is disabled until `CONTACT_ENABLED`, `BREVO_API_KEY`, and `CONTACT_FROM` are set.

The form has a honeypot; enable additional rate limiting/abuse controls for sustained public use.
