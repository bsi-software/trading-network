mzi@BSIM3236 ~/Desktop/oss/github/trading-network/docker_testrpc (master)
$ docker build -t ethereum_testrpc .

$ docker run -p 8545:8545 -d ethereum_testrpc

mzi@BSIM3236 ~/Desktop/oss/github/trading-network/docker_testrpc (master)
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED
b24b8fc1f364        ethereum_testrpc    "testrpc"           16 seconds ago

$ docker exec -it b24b8fc1f364 bash

