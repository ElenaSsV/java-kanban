package TaskTracker;

import TaskTracker.service.Manager;
import TaskTracker.model.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        String status = "";
        int id = 0;

        manager.createEpic(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people", id, status, new ArrayList<>()));

        manager.createSubtask(new Subtask("Find venue",
                "Find suitable venue", id, status, 1));
        manager.createSubtask(new Subtask("Organise catering", "Find a company", id, status,
                1));

        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        manager.updateSubtask(new Subtask("Organise catering", "Find a company", 3,
                "IN PROGRESS", 1));
        manager.updateSubtask(new Subtask("Find venue",
                "Find suitable venue", 2, "DONE", 1));

        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        manager.removeSubtaskById(3);

        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

    }
}
