# ── Stage 1: Build the Vue/Vite frontend ─────────────────────────────────────
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend

# Install dependencies first (layer-cache friendly)
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

# Copy source and build
COPY frontend/ ./
RUN npm run build

RUN apk add --no-cache mailcap

# ── Stage 2: Build the Spring Boot backend ───────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /app/backend

# Copy pom.xml and fetch dependencies first (layer-cache friendly)
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -q

# Copy compiled frontend assets into Spring Boot's static resources directory
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

# Copy backend source
COPY backend/src ./src

# Build the fat JAR, skipping tests
RUN mvn clean package -DskipTests -q

# ── Stage 3: Minimal runtime image ───────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
