package quiztest.main.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VehicleMapper implements RowMapper<Vehicle> {

    @Override
    public Vehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vehicle vehicle = new Vehicle(rs.getString("make"), rs.getString("model"), rs.getString("color"), rs.getString("VIN"), rs.getBoolean("available"), rs.getInt("location"),
            rs.getTimestamp("dateofsale"));

        return vehicle;
    }

}
