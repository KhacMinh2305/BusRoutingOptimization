import java.util.ArrayList;
import java.util.Random;

public class BusRoutingOptimization {

    private static final int BUS_SPEED = 20;
    private static final int BUS_CAPACITY = 80;
    private static final int BUS_INTERVAL_TIME = 15; // (minute)
    private static final int MIN_WAITING_PASSENGERS = 20;
    private static final int MAX_WAITING_PASSENGERS = 50;
    private static final int DEFAULT_NUM_PASSENGERS_BUS = 60;
    private static final int MAX_NUM_PASSENGERS_OUT = 10;

    public static class Stop {
        public int ID;
        public int numWaitingPassengers;
        public int numOutPassengers;

        public Stop() {

        }

        public Stop(int ID) {
            this.ID = ID;
            randomPassengers(this);
        }
    }

    public BusRoutingOptimization() {

    }

    public static void main(String[] args) {

        Stop[] stops = new Stop[10];
        int[][] stopsDistance = createDummyData(stops);

        System.out.println(optimizing(stopsDistance, stops, 1).toString());
    }

    public static int[][] createDummyData(Stop[] stops) {
        Stop s;
        for (int i = 0; i < 10; i++) {
            s = new Stop(i + 1);
            stops[i] = s;
        }

        int[][] stopsDistance = 
                { { 0, 5, 17, 9, 12, 2, 8, 15, 3, 10 },
                { 5, 0, 6, 4, 13, 7, 15, 14, 22, 18 },
                { 17, 6, 0, 9, 2, 11, 5, 8, 14, 11 },
                { 9, 4, 9, 0, 22, 16, 7, 11, 5, 10 },
                { 12, 13, 2, 22, 0, 10, 3, 8, 4, 6 },
                { 2, 7, 11, 16, 10, 0, 13, 8, 3, 1 },
                { 8, 15, 5, 7, 3, 13, 0, 10, 6, 2 },
                { 15, 14, 8, 11, 8, 8, 10, 0, 6, 3 },
                { 3, 22, 14, 5, 4, 3, 6, 6, 0, 15 },
                { 10, 18, 11, 10, 6, 1, 2, 3, 15, 0 } };

        return stopsDistance;
    }

    public static void randomPassengers(Stop stop) {
        stop.numWaitingPassengers = MIN_WAITING_PASSENGERS
                + (int) (Math.random() * (MAX_WAITING_PASSENGERS - MIN_WAITING_PASSENGERS + 1));
        stop.numOutPassengers = (new Random()).nextInt(MAX_NUM_PASSENGERS_OUT + 1);
    }

    public static int measureTime(int[][] stopsDistanceMap, Stop[] stops, int currentStopID, int nextStopID) {

        // calculate the time to go from current stop to next stop
        int travelingTime = (int) ((1.00f * stopsDistanceMap[currentStopID - 1][nextStopID - 1] / BUS_SPEED) * 60);

        // measure the minimum time to wait a bus come to the next stop  
        int intervalTimeWaiting = (travelingTime % BUS_INTERVAL_TIME == 0) ? 0
                : BUS_INTERVAL_TIME - (travelingTime % BUS_INTERVAL_TIME);

        // random the passengers on the bus
        int passengersOnBus = (new Random()).nextInt(DEFAULT_NUM_PASSENGERS_BUS + 1); // random so luong hanh khach

        // checking the bus is full or not , we have to wait the next bus and the waiting time increase a BUS_INTERVAL_TIME
        while (passengersOnBus - stops[nextStopID - 1].numOutPassengers
                + stops[nextStopID - 1].numWaitingPassengers > BUS_CAPACITY) {
            intervalTimeWaiting += BUS_INTERVAL_TIME;
            randomPassengers(stops[nextStopID - 1]);
            passengersOnBus = (new Random()).nextInt(DEFAULT_NUM_PASSENGERS_BUS + 1);
        }
        // sum of the time we need to go this stop and wait for the bus to go travel to next stop
        return travelingTime + intervalTimeWaiting;
    }

    public static ArrayList<Integer> optimizing(int[][] stopsDistanceMap, Stop[] stops, int beginStopID) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(beginStopID);
        int currentStopID = beginStopID;
        do {
            int nextStopID = -1;
            int minTime = Integer.MAX_VALUE;
            
            for (Stop stop : stops) {

                // ignore this stop if it was traveled
                if (result.contains(stop.ID)) {
                    continue;
                }

                // get the time we need to go this stop
                int waitingTime = measureTime(stopsDistanceMap, stops, currentStopID, stop.ID);
                if (waitingTime < minTime) {
                    minTime = waitingTime;
                    nextStopID = stop.ID;
                }
            }

            // add the stop that we need minimum time to go in the journey
            result.add(nextStopID);
            // apdate current stop to the next stop that have already added above
            currentStopID = nextStopID;
        } while (result.size() < stops.length);
        return result;
    }

}
