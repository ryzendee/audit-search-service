#!/bin/sh

ES_HOSTNAME=${ES_HOSTNAME:-$(hostname)}
ES_PORT=${ES_PORT:-9200}
ES_URL="http://$ES_HOSTNAME:$ES_PORT"

until curl -s "$ES_URL" >/dev/null; do
  echo "Waiting... $ES_URL"
  sleep 2
done

echo "Creating index - audit-methods"
curl -X PUT "$ES_URL/audit-methods" -H 'Content-Type: application/json' -d @/mappings/audit-methods.json

echo "Creating index - auit-http"
curl -X PUT "$ES_URL/audit-http" -H 'Content-Type: application/json' -d @/mappings/audit-http.json

echo "Indexes were created"
