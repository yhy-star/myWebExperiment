package org.csu.mypetstore.web.servlets;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.LogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RemoveItemFromCartServlet extends HttpServlet {

    private static final String VIEW_CART = "/WEB-INF/jsp/cart/Cart.jsp";
    private static final String ERROR = "/WEB-INF/jsp/common/Error.jsp";

    private String workingItemId;
    private Cart cart;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        workingItemId = request.getParameter("workingItemId");

        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if(account == null){
            session.setAttribute("message", "请先登录！");
            request.getRequestDispatcher(ERROR).forward(request,response);
            return;
        }

        if(account.getCart() == null){
            session.setAttribute("message", "你的购物车为空！");
            request.getRequestDispatcher(ERROR).forward(request,response);
            return;
        }

        for (int i = 0; i < account.getCart().getCartItemList().size(); i++) {
            if (account.getCart().getCartItemList().get(i).getItem().getItemId().equals(workingItemId)) {
                account.getCart().getCartItemList().remove(i);
                account.getCart().setPrice(account.getCart().getSubTotal());
                break;
            }
        }

        CartService cartService = new CartService();
        cartService.removeCartItem(workingItemId, account.getUsername());
        cartService.updateCart(account.getCart().getSubTotal(), account.getUsername());

        if (account.getCart().getCartItemList().size() == 0) {
            account.setCart(null);
            cartService.removeCart(account.getUsername());
        }

        request.getRequestDispatcher(VIEW_CART).forward(request,response);


//        cart = (Cart)session.getAttribute("cart");
//
//        Item item = cart.removeItemById(workingItemId);
//
//        if(item == null) {
//            session.setAttribute("message", "Attempted to remove null CartItem from Cart.");
//
//            Account account = (Account)session.getAttribute("account");
//
//            if(account != null){
//                HttpServletRequest httpRequest= request;
//                String strBackUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
//                        + httpRequest.getContextPath() + httpRequest.getServletPath() + "?" + (httpRequest.getQueryString());
//
//                LogService logService = new LogService();
//                String logInfo = logService.logInfo(" ") + strBackUrl + " 物品为空，不能移除";
//                logService.insertLogInfo(account.getUsername(), logInfo);
//            }
//
//            request.getRequestDispatcher(ERROR).forward(request, response);
//        }else{
//
//            Account account = (Account)session.getAttribute("account");
//
//            if(account != null){
//                HttpServletRequest httpRequest= request;
//                String strBackUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
//                        + httpRequest.getContextPath() + httpRequest.getServletPath() + "?" + (httpRequest.getQueryString());
//
//                LogService logService = new LogService();
//                String logInfo = logService.logInfo(" ") + strBackUrl + " " + item + " 已从购物车中移除";
//                logService.insertLogInfo(account.getUsername(), logInfo);
//            }
//
//            request.getRequestDispatcher(VIEW_CART).forward(request, response);
//        }
    }
}
