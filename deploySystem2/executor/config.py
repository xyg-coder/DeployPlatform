single_file_impl = dict(
    port=9000,
    image_name='alpine_single_file',
    host_code_path='/home/xinyuan/workspace/git/DeployPlatform/deploySystem2/codes',
    guest_code_path='/code',
    file_size_limit=1000000,
    time_limit=20,
    script_name = dict(
        java='docker_java_impl.sh',
        python='docker_python3_impl.sh',
        cpp='docker_cpp_impl.sh'
    )
)
