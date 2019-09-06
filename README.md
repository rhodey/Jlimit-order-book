# limit-order-book
A Java [limit order book](https://www.investopedia.com/terms/o/order-book.asp) implementation supporting limit and market orders.

## Install
```
$ git clone https://github.com/rhodey/limit-order-book
$ cd limit-order-book
$ mvn install
```

## pom.xml
```
<dependencies>
  <dependency>
    <groupId>org.anhonesteffort.trading.book</groupId>
    <artifactId>limit-order-book</artifactId>
    <version>0.0.1</version>
  </dependency>
</dependencies>
```

## Usage
```java
package com.example;

import org.anhonesteffort.trading.book.LimitOrderBook;
import org.anhonesteffort.trading.book.Order;
import org.anhonesteffort.trading.book.TakeResult;

public class Main {
    public static void main(String[] args) {
        LimitOrderBook book = new LimitOrderBook(16);
        Order ask = new Order("00", Order.Side.ASK, 10, 20);
        Order bid = new Order("01", Order.Side.BID, 15, 5);

        TakeResult result = book.add(ask);
        System.out.println(result.getTakeSize()); // 0.0

        result = book.add(bid);
        System.out.println(result.getTakeSize()); // 5.0
    }
}
```

## License
Copyright 2019 An Honest Effort LLC
Licensed under the GPLv3: http://www.gnu.org/licenses/gpl-3.0.html