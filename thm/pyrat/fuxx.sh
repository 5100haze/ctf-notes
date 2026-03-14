#!/bin/bash

while read -r line; do
	s="$(echo "$line" | socat - TCP:10.65.143.155:8000|tee -a log.txt)"
	[[ "$s" =~ "not defined" ]] || echo "interesting response for word $line: $s"
done < words.list
