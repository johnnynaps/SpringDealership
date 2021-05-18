package quiztest.main.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VehicleMapper implements RowMapper<Vehicle> {

    @Override
    public Vehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setVIN(rs.getString("VIN"));
        vehicle.setAvailable(rs.getBoolean("available"));
        vehicle.setLocation(rs.getInt("location"));
        vehicle.setDateOfSale(rs.getTimestamp("dateofsale"));
        return vehicle;
    }

}
