pragma solidity ^0.4.6;

contract OrderBook {

  struct Order {
    uint256 quantity;
    uint256 price;
    bool buy;
    address participant;
    uint256 id;
  }

  Order [] public buyOrders;
  Order [] public sellOrders;
  Order [] public executedOrders;
  
  string public symbol;
  
  mapping (address => bool) participants;
  address owner;
  
  // order id (number) to increment
  uint256 orderId;

  function OrderBook(string _symbol) {
    owner = msg.sender;
    participants[owner] = true;
    
    symbol = _symbol;
    orderId = 1;
  }
  
  function kill() {
    if (msg.sender == owner) { 
      selfdestruct(owner);
    }
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
  
  function isParticipant(address _participant) constant returns (bool) {
    return participants[_participant];
  }
  
  function addParticipant(address _participant) {
    participants[_participant] = true;
  }
  
  function removeParticipant(address _participant) {
    participants[_participant] = false;
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
  
  function executeMatch(int256 _buyOrderId, int256 _sellOrderId) returns (int8) {

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

  function cancelOrder(int256 _orderId, bool _buy) returns (string _errorMessage) {
    bool found = false;
    Order [] arrayToRemove = _buy ? buyOrders : sellOrders;
    for (uint i = 0; i < arrayToRemove.length; i++) {
      if (arrayToRemove[i].id == uint256(_orderId)) {
        found = true;
      }
      if (found) {
        if (i < arrayToRemove.length -1) {
          arrayToRemove[i] = arrayToRemove[i+1];
        } else {
          delete arrayToRemove[i];
          arrayToRemove.length = arrayToRemove.length - 1;
        }
      }
    }
  }

  function createOrder (
    uint256 _quantity,
    uint256 _price,
    bool _buy)
    returns (uint256) 
  {
    if(!participants[msg.sender]) {
      throw;
    }
    
    Order memory order = Order(_quantity, _price, _buy, msg.sender, orderId++);
     
    if (_buy) {
      buyOrders.push(order);
      sortOrders(true);
    } else {
      sellOrders.push(order);
      sortOrders(false);
    }
     
    return order.id;
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
    } else {
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
