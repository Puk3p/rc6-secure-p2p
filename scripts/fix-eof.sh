#!/usr/bin/env bash
set -e

echo "Fixing missing final newlines..."

FIXED=0

find "$(git rev-parse --show-toplevel)" -type f \
  -not -path '*/.git/*' \
  -not -path '*/.idea/*' \
  -not -path '*/target/*' \
  -not -name '.DS_Store' \
  \( -name '*.java' -o -name '*.xml' -o -name '*.md' -o -name '*.properties' \
     -o -name '*.txt' -o -name '*.yml' -o -name '*.yaml' -o -name '*.sh' \
     -o -name '*.ps1' -o -name '.gitignore' -o -name '.editorconfig' \) \
  | while read -r file; do
    if [ ! -s "$file" ]; then
      printf '\n' > "$file"
      echo "  fixed (empty): $file"
      FIXED=$((FIXED + 1))
    elif [ "$(tail -c 1 "$file" | wc -l)" -eq 0 ]; then
      printf '\n' >> "$file"
      echo "  fixed: $file"
      FIXED=$((FIXED + 1))
    fi
  done

echo "Done."
