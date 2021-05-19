package quiztest.main.inventory;

import java.sql.Timestamp;

public class Vehicle {

    private String make;
    private String model;
    private String color;
    private String VIN;
    private boolean available;
    private int location;
    private Timestamp dateOfSale;

    public Vehicle() {
    }

    public Vehicle(String make, String model, String color, String VIN, boolean available, int location, Timestamp dateOfSale) {
        super();
        this.make = make;
        this.model = model;
        this.color = color;
        this.VIN = VIN;
        this.available = available;
        this.location = location;
        this.dateOfSale = dateOfSale;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public Timestamp getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(Timestamp dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle [make=").append(make).append(", model=").append(model).append(", color=").append(color).append(", VIN=").append(VIN).append(", available=")
            .append(available).append(", location=").append(location).append("]");
        return builder.toString();
    }

}
