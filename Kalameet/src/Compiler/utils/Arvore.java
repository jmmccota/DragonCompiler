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

    public void setEsquerda(Arvore<T> esq) {
        this.esq = esq;
    }

    public Arvore<T> getDir() {
        return dir;
    }

    public void setDireita(Arvore<T> dir) {
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
        ArrayList<Arvore<T>> fila;
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

    public ArrayList<Arvore<T>> folhas(int altura) {
        ArrayList<Arvore<T>> fila = new ArrayList<>();
        if (altura == 0) {
            fila.add(this);
        } else {
            ArrayList<Arvore<T>> fAux;

            if (esq != null) {
                fAux = esq.folhas(altura - 1);
            } else {
                fAux = new ArrayList<>();
                fAux.add(null);
            }
            for (Arvore<T> fila1 : fAux) {
                fila.add(fila1);
            }

            if (dir != null) {
                fAux = dir.folhas(altura - 1);
            } else {
                fAux = new ArrayList<>();
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

    public Arvore<T> subarvoreReplace(T substituto) {
        if (esq.altura() > 1) {
            return esq.subarvoreReplace(substituto);
        } else if (dir.altura() > 2
                || (dir.altura() == 2
                && !this.nodo.equals(new Token("=", "")))) {
            return dir.subarvoreReplace(substituto);
        } else {
            Arvore<T> aux = new Arvore<>(this.nodo);
            aux.esq = this.esq;
            aux.dir = this.dir;
            this.nodo = substituto;
            this.esq = null;
            this.dir = null;
            return aux;
        }
    }

    public int nOperacoes() {
        int hEsq = 0, hDir = 0;
        if (esq == null && dir == null) {
            return 0;
        }
        if (esq != null) {
            hEsq = esq.nOperacoes();
        }
        if (dir != null) {
            hDir = dir.nOperacoes();
        }
        return 1 + hEsq + hDir;
    }
}
