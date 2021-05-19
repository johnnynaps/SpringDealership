package quiztest.main.inventory;

public class Dealership {

    private int id;
    private String name;
    private String address;
    private String manager;

    public Dealership() {
    }

    public Dealership(int id, String name, String address, String manager) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.manager = manager;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Dealership [id=").append(id).append(", name=").append(name).append(", address=").append(address).append(", manager=").append(manager).append("]");
        return builder.toString();
    }

}
