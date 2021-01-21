package org.csu.mypetstore.persistence.Impl;

import org.csu.mypetstore.domain.*;
import org.csu.mypetstore.persistence.CartDAO;
import org.csu.mypetstore.persistence.DBUtil;
import org.csu.mypetstore.persistence.ItemDAO;
import org.csu.mypetstore.service.CatalogService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CartDAOImpl implements CartDAO {

    private static final String SEARCHCART= "select * FROM cart WHERE cartanduserId = ?";

    private static final String SEARCHCARTIEM= "select * FROM cartItem WHERE userId = ?";

    private static final String CREATECART = "INSERT INTO cart(cartanduserId,price) VALUES(?,?)";

    private static final String CREATECARTITEM = "INSERT INTO cartItem(itemId,quantity,isStock,total,userId) VALUES(?,?,?,?,?)";

    private static final String UPDATECARTITEM =  "UPDATE cartItem SET quantity = ?,total = ? WHERE userId= ? and itemId = ?";

    private static final String UPDATECART  = "UPDATE cart SET price = ? WHERE cartanduserId = ?";

    private static final String DELETECARTITEM = "DELETE from cartItem WHERE itemId = ? and userId = ?";

    private static final String DELETECART = "DELETE from cart WHERE cartanduserId = ?";

    private static final String DELETECARTITEMALL = "DELETE from cartItem WHERE userId = ?";

    public Cart getCartByCartId(String username){
        Cart cart = null;
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCHCART);
            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                BigDecimal price = resultSet.getBigDecimal(2);
                cart = new Cart();
                ArrayList<CartItem> cartItemList = getCartItemByCartItemId(resultSet.getString(1));
                cart.setList(cartItemList);
                cart.setPrice(price);
            }
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);

        }catch(Exception e){
            e.printStackTrace();
        }
        return cart;
    }

    public ArrayList<CartItem> getCartItemByCartItemId(String string) {
        ArrayList<CartItem> cartItemList = null;

        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SEARCHCARTIEM);
            preparedStatement.setString(1,string);
            ResultSet resultSet = preparedStatement.executeQuery();
            cartItemList = new ArrayList<>();
            while(resultSet.next()){
                CartItem cartItem = new CartItem();

                CatalogService catalogService = new CatalogService();
                Item item = catalogService.getItem(resultSet.getString(1));
                cartItem.setItem(item);
                cartItem.setQuantity(resultSet.getInt(2));
                cartItem.setInStock(resultSet.getInt(3) == 1);
                cartItem.setTotal(resultSet.getBigDecimal(4));
                cartItemList.add(cartItem);
            }

            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);

        }catch (Exception e){
            e.printStackTrace();
        }

        return cartItemList;
    }

    public  Cart createCart(String username, String workingItemId){
        Cart cart = new Cart();
        try{

            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATECART);
            PreparedStatement preparedStatement1 = connection.prepareStatement(CREATECARTITEM);

            CatalogService catalogService = new CatalogService();
            Item item = catalogService.getItem(workingItemId);
            cart.setPrice(item.getListPrice());

            preparedStatement.setString(1,username);
            preparedStatement.setBigDecimal(2,item.getListPrice());
            preparedStatement.executeUpdate();

            preparedStatement1.setString(1,workingItemId);
            preparedStatement1.setInt(2,1);
            preparedStatement1.setInt(3,1);
            preparedStatement1.setBigDecimal(4,item.getListPrice());
            preparedStatement1.setString(5,username);
            preparedStatement1.executeUpdate();

            ArrayList<CartItem> cartItemList = new ArrayList<>();
            CartItem cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(1);
            cartItem.setInStock(true);
            cartItem.setTotal(item.getListPrice());
            cartItemList.add(cartItem);
            cart.setList(cartItemList);

            DBUtil.closePreparedStatent(preparedStatement1);
            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cart;
    }

    public CartItem createCartItem(String workingItemId, String username){
        CartItem cartItem = new CartItem();
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATECARTITEM);
            PreparedStatement preparedStatement1 = connection.prepareStatement(UPDATECART);

            CatalogService catalogService = new CatalogService();
            Item item = catalogService.getItem(workingItemId);

            preparedStatement.setString(1,workingItemId);
            preparedStatement.setInt(2,1);
            preparedStatement.setInt(3,1);
            preparedStatement.setBigDecimal(4,item.getListPrice());
            preparedStatement.setString(5,username);
            preparedStatement.executeUpdate();

            cartItem.setItem(item);
            cartItem.setTotal(item.getListPrice());
            cartItem.setInStock(true);
            cartItem.setQuantity(1);

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);

        }catch (Exception e){
            e.printStackTrace();
        }
        return cartItem;
    }

    public void updateCartItem(String workingItemId, int i,String username){
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATECARTITEM);

            CatalogService catalogService = new CatalogService();
            Item item = catalogService.getItem(workingItemId);
            preparedStatement.setInt(1,i);

            preparedStatement.setBigDecimal(2,item.getListPrice().multiply(new BigDecimal(i)));
            preparedStatement.setString(3,username);
            preparedStatement.setString(4,workingItemId);
            preparedStatement.executeUpdate();

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateCart(BigDecimal price, String username){
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATECART);
            preparedStatement.setBigDecimal(1,price);
            preparedStatement.setString(2,username);
            preparedStatement.executeUpdate();

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeCartItem(String workingItemId, String username){
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETECARTITEM);
            preparedStatement.setString(1,workingItemId);
            preparedStatement.setString(2,username);
            preparedStatement.executeUpdate();

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void removeCart(String username){
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETECART);
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void removeCartAll(String username) {
        try{
            Connection connection = DBUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETECARTITEMALL);
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();

            DBUtil.closePreparedStatent(preparedStatement);
            DBUtil.closeConnection(connection);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
