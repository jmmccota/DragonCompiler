package Compiler.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Arvore<T> implements Serializable {

    private Arvore<T> esq, dir;
    private T nodo;

    public Arvore(T node) {
        esq = null;
        dir = null;
        nodo = node;
    }

    public void setNodo(T x) {
        nodo = x;
    }

    public T getNodo() {
        return nodo;
    }

    public Arvore<T> getEsq() {
        return esq;
    }

    public void setEsq(Arvore<T> esq) {
        this.esq = esq;
    }

    public Arvore<T> getDir() {
        return dir;
    }

    public void setDir(Arvore<T> dir) {
        this.dir = dir;
    }

    public ArrayList<T> inOrdem() {
        ArrayList<T> lista = new ArrayList<>();
        if (esq != null) {
            ArrayList<T> x = esq.inOrdem();
            for (T i : x) {
                lista.add(i);
            }
        }
        if (nodo != null) {
            lista.add(nodo);
        }
        if (dir != null) {
            ArrayList<T> x = dir.inOrdem();
            for (T i : x) {
                lista.add(i);
            }
        }
        return lista;
    }

    @Override
    public String toString() {
        return nodo.toString();
    }

    public void print() {
        int alt = altura();
        int esp = (int) (Math.pow(2, alt));
        LinkedList<Arvore<T>> fila;
        for (int i = 0; i < alt; i++) {
            fila = this.folhas(i);
            for (int j = 0; j < esp / 2 - 1; j++) {
                System.out.print("\t");
            }
            for (Iterator<Arvore<T>> iterator = fila.iterator(); iterator.hasNext();) {
                Arvore<T> next = iterator.next();
                if (next != null) {
                    System.out.print(next.toString());
                }
                for (int j = 0; j < esp; j++) {
                    System.out.print("\t");
                }
            }
            esp = esp / 2;
            System.out.println("");
        }
    }

    protected LinkedList<Arvore<T>> folhas(int altura) {
        LinkedList<Arvore<T>> fila = new LinkedList<>();
        if (altura == 0) {
            fila.add(this);
        } else {
            LinkedList<Arvore<T>> fAux;

            if (esq != null) {
                fAux = esq.folhas(altura - 1);
            } else {
                fAux = new LinkedList<>();
                fAux.add(null);
            }
            for (Arvore<T> fila1 : fAux) {
                fila.add(fila1);
            }

            if (dir != null) {
                fAux = dir.folhas(altura - 1);
            } else {
                fAux = new LinkedList<>();
                fAux.add(null);
            }
            for (Arvore<T> fila1 : fAux) {
                fila.add(fila1);
            }
        }
        return fila;
    }

    public int altura() {
        int hEsq = 0, hDir = 0;
        if (esq != null) {
            hEsq = esq.altura();
        }
        if (dir != null) {
            hDir = dir.altura();
        }
        return 1 + (hEsq > hDir ? hEsq : hDir);
    }
}
