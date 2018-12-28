#! /bin/sh

# This is the running script for single-file-python
# $1 time limit (seconds)
# $2 size limit of the whole folder
# $3 stdin

cd code
rm -rf result.log
{ time python main.py $3 >> result.log 2>>result.log & } 2>> result.log

# monitor the size of the folder every 5 seconds, exit if exceed the limit
time_limit=$1
size_limit=$2
ps -ef | grep "python main.py" | grep -v grep > /dev/null
while [ $? -eq 0 ]; do
    echo "Still Running"
    sleep 5
    time_limit=`expr $time_limit - 5`
    echo time limit is $time_limit
    if [ "$time_limit" -lt 0 ]; then
        echo "Time limit exceeds" >> result.log
        exit 1
    fi
    cur_size=$(du -s . | cut -f1)
    echo cur_size is $cur_size
    if [ "$cur_size" -gt "$size_limit" ]; then
        echo "Size limit exceeds" >> result.log
        exit 1
    fi
    ps -ef | grep "python main.py" | grep -v grep > /dev/null
done
echo "\n\n\nFinish Running" >> result.log
exit 0
