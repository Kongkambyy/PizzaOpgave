package com.example.pizzaopgave.presentation;

import com.example.pizzaopgave.application.interfaces.IOrderService;
import com.example.pizzaopgave.application.interfaces.IPizzaService;
import com.example.pizzaopgave.application.interfaces.IUserService;
import com.example.pizzaopgave.domain.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final IOrderService orderService;
    private final IUserService userService;
    private final IPizzaService pizzaService;

    public OrderController(IOrderService orderService, IUserService userService, IPizzaService pizzaService) {
        this.orderService = orderService;
        this.userService = userService;
        this.pizzaService = pizzaService;
    }

    @GetMapping("/new")
    public String createNewOrder(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        try {
            Order order = orderService.createOrder(userId);
            session.setAttribute("currentOrderId", order.getId());
            return "redirect:/pizza/menu";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    @GetMapping("/add/{pizzaId}")
    public String addPizzaToOrder(@PathVariable Long pizzaId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Long orderId = (Long) session.getAttribute("currentOrderId");

        if (orderId == null) {
            return "redirect:/order/new";
        }

        try {
            orderService.addPizzaToOrder(orderId, pizzaId);
            redirectAttributes.addFlashAttribute("success", "Pizza added to your order");
            return "redirect:/order/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pizza/menu";
        }
    }

    @GetMapping("/remove/{pizzaId}")
    public String removePizzaFromOrder(@PathVariable Long pizzaId,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Long orderId = (Long) session.getAttribute("currentOrderId");

        if (orderId == null) {
            return "redirect:/order/new";
        }

        try {
            orderService.removePizzaFromOrder(orderId, pizzaId);
            redirectAttributes.addFlashAttribute("success", "Pizza removed from your order");
            return "redirect:/order/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/view";
        }
    }

    @GetMapping("/view")
    public String viewCurrentOrder(HttpSession session, Model model) {
        Long orderId = (Long) session.getAttribute("currentOrderId");

        if (orderId == null) {
            return "redirect:/order/new";
        }

        Optional<Order> orderOptional = orderService.getOrderById(orderId);

        if (orderOptional.isEmpty()) {
            session.removeAttribute("currentOrderId");
            return "redirect:/order/new";
        }

        model.addAttribute("order", orderOptional.get());
        model.addAttribute("pizzas", pizzaService.getAllPizzas());
        return "order/view";
    }

    @GetMapping("/checkout")
    public String showCheckoutForm(HttpSession session, Model model) {
        Long orderId = (Long) session.getAttribute("currentOrderId");

        if (orderId == null) {
            return "redirect:/order/new";
        }

        Optional<Order> orderOptional = orderService.getOrderById(orderId);

        if (orderOptional.isEmpty()) {
            session.removeAttribute("currentOrderId");
            return "redirect:/order/new";
        }

        Order order = orderOptional.get();

        if (order.getPizzas().isEmpty()) {
            model.addAttribute("error", "Your order is empty. Please add some pizzas first.");
            return "redirect:/pizza/menu";
        }

        model.addAttribute("order", order);

        Long userId = (Long) session.getAttribute("userId");
        userService.getUserById(userId).ifPresent(user -> model.addAttribute("user", user));

        return "order/checkout";
    }

    @PostMapping("/complete")
    public String completeOrder(HttpSession session, RedirectAttributes redirectAttributes) {
        Long orderId = (Long) session.getAttribute("currentOrderId");

        if (orderId == null) {
            return "redirect:/order/new";
        }

        try {
            Order completedOrder = orderService.completeOrder(orderId);
            redirectAttributes.addFlashAttribute("success",
                    "Order completed successfully! You earned " + completedOrder.getEarnedBonusPoints() + " bonus points.");
            session.removeAttribute("currentOrderId");
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    @GetMapping("/history")
    public String showOrderHistory(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("orders", orderService.getOrdersByUser(userId));
        return "order/history";
    }

    @GetMapping("/details/{id}")
    public String showOrderDetails(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/user/login";
        }

        Optional<Order> orderOptional = orderService.getOrderById(id);

        if (orderOptional.isEmpty() || !orderOptional.get().getUser().getId().equals(userId)) {
            return "redirect:/order/history";
        }

        model.addAttribute("order", orderOptional.get());
        return "order/details";
    }
}