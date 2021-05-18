package quiztest.main.inventory;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepositoryJDBC {

    Logger logger = LoggerFactory.getLogger(InventoryRepositoryJDBC.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int save(Vehicle vehicle) {
        String sql = "insert into inventory (make, model, color, \"VIN\", available, location) values (?, ?, ?, ?, ?, ?);";
        return jdbcTemplate.update(sql, vehicle.getMake(), vehicle.getModel(), vehicle.getColor(), vehicle.getVIN(), vehicle.isAvailable(), vehicle.getLocation());
    }

    public int saveSoldVehicle(Vehicle vehicle) {
        String sql = "insert into inventory (make, model, color, \"VIN\", available, location, dateOfSale) values (?, ?, ?, ?, ?, ?, ?);";
        return jdbcTemplate.update(sql, vehicle.getMake(), vehicle.getModel(), vehicle.getColor(), vehicle.getVIN(), vehicle.isAvailable(), vehicle.getLocation(),
            vehicle.getDateOfSale());
    }

    public int deleteByVIN(String VIN) {
        String sql = "delete from inventory WHERE \"VIN\" = ?;";
        Object[] args = new Object[] { VIN };
        return jdbcTemplate.update(sql, args);
    }

    public List<Vehicle> findAll() {
        String sql = "SELECT * from inventory;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper());
        return vehicleList;
    }

    public List<Vehicle> findAllAvailable() {
        String sql = "SELECT * from inventory WHERE available;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper());
        return vehicleList;
    }

    public List<Vehicle> findByColor(String color) {
        String sql = "SELECT * from inventory WHERE color = ? and available;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper(), color);
        return vehicleList;
    }

    public List<Vehicle> findByModel(String model) {
        String sql = "SELECT * from inventory WHERE model = ? and available;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper(), model);
        return vehicleList;
    }

    public Vehicle findByVIN(String VIN) {
        Vehicle vehicle = new Vehicle();
        String sql = "SELECT * from inventory WHERE \"VIN\" = ?;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper(), VIN);
        try {
            if (vehicleList.size() > 0) {
                vehicle = vehicleList.get(0);
                return vehicle;
            }
        }
        catch (Exception e) {
            System.err.println("o no");
        }
        return null;
    }

    public void purchaseVehicle(String VIN, Timestamp curr) {
        String sql = "update inventory set available = false, dateofsale = ? where \"VIN\" = ?;";
        jdbcTemplate.update(sql, curr, VIN);
    }

    public void purchaseRandomVehicle(String VIN, Timestamp curr) {
        String sql = "update inventory set available = false, dateofsale = ? where \"VIN\" = ?;";
        jdbcTemplate.update(sql, curr, VIN);
    }

    public Vehicle getRandomAvailableVehicle() {
        Vehicle vehicle = new Vehicle();
        String sql = "SELECT * from inventory WHERE available = true ORDER BY random() LIMIT 1;";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper());
        vehicle = vehicleList.get(0);

        return vehicle;
    }

    public List<Vehicle> getdOlderThanSixty() {
        String sql = "SELECT * from inventory WHERE dateofsale < NOW() - INTERVAL '60 days';";
        List<Vehicle> vehicleList = jdbcTemplate.query(sql, new VehicleMapper());

        return vehicleList;
    }

    public void deleteOlderThanSixty() {
        String sql = "DELETE from inventory WHERE dateofsale < NOW() - INTERVAL '60 days';";
        jdbcTemplate.update(sql);
    }

    public Dealership getDealershipById(int location) {
        String sql = "SELECT * FROM dealership WHERE id = ?;";
        List<Dealership> dealershipList = jdbcTemplate.query(sql, new DealershipMapper(), location);
        Dealership dealership = dealershipList.get(0);
        return dealership;
    }
}
