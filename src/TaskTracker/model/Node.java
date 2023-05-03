package TaskTracker.model;

public class Node<E> {

   private Node<E> previous;
   private E data;
   private Node<E> next;

    public Node(Node<E> previous, E data, Node<E> next) {
        this.previous = previous;
        this.data = data;
        this.next = next;
    }

    public Node<E> getNext() {
        return next;
    }

    public Node<E> getPrevious() {
        return previous;
    }

    public E getData() {
        return data;
    }

    public void setPrevious(Node<E> previous) {
        this.previous = previous;
    }

    public void setData(E data) {
        this.data = data;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return next.equals(node.next) && previous.equals(node.previous) && data.equals(node.data);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (next != null) {
            hash = next.hashCode();
        }
        hash = 31 * hash;
        if (previous != null) {
            hash = hash + previous.hashCode();
        }
        hash = 31 * hash;
        if (data != null) {
            hash = hash + data.hashCode();
        }
        return hash;
    }
}

