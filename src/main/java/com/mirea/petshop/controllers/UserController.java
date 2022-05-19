package com.mirea.petshop.controllers;
import com.mirea.petshop.models.Basket;
import com.mirea.petshop.models.Product;
import com.mirea.petshop.models.User;
import com.mirea.petshop.services.*;

import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {
    private final TypeService typeService;
    private final ProductService productService;
    private final CriteriaService criteriaService;
    private final UserService userService;
    private final EmailService emailService;
    private final BasketService basketService;

    private String addBasketStatus = "";

    public UserController(TypeService typeService, ProductService productService, CriteriaService criteriaService,
                          UserService userService, EmailService emailService, BasketService basketService) {
        this.typeService = typeService;
        this.productService = productService;
        this.criteriaService = criteriaService;
        this.userService = userService;
        this.emailService = emailService;
        this.basketService = basketService;
    }

    @GetMapping
    public String index(@RequestParam(name = "typeId", required = false) Integer typeId, Model model,
                        Authentication authentication) {
        String userRole = getUserRole(authentication);
        if (typeId == null)
            typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        return "UserController/index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(name = "typeId", required = false) Integer typeId, Model model,
                           Authentication authentication) {
        String userRole = getUserRole(authentication);
        if (typeId == null)
            typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProductsByTypeId(typeId));
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        return "UserController/products";
    }

    @GetMapping("/product")
    public String product(@RequestParam(name = "productId",required = false) Integer productId,
                           @RequestParam(name = "typeId", required = false) Integer typeId,
                           Model model, Authentication authentication) {
        String userRole = getUserRole(authentication);
        if (productId == null)
            productId = 0;
        if (typeId == null)
            typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProductsByTypeId(typeId));
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("Status", addBasketStatus);
        reloadAddBasketStatus();
        return "UserController/product";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam(name = "name") String name, Model model, Authentication authentication) {
        String userRole = getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("products",criteriaService.getAllByName(name));
        return "UserController/search";
    }

    private String getUserRole(Authentication authentication) {
        if (authentication == null)
            return "GUEST";
        else
            return ((User)userService.loadUserByUsername(authentication.getName())).getRole();
    }

    private int getUserId(Authentication authentication) {
        if (authentication == null)
            return 0;
        else
            return ((User)userService.loadUserByUsername(authentication.getName())).getId();
    }

    @GetMapping("/sign")
    public String sign() {
        return "UserController/registration";
    }

    @PostMapping("/sign")
    public String signCreate(HttpServletRequest request,
                             @RequestParam(name = "email") String email,
                             @RequestParam(name = "username") String username,
                             @RequestParam(name = "password") String password, Model model) {
        if (userService.loadUserByUsername(username) != null) {
            model.addAttribute("Status", "user_exists");
            return "UserController/registration";
        } else {
            userService.create(email,username,password);
            authWithHttpServletRequest(request, username, password);
            return "redirect:/";
        }
    }

    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
        }
    }

    private void reloadAddBasketStatus() {
        addBasketStatus = "";
    }

    private void setAddBasketStatus(String status) {
        addBasketStatus = status;
    }

    private int getTotalPrice(List<Basket> baskets) {
        int res = 0;
        for (Basket basket : baskets) {
            res += productService.getProductById(basket.getProductId()).getPrice() * basket.getProductCount();
        }
        return res;
    }

    private String createMessageForUser(List<Basket> userBaskets) {
        List<Product> userProducts = new ArrayList<>();
        for (Basket basket:userBaskets){
            userProducts.add(productService.getProductById(basket.getProductId()));
        }
        String res = "Здравствуйте, ваш заказ:<br>";
        for (int i = 0; i < userProducts.size(); i++) {
            res += (i + 1) + ") " + userProducts.get(i).getName() + " (Количество: " +
                    userBaskets.get(i).getProductCount() + ", Стоимость: " + (userProducts.get(i).getPrice() *
                    userBaskets.get(i).getProductCount()) + " ₽)<br>";
        }
        res += "Конечная стоимость: " + getTotalPrice(userBaskets) + " ₽<br>";
        return res;
    }

    @PostMapping("/product")
    public String addToBasket(Authentication authentication,
                              @RequestParam(name="productId", required = false) Integer productId,
                              @RequestParam(name = "productCount") int productCount) {
        String userRole = getUserRole(authentication);
        String redirectString = "redirect:/product?productId=" + productId;
        if (userRole == "GUEST") {
            setAddBasketStatus("user_guest");
            return redirectString;
        } else {
            int userId = getUserId(authentication);
            Basket basket = basketService.getBasketByUserIdAndProductId(userId, productId);
            if (basket == null) {
                Basket newBasket = new Basket();
                newBasket.setUserId(userId);
                newBasket.setProductId(productId);
                newBasket.setProductCount(productCount);
                basketService.saveBasket(newBasket);
                setAddBasketStatus("Ok");
                return redirectString;
            } else {
                if (basket.getProductCount() + productCount > 100) {
                    setAddBasketStatus("count_overflow");
                    return redirectString;
                } else {
                    basket.setProductCount(basket.getProductCount() + productCount);
                    basketService.saveBasket(basket);
                    setAddBasketStatus("Ok");
                    return redirectString;
                }
            }
        }
    }

    @GetMapping("/basket")
    public String basket(Model model, Authentication authentication) {
        String userRole = getUserRole(authentication);
        int userId = getUserId(authentication);
        model.addAttribute("totalPrice", getTotalPrice(basketService.getBasketByUserId(userId)));
        model.addAttribute("userRole", userRole);
        model.addAttribute("types", typeService.getAllTypes());
        List<Basket> baskets = basketService.getBasketByUserId(userId);
        Collections.sort(baskets, Comparator.comparing(Basket::getId));
        model.addAttribute("basket", baskets);
        model.addAttribute("productService", productService);
        return "UserController/basket";
    }

    @PostMapping(value = "/basketOperation", params = "delete")
    public String deleteBasket(@RequestParam(name = "basketToDeleteId") int basketToDeleteId) {
        basketService.deleteBasketById(basketToDeleteId);
        return "redirect:/basket";
    }

    @SneakyThrows
    @PostMapping(value = "/sendBasket")
    public String sendBasket(Authentication authentication, @RequestParam(name = "address") String address,
                             @RequestParam(name = "telephone") String telephone) {
        User user = (User)userService.loadUserByUsername(authentication.getName());
        String userMessage = createMessageForUser(basketService.getBasketByUserId(user.getId()));
        emailService.sendmail(userMessage, user.getEmail());
        basketService.deleteAllByUserId(user.getId());
        return "redirect:/basket";
    }
}
