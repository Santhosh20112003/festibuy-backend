#!/bin/bash
set -e

echo "=========================================="
echo "  🚀 FestiBuy Build & Deployment Script   "
echo "=========================================="

echo ""
echo "🛑 Stopping existing containers..."
docker compose down

echo ""
echo "🔨 Building and starting containers..."
docker compose up --build -d

echo ""
echo "⏳ Waiting for services to become healthy..."
sleep 3

echo ""
echo "📊 Current Container Status:"
docker compose ps

echo ""
echo "=========================================="
echo "  ✅ Deployment completed successfully!   "
echo "=========================================="
