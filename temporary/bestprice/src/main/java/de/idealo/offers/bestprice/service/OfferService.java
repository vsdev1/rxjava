package de.idealo.offers.bestprice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;

public class OfferService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferService.class);

    private static final long SERVICE_DELAY_MS = 100L;

    // shopName -> productName -> Offer
    private static Map<String, Map<String, Offer>> offerStore;

    static {
        offerStore = Shop.SHOPS.stream().collect(Collectors.toMap(Shop::getName, s -> new HashMap<>()));

        // EUR
        addOffer(Shop.SHOP_BEST_PRICE, "iPhone", 600.0);
        addOffer(Shop.SHOP_BEST_PRICE, "iPad", 500.0);
        addOffer(Shop.SHOP_BEST_PRICE, "Nexus 6", 400.0);

        // USD
        addOffer(Shop.LETS_SAVE_BIG, "iPhone", 700.0);
        addOffer(Shop.LETS_SAVE_BIG, "iPad", 550.0);
        addOffer(Shop.LETS_SAVE_BIG, "Samsung S6", 420.0);

        // MXN
        addOffer(Shop.MY_FAVORITE_SHOP, "iPhone", 1500.0);
        addOffer(Shop.MY_FAVORITE_SHOP, "iPad", 1400.0);

        // CAD
        addOffer(Shop.BUY_IT_ALL, "iPhone", 750.0);
        addOffer(Shop.BUY_IT_ALL, "iPad", 600.0);
        addOffer(Shop.BUY_IT_ALL, "Samsung S6", 800.0);

        // USD
        addOffer(Shop.BIG_SHOP, "iPhone", 850.0);
        addOffer(Shop.BIG_SHOP, "iPad", 900.0);
        addOffer(Shop.BIG_SHOP, "Samsung S6", 850.0);
        addOffer(Shop.BIG_SHOP, "Nexus 6", 600.0);
    }

    private static void addOffer(Shop shop, String productName, double price) {
        offerStore.get(shop.getName()).put(productName, new Offer(shop.getName(), productName, price, shop.getCurrency(), shop.getDiscount(), false));
    }

    public static Offer getOfferNullable(Shop shop, String productName) {
        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        return offerStore.get(shop.getName()).get(productName);
    }

    public static Offer getOfferWithException(Shop shop, String productName) throws NotFoundException {
        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        Offer offer = offerStore.get(shop.getName()).get(productName);
        if (offer == null) {
            throw new NotFoundException("No offer for shop " + shop + " and product '" + productName + "'");
        }
        return offer;
    }

    public static Optional<Offer> getOptionalOffer(Shop shop, String productName) {
        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        return Optional.ofNullable(offerStore.get(shop.getName()).get(productName));
    }
}
