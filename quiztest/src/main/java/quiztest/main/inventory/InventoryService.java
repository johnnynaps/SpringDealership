package quiztest.main.inventory;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InitializingBean {

    private final InventoryRepositoryRedis inventoryRepositoryRedis;
    private final InventoryRepositoryJDBC inventoryRepositoryJDBC;
    private Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private List<String> colorList = Arrays.asList("Blue", "Black", "Red", "Silver");
    private List<Pair<String, List<String>>> makeModelList = new ArrayList<>();
    private SecureRandom random = new SecureRandom();

    @Autowired
    public InventoryService(InventoryRepositoryJDBC inventoryRepositoryJDBC, InventoryRepositoryRedis inventoryRepositoryRedis) {
        this.inventoryRepositoryJDBC = inventoryRepositoryJDBC;
        this.inventoryRepositoryRedis = inventoryRepositoryRedis;
    }

    public List<Vehicle> getInventory() {
        logger.info("Returning inventory regardless of availability");
        return inventoryRepositoryJDBC.findAll();
    }

    public List<Vehicle> getInventoryByColor(String color) {
        logger.info("Returning all available vehicles by color");
        return inventoryRepositoryJDBC.findByColor(color);
    }

    public List<Vehicle> getInventoryByModel(String model) {
        logger.info("Returning all available vehicles by model");
        return inventoryRepositoryJDBC.findByModel(model);
    }

    public List<Vehicle> getAvailableInventory() {
        logger.info("Returning all available vehicles");
        return inventoryRepositoryJDBC.findAllAvailable();
    }

    public void addNewVehicle(Vehicle vehicle) {
        logger.info("Adding new vehicle {}", vehicle);
        if (!exists(vehicle)) {
            inventoryRepositoryJDBC.save(vehicle);
        }
        else {
            logger.warn("Duplicate vehicle insertion attempted");
        }
    }

    public void purchaseVehicle(String VIN) {
        logger.info("Purchasing vehicle VIN = {} If purchasable, sets availability to false", VIN);
        Vehicle vehicle = findByVIN(VIN);

        if (vehicle != null) {
            if (vehicle.isAvailable()) {
                Timestamp curr = new Timestamp(System.currentTimeMillis());
                logger.info("Vehicle is available. Setting availability to false. Setting dateofsale to {}", curr);

                inventoryRepositoryJDBC.purchaseVehicle(VIN, curr);
                vehicle.setDateOfSale(curr);
            }
            else {
                logger.warn("This vehicle has already been sold");
            }
        }
        else {
            String vehicleString = inventoryRepositoryRedis.getRecord(VIN);
            if (vehicleString != null) {
                String[] arr = vehicleString.split("\\|");
                String location = inventoryRepositoryJDBC.getDealershipById(Integer.parseInt(arr[0])).getName();
                logger.info("Vehicle {} was sold from location {} at {}", VIN, location, arr[1]);
            }
            else {
                logger.info("This vehicle does not exist");
            }
        }
    }

    public Vehicle findByVIN(String VIN) {
        logger.info("Searching for vehicles by VIN. VIN = {} ", VIN);
        return inventoryRepositoryJDBC.findByVIN(VIN);
    }

    public Vehicle createRandomVehicle() {
        logger.info("Creating a random vehicle.");
        Vehicle vehicle = new Vehicle();

        Pair<String, List<String>> beep = makeModelList.get(random.nextInt(makeModelList.size()));

        vehicle.setMake(beep.getLeft());
        vehicle.setModel(beep.getRight().get(random.nextInt(beep.getRight().size())));
        vehicle.setAvailable(true);
        vehicle.setColor(colorList.get(random.nextInt(colorList.size())));
        vehicle.setVIN(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
        vehicle.setLocation(Integer.parseInt(RandomStringUtils.randomNumeric(1)) + 1);
        return vehicle;
    }

    public Vehicle createRandomSoldVehicle() {
        logger.info("Creating a random sold vehicle.");
        Vehicle vehicle = new Vehicle();
        Long lastishMonth = System.currentTimeMillis() - 2592000000L;
        Long threeishMonthsAgo = System.currentTimeMillis() - 7776000000L;

        Timestamp ts = new Timestamp(ThreadLocalRandom.current().nextLong(threeishMonthsAgo, lastishMonth));

        Pair<String, List<String>> entry = makeModelList.get(random.nextInt(makeModelList.size()));

        vehicle.setMake(entry.getLeft());
        vehicle.setModel(entry.getRight().get(random.nextInt(entry.getRight().size())));
        vehicle.setAvailable(false);
        vehicle.setColor(colorList.get(random.nextInt(colorList.size())));
        vehicle.setVIN(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
        vehicle.setLocation(Integer.parseInt(RandomStringUtils.randomNumeric(1)) + 1);
        vehicle.setDateOfSale(ts);
        return vehicle;
    }

    public boolean exists(Vehicle vehicle) {
        if (findByVIN(vehicle.getVIN()) != null) {
            logger.info("{} exists.", vehicle.getVIN());
            return true;
        }
        logger.info("Proposed Vehicle does not exist yet.");
        return false;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Making model list");

        makeModelList.add(Pair.of("Ford", Arrays.asList("Mustang", "Taurus")));
        makeModelList.add(Pair.of("Tesla", Arrays.asList("Model 3", "Model S")));
        makeModelList.add(Pair.of("Honda", Arrays.asList("Accord", "Fit")));
        makeModelList.add(Pair.of("Nissan", Arrays.asList("350z", "Sentra")));

    }

}
