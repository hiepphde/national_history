#!/bin/bash

curl -X POST http://localhost:8093/connectors -H 'Content-Type: application/json' -d @mongo-connector.json