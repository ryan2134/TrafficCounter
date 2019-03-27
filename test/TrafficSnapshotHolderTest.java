import org.junit.Assert;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Test class that checks the functionality and correctness of the methods within the TrafficSnapshotHolder
 * class, by default all tests will also show correct population of the TrafficSnapshotHolder properly
 *
 * @author  Ryan Cheng
 */
public class TrafficSnapshotHolderTest {
    private TrafficSnapshotHolder counter;

    /**
     * Mirrors the file reading and data extracting as the main method in TrafficSnapshot class
     *
     * @param fileName file to be read for a certain test
     * @return a filled TrafficSnapshotHolder ArrayList for a certain test
     */
    public TrafficSnapshotHolder readFile(String fileName){
        TrafficSnapshotHolder counter = new TrafficSnapshotHolder();
        String timestamp;
        try (FileReader fr = new FileReader(fileName);
             BufferedReader br = new BufferedReader(fr)) {
            while ((timestamp = br.readLine()) != null)  {
                counter.add(TrafficCounter.extractInfo(timestamp));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }

    /**
     * Test to check correctness of summing the total cars seen by the traffic counter
     */
    @Test
    public void totalCarsTest(){
        int actual, expected =  410;
        String fileName = "data/traffic2.txt";
        counter = readFile(fileName);
        actual = counter.totalCars();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test to check if the LinkedHashMap is populated properly and with the same correct data
     */
    @Test
    public void carsByDateTest() {
        String fileName = "data/traffic3.txt";
        counter = readFile(fileName);
        LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> actual;
        expected.put("2016-12-01", 150);
        expected.put("2016-12-02", 300);
        expected.put("2016-12-03", 450);
        actual = counter.carsByDate();
        Assert.assertEquals(expected.size(),actual.size());
        Assert.assertTrue(actual.equals(expected));
    }

    /**
     * Test to check if the program chooses the correct top 3 timestamps and in order too
     */
    @Test
    public void topThreeTest() {
        String fileName = "data/traffic4.txt";
        counter = readFile(fileName);
        String expectedOne = "2016-12-01T13:30:00 50";
        String expectedTwo = "2016-12-01T06:30:00 49";
        String expectedThree = "2016-12-01T11:30:00 48";
        ArrayList<TrafficSnapshot> topThreeSnapshots = counter.topThree();
        Assert.assertEquals(expectedOne, topThreeSnapshots.get(0).backToTimeStamp());
        Assert.assertEquals(expectedTwo, topThreeSnapshots.get(1).backToTimeStamp());
        Assert.assertEquals(expectedThree, topThreeSnapshots.get(2).backToTimeStamp());

    }

    /**
     * Simple check if data file size is < 3, resulting in an empty top three ArrayList
     */
    @Test
    public void topThreeSizeErrorTest(){
        String fileName = "data/traffic5.txt";
        counter = readFile(fileName);
        ArrayList<TrafficSnapshot> topThreeSnapshots = counter.topThree();
        Assert.assertTrue(topThreeSnapshots.isEmpty());
    }

    /**
     * Test to check if the program chooses the correct top same value
     */
    @Test
    public void topThreeSameValueTest(){
        String fileName = "data/traffic6.txt";
        counter = readFile(fileName);
        String expectedOne = "2016-12-01T07:30:00 50";
        String expectedTwo = "2016-12-01T23:30:00 50";
        String expectedThree = "2016-12-08T19:00:00 50";
        ArrayList<TrafficSnapshot> topThreeSnapshots = counter.topThree();
        Assert.assertEquals(expectedOne, topThreeSnapshots.get(0).backToTimeStamp());
        Assert.assertEquals(expectedTwo, topThreeSnapshots.get(1).backToTimeStamp());
        Assert.assertEquals(expectedThree, topThreeSnapshots.get(2).backToTimeStamp());
    }

    /**
     * Basic test to check if parsing a String to an integer is working as intended
     */
    @Test
    public void parseTimeTest() {
        String fileName = "data/traffic5.txt";
        counter = readFile(fileName);
        int actual = counter.parseTime(counter.get(0).getTime());
        int actualTwo = counter.parseTime(counter.get(1).getTime());
        int expected = 500, expectedTwo = 530;
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedTwo, actualTwo);
    }

    /**
     * Test to find the least cars seen in a 1.5 hour period, with one (but not least) being found at the
     * start of the data file
     */
    @Test
    public void leastCarsTest() {
        String fileName = "data/traffic4.txt";
        counter = readFile(fileName);
        TrafficSnapshot testOne = new TrafficSnapshot("2016-12-01","9:00:00", 9);
        TrafficSnapshot testTwo = new TrafficSnapshot("2016-12-01","9:30:00", 11);
        TrafficSnapshot testThree = new TrafficSnapshot("2016-12-01","10:00:00", 0);
        ArrayList<TrafficSnapshot> expected = new ArrayList<>(Arrays.asList(testOne, testTwo, testThree));
        ArrayList<TrafficSnapshot> actual = counter.leastCars();
        Assert.assertEquals(expected.get(0).backToTimeStamp(), actual.get(0).backToTimeStamp());
        Assert.assertEquals(expected.get(1).backToTimeStamp(), actual.get(1).backToTimeStamp());
        Assert.assertEquals(expected.get(2).backToTimeStamp(), actual.get(2).backToTimeStamp());
    }

    /**
     * Simple check if data file size is < 3, resulting in an empty least 1.5 hour period ArrayList
     */
    @Test
    public void leastCarsSizeErrorTest(){
        String fileName = "data/traffic5.txt";
        counter = readFile(fileName);
        ArrayList<TrafficSnapshot> leastCars = counter.leastCars();
        Assert.assertTrue(leastCars.isEmpty());
    }

    /**
     * Test to check if there is no possible 1.5 hour contiguous period, where the data file is edited for it to
     * not be possible
     */
    @Test
    public void leastCarsNonePossibleTest(){
        String fileName = "data/traffic6.txt";
        counter = readFile(fileName);
        ArrayList<TrafficSnapshot> leastCars = counter.leastCars();
        Assert.assertTrue(leastCars.isEmpty());
    }
}