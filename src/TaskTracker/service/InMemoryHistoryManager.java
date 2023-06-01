package TaskTracker.service;
import TaskTracker.model.Node;
import TaskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> viewedTasks = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void addTask(Task task) {
     if (nodeMap.containsKey(task.getId())) {
         remove(task.getId());
        }
     Node<Task> node = viewedTasks.linkLast(task);
     nodeMap.put(task.getId(), node);
    }

    @Override
    public void remove (int id) {
        if (!nodeMap.containsKey(id)) {
            return;
        }
        viewedTasks.removeNode(nodeMap.get(id));
        nodeMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks.getTasks();
    }

    private static class CustomLinkedList<T> {
        Node<T> head;
        Node<T> tail;

        private Node<T> linkLast(T element) {
            final Node<T> newNode = new Node<T>(tail, element, null);
            if (tail == null) {
                head = newNode;
            } else {
                tail.setNext(newNode);
            //tail = newNode;
        }
            tail = newNode;
          return newNode;
        }

        private List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> currentNode = head;
            while (currentNode != null) {
                tasks.add(currentNode.getData());
                currentNode = currentNode.getNext();
            }
            return tasks;
        }

        private void removeNode(Node<T> node) {
           if (node == head && head.getNext() != null) {
                head = node.getNext();
                head.setPrevious(null);
            } else if (node == tail) {
                tail = node.getPrevious();
               // tail.setNext(null);
            } else {
                node.getPrevious().setNext(node.getNext());
                node.getNext().setPrevious(node.getPrevious());
            }
        }
    }
}
