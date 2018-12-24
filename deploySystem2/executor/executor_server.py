import sys

from flask import Flask, abort, request, jsonify
from rq import Queue
from redis import Redis

from single_file_code_impl import build_and_run
import config

app = Flask(__name__)
redis_conn = Redis()
q = Queue(connection=redis_conn)

IMAGE_NAME = config.single_file_impl['java']['image_name']
GUEST_CODE_PATH = config.single_file_impl['java']['guest_code_path']
HOST_CODE_PATH = config.single_file_impl['java']['host_code_path']
FILE_SIZE_LIMIT = config.single_file_impl['java']['file_size_limit']
TIME_LIMIT = config.single_file_impl['java']['time_limit']


@app.route("/")
def hello():
    return "Hello world. This is executor"


@app.route("/run-single-file/<code_id>", methods=['GET'])
def run_single_file_code(code_id):
    """
    run the single file code
    request contain the json, should contain {"type", "stdin"}
    :return:
    """
    data = request.json
    if not data or not data['type']:
        return abort(400, 'should contain type in json')
    code_type = data['type']
    if code_type != 'java':
        return abort(400, 'currently only support java code')

    stdin = data.get('stdin', '')
    host_code_path = "%s/%s" % (HOST_CODE_PATH, code_id)
    q.enqueue_call(func=build_and_run,
                   args=[
                       IMAGE_NAME,  # image name
                       code_id,  # code id
                       TIME_LIMIT,  # time limit
                       FILE_SIZE_LIMIT,  # file size limit,
                       host_code_path,  # host path
                       GUEST_CODE_PATH,  # guest code path
                       stdin,  # stdin
                   ])
    resp = jsonify(success=True)
    return resp


if __name__ == "__main__":
    port = int(sys.argv[1])
    # app.run(debug=True, threaded=True, port=port, host='0.0.0.0') # with host, can receive public request
    app.run(debug=True, threaded=True, port=port)
