import docker

print(">>>> docker client is imported once")

docker_client = docker.from_env()