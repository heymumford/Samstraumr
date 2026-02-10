#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# This is a redirect script that points to the new location

echo -e "\033[1;33mWARNING: map-test-type.sh has been moved to util/\033[0m"
echo -e "Please use \033[1;32m./util/test-map-type.sh\033[0m instead."
echo ""

# Forward to new script
./util/test-map-type.sh "$@"
