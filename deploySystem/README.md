# Deploy System

This is the first version for Deploy System. Later development is moved to `Deploy System2`.

## Java Project

* User can use this website to deploy maven based java project.
* Use websocket to get the current status of projects and update it on the list page.
* In the java-project-detail page, the status is gotten through the API call every 5 seconds. The deploy log and running log is gotten through websocket.