# 至慧云

### 超轻量级大模型调用平台

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhihuiyun)](https://hub.docker.com/r/isxcode/zhihuiyun)
[![build](https://github.com/isxcode/pytorch-yun/actions/workflows/build-zhihuiyun.yml/badge.svg?branch=main)](https://github.com/isxcode/pytorch-yun/actions/workflows/build-zhihuiyun.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/pytorch-yun)](https://github.com/isxcode/pytorch-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/pytorch-yun)](https://github.com/isxcode/pytorch-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fpytorch-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fpytorch-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/pytorch-yun)](https://github.com/isxcode/pytorch-yun/blob/main/LICENSE)

|           |                                                                         |
|-----------|-------------------------------------------------------------------------|
| 官网地址:     | https://zhihuiyun.isxcode.com                                           |
| 源码地址:     | https://github.com/isxcode/pytorch-yun                                  |
| 演示环境:     | https://zhihuiyun-demo.isxcode.com                                      |
| 安装包下载:    | https://isxcode.oss-cn-shanghai.aliyuncs.com/zhihuiyun/zhihuiyun.tar.gz |
| 许可证下载:    | https://isxcode.oss-cn-shanghai.aliyuncs.com/zhihuiyun/license.lic      |
| docker镜像: | https://hub.docker.com/r/isxcode/zhihuiyun                              |
| 阿里云镜像:    | https://zhihuiyun.isxcode.com/zh/docs/zh/1/1-docker                     |
| 关键词:      | 大模型平台, 模型调用, 人工智能, Pytorch, Docker                                      |
|           |                                                                         |

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至慧云是一款超轻量级、企业级大模型训练平台，基于Pytorch生态打造。一键部署，开箱即用。快速实现大模型训练、模型调用、机器人代理、作业流调度、自定义接口、等多种功能，为企业提供高效便捷的大模型解决方案。

### 功能特点

- **轻量级产品**: 无需额外组件安装，一键部署，开箱即用。
- **云原生私有化**: 兼容云原生架构，支持多节点安装与高可用集群部署。
- **复杂模型调试**: 基于原生Pytorch分布式架构，高效地执行模型训练计算。

### 立即体验

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演示地址：https://zhihuiyun-demo.isxcode.com </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;体验账号：user001 </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;账号密码：welcome1

### 快速部署

> 访问地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhihuiyun
```

### 相关文档

- [快速入门](https://zhihuiyun.isxcode.com/zh/docs/zh/1/0)
- [产品手册](https://zhihuiyun.isxcode.com/zh/docs/zh/2/0)
- [开发手册](https://zhihuiyun.isxcode.com/zh/docs/zh/6/1)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 源码构建

> 注意！！！编译环境需访问外网，且需提前安装Nodejs和Java，推荐版本如下: </br>
> Java: zulu8.78.0.19-ca-jdk8.0.412-x64 </br>
> Nodejs: node-v18.20.3-x64

##### MacOS/Linux

> 安装包路径: pytorch-yun/pytorch-yun-dist/build/distributions/zhihuiyun.tar.gz

```bash
git clone https://github.com/isxcode/pytorch-yun.git
cd pytorch-yun
./gradlew install clean package
```

##### Windows10/11

> 注意！！！请使用Git Bash终端工具执行以下命令

```bash
git clone https://github.com/isxcode/pytorch-yun.git
cd pytorch-yun
./gradlew.bat install clean package
```

### 联系我们

邮箱：hello@isxcode.com
