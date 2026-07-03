# Wire — a simplified Twitter clone

A minimal Twitter-style app: post short messages, like, follow people, and browse
a feed. Built with Spring Boot (REST API + JWT auth) on MongoDB, and a Vue 3
single-page frontend.

```
mini-twitter/
├── backend/    Spring Boot API (Java 17, MongoDB, JWT auth)
└── frontend/   Vue 3 SPA (Vite, Pinia, Vue Router)
```

## What's included

- Sign up / sign in (JWT-based, password hashed with BCrypt)
- Post short messages (280 char limit, with a live character-count ring)
- Home feed (people you follow) and an Explore feed (everyone)
- Like / unlike, delete your own posts
- Follow / unfollow, public profile pages with a search by username
- A single warm, deliberately non-default visual style (see "Design notes" below)

## Prerequisites

- Java 17+
- Maven 3.8+ (or use your IDE's built-in Maven)
- Node.js 18+ and npm
- MongoDB running locally on `27017`, or a MongoDB Atlas connection string

If you don't have MongoDB installed locally, the quickest path is Docker:

```bash
docker run -d --name wire-mongo -p 27017:27017 mongo:7
```

## Running the backend

```bash
cd backend
mvn spring-boot:run
```

This starts the API on `http://localhost:8080`. On first run, Spring Data
MongoDB will create the `minitwitter` database and the `users` / `tweets`
collections automatically the first time a document is written — no manual
schema setup needed.

### Configuration

All config lives in `backend/src/main/resources/application.yml` and can be
overridden with environment variables:

| Variable | Default | Purpose |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/minitwitter` | Mongo connection string |
| `JWT_SECRET` | a placeholder dev string | **Change this in any real deployment** — use a long random value (32+ chars) |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | JWT token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated list of allowed frontend origins |

For example:

```bash
JWT_SECRET="$(openssl rand -base64 48)" MONGODB_URI="mongodb://localhost:27017/minitwitter" mvn spring-boot:run
```

## Running the frontend

```bash
cd frontend
npm install
npm run dev
```

This starts the Vite dev server on `http://localhost:5173`, with API calls to
`/api/*` automatically proxied to the backend on port 8080 (see
`vite.config.js`). Open `http://localhost:5173` in your browser, you'll land
on the sign-in page since there's no session yet — click through to "Create
an account" to register your first user.

### Building for production

```bash
npm run build
```

Outputs static files to `frontend/dist`. Serve them with any static file host
(nginx, Caddy, S3 + CloudFront, etc.), and make sure that host proxies `/api/*`
to wherever you deploy the Spring Boot backend, or update `CORS_ALLOWED_ORIGINS`
on the backend and call the API by full URL from the frontend instead.

## API overview

All endpoints are under `/api`. `/api/auth/**` and `/api/health` are public;
everything else requires `Authorization: Bearer <token>`.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account, returns a JWT |
| POST | `/api/auth/login` | Sign in, returns a JWT |
| GET | `/api/users/me` | Current authenticated user |
| GET | `/api/users/{username}` | Public profile |
| GET | `/api/users/search?query=` | Search users by username |
| POST | `/api/users/{username}/follow` | Follow a user |
| DELETE | `/api/users/{username}/follow` | Unfollow a user |
| POST | `/api/tweets` | Post a new tweet |
| GET | `/api/tweets/feed` | Tweets from people you follow (+ your own) |
| GET | `/api/tweets/explore` | Global feed, newest first |
| GET | `/api/tweets/user/{username}` | A specific user's tweets |
| POST | `/api/tweets/{id}/like` | Toggle like on a tweet |
| DELETE | `/api/tweets/{id}` | Delete your own tweet |

## Design notes

The visual identity ("Wire") uses a warm paper background, a single confident
blue accent rather than the generic Bootstrap palette, and a Fraunces/Inter
type pairing. The signature element is the composer's character-count ring,
which fills in like a spool of wire as you type, instead of a generic counter.

## A note on how this was built

The frontend was installed and built with Vite in the sandbox used to write
this, with no errors — but the Spring Boot backend could **not** be compiled
here because this environment's network policy blocks Maven Central. The Java
code was therefore checked by careful manual review (consistent imports,
matching dependency versions for jjwt 0.12.x's fluent API, and correct request
chain through the JWT filter) rather than a real `mvn compile`. Please run
`mvn clean install` yourself the first time to confirm — and let me know what
you see if anything doesn't compile, so it can be fixed.
