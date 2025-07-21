#!/bin/bash

curl.exe -X POST http://localhost:8093/connectors -H 'Content-Type: application/json' -d @mongo-connector.json
curl.exe -X POST http://localhost:8093/connectors -H 'Content-Type: application/json' -d @postgres-connector.json