import java.io.*;

/**
 * Main driver class that initially reads the traffic counter files and extracts the appropriate information
 * storing them in a fundamental object TrafficSnapshot for each individual traffic count, all of which are then
 * grouped and stored together in the TrafficSnapshotHolder object
 *
 * @author Ryan Cheng
 */
public class TrafficCounter {

    /**
     * Driver method that reads the given .txt file holding the traffic counting
     * @param args standard main parameter
     */
    public static void main(String[] args) {
        String timestamp;
        TrafficSnapshotHolder counter = new TrafficSnapshotHolder();
        try (FileReader fr = new FileReader("data/traffic1.txt");
             BufferedReader br = new BufferedReader(fr)) {
            while ((timestamp = br.readLine()) != null)  {
                counter.add(extractInfo(timestamp));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(counter.isEmpty()){
            System.out.println("File at location is empty, please check file");
            System.exit(0);
        }
        counter.processSnapshots();
    }

    /**
     * Extracts and parses the required information from the provided data which are then stored within a
     * created TrafficSnapshot object
     *
     * @param timestamp String representation of the ISO 8601 date format
     * @return an individual snapshot of a traffic count
     */
    public static TrafficSnapshot extractInfo(String timestamp) {
        // "2016-12-01T05:00:00 5" -> ("2016-12-01", "05:00:00 5")
        String[] split = timestamp.split("T");
        String date = split[0];
        // "5:00:00 5" -> ("05:00:00", "5")
        String[] timeAndCars = split[1].split(" ");
        String time = timeAndCars[0];
        int cars = Integer.parseInt(timeAndCars[1]);
        TrafficSnapshot ts = new TrafficSnapshot(date, time, cars);
        return ts;
    }
}
