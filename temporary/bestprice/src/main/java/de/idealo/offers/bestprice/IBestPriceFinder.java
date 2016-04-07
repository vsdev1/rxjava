package de.idealo.offers.bestprice;

import java.util.List;
import java.util.Optional;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.NotFoundException;


public interface IBestPriceFinder {

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop, in shop's currency
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    Double getDiscountedPriceNullable(Shop shop, String product) throws Exception;

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop or fallback, in shop's currency
     * @throws NotFoundException
     *             if no offer for product in any shop
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    Double getDiscountedPriceWithException(Shop shop, String product) throws Exception;

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return list of offers with discounted prices in target currency
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    List<Offer> findOffers(String product, Currency targetCurrency) throws Exception;

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return average discounted price for the product in USD.
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    Double getAveragePriceNullable(String product, Currency targetCurrency) throws Exception;
}
