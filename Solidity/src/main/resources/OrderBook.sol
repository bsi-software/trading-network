pragma solidity ^0.4.6;

contract OrderBook {

  struct Order {
    uint256 quantity;
    uint256 price;
    bool buy;
    address participant;
    uint256 id;
    uint256 externId;
  }

  // id of the order book, eg. EURUSD for an Euro/USD exchange
  string public symbol;
  
  // creation time of this contract
  uint public creationTime;

  // owner of this contract
  address owner;
  
  // participants that are allowed to change state
  mapping (address => bool) participants;
  
  // active buy and sell order
  Order [] public buyOrders;
  Order [] public sellOrders;
  
  // executed orders
  Order [] public executedOrders;

  // order id (number) to increment
  uint256 orderId;
  
  // restricts function access to contract owner
  modifier onlyOwner() {
  	require(msg.sender == owner);
  	_;
  }
  
  // restricts function access to participants
  modifier onlyParticipant() {
  	require(participants[msg.sender]);
  	_;
  }

  // constructor function
  function OrderBook(string _symbol) {
    symbol = _symbol;
    creationTime = now;
    owner = msg.sender;
    participants[owner] = true;
    orderId = 1;
  }

  // destructor function
  function kill() onlyOwner() {
    selfdestruct(owner);
  }

  function addParticipant(address _participant) onlyOwner() {
    participants[_participant] = true;
  }

  function removeParticipant(address _participant) onlyOwner() {
    participants[_participant] = false;
  }

  // create a new order
  function createOrder (uint256 _quantity, uint256 _price, bool _buy, uint256 _externId)
    onlyParticipant()
    returns (uint256)
  {
    Order memory order = Order(_quantity, _price, _buy, msg.sender, orderId++, _externId);

    if (_buy) {
      buyOrders.push(order);
      sortOrders(true);
    } 
    else {
      sellOrders.push(order);
      sortOrders(false);
    }

    return order.id;
  }

  // cancel an existing order
  function cancelOrder(int256 _orderId, bool _buy) onlyParticipant() {
    bool found = false;
    Order [] arrayToRemove = _buy ? buyOrders : sellOrders;
    
    for (uint i = 0; i < arrayToRemove.length; i++) {
      if (arrayToRemove[i].id == uint256(_orderId)) {
        found = true;
      }
      if (found) {
        if (i < arrayToRemove.length - 1) {
          arrayToRemove[i] = arrayToRemove[i+1];
        } 
        else {
          delete arrayToRemove[i];
          arrayToRemove.length = arrayToRemove.length - 1;
        }
      }
    }
  }

  function isPending(uint256 _orderId) constant returns (bool) {
    if(_orderId <= 0) {
      return false;
    }

  	for(uint i = 0; i < buyOrders.length; i++) {
  	  if(_orderId == buyOrders[i].id) {
  	    return true;
      }
  	}

  	for(i = 0; i < sellOrders.length; i++) {
  	  if(_orderId == sellOrders[i].id) {
  	    return true;
  	  }
  	}

  	return false;
  }

  function getNumberOfBuyOrders() constant returns (uint256) {
    return buyOrders.length;
  }

  function getNumberOfSellOrders() constant returns (uint256) {
    return sellOrders.length;
  }

  function getNumberOfExecutedOrders() constant returns (uint256) {
    return executedOrders.length;
  }

  function matchExists() constant returns (bool) {
    if(buyOrders.length == 0 || sellOrders.length == 0) {
      return false;
    }

    Order buyOrder = buyOrders[buyOrders.length - 1];
    Order sellOrder = sellOrders[sellOrders.length - 1];

    // match criteria
    if(buyOrder.price >= sellOrder.price) {
      return true;
    }

    return false;
  }

  function topBuyOrderId() constant returns (int256) {
    if(buyOrders.length == 0) {
      return int256(-1);
    }

    return int256(buyOrders[buyOrders.length - 1].id);
  }

  function topSellOrderId() constant returns (int256) {
    if(sellOrders.length == 0) {
      return int256(-1);
    }

    return int256(sellOrders[sellOrders.length - 1].id);
  }

  function executeMatch(int256 _buyOrderId, int256 _sellOrderId) 
    onlyParticipant() 
    returns (int8) 
  {

    // no matching buy order
    if(buyOrders.length == 0 || uint256(_buyOrderId) != buyOrders[buyOrders.length - 1].id) {
      return int8(-1);
    }

    // no matching sell order
    if(sellOrders.length == 0 || uint256(_sellOrderId) != sellOrders[sellOrders.length - 1].id) {
      return int8(-2);
    }

    // no price match
    if(buyOrders[buyOrders.length - 1].price < sellOrders[sellOrders.length - 1].price) {
      return int8(-3);
    }

    Order buyOrder = buyOrders[buyOrders.length - 1];
    Order sellOrder = sellOrders[sellOrders.length - 1];

    // calculate quantity and price for matching order
    uint256 quantity = buyOrder.quantity < sellOrder.quantity ? buyOrder.quantity : sellOrder.quantity;
    uint256 price = (buyOrder.price + sellOrder.price) / 2;

	// update order book and notify order owners
	execute(buyOrder, quantity, price);
	execute(sellOrder, quantity, price);

	return int8(0);
  }

  function execute(Order _order, uint256 _quantity, uint256 _price) private {
    uint256 remainingQuantity = _order.quantity - _quantity;

    if(remainingQuantity == 0) {
      if(_order.buy) {
        delete buyOrders[buyOrders.length - 1];
        buyOrders.length = buyOrders.length - 1;
      }
      else {
        delete sellOrders[sellOrders.length - 1];
        sellOrders.length = sellOrders.length - 1;
      }
    }
    else {
      if(_order.buy) {
        buyOrders[buyOrders.length - 1].quantity = remainingQuantity;
      }
      else {
        sellOrders[sellOrders.length - 1].quantity = remainingQuantity;
      }
    }

    _order.quantity = _quantity;
    _order.price = _price;

    executedOrders.push(_order);
  }

  function sortOrders(bool _buy) private {
    uint256 arrayLength;
    Order memory tmp;
    uint256 i;

    if(_buy) {
      arrayLength = buyOrders.length;

      for (i = arrayLength - 1; i > 0; i--) {
        if(buyOrders[i].price < buyOrders[i-1].price) {
          tmp = buyOrders[i-1];
          buyOrders[i-1] = buyOrders[i];
          buyOrders[i] = tmp;
        }
      }
    } 
    else {
      arrayLength = sellOrders.length;

      for (i = arrayLength - 1; i > 0; i--) {
        if(sellOrders[i].price > sellOrders[i-1].price) {
          tmp = sellOrders[i-1];
          sellOrders[i-1] = sellOrders[i];
          sellOrders[i] = tmp;
        }
      }
    }
  }
}
