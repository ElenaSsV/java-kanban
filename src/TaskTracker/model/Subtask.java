package TaskTracker.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime,
                   long duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId
                && getId() == subtask.getId()
                && getName().equals(subtask.getName())
                && getDescription().equals(subtask.getDescription())
                && getStatus().equals(subtask.getStatus())
                && getDuration() == subtask.getDuration()
                && getStartTime().equals(subtask.getStartTime())
                && getEndTime().equals(subtask.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", epicId=" + epicId +
                ", startTime='" + getStartTime().format(formatter) + '\'' +
                ", duration(min)='" + getDuration() + '\'' +
                ", endTime='" + getEndTime().format(formatter) + '\'' +
                '}';
    }
}
