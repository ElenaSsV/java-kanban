package TaskTracker;
import TaskTracker.service.*;

import TaskTracker.model.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Status status = null;
        int id = 0;

        manager.createEpic(new Epic("Workshop on 6 April",
                "Organise a workshop for 20 people", id, status, new ArrayList<>()));

        manager.createSubtask(new Subtask("Find venue",
                "Find suitable venue", id, status, 1));
        manager.createSubtask(new Subtask("Organise catering", "Find a company", id, status,
                1));
        manager.createEpic(new Epic("Project peach",
                "Complete project", id, status, new ArrayList<>()));

        manager.getSubtaskById(2);
        manager.getSubtaskById(3);
        manager.getEpicById(1);
        manager.getEpicById(4);
        manager.getSubtaskById(3);

        //manager.removeAllEpics();
       // manager.removeAllSubtasks();
        manager.removeEpicById(4);
       // manager.removeSubtaskById(2);
        System.out.println(historyManager.getHistory());
    }
}
