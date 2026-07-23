#!/bin/bash
# 下载并部署 OnlyOffice Personal 9.3 WASM 资源

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
OO_DIR="$PROJECT_DIR/onlyoffice"

echo "=== Docsx: 下载 OnlyOffice Personal 9.3 WASM 资源 ==="

if [ -d "$OO_DIR/9.3.0.133-24e34b4f401d86dfb31def637358e2fa" ]; then
    echo "OnlyOffice 资源已存在，跳过下载。"
    exit 0
fi

TEMP_DIR=$(mktemp -d)
echo "克隆 OnlyofficePersonal 到临时目录..."
git clone --depth 1 -b 9.3.0.133 https://github.com/fernfei/OnlyofficePersonal.git "$TEMP_DIR"

echo "复制资源到项目..."
cp -r "$TEMP_DIR/9.3.0.136-"* "$OO_DIR/" 2>/dev/null || cp -r "$TEMP_DIR/9.3.0.133-"* "$OO_DIR/" 2>/dev/null || true
cp -r "$TEMP_DIR/assets" "$OO_DIR/" 2>/dev/null || true
cp -r "$TEMP_DIR/blank" "$OO_DIR/" 2>/dev/null || true
cp "$TEMP_DIR/onlyoffice.html" "$OO_DIR/" 2>/dev/null || true

rm -rf "$TEMP_DIR"
echo "=== 完成！OnlyOffice 资源已部署到 $OO_DIR ==="
