package TaskTracker.model;


public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int iD, String status, int epicId) {
        super(name, description, iD, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", iD=" + getId() +
                ", status='" + getStatus() + '\'' +
                "epicId=" + epicId +
                '}';
    }
}
