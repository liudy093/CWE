docker build -t 192.168.6.90/workflow/controller:latest .
docker login 192.168.6.90
docker push 192.168.6.90/workflow/controller:latest
