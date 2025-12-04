#!/bin/bash
set -e

# 网络名称
NETWORK_NAME=xcplus

# 镜像和容器列表
SERVICES=("auth" "captcha" "content" "gateway" "media" "search" "system")
REGISTRY=localhost:5001

# 停止并删除旧容器
echo "Stopping old containers if exist..."
for svc in "${SERVICES[@]}"; do
    if [ "$(docker ps -aq -f name=${svc}-service)" ]; then
        docker stop ${svc}-service || true
        docker rm ${svc}-service || true
    fi
done

# 拉取最新镜像
echo "Pulling latest images..."
for svc in "${SERVICES[@]}"; do
    docker pull ${REGISTRY}/${svc}:latest
done

# 运行容器
echo "Running new containers..."
for svc in "${SERVICES[@]}"; do
    CMD="docker run -d --name ${svc}-service --network ${NETWORK_NAME}"

    # gateway 服务暴露 8080 端口
    if [ "$svc" = "gateway" ]; then
        CMD="$CMD -p 8020:8080"
    fi

    # 指定镜像
    CMD="$CMD ${REGISTRY}/${svc}:latest"

    echo "Executing: $CMD"
    eval $CMD
done

echo "All services are up."
