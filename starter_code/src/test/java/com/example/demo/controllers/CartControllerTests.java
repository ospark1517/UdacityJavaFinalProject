package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class CartControllerTests {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_happy_path() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setPrice(BigDecimal.valueOf(3.99));

        when(userRepository.findByUsername("request")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.addTocart(modifyRequest);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(response.getBody().getItems().get(0).getName(), "test item");
    }
    @Test
    public void add_to_cart_sad_path_user_not_exist() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);

        when(userRepository.findByUsername("request")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyRequest);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_sad_path_item_not_exist() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setPrice(BigDecimal.valueOf(3.99));

        when(userRepository.findByUsername("request")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.addTocart(modifyRequest);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }


    @Test
    public void remove_from_cart_happy_path() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setPrice(BigDecimal.valueOf(3.99));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("other test");
        item2.setPrice(BigDecimal.valueOf(2.99));

        user.getCart().addItem(item);
        user.getCart().addItem(item2);

        when(userRepository.findByUsername("request")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyRequest);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(response.getBody().getItems().get(0).getName(), "other test");
    }

    @Test
    public void remove_from_cart_sad_path_user_not_exist() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);

        when(userRepository.findByUsername("request")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyRequest);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_frpm_cart_sad_path_item_not_exist() throws Exception{
        ModifyCartRequest modifyRequest = new ModifyCartRequest();
        modifyRequest.setUsername("request");
        modifyRequest.setItemId(1L);
        modifyRequest.setQuantity(3);

        Cart cart = new Cart();

        User user = new User();
        user.setUsername("request");
        user.setCart(cart);
        
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setPrice(BigDecimal.valueOf(3.99));

        when(userRepository.findByUsername("request")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyRequest);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

}
