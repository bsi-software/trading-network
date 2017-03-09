pragma solidity ^0.4.2;

contract FXTrading {

  deal[] public buyDeals;
  deal[] public sellDeals;

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

  function mergeDeals(
    uint256 _dealNr1,
    uint256 _dealNr2)
    returns (string _errorMessage) {
    // todo implement
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

//  function orderArray() private
  // priority queue, order book, buyer has to confirm
}
