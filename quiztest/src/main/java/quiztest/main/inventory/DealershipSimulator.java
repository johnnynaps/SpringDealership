package quiztest.main.inventory;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile({ "dev", "dev-windows", "debug" })
public class DealershipSimulator {

    private Logger logger = LoggerFactory.getLogger(DealershipSimulator.class);
    private InventoryRepositoryJDBC inventoryRepositoryJDBC;
    private InventoryRepositoryRedis inventoryRepositoryRedis;
    private InventoryService inventoryService;

    @Autowired
    public DealershipSimulator(InventoryRepositoryJDBC inventoryRepositoryJDBC, InventoryRepositoryRedis inventoryRepositoryRedis, InventoryService inventoryService) {
        this.inventoryRepositoryJDBC = inventoryRepositoryJDBC;
        this.inventoryRepositoryRedis = inventoryRepositoryRedis;
        this.inventoryService = inventoryService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void moveToRedis() {
        logger.info(
            "Scheduled Task. Checking inventory DB daily, at midnight, for any vehicles sold prior to 60 days, moving vehicle information to redis, and removing vehicle from inventory");
        inventoryRepositoryRedis.soldRecordToRedisPipeline(inventoryRepositoryJDBC.getdOlderThanSixty());
        inventoryRepositoryJDBC.deleteOlderThanSixty();
    }

    @Scheduled(fixedRate = 10000)
    public void addRandomVehicle() {
        Vehicle vehicle = inventoryService.createRandomVehicle();
        if (!inventoryService.exists(vehicle)) {
            logger.info("Scheduled Task. Inserting {}", vehicle.toString());
            inventoryRepositoryJDBC.save(vehicle);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void addRandomSoldVehicle() {
        Vehicle vehicle = inventoryService.createRandomSoldVehicle();
        if (!inventoryService.exists(vehicle)) {
            logger.info("Scheduled Task. Inserting sold {} sold : {}", vehicle.toString(), vehicle.getDateOfSale());
            inventoryRepositoryJDBC.saveSoldVehicle(vehicle);
        }
    }

    @Scheduled(fixedRate = 25000)
    public void purchaseRandomVehicle() {
        Vehicle vehicle = inventoryRepositoryJDBC.getRandomAvailableVehicle();
        Timestamp curr = new Timestamp(System.currentTimeMillis());
        logger.info("Scheduled Task. Purchasing random {} at {}", vehicle.toString(), curr);
        inventoryRepositoryJDBC.purchaseRandomVehicle(vehicle.getVIN(), curr);
    }

}
