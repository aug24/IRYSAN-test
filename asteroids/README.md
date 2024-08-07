History of work completed:

Start a new project.
====================

I'm using scala/play because it's what I'm most familiar with.  Http4S is
very similar conceptually.

```
sbt new playframework/play-scala-seed.g8
```

Save the asteroid info file locally to examine content
======================================================
```
curl -s 'https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY' | jq '.' > asteroid.json
```
It's a paged json service.

Add the most likely scala libraries
===================================
Json, web client, etc.  

Create a model based on the file content.
=========================================
Add a single file as it's a simple model, and add a test class for it.
However the test class is very simple, so it's really just about parsing.

Add functionality to call the NASA endpoint
===========================================
Create a service and add a test class for it

Add a trivial web page for manual testing
=========================================
Twirl for the win :)

To Run
======
```
sbt run
```

To hit the service
==================
open https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY
open http://localhost:9000
open http://localhost:9000?startDate=2015-09-07&endDate=2015-09-08

