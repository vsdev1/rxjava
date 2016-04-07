package org.coding.reactive.rxjava.bestprice.model;

import static org.coding.reactive.rxjava.bestprice.model.DiscountCode.NONE;

import java.util.Optional;

public class Offer {

    private final String shopName;
    private final String productName;
    private final double price;
    private final Currency currency;
    private final DiscountCode discountCode;
    private final boolean discountApplied;

    public Offer(String shopName, String productName, double price, Currency currency, DiscountCode discountCode, boolean discountApplied) {
        this.shopName = shopName;
        this.productName = productName;
        this.price = price;
        this.currency = currency;
        this.discountCode = Optional.ofNullable(discountCode).orElse(NONE);
        this.discountApplied = discountApplied;
    }

    /**
     * @param newPrice
     *            new price
     * @param newCurrency
     *            new currency
     * @return a new offer instance with new price and currency
     */
    public Offer withNewPriceAndCurrency(double newPrice, Currency newCurrency) {
        return new Offer(shopName, productName, newPrice, newCurrency, discountCode, discountApplied);
    }

    /**
     * @param newPrice
     *            new price
     * @return a new offer instance with new price and discountApplied == true
     */
    public Offer withDiscountedPrice(double newPrice) {
        return new Offer(shopName, productName, newPrice, currency, discountCode, true);
    }

    public String getShopName() {
        return shopName;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public boolean isDiscountApplied() {
        return discountApplied;
    }

    @Override
    public String toString() {
        return "Offer [" + "shopName='" + shopName + '\'' + ", productName='" + productName + '\'' + ", price=" + price + ", currency=" + currency
                + ", discountCode=" + discountCode + ", discountApplied=" + discountApplied + ']';
    }
}
