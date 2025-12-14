package com.example.userdemo;

import com.example.userdemo.model.User;
import com.example.userdemo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && password.equals(userOpt.get().getPassword())) {
            session.setAttribute("user", userOpt.get().getUsername());
            return "redirect:/";
        }
        return "redirect:/login?error=1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/shop")
    public String shopPage() {
        return "shop";
    }

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "checkout";
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buy(@RequestParam String product, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("LOGIN_REQUIRED");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orders = (List<Map<String, Object>>) session.getAttribute("orders");
        if (orders == null) {
            orders = new ArrayList<>();
            session.setAttribute("orders", orders);
        }
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("product", product);
        entry.put("username", user.toString());
        entry.put("time", LocalDateTime.now().toString());
        orders.add(entry);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "index2";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/products/new")
    public String createProductPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "product-create";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String brand,
                                @RequestParam String model,
                                @RequestParam String category,
                                @RequestParam(required = false, defaultValue = "") String description,
                                @RequestParam String price,
                                @RequestParam(required = false, defaultValue = "") String rating,
                                @RequestParam(required = false, defaultValue = "") String imageUrl,
                                HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) session.getAttribute("products");
        if (products == null) {
            products = new ArrayList<>();
            session.setAttribute("products", products);
        }
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("brand", brand);
        p.put("model", model);
        p.put("category", category);
        p.put("description", description);
        p.put("price", price);
        p.put("rating", rating);
        p.put("imageUrl", imageUrl);
        products.add(0, p);
        return "redirect:/shop";
    }

    @GetMapping("/products/list")
    @ResponseBody
    public List<Map<String, Object>> listProducts(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) session.getAttribute("products");
        return products == null ? Collections.emptyList() : products;
    }

    @GetMapping("/my-orders")
    public String myordersPage() {
        return "my-orders";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/login";
    }
}
