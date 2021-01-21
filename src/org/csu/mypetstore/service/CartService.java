package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.persistence.CartDAO;
import org.csu.mypetstore.persistence.Impl.CartDAOImpl;

import java.math.BigDecimal;

public class CartService {
    CartDAO cartDAO;

    public CartService(){
        cartDAO = new CartDAOImpl();
    }
    public Cart getCartByCartId(String username) {
        return cartDAO.getCartByCartId(username);
    }

    public Cart createCart(String username, String workingItemId) {
        return cartDAO.createCart(username,workingItemId);
    }

    public void updateCartItem(String workingItemId, int i,String username) {
        cartDAO.updateCartItem(workingItemId,i,username);
    }

    public void updateCart(BigDecimal price, String username) {
        cartDAO.updateCart(price,username);
    }

    public CartItem createCartItem(String workingItemId, String username) {
        return cartDAO.createCartItem(workingItemId,username);
    }

    public void removeCartItem(String workingItemId, String username) {
        cartDAO.removeCartItem(workingItemId,username);
    }

    public void removeCart(String username) {
        cartDAO.removeCart(username);
    }

    public void removeCartItemAll(String username) {
        cartDAO.removeCartAll(username);
    }
}
