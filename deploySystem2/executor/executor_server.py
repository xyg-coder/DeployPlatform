import sys

from flask import Flask, abort, request, jsonify
from rq import Queue
from redis import Redis

from single_file_code_impl import build_and_run
import config

app = Flask(__name__)
redis_conn = Redis()
q = Queue(connection=redis_conn)

IMAGE_NAME = config.single_file_impl['image_name']
GUEST_CODE_PATH = config.single_file_impl['guest_code_path']
HOST_CODE_PATH = config.single_file_impl['host_code_path']
FILE_SIZE_LIMIT = config.single_file_impl['file_size_limit']
TIME_LIMIT = config.single_file_impl['time_limit']
PORT = config.single_file_impl['port']


@app.route("/")
def hello():
    return "Hello world. This is executor"


@app.route("/run-single-file", methods=['POST'])
def run_single_file_code():
    """
    run the single file code
    request contain the json, should contain {"type", "stdin"}
    :return:
    """
    print(request)
    data = request.json
    if not data or not data['type'] or not data['code_id']:
        return abort(400, 'should contain type and code_id in json')
    code_type = data['type']
    if code_type not in ['java', 'cpp', 'python']:
        return abort(400, 'code type not supported')
    code_id = data['code_id']
    script_name = config.single_file_impl['script_name'][code_type]
    stdin = data.get('stdin', '')
    host_code_path = "%s/%s" % (HOST_CODE_PATH, code_id)
    q.enqueue_call(func=build_and_run,
                   args=[
                       IMAGE_NAME,  # image name
                       code_id,  # code id
                       TIME_LIMIT,  # time limit
                       FILE_SIZE_LIMIT,  # file size limit,
                       script_name,  # script name
                       host_code_path,  # host path
                       GUEST_CODE_PATH,  # guest code path
                       stdin,  # stdin
                   ])
    resp = jsonify(success=True)
    return resp


if __name__ == "__main__":
    app.run(debug=True, threaded=True, port=PORT) # with host, can receive public request
    # app.run(debug=True, threaded=True, port=8080, host='0.0.0.0')
