package TaskTracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
    private LocalDateTime defaultTime = LocalDateTime.now();

    public Epic(String name, String description) {
        super(name, description, Status.NEW, LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)),
                0);
    }
    public LocalDateTime getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(LocalDateTime defaultTime) {
        this.defaultTime = defaultTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtasks (List<Integer> subtasks) {
        this.subtaskIds = subtasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return  getId() == epic.getId()
                && getName().equals(epic.getName())
                && getDescription().equals(epic.getDescription())
                && getStatus().equals(epic.getStatus())
                && getDuration() == epic.getDuration()
                && subtaskIds.equals(epic.subtaskIds)
                && getStartTime().equals(epic.getStartTime())
                && endTime.equals(epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, getStartTime(), getDuration(), endTime);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", subtasks=" + subtaskIds +
                ", startTime='" + getStartTime().format(formatter) + '\'' +
                ", duration(min)='" + getDuration() + '\'' +
                ", endTime='" + endTime.format(formatter) + '\'' +
                '}';
    }
}
