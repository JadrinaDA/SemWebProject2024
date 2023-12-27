package semweb_project2;

public class UserProfile {
    private String userName;
    private double latitude;
    private double longitude;
    private int geoRadius;
    private int postalCode;
    private double maxPrice;

    // Constructor
    public UserProfile(String userName, double latitude, double longitude,
                       int geoRadius, int postalCode, double maxPrice) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoRadius = geoRadius;
        this.postalCode = postalCode;
        this.maxPrice = maxPrice;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getGeoRadius() {
        return geoRadius;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    // toString method for easy printing
    @Override
    public String toString() {
        return "User Name: " + userName +
                "\nLatitude: " + latitude +
                "\nLongitude: " + longitude +
                "\nGeo Radius: " + geoRadius +
                "\nPostal Code: " + postalCode +
                "\nMax Price: " + maxPrice;
    }

}
