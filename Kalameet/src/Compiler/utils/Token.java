/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler.utils;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author JM
 */
public class Token implements Serializable {

    private String tipo;
    private String valor;

    public Token(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "<" + tipo + "," + valor + ">";
//        return "<"+valor+">";
//        return valor;;
    }

    public boolean equalsIgnoreCase(Object obj) {
        Token o = (Token) obj;
        return (tipo.equalsIgnoreCase(o.getTipo()));// && valor.equalsIgnoreCase(o.getValor()));
    }

    @Override
    public boolean equals(Object obj) {
        Token o = (Token) obj;
        return (tipo.equalsIgnoreCase(o.getTipo()) && valor.equalsIgnoreCase(o.getValor()));
    }
}
