/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler.utils;

import java.util.ArrayList;

public class Simbolo {

    public String nome;
    public String tipo;
    public String hash;
    public int ultimoUso;
    public boolean isFuncao;
    public boolean isVetor;

    public ArrayList<String> parametros;

    public Simbolo(String nome, String tipo, String hash, int ultimoUso, boolean isFuncao, boolean isVetor) {
        this.nome = nome;
        this.tipo = tipo;
        this.hash = hash;
        this.ultimoUso = ultimoUso;
        this.isFuncao = isFuncao;
        this.isVetor = isVetor;
        this.parametros = null;
        if (isFuncao) {
            this.parametros = new ArrayList<>();
        }
    }

    public boolean equals(Simbolo x) {
        return hash.equals(x.hash);
    }

    public ArrayList<String> getParametros() {
        return parametros;
    }

    public void addParametro(String tipoParametro) {
        parametros.add(tipoParametro);
    }

    @Override
    public String toString() {
        String ret = "nome: " + nome + "\n";
        ret += "tipo: " + tipo;
        if (isFuncao) {
            ret += "\nfuncao\n";
            ret += "parametros:  ";
            for (String i : parametros) {
                if (i != null) {
                    ret += i + ", ";
                }
            }
            ret = ret.substring(0, ret.length() - 2);
        } else if (isVetor) {
            ret += "\nvetor";
        }
        return ret;
    }
}
