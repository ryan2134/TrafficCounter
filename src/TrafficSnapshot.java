/**
 * Fundamental class which represents how the elements of the data is stored and represented, this class contains all
 * the necessary information for the program to easily access as required
 *
 * @author Ryan Cheng
 */
public class TrafficSnapshot {
    private int cars;
    private String date;
    private String time;

    /**
     * standard constructor method for instantiating this object
     * @param date String representation of the ISO 8601 date
     * @param time String representation of the ISO 8601 time
     * @param cars number of cars seen in the respective timestamp
     */
    public TrafficSnapshot(String date, String time, int cars){
        this.cars = cars;
        this.date = date;
        this.time = time;
    }

    /**
     * Basic method that turns the objects private variables back to ISO 8601 format
     * @return String representing the same format as the input
     */
    public String backToTimeStamp(){
        String timestamp;
        timestamp = this.date + "T" + this.time + " " + this.cars;
        return timestamp;
    }

    /**
     * Retrieves the objects private car constant
     * @return number of cars in this certain timestamp
     */
    public int getCars() {
        return this.cars;
    }

    /**
     * Retrieves the object's String date
     * @return the String date of the timestamp
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Retrieves the objects String time
     * @return the String time of the timestamp
     */
    public String getTime() {
        return this.time;
    }
}
