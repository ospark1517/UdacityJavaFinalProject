package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTests {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items_happy_path() throws Exception{
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("this is a test");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAll()).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(response.getBody().get(0).getName(), "test item");
        assertEquals(response.getBody().get(0).getDescription(), "this is a test");
        assertEquals(response.getBody().get(0).getPrice(), price);
    }

    @Test
    public void get_item_by_id_happy_path() throws Exception{
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("this is a test");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(response.getBody().getName(), "test item");
        assertEquals(response.getBody().getDescription(), "this is a test");
        assertEquals(response.getBody().getPrice(), price);
    }

    @Test
    public void get_items_by_name_happy_path() throws Exception{
        User user = new User();
        user.setUsername("submit");

        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("this is a test");
        BigDecimal price = new BigDecimal("3.99");
        item.setPrice(price);

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findByName("submit")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("submit");
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(response.getBody().get(0).getName(), "test item");
        assertEquals(response.getBody().get(0).getDescription(), "this is a test");
        assertEquals(response.getBody().get(0).getPrice(), price);
    }

    @Test
    public void get_items_by_name_sad_path_null_items() throws Exception{
        User user = new User();
        user.setUsername("submit");

        when(itemRepository.findByName("submit")).thenReturn(null);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("submit");
        
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void get_items_by_name_sad_path_items_empty() throws Exception{
        User user = new User();
        user.setUsername("submit");

        when(itemRepository.findByName("submit")).thenReturn(new ArrayList<>());

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("submit");
        
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }


}
