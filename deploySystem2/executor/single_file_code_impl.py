from docker.errors import ContainerError, NotFound
from get_docker_client import docker_client


def build_and_run(image_name, code_id, time_limit, file_size_limit,
                  script_name, host_path, guest_path, stdin=""):
    """
    run the container
    :param image_name:
    :param code_id:
    :param time_limit:
    :param file_size_limit:
    :param host_path:
    :param guest_path:
    :param stdin:
    :return:
    """
    container_name = "single_file_code_%s" % code_id
    try:
        docker_client.containers.get(container_name)
        print('container already runs, quits')
        return "Container already runs"
    except NotFound as e:
        pass
    command = "sh -c '/implsh/%s %s %s \"%s\"'" % (script_name, time_limit, file_size_limit, stdin)
    try:
        log = docker_client.containers.run(
            image=image_name,
            command=command,
            volumes={
                host_path: {
                    'bind': guest_path,
                    'mode': 'rw'
                }
            },
            working_dir='/',
            auto_remove=True,
            name=container_name
        )
        return log
    except ContainerError as e:
        return e.stderr
