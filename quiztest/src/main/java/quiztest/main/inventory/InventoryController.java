package quiztest.main.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/dealership")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Vehicle> getVehicles() {
        return inventoryService.getAvailableInventory();
    }

    @GetMapping("/{color}")
    public List<Vehicle> getVehicleByColor(@PathVariable String color) {
        return inventoryService.getInventoryByColor(color);
    }

    @GetMapping("/{model}")
    public List<Vehicle> getVehicleByModel(@PathVariable String model) {
        return inventoryService.getInventoryByColor(model);
    }

    @PutMapping("/purchase/{VIN}")
    public void purchaseVehicle(@PathVariable String VIN) {
        inventoryService.purchaseVehicle(VIN);
    }
}
