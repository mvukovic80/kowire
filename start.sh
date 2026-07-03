#!/bin/bash

# Exit on error
set -e

echo "=== Installing frontend dependencies ==="
cd frontend
npm install

echo "=== Building Vue + Vite frontend ==="
npm run build

echo "=== Moving built assets to Java backend ==="
cd ..
rm -rf backend/src/main/resources/static
mkdir -p backend/src/main/resources/static
cp -r frontend/dist/* backend/src/main/resources/static/

echo "=== Building Java backend (Maven) ==="
cd backend
./mvnw clean package -DskipTests

echo "=== Running Java backend ==="
java -jar target/*.jar
