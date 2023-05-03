package TaskTracker.service;
import TaskTracker.model.Node;
import TaskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static CustomLinkedList<Task> viewedTasks = new CustomLinkedList<>();
    private static Map<Integer, Node<Task>> nodesToDel = new HashMap<>();

    @Override
    public void addTask(Task task) {
     if (nodesToDel.containsKey(task.getId())) {
         remove(task.getId());
        }
     Node<Task> node = viewedTasks.linkLast(task);
     nodesToDel.put(task.getId(), node);
    }

    @Override
    public void remove (int id) {
        Node<Task> node = nodesToDel.get(id);
        viewedTasks.removeNode(node);
        nodesToDel.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks.getTasks();
    }

    private static class CustomLinkedList<T> {
        Node<T> head;
        Node<T> tail;

        private Node<T> linkLast(T element) {
          final Node<T> oldTail = tail;
          final Node<T> newNode = new Node(tail, element, null);
          tail = newNode;
          if (oldTail == null)
              head = newNode;
          else
              oldTail.setNext(newNode);
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
            if (node == head) {
                head = node.getNext();
                head.setPrevious(null);
            } else if (node == tail) {
                tail = node.getPrevious();
                tail.setNext(null);
            } else {
                node.getPrevious().setNext(node.getNext());
                node.getNext().setPrevious(node.getPrevious());
            }
        }
    }
}
