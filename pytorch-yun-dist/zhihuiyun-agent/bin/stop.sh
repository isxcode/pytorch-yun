#!/bin/bash

######################
# 停止脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

if [ -e "zhihuiyun-agent.pid" ]; then
  pid=$(cat "zhihuiyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -9 ${pid}
   rm zhihuiyun-agent.pid
   echo "【至慧云代理】: CLOSED"
   exit 0
  fi
fi

echo "【至慧云代理】: HAS CLOSED"
exit 0