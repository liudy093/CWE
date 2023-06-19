docker build -t harbor.cloudcontrolsystems.cn/ccs/scheduler_controller:latest .
docker login harbor.cloudcontrolsystems.cn
docker push harbor.cloudcontrolsystems.cn/ccs/scheduler_controller:latest
