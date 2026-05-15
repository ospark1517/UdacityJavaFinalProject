package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTests {
    
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_order_happy_path(){
        User user = new User();
        user.setId(1L);
        user.setUsername("submit");
        user.setPassword("hashedPass");
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);
        item.setDescription("This is a test description");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername("submit")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("submit");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder u = response.getBody();
        assertNotNull(u);

    }

    @Test
    public void submit_order_sad_path(){
        User user = new User();
        user.setId(1L);
        user.setUsername("submit");
        user.setPassword("hashedPass");
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);
        item.setDescription("This is a test description");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername("submit")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("submit");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void get_orders_for_user_happy_path() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setUsername("submit");
        user.setPassword("hashedPass");
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);
        item.setDescription("This is a test description");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        user.setCart(cart);
        cart.setUser(user);

        UserOrder userOrder = UserOrder.createFromCart(user.getCart());
        List<UserOrder> orders = new ArrayList<>();
        orders.add(userOrder);


        when(userRepository.findByUsername("submit")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("submit");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<UserOrder> resOrders = response.getBody();
        assertNotNull(resOrders);
        assertEquals(resOrders.get(0).getItems().get(0).getName(), "test item");
        assertEquals(resOrders.get(0).getUser().getUsername(), "submit");
        assertEquals(resOrders.get(0).getTotal(), price);
    }

    @Test
    public void get_orders_for_user_sad_path() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setUsername("submit");
        user.setPassword("hashedPass");
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);
        item.setDescription("This is a test description");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        user.setCart(cart);
        cart.setUser(user);

        UserOrder userOrder = UserOrder.createFromCart(user.getCart());
        List<UserOrder> orders = new ArrayList<>();
        orders.add(userOrder);


        when(userRepository.findByUsername("submit")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("submit");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
    }
}
