#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

# 关闭进程
if [ -e "zhihuiyun.pid" ]; then
  pid=$(cat "zhihuiyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -9 ${pid}
   rm zhihuiyun.pid
   echo "【至慧云】: CLOSED"
   exit 0
  fi
fi

echo "【至慧云】: HAS CLOSED"
exit 0