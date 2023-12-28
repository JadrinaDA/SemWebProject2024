package semweb_project2;

public class UserProfile {
    private String userName;
    private double latitude;
    private double longitude;
    private int geoRadius;
    private int postalCode;
    private double maxPrice;
    private String dayOfWeekSelected;
    private int hourSelected;
    private int minutesSelected;

    // Constructor
    public UserProfile(String userName, double latitude, double longitude,
                       int geoRadius, int postalCode, double maxPrice) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoRadius = geoRadius;
        this.postalCode = postalCode;
        this.maxPrice = maxPrice;
        this.dayOfWeekSelected = null;
    }
    
    //Setters
    public void setTime(String day, int hour, int minutes) {
    	this.dayOfWeekSelected = day;
    	this.hourSelected = hour;
    	this.minutesSelected = minutes;
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
    
    public String getDayOfWeek() {
        return dayOfWeekSelected;
    }
    
    public int getHour() {
        return hourSelected;
    }
    
    public int getMinutes() {
        return minutesSelected;
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
