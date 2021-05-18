package quiztest.main.inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InitializingBean {

    private final InventoryRepositoryRedis inventoryRepositoryRedis;
    private final InventoryRepositoryJDBC inventoryRepositoryJDBC;
    Logger logger = LoggerFactory.getLogger(InventoryService.class);
    List<String> colorList = Arrays.asList("Blue", "Black", "Red", "Silver");
    List<Pair<String, List<String>>> makeModelList = new ArrayList<>();

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
                logger.info("This vehicle is sold");
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

    @Scheduled(fixedRate = 10000)
    public void addRandomVehicle() {
        Vehicle vehicle = createRandomVehicle();
        if (!exists(vehicle)) {
            logger.info("Scheduled Task. Inserting {}", vehicle.toString());
            inventoryRepositoryJDBC.save(vehicle);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void addRandomSoldVehicle() {
        Vehicle vehicle = createRandomSoldVehicle();
        if (!exists(vehicle)) {
            logger.info("Scheduled Task. Inserting {} sold : {}", vehicle.toString(), vehicle.getDateOfSale());
            inventoryRepositoryJDBC.saveSoldVehicle(vehicle);
        }
    }

    @Scheduled(fixedRate = 25000)
    public void purchaseRandomVehicle() {
        Vehicle vehicle = inventoryRepositoryJDBC.getRandomAvailableVehicle();
        Timestamp curr = new Timestamp(System.currentTimeMillis());
        logger.info("Scheduled Task. Purchasing random vehicle {} at {}", vehicle.toString(), curr);
        inventoryRepositoryJDBC.purchaseRandomVehicle(vehicle.getVIN(), curr);
    }

    public Vehicle findByVIN(String VIN) {
        logger.info("Searching for vehicles by VIN. VIN = {} ", VIN);
        return inventoryRepositoryJDBC.findByVIN(VIN);
    }

    public Vehicle createRandomVehicle() {
        logger.info("Creating a random vehicle.");
        Random random = new Random();
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
        Random random = new Random();
        Vehicle vehicle = new Vehicle();

        long offset = Timestamp.valueOf("2021-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2021-05-01 00:00:00").getTime();
        long diff = end - offset + 1;
        Timestamp ts = new Timestamp(offset + (long) (Math.random() * diff));

        Pair<String, List<String>> beep = makeModelList.get(random.nextInt(makeModelList.size()));

        vehicle.setMake(beep.getLeft());
        vehicle.setModel(beep.getRight().get(random.nextInt(beep.getRight().size())));
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

    @Scheduled(cron = "0 0 * * * *")
    public void moveToRedis() {
        logger.info(
            "Scheduled Task. Checking inventory DB daily, at midnight, for any vehicles sold prior to 60 days, moving vehicle information to redis, and removing vehicle from inventory");
        inventoryRepositoryRedis.soldRecordToRedisPipeline(inventoryRepositoryJDBC.getdOlderThanSixty());
        inventoryRepositoryJDBC.deleteOlderThanSixty();
    }

    // public void generateSales() {
    // logger.info("inserting a million records");
    // for (int i = 0; i < 1000000; i++) {
    // Vehicle vehicle = createRandomVehicle();
    // if (!exists(vehicle)) {
    // inventoryRepositoryJDBC.save(vehicle);
    // }
    // if (i % 10000 == 0) {
    // logger.error("ANOTHER TEN THOUSAND INSERTED");
    //
    // }
    // }
    // }

    // public void generateSoldVehicles() {
    // logger.info("Inserting 1000000 sold vehicle test records");
    // for (int i = 0; i < 1000000; i++) {
    // Vehicle vehicle = createRandomSoldVehicle();
    // if (!exists(vehicle)) {
    // inventoryRepositoryJDBC.saveSoldVehicle(vehicle);
    //
    // }
    // if (i % 10000 == 0) {
    // logger.error("{} inserted so far", i);
    //
    // }
    // }
    // }

}
