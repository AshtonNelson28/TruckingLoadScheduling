package JobPackage;
public class Job {
    private int jobId;
    private String description;
    private String origin;
    private String destination;
    private int distance;
    private double pay;  // Changed from BigDecimal to double
    private String requiredTruckType;

    // Constructor
    public Job(int jobId, String description, String origin, String destination, int distance, double pay, String requiredTruckType) {
        this.jobId = jobId;
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.pay = pay;  // This now accepts a double
        this.requiredTruckType = requiredTruckType;
    }

    public int getJobId() {
        return jobId;
    }

    public String getDescription() {
        return description;
    } // Getters and setters for each field
    // ...
    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getRequiredTruckType() {
        return requiredTruckType;
    }

    public double getPay() {
        return pay;
    }

    public int getDistance() {
        return distance;
    }


    // toString method for debugging
    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", description='" + description + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", distance=" + distance +
                ", pay=" + pay +
                ", requiredTruckType='" + requiredTruckType + '\'' +
                '}';
    }
}
