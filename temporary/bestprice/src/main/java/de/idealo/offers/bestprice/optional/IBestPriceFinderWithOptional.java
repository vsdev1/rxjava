package de.idealo.offers.bestprice.optional;

import java.util.List;
import java.util.Optional;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.NotFoundException;


public interface IBestPriceFinderWithOptional {

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return optional discounted price for product offered by shop, in shop's currency
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    Optional<Double> getOptionalDiscountedPrice(Shop shop, String product) throws Exception;

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return optional average discounted price for the product in USD.
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    Optional<Double> getOptionalAveragePrice(String product, Currency targetCurrency) throws Exception;
}
