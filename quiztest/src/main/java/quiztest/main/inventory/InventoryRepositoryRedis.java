package quiztest.main.inventory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@Repository
public class InventoryRepositoryRedis {
    private Logger logger = LoggerFactory.getLogger(InventoryRepositoryRedis.class);
    private static Jedis jedis;

    static {
        jedis = new Jedis("localhost", 6379);
    }

    public void pingRedis() {
        logger.error(jedis.ping());
    }

    public void soldRecordToRedis(List<Vehicle> vehicleList) {
        for (Vehicle vehicle : vehicleList) {
            String vehicleString = new StringBuilder().append(vehicle.getLocation()).append('|').append(vehicle.getDateOfSale()).toString();
            jedis.set(vehicle.getVIN(), vehicleString);
        }
    }

    public void soldRecordToRedisPipeline(List<Vehicle> vehicleList) {
        Pipeline pipeline = jedis.pipelined();
        for (Vehicle vehicle : vehicleList) {
            String vehicleString = new StringBuilder().append(vehicle.getLocation()).append('|').append(vehicle.getDateOfSale()).toString();
            pipeline.set(vehicle.getVIN(), vehicleString);
        }
        pipeline.sync();
    }

    public String getRecord(String VIN) {
        return jedis.get(VIN);
    }

}
