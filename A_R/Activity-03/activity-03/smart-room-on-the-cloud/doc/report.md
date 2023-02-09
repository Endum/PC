# Activity-03
## On the cloud
### Main idea
The idea behind this project was to solve the problem about the Activity-02: where do we put the logic of reading and controlling the smart room? We had written the logic of controlling in various way, but the problem was always the same, where should i compute necessary calculations?

This project answere like: why not on cloud?

### Preamble
The first cloud platform i tried was Microsoft azure iot, after hours of research through documentation i wasn't able to make something works how i expected to work; like this i dropped the iot platform and switched to basic azure pas service, here it came the big question: Java or Python? previous activites were done in Java, i thought it was a good idea to try another language, good practice in my mind.

It goes good, locally i was able to make it work, later on when uploaded on azure, i found out the platform doesn't leave you freedom of opening ports or sockets different from the one you should be using with "flask" when using python.

That fact made me switch to another platform, same code no problem at all, i just needed to uploaded it somewhere else, the platform choosed was "Heroku", after a first try everything was working, but implementation wasn't done, hopes were true.. but... The very next day "heroku" became a paid service.

Time to find another platform i thought, and that what i've done, the one supporting python i found was: "OnRender", files uploaded, service created, same error as Azure, i couldn't open my ports or open different sockets from the "flask" one.

Here, i stopped, too much time spent on searching, i limited myself on finishing the implementation, i know that with the right cloud service it works, but i don't know any of this "right" service for free.

### Design
As simple as in latest activity, a simulator for each sensor, but this time the controlling logic, the consumer, was on another machine, the cloud.

### Python WoT
There's no official WoT library for Python (at the time i'm doing this), so i had to rely on one found on github, which is "work in progress", but it actually worked pretty good.

This library rely on Tornado framework for async managing, on W3C WoT Runtime and the W3C WoT Scripting API to comply with standards.

The structure of WoT implementation it's a bit different from the one we saw in class, in this library there's some concept i didn't know:
- Servient: this figure is the catalogue holder, one servient is enough for all the sensor in the room.
- Catalogue: the holder of sensor, a list with all the information needed to find and communicate with the sensor you need.
  
In order to discover and start a communication with sensor to retrieve data, you have to ask the servient with the id of the sensor you need, once you get information needed you just connect and subscribe to events emitted, or if you want you can use all the existing WoT actions.

### Implementation
To avoid cross language incompatibility, i choosed to reprogram a simplified version also for the sensors, for each of them there's a different file.

The last file is the only that should have been uploaded on cloud, the SmartRoom one is the one who connects to sensors and manipulate the lamp autonomously.