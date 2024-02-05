package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.Cart;
import com.example.demo.model.Item;
import com.example.demo.model.User;
import com.example.demo.model.UserOrder;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.ItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.requests.CreateUserRequest;
import com.example.demo.requests.ModifyCartRequest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

public class ControllersTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);




    @Test
    public void addToCart() {
        //User
        User newUser = new User();
        newUser.setId(5L);
        newUser.setUsername("Username");
        //item
        Item item = new Item() {
            {
                setId(5L);
                setName("item");
                setPrice(new BigDecimal("5"));
                setDescription("description for the itm ");
            }
        };
        //cart
        Cart c = new Cart();
        c.setId(5L);
        List<Item> item1 = new ArrayList<>();
        item1.add(item);
        c.setItems(item1);
        c.setTotal(item.getPrice());
        //cart to user
        c.setUser(newUser);
        newUser.setCart(c);

        doReturn(newUser).when(userRepository).findByUsername("Username");
        doReturn(Optional.of(item)).when(itemRepository).findById(5L);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(5L);
        request.setQuantity(1);
        request.setUsername("Username");
        CartController cartController = new CartController(userRepository, cartRepository, itemRepository);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Cart retrievedCart = responseEntity.getBody();
        assertNotNull(retrievedCart);
        assertEquals((Long) 5L, retrievedCart.getId());
        List<Item> items2 = retrievedCart.getItems();
        assertNotNull(items2);
        Item retrievedItem = items2.get(0);
        assertAll("Cart assertions",
                () -> assertEquals(2, items2.size()),
                () -> assertNotNull(retrievedItem),
                () -> assertEquals(item, retrievedItem),
                //5+5=10
                () -> assertEquals(new BigDecimal("10"), retrievedCart.getTotal()),
                () -> assertEquals(newUser, retrievedCart.getUser())
        );

    }

    @Test
    public void removeFromCart() {
        //user to be deleted
        User deleteUser = new User();
        deleteUser.setUsername("Username");
       //item
        Item item = new Item();
        item.setId(5L);
        item.setName("item");
        item.setPrice(new BigDecimal("5"));
        item.setDescription(" description for the item ");
        //new cart
        Cart cart = new Cart();
        cart.setId(5L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        //insert items to cart
        cart.setItems(itemList);
        cart.setTotal(item.getPrice());
        cart.setUser(deleteUser);
        deleteUser.setCart(cart);


        doReturn(deleteUser).when(userRepository).findByUsername("Username");
        doReturn(Optional.of(item)).when(itemRepository).findById(5L);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(5L);
        request.setQuantity(1);
        request.setUsername("Username");
        CartController cartController = new CartController(userRepository, cartRepository, itemRepository);
        AtomicReference<ResponseEntity<Cart>> responseEntity = new AtomicReference<>(cartController.addTocart(request));

        assertAll("Cart assertions",
                () -> {
                    assertNotNull(responseEntity);
                    assertEquals(200, responseEntity.get().getStatusCodeValue());
                    Cart retrievedCart = responseEntity.get().getBody();
                    assertNotNull(retrievedCart);
                    assertEquals((Long) 5L, retrievedCart.getId());
                    List<Item> items = retrievedCart.getItems();
                    assertNotNull(items);
                    Item retrievedItem = items.get(0);
                    assertEquals(2, items.size());
                    assertNotNull(retrievedItem);
                    assertEquals(item, retrievedItem);
                    assertEquals(new BigDecimal("10"), retrievedCart.getTotal());
                    assertEquals(deleteUser, retrievedCart.getUser());
                },
                () -> {
                    responseEntity.set(cartController.removeFromcart(request));
                    assertNotNull(responseEntity);
                    assertEquals(200, responseEntity.get().getStatusCodeValue());
                    Cart retrievedCart = responseEntity.get().getBody();
                    assertNotNull(retrievedCart);
                    assertEquals((Long) 5L, retrievedCart.getId());
                    List<Item> items = retrievedCart.getItems();
                    assertNotNull(items);
                    Item retrievedItem = items.get(0);
                    assertEquals(1, items.size());
                    assertNotNull(retrievedItem);
                    assertEquals(new BigDecimal("5"), retrievedCart.getTotal());
                    assertEquals(deleteUser, retrievedCart.getUser());
                }
        );

    }

    @Test
    public void testAddToEmptyUserCart() {

        User Nulluser = new User();
        Nulluser.setUsername("Username");
        Cart cart = new Cart();
        Item item = new Item();
        cart.setId(5L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("5"));
        cart.setUser(Nulluser);
        Nulluser.setCart(cart);
//null
        doReturn(null).when(userRepository).findByUsername(Nulluser.getUsername());
        doReturn(Optional.of(item)).when(itemRepository).findById(5L);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(5L);
        request.setQuantity(1);
        request.setUsername("requestUsername");
        CartController cartController = new CartController(userRepository, cartRepository, itemRepository);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
    //****order controller****
    @Test
    public void Submit() {
        User user = new User();
user.setUsername("Username");
//item
        Item item = new Item();
        item.setId(5L);
        item.setName("item");
        item.setPrice(new BigDecimal("5"));
        item.setDescription(" description for the item ");

        Cart cart = new Cart();
        cart.setId(5L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        //insert items to cart
        cart.setItems(itemList);
        cart.setTotal(item.getPrice());
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("Username")).thenReturn(user);


        OrderController orderController = new OrderController(userRepository,orderRepository);
        ResponseEntity<UserOrder> response = orderController.submit("Username");

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        UserOrder retrievedUserOrder = response.getBody();
        assertThat(retrievedUserOrder).isNotNull();
        assertThat(retrievedUserOrder.getItems()).isNotNull();
        assertThat(retrievedUserOrder.getTotal()).isNotNull();
        assertThat(retrievedUserOrder.getUser()).isNotNull();
    }

    @Test
    public void getOrdersForUser() {
        User user = new User();
        user.setUsername("Username");
        user.setId(5L);

        //item
        Item item = new Item();
        item.setId(5L);
        item.setName("item");
        item.setPrice(new BigDecimal("5"));
        item.setDescription(" description for the item ");

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Cart cart = new Cart();
        cart.setItems(itemList);
        cart.setTotal(item.getPrice());
        cart.setUser(user);
        user.setCart(cart);

        doReturn(user).when(userRepository).findByUsername("Username");

        OrderController orderController = new OrderController(userRepository,orderRepository);
        orderController.submit("Username");
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("Username");

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        List<UserOrder> userOrders = responseEntity.getBody();
        Assertions.assertThat(userOrders).isEmpty();
        Assertions.assertThat(userOrders).isNotNull();

    }
    @Test
    public void getOrdersForNoneExitingUser() {
        doReturn(null).when(userRepository).findByUsername("Username");
        OrderController orderController = new OrderController(userRepository,orderRepository);
        orderController.submit("Username");

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("Username");

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }
    @Test
    public void SubmitNoneExitingUser() {
        //null
        doReturn(null).when(userRepository).findByUsername("Username");
        //create orderController
        OrderController orderController = new OrderController(userRepository,orderRepository);
        //submit null user

        ResponseEntity<UserOrder> response = orderController.submit("Username");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
//***USer controller ***

    @Test
    public void createUser() {

        CreateUserRequest request = new CreateUserRequest("Username","Pass123");
        User user = new User();
        user.setUsername("Username");
        doReturn("Pass123").when(encoder).encode(request.getPassword());
        doReturn(user).when(userRepository).save(ArgumentMatchers.any(User.class));
         UserController userController = new UserController(userRepository,cartRepository,encoder);
        ResponseEntity<User> response = userController.createUser(request);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        User newUser = response.getBody();
        assertThat(newUser).isNotNull();
        assertThat(newUser.getId()).isEqualTo(0);
        assertThat(newUser.getUsername()).isEqualTo("Username");
        assertThat(newUser.getPassword()).isEqualTo("Pass123");
    }

    @Test
    public void findByUserName() {

        CreateUserRequest createUserRequest = new CreateUserRequest("Username","pass123");
        UserController userController = new UserController(userRepository,cartRepository,encoder);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User newUser = response.getBody();
        assertThat(newUser).isNotNull();

        doReturn(newUser).when(userRepository).findByUsername("Username");

        ResponseEntity<User> res = userController.findByUserName("Username");
        User foundUser = res.getBody();

        assertThat(foundUser).isNotNull();
        //same id
        assertThat(foundUser.getId()).isEqualTo(newUser.getId());
        //sane user name
        assertThat(foundUser.getUsername()).isEqualTo(newUser.getUsername());
        //same password
        assertThat(foundUser.getPassword()).isEqualTo(newUser.getPassword());
    }
    @Test
    public void findById() {

        CreateUserRequest createUserRequest = new CreateUserRequest("Username","pass123");
        UserController userController = new UserController(userRepository,cartRepository,encoder);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User newUser = response.getBody();

        when(userRepository.findById(eq(newUser.getId()))).thenReturn(Optional.of(newUser));

        ResponseEntity<User> res = userController.findById(newUser.getId());
        User foundUser = res.getBody();

        assertNotNull(foundUser);
        //same id
        assertEquals(newUser.getId(), foundUser.getId());
        //same user name
        assertEquals(newUser.getUsername(), foundUser.getUsername());
        //same password
        assertEquals(newUser.getPassword(), foundUser.getPassword());
    }
    //****ItemController***


    @Test
    public void testGetItems() {
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setName("item1");
        item2.setName("item2");
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);
ItemController itemController=new ItemController(itemRepository);
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> retrievedItems = response.getBody();
        assertEquals(2, retrievedItems.size());
        assertEquals(item1, retrievedItems.get(0));
        assertEquals(item2, retrievedItems.get(1));
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(5L);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        ItemController itemController=new ItemController(itemRepository);
        ResponseEntity<Item> response = itemController.getItemById(5L);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item retrievedItem = response.getBody();
        assertNotNull(retrievedItem);
        assertEquals(item, retrievedItem);
    }



//additional

    @Test
    public void submitOrderReturnTest() {
        User user = new User();
        user.setUsername("Username");

        Item item = new Item();
        item.setId(5L);
        item.setName("item");
        item.setPrice(new BigDecimal("5"));
        item.setDescription(" description for the item ");

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Cart cart = new Cart();
        cart.setItems(itemList);
        cart.setTotal(item.getPrice());
        cart.setUser(user);
        user.setCart(cart);

        doReturn(user).when(userRepository).findByUsername("Username");

        OrderController orderController = new OrderController(userRepository,orderRepository);
        ResponseEntity<UserOrder> response = orderController.submit("Username");

        Assertions.assertThat(response).isNotNull();
        //200 ok
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        //response
        UserOrder retrievedUserOrder = response.getBody();
        //not null
        Assertions.assertThat(retrievedUserOrder).isNotNull();
        //size 1
        Assertions.assertThat(retrievedUserOrder.getItems()).hasSize(1);
        //same total
        Assertions.assertThat(retrievedUserOrder.getTotal()).isEqualByComparingTo(new BigDecimal("5"));
        //same user
        Assertions.assertThat(retrievedUserOrder.getUser()).isEqualTo(user);
    }
}