Minecraft Docker-Proxy
======================

A simple proxy application to help manage minecraft servers running in docker containers.
This application is designed to be used in a docker container, but it can also be used outside of docker.

## Features
### Server startup
This application accepts a bash command that starts the minecraft server. On startup that command is executed in a
child process. It is expected that this process starts the minecraft server. It may be a direct `java` command or e.g.
a shell script that in turn starts the minecraft server. It is recommended to use a shell script for server startup.
This is already the default for FTB servers.

***Attention:*** It can not be guaranteed that the startup script starting the minecraft server is executed from the
directory it resides in. Consequently the script should probably itself switch to the correct directory. (FTB server scripts
don't do that by default!)
It is therefore recommended to add the following line to the startup script:
```bash
cd "$(dirname "$0")"
```

### Output forwarding
The proxy captures both the `stdout` and `stderr` streams of the minecraft server. All messages are printed to the `stdout` output
of the proxy itself. To differentiate between log messages of the proxy and those of the minecraft server, a label is prepended
to all messages:
```
[proxy ] this message comes from the proxy itself
[server] this message comes from the minecraft server
```

### Input forwarding
To facilitate sending commands to the minecraft server (even from other processes), an input file path must be configured.
The applications regularily tests this path (once per second). If the file exists, the content of the file is sent to the
minecraft server line by line. After that the file is deleted. This simple bash command sends a server message to the minecraft
chat (assuming the proxy expects a file `input`):
```bash
echo "say welcome players!" >input
```

### Graceful Shutdown
When using `docker stop`, docker sends a `SIGTERM` signal to the running process. Minecraft servers however seem to ignore
this signal. As a result the server simply keeps running until the configured grace period expires. At that point the server
gets forcefully terminated using `SIGKILL`. This is not ideal as the server will not be able to perform a graceful shutdown.

This applications captures all shutdown signals and instructs the server to perfom a graceful shutdown.
First a 10 second count-down is printed in the server chat. This alerts players of the impending shutdown.
After that a `/stop` command is sent to the server. The server should gracefully stop. After that the proxy itself terminates.
If for any reason the server does not stop sufficiently quickly (again - configured in the grace period) it may still be
killed by docker.

### Watchdog Timer
Sometimes a server may freeze. In this state the process keeps running but the server does not function. The proxy monitors
the server log and if no messages are printed for too long, it tries to force the server to print a log message by sending it
an invalid command. If the server does not respond to this for 5 minutes, it gets forcefully killed. After that the proxy itself
terminates. Assuming docker was configured correctly, docker can then simply restart the server.

## Usage
This application requires the 3 following arguments:
 1. input file path
 2. startup command

Example run command:
```bash
java -jar proxy.jar input /bin/sh start.sh
```
This instructs the proxy to use the path `./input` for the input file and to start the minecraft server by executing the script
`start.sh`.

## Example `docker-compose.yml`
```yaml
version: '2.0'
services:
  minecraft_server:
    container_name: minecraft_server
    image: java:8-jre
    command: java -jar /app/proxy.jar /app/input /bin/sh /app/start.sh
    stop_grace_period: 1m
    restart: unless-stopped
    ports:
      - 25565:25565
    volumes:
      - ./minecraft_server:/app
```
This configuration mounts the directory `./minecraft_server` inside the docker container at the path `/app`.
