/*
 * Copyright (C) 2016 An Honest Effort LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.anhonesteffort.trading.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LimitOrderBook {

  protected final LimitQueue askLimits;
  protected final LimitQueue bidLimits;

  public LimitOrderBook(int initLimitSize) {
    askLimits = new LimitQueue(Order.Side.ASK, initLimitSize);
    bidLimits = new LimitQueue(Order.Side.BID, initLimitSize);
  }

  public LimitQueue getAskLimits() {
    return askLimits;
  }

  public LimitQueue getBidLimits() {
    return bidLimits;
  }

  private List<Order> matchTaker(Order taker) {
    List<Order> makers    = new ArrayList<>();
    LimitQueue  makeLimit = taker.side == Order.Side.ASK ? bidLimits : askLimits;
    LimitQueue  takeLimit = taker.side == Order.Side.ASK ? askLimits : bidLimits;
    List<Order> matched   = makeLimit.takeLiquidityFromBestLimit(taker);

    while (!matched.isEmpty()) {
      makers.addAll(matched);
      matched = makeLimit.takeLiquidityFromBestLimit(taker);
    }

    if (taker.getSizeRemaining() > 0d && !(taker instanceof MarketOrder)) {
      takeLimit.addOrder(taker);
    }

    return makers;
  }

  public TakeResult add(Order taker) {
    List<Order> makers = matchTaker(taker);
    if (!(taker instanceof MarketOrder)) {
      return new TakeResult(taker, makers, (taker.getSize() - taker.getSizeRemaining()));
    } else {
      return new TakeResult(taker, makers, ((MarketOrder) taker).getVolumeRemoved());
    }
  }

  public Optional<Order> remove(Order.Side side, Double price, String orderId) {
    if (side.equals(Order.Side.ASK)) {
      return askLimits.removeOrder(price, orderId);
    } else {
      return bidLimits.removeOrder(price, orderId);
    }
  }

  public Optional<Order> reduce(Order.Side side, Double price, String orderId, double size) {
    if (side.equals(Order.Side.ASK)) {
      return askLimits.reduceOrder(price, orderId, size);
    } else {
      return bidLimits.reduceOrder(price, orderId, size);
    }
  }

  public void clear() {
    askLimits.clear();
    bidLimits.clear();
  }

}
