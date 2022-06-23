package cl.rutasegura.rutaseguracliente.model;

public class Driver {

    String idDriver;
    String firstname;
    String lastname;
    String email;
    String vehicleBrand;
    String vehiclePlate;
    String image;

    public Driver() {
    }

    public Driver(String idDriver, String firstname, String lastname, String email, String vehicleBrand, String vehiclePlate, String image) {
        this.idDriver = idDriver;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.vehicleBrand = vehicleBrand;
        this.vehiclePlate = vehiclePlate;
        this.image = image;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
