package quiztest.main.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DealershipMapper implements RowMapper<Dealership> {

    @Override
    public Dealership mapRow(ResultSet rs, int rowNum) throws SQLException {
        Dealership dealership = new Dealership();

        dealership.setId(rs.getInt("id"));
        dealership.setName(rs.getString("name"));
        dealership.setAddress(rs.getString("address"));
        dealership.setManager(rs.getString("manager"));

        return dealership;
    }

}
