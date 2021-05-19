package quiztest.main.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DealershipMapper implements RowMapper<Dealership> {

    @Override
    public Dealership mapRow(ResultSet rs, int rowNum) throws SQLException {
        Dealership dealership = new Dealership(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("manager"));

        return dealership;
    }

}
