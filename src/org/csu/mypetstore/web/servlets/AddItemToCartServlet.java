package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.CatalogService;
import org.csu.mypetstore.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

public class AddItemToCartServlet extends HttpServlet {
    //Servlet的功能即负责中转
    //1.处理完请求后的跳转页面
    private static final String VIEW_CART = "/WEB-INF/jsp/cart/Cart.jsp";
    private static final String SIGN_ON = "/WEB-INF/jsp/account/SignonForm.jsp";

    //2.定义处理该请求所需要的数据
    private String workingItemId;
    private Cart cart;             //购物车

    //3.是否需要调用业务逻辑层
    private CatalogService catalogService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        workingItemId = request.getParameter("workingItemId");
        HttpSession session = request.getSession();

        Account account = (Account) session.getAttribute("account");

        if(account == null){
            request.getRequestDispatcher(SIGN_ON).forward(request,response);
            return;
        }
        CartService cartService = new CartService();
        Cart cart = cartService.getCartByCartId(account.getUsername());
        if(cart == null){
            cart = cartService.createCart(account.getUsername(),workingItemId);
        }else{
            boolean is = false;
            int count = 0;
            for(int i = 0;i < cart.getCartItemList().size();i++){
                if(cart.getCartItemList().get(i).getItem().getItemId().equals(workingItemId)){
                    count = cart.getCartItemList().get(i).getQuantity() + 1;
                    cart.getCartItemList().get(i).setQuantity(count);
                    cart.getCartItemList().get(i).setTotal(cart.getCartItemList().get(i).getItem().getListPrice().multiply(new BigDecimal(count)));
                    cart.setPrice(cart.getSubTotal());
                    is = true;
                    break;
                }
            }

            if(is){
                cartService.updateCartItem(workingItemId,count,account.getUsername());
                cartService.updateCart(cart.getSubTotal(),account.getUsername());
            }else{
                CartItem cartItem = cartService.createCartItem(workingItemId,account.getUsername());
                cart.getCartItemList().add(cartItem);
                cart.setPrice(cart.getSubTotal().add(cartItem.getTotal()));
                cartService.updateCart(cart.getSubTotal(),account.getUsername());
            }
        }

        account.setCart(cart);
        request.getRequestDispatcher(VIEW_CART).forward(request,response);

    }
}
