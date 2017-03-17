package org.eclipse.scout.trading.network;

/**
 * <h3>{@link OrderMatch}</h3>
 *
 * @author uko
 */
public class OrderMatch {

  private int m_buyNr;
  private int m_sellNr;

  public OrderMatch(int buyNr, int sellNr) {
    setBuyNr(buyNr);
    setSellNr(sellNr);
  }

  public int getBuyNr() {
    return m_buyNr;
  }

  public void setBuyNr(int buyNr) {
    m_buyNr = buyNr;
  }

  public int getSellNr() {
    return m_sellNr;
  }

  public void setSellNr(int sellNr) {
    m_sellNr = sellNr;
  }

}
