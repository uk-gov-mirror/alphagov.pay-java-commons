#!/bin/bash

MODULE=$(git log --oneline -2 | grep -oh "module=[A-Za-z-]*" | cut -d= -f2)

if [ ! -z "$MODULE" ]; then
    echo "releasing $MODULE in bintray"
    mvn releaser:release -DmodulesToRelease=$MODULE
fi
