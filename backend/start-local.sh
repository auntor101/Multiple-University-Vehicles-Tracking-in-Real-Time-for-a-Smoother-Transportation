#!/bin/bash

echo "Starting Vehicle Tracking System - Local Development"
echo ""
echo "Make sure you have:"
echo "1. Java 17+ installed"
echo "2. Maven installed"
echo "3. Firebase service account key in src/main/resources/firebase-service-account.json"
echo ""
echo "Starting backend server..."

mvn clean spring-boot:run
