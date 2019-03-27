import java.util.*;

/**
 * This class is the powerhouse of the application. Inherits and behaves like an ArrayList but with the extended
 * functionality for the purpose of the program. It contains all the TrafficSnapshot objects and methods to process
 * them for the desired correct output.
 *
 * @author Ryan Cheng
 */
public class TrafficSnapshotHolder extends ArrayList<TrafficSnapshot> {
    // Since the timestamp changes by either increments of 30 or 70 (e.g. 500 -> 530 -> 600)
    private static final int MAX_TIME_DIFFERENCE = 70;

    /**
     * Simple method which groups and calls the methods needed for the output
     */
    public void processSnapshots() {
        int total = totalCars();
        LinkedHashMap<String, Integer> dateHashMap = carsByDate();
        ArrayList<TrafficSnapshot> topThreeSnapshots = topThree();
        ArrayList<TrafficSnapshot> leastCars = leastCars();
        outputResult(total, dateHashMap, topThreeSnapshots, leastCars);
    }

    /**
     * Iterates over the TrafficSnapshotHolder ArrayList and sums all the total cars seen
     */
    public int totalCars() {
        int total = 0;
        for (TrafficSnapshot ts : this){
            total += ts.getCars();
        }
        return total;
    }

    /**
     * WIth the whole traffic data, this method will check the total cars seen on a particular date.
     * Stored within a LinkedHashMap for easy adding and iteration for an ordered output (with the assumption that
     * the data provided in the file is ordered in chronological order)
     */
    public LinkedHashMap<String, Integer> carsByDate() {
        LinkedHashMap<String, Integer> dateHashMap = new LinkedHashMap<>();
        String currentDate = this.get(0).getDate();
        int currentCars = 0;
        for(TrafficSnapshot ts : this){
            // Sum cars seen by date, or else change to new date and continue to put in the hashmap
            if(ts.getDate().equals(currentDate)){
                currentCars += ts.getCars();
            }
            else{
                currentDate = ts.getDate();
                currentCars = ts.getCars();
            }
            dateHashMap.put(currentDate, currentCars);
        }
        return dateHashMap;
    }

    /**
     * Method that calculates the top 3 traffic snapshots given the whole data. Uses a 'blank' pre-filled ArrayList
     * for ease of logic of only needing 3 objects in it
     */
    public ArrayList<TrafficSnapshot> topThree() {
        TrafficSnapshot blank = new TrafficSnapshot("", "", 0);
        TrafficSnapshot temp, tempTwo;
        ArrayList<TrafficSnapshot> topThreeSnapshots = new ArrayList<>();
        // Standard check if it's not possible to find top 3
        if(this.size() < 3){
            return topThreeSnapshots;
        }
        topThreeSnapshots.addAll(Arrays.asList(blank, blank, blank));
        for(TrafficSnapshot ts :this){
            if(ts.getCars() > topThreeSnapshots.get(0).getCars()){
                temp = topThreeSnapshots.get(0);
                topThreeSnapshots.set(0, ts);
            }
            // If not a new max, check if current is bigger than 2nd in ArrayList
            else{
                temp = ts;
            }
            if(temp.getCars() > topThreeSnapshots.get(1).getCars()){
                tempTwo = topThreeSnapshots.get(1);
                topThreeSnapshots.set(1, temp);
            }
            // If not a new max, check if current is bigger than 3rd in ArrayList
            else{
                tempTwo = temp;
            }
            if(tempTwo.getCars() > topThreeSnapshots.get(2).getCars()){
                topThreeSnapshots.set(2, tempTwo);
            }
        }
        return topThreeSnapshots;
    }

    /**
     * Finds the least cars found in a 1.5 hour period (3 contiguous half hour records) by utilising ArrayLists to
     * manipulate the data. By using the possibleTimestamps method, we have filtered to only an ArrayList of
     * possible contiguous records so that we only need simple ArrayList manipulation to find the least cars period/s.
     */
    public ArrayList<TrafficSnapshot> leastCars() {
        ArrayList<TrafficSnapshot> possibleTimestamps = new ArrayList<>();
        ArrayList<TrafficSnapshot> leastCars = new ArrayList<>();
        ArrayList<TrafficSnapshot> temp = new ArrayList<>();
        String currentDate;
        int currentTime, nextTime, currentPeriod;
        int min = Integer.MAX_VALUE;
        // Check if it's not possible to compute the least 1.5 hour period
        if(this.size() < 3 || !possiblePeriods(possibleTimestamps)){
            return leastCars;
        }
        currentDate = possibleTimestamps.get(0).getDate();
        currentTime = parseTime(possibleTimestamps.get(0).getTime());
        for(TrafficSnapshot ts : possibleTimestamps){
            nextTime = parseTime(ts.getTime());
            // If we come across a new date or discontinuous time of the day, clear ArrayList and continue
            if(!currentDate.equals(ts.getDate()) || !(nextTime - currentTime <= MAX_TIME_DIFFERENCE)){
                temp.clear();
            }
            temp.add(ts);
            currentDate = ts.getDate();
            currentTime = nextTime;
            // If the new contiguous block found is the new min, replace and clear current. If equal, we keep both
            // periods then remove first item so that ArrayList is back to 2 items for the next iteration
            if(temp.size() == 3){
                currentPeriod = temp.get(0).getCars() + temp.get(1).getCars() + temp.get(2).getCars();
                if(currentPeriod < min){
                    min = currentPeriod;
                    leastCars.clear();
                    leastCars.addAll(temp);
                }
                else if (currentPeriod == min) {
                    leastCars.addAll(temp);
                }
                temp.remove(0);
            }
        }
        return leastCars;
    }

    /**
     * With the whole ArrayList of timestamps this method filters out the ones that do not make it possible to obtain
     * a 3 half hour contiguous block and retains all the possible combinations of periods
     *
     * @param possiblePeriods empty ArrayList to be filled with possible periods of least cars
     * @return boolean value of whether possible periods exist
     */
    public boolean possiblePeriods(ArrayList<TrafficSnapshot> possiblePeriods) {
        ArrayList<TrafficSnapshot> temp = new ArrayList<>();
        int nextTime;
        String currentDate = this.get(0).getDate();
        int currentTime = parseTime(this.get(0).getTime());
        for(TrafficSnapshot ts : this){
            nextTime = parseTime(ts.getTime());
            // If current iterated TrafficSnapshot makes a possible 1.5 hour block, add it
            if(ts.getDate().equals(currentDate) && (nextTime - currentTime <= MAX_TIME_DIFFERENCE)){
                temp.add(ts);
                currentDate = ts.getDate();
                currentTime = parseTime(ts.getTime());
            }
            // Get here if current snapshot doesn't make a block or file is just one date, then add previous
            // timestamps to possible period ArrayList if possible, then replace
            if(!ts.getDate().equals(currentDate) || nextTime - currentTime > MAX_TIME_DIFFERENCE ||
                    this.indexOf(ts) == this.size() -1){
                if(temp.size() > 2){
                    possiblePeriods.addAll(temp);
                }
                temp.clear();
                currentDate = ts.getDate();
                currentTime = nextTime;
                temp.add(ts);
            }
        }
        // No possible period available
        if(possiblePeriods.size() < 3){
            return false;
        }
        return true;
    }

    /**
     * Parses the timestamp portion of the ISO 8601 format into an integer data type
     *
     * @param timeStamp String representing the hour and  time of the timestamp
     * @return the hour-minute timestamp as an integer
     */
    public int parseTime(String timeStamp) {
        String time;
        int result;
        String[] split = timeStamp.split(":");
        time = split[0] + split[1];
        result = Integer.parseInt(time);
        return result;
    }

    /**
     * Outputs the result for each of the required tasks in the format given in the specs
     *
     * @param total total cars seen
     * @param dateHashMap Hashmap containing cars by date
     * @param topThreeSnapshots ArrayList with top 3 timestamps of most cars
     * @param leastCars ArrayList with the 1.5 hour periods of least cars
     */
    private void outputResult(int total, LinkedHashMap<String, Integer> dateHashMap,
                              ArrayList<TrafficSnapshot> topThreeSnapshots,
                              ArrayList<TrafficSnapshot> leastCars) {
        int i = 0;
        // Total cars seen in data
        System.out.println("Total cars: " + total);
        System.out.println("----------------------");

        // Number of cars seen by date
        System.out.println("Cars by date:");
        dateHashMap.forEach((key,value) -> System.out.println(key + " " + value));
        System.out.println("----------------------");

        // Top 3 timestamps with most cars
        if(topThreeSnapshots.isEmpty()){
            System.out.println("Top 3 half hours with most cars NOT APPLICABLE");
        }
        else {
            System.out.println("Top 3 half hours with most cars:");
            topThreeSnapshots.forEach(name -> System.out.println(name.backToTimeStamp()));
            System.out.println("----------------------");
        }

        // Least 1.5 period of cars
        if(leastCars.isEmpty()){
            System.out.println("Least 1.5 hour period NOT APPLICABLE");
        }
        else {
            System.out.println("1.5 hour period with least cars:");
            for (TrafficSnapshot ts : leastCars) {
                System.out.println(ts.backToTimeStamp());
                i++;
                if (i == 3) {
                    System.out.println("----------------------");
                    i = 0;
                }
            }
        }
    }
}
