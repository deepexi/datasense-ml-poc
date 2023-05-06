#!/bin/bash

mvn clean generate-sources
# in case first fail
if [ $? -eq 0 ]; then
    :
else
  mvn clean generate-sources
fi
