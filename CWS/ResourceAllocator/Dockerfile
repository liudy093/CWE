#编译镜像
FROM golang:1.15 as builder
MAINTAINER bit_scg "uzz_scg@163.com"
ENV GOPROXY https://goproxy.io
ENV GO111MODULE on
WORKDIR /usr/local/go/src/resourceAllocator
ADD ./go.mod .
ADD ./go.sum .
RUN go mod download
ADD .  /usr/local/go/src/resourceAllocator/
WORKDIR /usr/local/go/src/resourceAllocator

#go构建可执行文件,-o 生成Server，放在当前目录
RUN go build -ldflags="-w -s" -o server .

#执行镜像
FROM ubuntu:latest
WORKDIR /root/go/resourceAllocator
COPY --from=builder /usr/local/go/src/resourceAllocator .
ENTRYPOINT  ["./server"]