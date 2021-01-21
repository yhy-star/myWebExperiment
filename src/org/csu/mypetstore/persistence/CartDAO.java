package org.csu.mypetstore.persistence;


import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface CartDAO {

    Cart getCartByCartId(String username);

    Cart createCart(String username, String workingItemId);

    void updateCartItem(String workingItemId, int i,String username);

    void updateCart(BigDecimal price, String username);

    CartItem createCartItem(String workingItemId, String username);

    void removeCartItem(String workingItemId, String username);

    void removeCart(String username);

    void removeCartAll(String username);

}
