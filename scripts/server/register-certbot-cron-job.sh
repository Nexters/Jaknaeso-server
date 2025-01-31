#!/bin/bash

CRON_JOBS=$(crontab -l 2>/dev/null)
CERTBOT_RENEW_JOB="30 5 * * 1 certbot renew --webroot -w /var/www/certbot --quiet >> /var/log/certbot.log 2>&1"

if ! echo "$CRON_JOBS" | grep -q "certbot renew"; then
  (echo "$CRON_JOBS"; echo "$CERTBOT_RENEW_JOB") | crontab -
fi