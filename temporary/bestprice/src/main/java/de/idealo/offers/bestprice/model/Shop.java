package de.idealo.offers.bestprice.model;

import java.util.Arrays;
import java.util.List;

public class Shop {

    public static final List<Shop> SHOPS;

    public static final Shop SHOP_BEST_PRICE = new Shop("BestPrice", Currency.EUR, DiscountCode.DIAMOND);

    public static final Shop LETS_SAVE_BIG = new Shop("LetsSaveBig", Currency.USD, DiscountCode.GOLD);

    public static final Shop MY_FAVORITE_SHOP = new Shop("MyFavoriteShop", Currency.MXN, DiscountCode.NONE);

    public static final Shop BUY_IT_ALL = new Shop("BuyItAll", Currency.CAD, DiscountCode.SILVER);

    public static final Shop BIG_SHOP = new Shop("BigShop", Currency.USD, DiscountCode.SILVER);

    static {
        SHOPS = Arrays.asList(SHOP_BEST_PRICE, LETS_SAVE_BIG, MY_FAVORITE_SHOP, BUY_IT_ALL, BIG_SHOP);
    }

    private final String name;
    private final Currency currency;
    private final DiscountCode discount;

    // private so that no new Shops can be created after OfferService shopName->productName->Offer map has been initialized
    private Shop(String name, Currency currency, DiscountCode discount) {
        this.name = name;
        this.currency = currency;
        this.discount = discount;
    }

    public DiscountCode getDiscount() {
        return discount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Shop [" + "name='" + name + '\'' + ", currency=" + currency + ", discount=" + discount + ']';
    }
}
