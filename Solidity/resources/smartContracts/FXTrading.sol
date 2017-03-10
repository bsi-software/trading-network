pragma solidity ^0.4.2;

contract FXTrading {

  deal[] public buyDeals;
  deal[] public sellDeals;
  deal[] public doneDeals;

  // nice, bool to automatch

  struct deal {
    uint256 quantity;
    uint256 price;
    bool buy;
    string company;
    uint256 dealNr;
  }

  // companys allowed
  string[] companys;

  // nice, is an event a transaction? const method to get status
  event statusChanged(string eventType);

  // deal nr to increment, nice: use UUID https://github.com/pipermerriam/ethereum-uuid
  uint256 dealNr = 0;

  string public currencyPair;

  // constructor, initial companys, TODO: currently not modifiable
  function FXTrading(string _currencyPair) {
    currencyPair = _currencyPair;
    companys.push("Firma1");
    companys.push("Firma2");
    companys.push("Firma3");
    companys.push("Firma4");
    companys.push("Firma5");
  }

  function currentMatch() 
    returns (uint256 [2] _match) 
  {
    if(buyDeals.length == 0 || sellDeals.length == 0) {
      return ([uint256(-1),uint256(-1)]);    
    }
    
    deal dealBuy = buyDeals[buyDeals.length - 1];
    deal dealSell = sellDeals[sellDeals.length - 1];
    
    if(dealBuy.price >= dealSell.price) {
      return ([dealBuy.dealNr, dealSell.dealNr]);
    }
    
    return ([uint256(-1),uint256(-1)]);    
  }
  
  function executeMatch(uint256 _buyDealNr, uint256 _sellDealNr) 
    returns (uint8 _status) 
  {
    // no matching buy order
    if(buyDeals.length == 0 || _buyDealNr != buyDeals[buyDeals.length - 1].dealNr) {
      return uint8(-1);
    } 
    
    // no matching sell order
    if(sellDeals.length == 0 || _sellDealNr != sellDeals[sellDeals.length - 1].dealNr) {
      return uint8(-2);
    }
    
    // no price match
    if(buyDeals[buyDeals.length - 1].price < sellDeals[sellDeals.length - 1].price) {
      return uint8(-3);
    }
    
    deal dealBuy = buyDeals[buyDeals.length - 1];
    deal dealSell = sellDeals[sellDeals.length - 1];
    
    // calculate quantity and price for matching order
    uint256 quantity = dealBuy.quantity < dealSell.quantity ? dealBuy.quantity : dealSell.quantity;
    uint256 price = (dealBuy.price + dealSell.price) / 2;
    
	// update order book and notify order owners
	execute(dealBuy, quantity, price);
	execute(dealSell, quantity, price);
	
	return uint8(0);
  }
  
  function execute(deal _deal, uint256 _quantity, uint256 _price) private {
    uint256 remainingQuantity = _deal.quantity - _quantity;
    
    if(remainingQuantity == 0) {
      if(_deal.buy) {
        delete buyDeals[buyDeals.length - 1];
      }
      else {
        delete sellDeals[sellDeals.length - 1];
      }
    }
    else {
      if(_deal.buy) {
        buyDeals[buyDeals.length - 1].quantity = remainingQuantity;
      }
      else {
        sellDeals[sellDeals.length - 1].quantity = remainingQuantity;
      }
    }
    
    _deal.quantity = _quantity;
    _deal.price = _price;
    doneDeals.push(_deal);
  }
  
  function isPending(uint256 _dealNr) 
    returns (bool _pending) 
  {
    uint i;
    
    if(_dealNr < 0) {
      return false;
    }
    
  	for(i = 0; i < buyDeals.length; i++) {
  	  if(_dealNr == buyDeals[i].dealNr) {
  	    return true;
      }
  	}
  	
  	for(i = 0; i < sellDeals.length; i++) {
  	  if(_dealNr == sellDeals[i].dealNr) {
  	    return true;
  	  }
  	}
    
  	return false;
  }

  function revokeDeal(uint256 _dealNr, bool _buy) returns (string _errorMessage) {
      bool found = false;
      deal[] arrayToRemove = _buy ? buyDeals : sellDeals;
        for (uint i = 0; i < arrayToRemove.length; i++) {
            if (arrayToRemove[i].dealNr == _dealNr) {
                found = true;
            }
            if (found) {
                if (i < arrayToRemove.length -1) {
                    arrayToRemove[i] = arrayToRemove[i+1];
                } else {
                    // WARN: this does not remove an element, it just sets all values to default!!! check unit test an array state printing!
                    delete arrayToRemove[i];
                }
            }
        }
  }

  // a smart contract for each currency pair
  function createDeal(
    uint256 _quantity,
    uint256 _price,
    bool _buy,
    string _company)
    returns (uint256 _dealNrReturn) {
        _dealNrReturn = dealNr++;
        deal memory myDeal = deal({quantity:_quantity, price:_price, buy:_buy, company:_company, dealNr:_dealNrReturn});
       if (_buy) {
           buyDeals.push(myDeal);
           sortDeals(true);
       } else {
           sellDeals.push(myDeal);
           sortDeals(false);
       }
    }

    function sortDeals(bool _buy) private {
        uint256 arrayLength;
        deal memory tmp;
        uint256 i;
        
        if(_buy) {
         arrayLength = buyDeals.length;

        for (i = arrayLength - 1; i > 0; i--) {
             if(buyDeals[i].price < buyDeals[i-1].price) {
                tmp = buyDeals[i-1];
                buyDeals[i-1] = buyDeals[i];
                buyDeals[i] = tmp;
            }
         }
        }
        else {
        arrayLength = sellDeals.length;

        for (i = arrayLength - 1; i > 0; i--) {
            if(sellDeals[i].price > sellDeals[i-1].price) {
                 tmp = sellDeals[i-1];
                sellDeals[i-1] = sellDeals[i];
                sellDeals[i] = tmp;
            }
         }
        }
     }
}
