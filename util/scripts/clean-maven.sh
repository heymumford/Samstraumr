#!/bin/bash
# Maven repository cleanup script
# Usage: ./cleanup-maven.sh [--deep]

set -e

echo "🧹 Starting Maven repository cleanup"

if [ "$1" == "--deep" ]; then
  echo "⚠️ Performing deep cleanup (removing all locally cached artifacts)"
  
  # Ask for confirmation for deep cleanup
  read -p "This will delete all cached Maven artifacts. Continue? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️ Removing all cached artifacts..."
    rm -rf ~/.m2/repository/*
    echo "✅ Deep cleanup complete"
  else
    echo "❌ Deep cleanup cancelled"
    exit 1
  fi
else
  echo "🧹 Performing standard cleanup (removing only unused artifacts)"
  
  # Standard cleanup using Maven
  mvn dependency:purge-local-repository -DreResolve=false
  
  # Remove Maven's _remote.repositories files
  echo "🧹 Removing _remote.repositories files..."
  find ~/.m2/repository -name "_remote.repositories" -type f -delete
  
  # Remove Maven's resolver-status.properties files
  echo "🧹 Removing resolver-status.properties files..."
  find ~/.m2/repository -name "resolver-status.properties" -type f -delete
  
  echo "✅ Standard cleanup complete"
fi

echo "📊 Current Maven repository size: $(du -sh ~/.m2/repository | cut -f1)"