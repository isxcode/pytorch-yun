#!/bin/bash

echo "开始安装"

# 检查tar命令
if ! command -v tar &>/dev/null; then
  echo "【安装结果】：未检测到tar命令,请安装tar"
  exit 1
fi

# 检查java命令
if ! command -v java &>/dev/null; then
  echo "【安装结果】：未检测到java命令，请安装java"
  exit 1
fi

# 检查node命令
if ! command -v node &>/dev/null; then
  echo "【安装结果】：未检测到node命令，请安装nodejs"
  exit 1
fi

# 检查pnpm命令
if ! command -v pnpm &>/dev/null; then
   echo "【提示】：未检测到pnpm命令，已经执行命令: npm install pnpm@9.0.6 -g"
   npm install pnpm@9.0.6 -g
fi

# 返回状态
echo "【安装结果】：安装成功"