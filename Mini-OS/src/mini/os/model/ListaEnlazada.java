package mini.os.model;

import java.io.Serializable;

// Mi propia implementación de una lista enlazada para no usar las de Java
public class ListaEnlazada<T> implements Serializable{
    Nodo<T> cabeza;
    int size;

    public ListaEnlazada() {
        cabeza = null;
        size = 0;
    }

    // Mete un dato nuevo al final de la lista
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<T>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getNext() != null) {
                actual = actual.getNext();
            }
            actual.setNext(nuevo);
        }
        size++;
    }

    // Imprime todo lo que hay en la lista
    public void mostrar() {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            System.out.println(actual.getInfo());
            actual = actual.getNext();
        }
    }

    // Chequea si un dato ya está en la lista
    public boolean contiene(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getInfo().equals(dato)) {
                return true;
            }
            actual = actual.getNext();
        }
        return false;
    }

    // Quita un dato de la lista si lo encuentra
    public void eliminar(T dato) {
        Nodo<T> actual = cabeza;
        Nodo<T> previo = null;

        if (actual != null && actual.getInfo().equals(dato)) {
            cabeza = actual.getNext();
            size--;
            return;
        }

        while (actual != null && !actual.getInfo().equals(dato)) {
            previo = actual;
            actual = actual.getNext();
        }

        if (actual == null) {
            return;
        }

        previo.setNext(actual.getNext());
        size--;
    }
}
