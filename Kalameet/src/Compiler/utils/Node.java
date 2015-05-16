/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Node<T> {

    private T vertex;

    private List<Edge<T>> edges;

    private Node<T> parent;

    private boolean isVisited;

    public Node(T vertex) {
        this.vertex = vertex;
        this.edges = new ArrayList<>();
    }

    public T vertex() {
        return vertex;
    }

    public boolean addEdge(Node<T> node, int weight) {
        if (hasEdge(node)) {
            return false;
        }
        Edge<T> newEdge = new Edge<>(this, node, weight);
        return edges.add(newEdge);
    }

    public boolean removeEdge(Node<T> node) {
        Optional<Edge<T>> optional = findEdge(node);
        if (optional.isPresent()) {
            return edges.remove(optional.get());
        }
        return false;
    }

    public boolean hasEdge(Node<T> node) {
        return findEdge(node).isPresent();
    }

    private Optional<Edge<T>> findEdge(Node<T> node) {
        return edges.stream()
                .filter(edge -> edge.isBetween(this, node))
                .findFirst();
    }

    public List<Edge<T>> edges() {
        return edges;
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public Node<T> parent() {
        return parent;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }
}
