package mini.os.model;

import java.io.Serializable;

// Un nodo simple para la lista enlazada
public class Nodo<T> implements Serializable{
    private T info;
    private Nodo<T> next;

    public Nodo(T dato){
        this.info=dato;
        this.next=null;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public Nodo<T> getNext() {
        return next;
    }

    public void setNext(Nodo<T> next) {
        this.next = next;
    }

    
}
