#\!/bin/bash
# Run biological lifecycle tests for Samstraumr

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Define the usage
function show_usage {
    echo -e "${BLUE}Biological Lifecycle Test Runner${RESET}"
    echo
    echo "This script runs the biological lifecycle tests for the Samstraumr framework."
    echo
    echo -e "${YELLOW}Usage:${RESET}"
    echo "  ./run-bio-tests.sh [options]"
    echo
    echo -e "${YELLOW}Options:${RESET}"
    echo "  --atl                 Run only Above The Line tests (critical)"
    echo "  --btl                 Run only Below The Line tests (robustness)"
    echo "  --all                 Run all tests (default)"
    echo "  --phase=<phase>       Run tests for a specific phase (conception, embryonic, infancy, childhood)"
    echo "  --initiative=<init>   Run tests for a specific initiative (substrate, structural, memory, functional)"
    echo "  --help                Show this help message"
    echo
    echo -e "${YELLOW}Examples:${RESET}"
    echo "  ./run-bio-tests.sh --atl                 # Run all ATL biological lifecycle tests"
    echo "  ./run-bio-tests.sh --phase=conception    # Run all Conception phase tests"
    echo "  ./run-bio-tests.sh --initiative=memory   # Run all Memory Identity tests"
    echo
}

# Parse command line arguments
ATL_FLAG=""
BTL_FLAG=""
PHASE=""
INITIATIVE=""

for arg in "$@"
do
    case $arg in
        --atl)
            ATL_FLAG="--atl"
            ;;
        --btl)
            BTL_FLAG="--btl"
            ;;
        --all)
            ATL_FLAG=""
            BTL_FLAG=""
            ;;
        --phase=*)
            PHASE="${arg#*=}"
            ;;
        --initiative=*)
            INITIATIVE="${arg#*=}"
            ;;
        --help|-h)
            show_usage
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $arg${RESET}"
            show_usage
            exit 1
            ;;
    esac
done

# Set default filtering if not specified
if [ -z "$ATL_FLAG" ] && [ -z "$BTL_FLAG" ]; then
    echo -e "${YELLOW}Running all biological lifecycle tests${RESET}"
    CMD="mvn test -P lifecycle-tests"
elif [ -n "$ATL_FLAG" ] && [ -z "$BTL_FLAG" ]; then
    echo -e "${YELLOW}Running ATL (critical) biological lifecycle tests${RESET}"
    CMD="mvn test -Dtest=RunLifecycleATLCucumberTest"
elif [ -z "$ATL_FLAG" ] && [ -n "$BTL_FLAG" ]; then
    echo -e "${YELLOW}Running BTL (robustness) biological lifecycle tests${RESET}"
    CMD="mvn test -Dtest=RunLifecycleBTLCucumberTest"
else
    # Both ATL and BTL specified - run all tests
    echo -e "${YELLOW}Running all biological lifecycle tests${RESET}"
    CMD="mvn test -P lifecycle-tests"
fi

# Add phase filter if specified
if [ -n "$PHASE" ]; then
    # First letter to uppercase, rest to lowercase
    PHASE_TAG=$(echo "$PHASE" | awk '{print toupper(substr($0,1,1)) tolower(substr($0,2))}')
    echo -e "${YELLOW}Filtering for phase: $PHASE_TAG${RESET}"
    CMD="$CMD -Dcucumber.filter.tags=\"@$PHASE_TAG\""
fi

# Add initiative filter if specified
if [ -n "$INITIATIVE" ]; then
    case $INITIATIVE in
        substrate)
            INITIATIVE_TAG="SubstrateIdentity"
            ;;
        structural)
            INITIATIVE_TAG="StructuralIdentity"
            ;;
        memory)
            INITIATIVE_TAG="MemoryIdentity"
            ;;
        functional)
            INITIATIVE_TAG="FunctionalIdentity"
            ;;
        *)
            echo -e "${RED}Unknown initiative: $INITIATIVE${RESET}"
            echo "Valid initiatives: substrate, structural, memory, functional"
            exit 1
            ;;
    esac
    
    echo -e "${YELLOW}Filtering for initiative: $INITIATIVE_TAG${RESET}"
    if [[ $CMD == *"cucumber.filter.tags"* ]]; then
        # Add to existing tags
        CMD="${CMD%\"} and @$INITIATIVE_TAG\""
    else
        # Create new tag filter
        CMD="$CMD -Dcucumber.filter.tags=\"@$INITIATIVE_TAG\""
    fi
fi

# Execute the tests
echo -e "${GREEN}Executing: $CMD${RESET}"
eval $CMD

# Check result
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Biological lifecycle tests executed successfully${RESET}"
    exit 0
else
    echo -e "${RED}Biological lifecycle tests failed${RESET}"
    exit 1
fi
