package TaskTracker.model;


public class Task {
    private String name;
    private String description;
    private int iD;
    private String status;

    public Task(String name, String description, int iD, String status) {
        this.name = name;
        this.description = description;
        this.iD = iD;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return iD;
    }

    public void setId(int iD) {
        this.iD = iD;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", iD=" + iD +
                ", status='" + status + '\'' +
                '}';
    }
}
