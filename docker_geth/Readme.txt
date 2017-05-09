mzi@BSIM3236 ~/Desktop/private/github/web3j_tests/docker_geth (master)
$ docker build -t ethereum_geth .

$ docker run -it -p 8545:8545 -d ethereum_geth

$ docker ps
CONTAINER ID        IMAGE               COMMAND
a24250f58021        ethereum_geth       "/geth --password Loc"

$ docker exec -it a24250f58021 bash

root@ae64747b4999:/# solc --version
solc, the solidity compiler commandline interface
Version: 0.4.11+commit.68ef5810.Linux.g++

root@a24250f58021:/# /geth attach
Welcome to the Geth JavaScript console!

instance: Geth/v1.6.0-unstable-562ccff8/linux/go1.7.5
coinbase: 0xc9a5e0d1722e47dead4569e90306ed27ca7ae881
at block: 12 (Fri, 24 Feb 2017 13:58:43 UTC)
datadir: /root/.ethereum
modules: admin:1.0 debug:1.0 eth:1.0 miner:1.0 net:1.0 personal:1.0 rpc:1.0 txpool:1.0 web3:1.0
> net
{
  listening: true,
  peerCount: 0,
  version: "1",
  getListening: function(callback),
  getPeerCount: function(callback),
  getVersion: function(callback)
}
> eth.mining
true
> web3.fromWei(eth.getBalance(eth.coinbase), "ether")
0

... wait some time ...

> eth.blockNumber
50
> exit
root@a24250f58021:/# exit
exit


// geth console https://github.com/ethereum/go-ethereum/wiki/JavaScript-Console 
// more commands https://github.com/ethereum/wiki/wiki/JavaScript-API

// install solc
https://github.com/threatstream/mhn/issues/284

apt-get install -y python-software-properties
apt-get install -y software-properties-common python-software-properties

https://github.com/ethereum/go-ethereum/wiki/Contract-Tutorial
sudo add-apt-repository ppa:ethereum/ethereum
sudo apt-get update
sudo apt-get install solc
which solc
