# Executor

One server will receive request to run code.

## Libraries

* Use `flask` to handle the request
* Use `rq` to send task to queue for later handle
* Use `docker` to run the code

## Files

* `executor_server.py`: flask server
* `config.py`: configuration file
* `docker`: Dockerfile and entrypoint
* `get_docker_client`: preload module and get the client once for every rq worker
* `rq_worker.py`: rq worker
* `single_file_code_impl.py`: The task in the queue to process
* `set_up.sh`: set up script

## TODO

* The stdin is still unable to run. See it later.