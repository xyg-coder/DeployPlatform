# DeploySystem

This is the 2.0 of the DeploySystem. Plan to reorganize the project.

## Sandbox function

1. Support cpp, java, python. User inputs the code in website. Can upload and download file.
2. Write one Dockerfile to support. For every user, create one container for it.
3. Can support file, stdin, stdout, download file and get file material

## Plan

1. Spring boot server mainly handle the request and response with the frontend part. Once the frontend gives it one task, it will send this task to the backend server (python).
2. Python server gets the request from the springboot server, and sends this task to RQ. RQ will run it later.
3. Frontend part can use websocket to read some file.

## TODO

1. Group in code
2. Web
3. Use Redis to cache