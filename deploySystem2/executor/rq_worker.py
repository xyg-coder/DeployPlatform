import sys
from rq import Connection, Worker
from get_docker_client import docker_client
"""
import this to prevent duplicate import
"""

with Connection():
    qs = sys.argv[1:] or ['default']

    w = Worker(qs)
    w.work()