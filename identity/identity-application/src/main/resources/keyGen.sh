#!/usr/bin/env bash
set -euo pipefail

KID="${1:-key-$(date +%Y%m%d-%H%M%S)}"
BASE_DIR="keys/${KID}"

mkdir -p "${BASE_DIR}"

openssl genpkey \
  -algorithm RSA \
  -out "${BASE_DIR}/private.pem" \
  -pkeyopt rsa_keygen_bits:2048

openssl rsa \
  -pubout \
  -in "${BASE_DIR}/private.pem" \
  -out "${BASE_DIR}/public.pem"

cat > "${BASE_DIR}/metadata.env" <<EOF
KID=${KID}
ALG=RS256
CREATED_AT=$(date -I seconds)
EOF

echo "Generated key pair:"
echo "- KID: ${KID}"
echo "- DIR: ${BASE_DIR}"