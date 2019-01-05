#! /bin/bash

rm -rf worker1.log worker2.log executor_server.log
# set up two workers
python3 rq_worker.py > worker1.log 2>>worker1.log & echo "finish worker1"
python3 rq_worker.py > worker2.log 2>>worker2.log & echo "finish worker2"

# set up server
python3 executor_server.py > executor_server.log 2>>executor_server.log & echo "finish executor server"
