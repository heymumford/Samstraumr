#!/bin/bash
# Map biological lifecycle tags to other tag categories for test filtering
# Usage: ./test-map-bio-tags.sh <tag-category> <tag-value>

CATEGORY="$1"
VALUE="$2"

if [ -z "$CATEGORY" ] || [ -z "$VALUE" ]; then
  echo "Error: Missing parameters"
  echo "Usage: $0 <tag-category> <tag-value>"
  echo "Example: $0 phase conception"
  exit 1
fi

CATEGORY=$(echo "$CATEGORY" | tr '[:upper:]' '[:lower:]')
VALUE=$(echo "$VALUE" | tr '[:upper:]' '[:lower:]')

case "$CATEGORY" in
  "initiative")
    case "$VALUE" in
      "substrate")
        echo "SubstrateIdentity"
        ;;
      "structural")
        echo "StructuralIdentity"
        ;;
      "memory")
        echo "MemoryIdentity"
        ;;
      "functional")
        echo "FunctionalIdentity"
        ;;
      "adaptive")
        echo "AdaptiveIdentity"
        ;;
      "cognitive")
        echo "CognitiveIdentity"
        ;;
      "specialized")
        echo "SpecializedIdentity"
        ;;
      "resilience")
        echo "ResilienceIdentity"
        ;;
      "closure")
        echo "ClosureIdentity"
        ;;
      "heritage")
        echo "HeritageIdentity"
        ;;
      "narrative")
        echo "NarrativeIdentity"
        ;;
      "crosscutting")
        echo "CrossCuttingIdentity"
        ;;
      "samstraumr")
        echo "SamstraumrSpecific"
        ;;
      *)
        echo "Unknown initiative: $VALUE"
        exit 1
        ;;
    esac
    ;;
  "phase")
    case "$VALUE" in
      "conception")
        echo "Conception"
        ;;
      "embryonic")
        echo "Embryonic"
        ;;
      "infancy")
        echo "Infancy"
        ;;
      "childhood")
        echo "Childhood"
        ;;
      "adolescence")
        echo "Adolescence"
        ;;
      "adulthood")
        echo "Adulthood"
        ;;
      "maturity")
        echo "Maturity"
        ;;
      "senescence")
        echo "Senescence"
        ;;
      "termination")
        echo "Termination"
        ;;
      "legacy")
        echo "Legacy"
        ;;
      *)
        echo "Unknown phase: $VALUE"
        exit 1
        ;;
    esac
    ;;
  "epic")
    case "$VALUE" in
      "unique" | "uniqueidentification")
        echo "UniqueIdentification"
        ;;
      "creation" | "creationtracking")
        echo "CreationTracking"
        ;;
      "lineage" | "lineagemanagement")
        echo "LineageManagement"
        ;;
      "hierarchical" | "hierarchicaladdressing")
        echo "HierarchicalAddressing"
        ;;
      "environmental" | "environmentalcontext")
        echo "EnvironmentalContext"
        ;;
      "state" | "statepersistence")
        echo "StatePersistence"
        ;;
      "experience" | "experiencerecording")
        echo "ExperienceRecording"
        ;;
      "adaptive" | "adaptivelearning")
        echo "AdaptiveLearning"
        ;;
      "performance" | "performanceawareness")
        echo "PerformanceAwareness"
        ;;
      "purpose" | "purposepreservation")
        echo "PurposePreservation"
        ;;
      *)
        echo "Unknown epic: $VALUE"
        exit 1
        ;;
    esac
    ;;
  "type")
    case "$VALUE" in
      "positive")
        echo "Positive"
        ;;
      "negative")
        echo "Negative"
        ;;
      *)
        echo "Unknown test type: $VALUE"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Unknown category: $CATEGORY"
    exit 1
    ;;
esac