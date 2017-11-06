# Scout/Ethereum Trading Network Demo

## Demo Setup

1. Run Ethereum TestRPC container
2. Run Postgres container
3. Start application
4. Check state of smart contract

## Build and Run TestRPC Container

```
docker build docker_testrpc
docker images -> image id
docker tag <image-id> ethereum_testrpc:latest

docker run -p 8545:8545 -d ethereum_testrpc
```

Check out Github repository matthiaszimmermann/web3j_demo for more information about this docker image

## Run PostgreSQL Container

```
docker run -p 5432:5432 -d postgres
```

The PostgreSQL image corresponds to the official Docker image. For development Version 9.6 of PostgreSQL was used.

## Start the Trading Application

Start the server and the client of the application from within the Eclipse IDE using the corresponding launchers.
Open Maven module org.eclipse.scout.tradingnetwork.all.app.dev and start application with launcher

- [org.eclipse.scout.tradingnetwork] dev server+ui

## Check State of Smart Contract

### Attach to TestRPC Container

```
docker ps
docker exec -it <container-id> bash
```

### Inside Container

```
cd /usr/lib/node_modules/ethereumjs-testrpc
node
var Web3 = require('web3')
var web3 = new Web3()
web3.setProvider(new Web3.providers.HttpProvider('http://localhost:8545'))
```

Further steps depend on the address of the deployed smart contract. Check the information icon provided by the USD/EUR page of the Trading application.

Example calls once, the USDEUR object is created.

```
USDEUR.symbol()
USDEUR.getNumberOfBuyOrders()
USDEUR.getNumberOfSellOrders()
USDEUR.matchExists()
USDEUR.topBuyOrderId()
USDEUR.topSellOrderId()
```
