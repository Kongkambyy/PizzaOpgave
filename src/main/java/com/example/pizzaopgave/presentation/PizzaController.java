package com.example.pizzaopgave.presentation;

import com.example.pizzaopgave.application.interfaces.IPizzaService;
import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.Topping;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    private final IPizzaService pizzaService;

    public PizzaController(IPizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        model.addAttribute("pizzas", pizzas);
        return "pizza/menu";
    }

    @GetMapping("/custom")
    public String showCustomPizzaForm(Model model) {
        List<Topping> toppings = pizzaService.getAllToppings();
        model.addAttribute("toppings", toppings);
        return "pizza/custom";
    }

    @PostMapping("/custom")
    public String processCustomPizza(@RequestParam String name,
                                     @RequestParam String description,
                                     @RequestParam double basePrice,
                                     @RequestParam(required = false) List<Long> toppingIds,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            Pizza pizza = pizzaService.createCustomPizza(name, description, basePrice);

            if (toppingIds != null) {
                for (Long toppingId : toppingIds) {
                    pizzaService.addToppingToPizza(pizza.getId(), toppingId);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Custom pizza created successfully");

            if (session.getAttribute("currentOrderId") != null) {
                return "redirect:/order/add/" + pizza.getId();
            }

            return "redirect:/pizza/menu";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pizza/custom";
        }
    }

    @GetMapping("/details/{id}")
    public String showPizzaDetails(@PathVariable Long id, Model model) {
        Optional<Pizza> pizzaOptional = pizzaService.getPizzaById(id);

        if (pizzaOptional.isEmpty()) {
            return "redirect:/pizza/menu";
        }

        model.addAttribute("pizza", pizzaOptional.get());
        return "pizza/details";
    }
}