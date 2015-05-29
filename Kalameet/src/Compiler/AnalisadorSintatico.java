/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import Compiler.utils.Token;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author JM
 */
public class AnalisadorSintatico {

    private AnalisadorLexico aL;
    //private HashMap<String, String> lexemas;

    public AnalisadorSintatico(AnalisadorLexico an) {
        this.aL = an;
    }

    private boolean verificaFim() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        Integer[] keys = new Integer[linhas.size()];
        keys = linhas.keySet().toArray(keys);
        boolean erro = false;
        for (int i = 0; i < keys.length; i++) {
            if (linhas.get(keys[i]).contains(new Token("fim", "fim"))) {
                if (linhas.get(keys[i]).size() > 2) {
                    System.err.println("Fim deve estar em uma linha separada. Linha: " + keys[i]);
                    erro = true;
                }
                if (i < (keys.length - 1)) {
                    System.err.println("Instrucoes apos o "
                            + "termino do programa. Linha: " + keys[i]);
                    erro = true;
                }
                return erro;
            }
        }
        System.err.println("Fim do programa nao encontrado.");
        return true;
    }

    private boolean verificaEnquanto() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();

        return false;
    }

    private boolean verificaSe() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        return false;
    }

    private boolean verificaPara() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        return false;
    }

    private boolean verificaFuncao() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        return false;
    }

    public boolean startAnalysis() {
        boolean erro = false;
        erro = verificaFim();
        if (!erro) {
            //erro =
        }
        return erro;
    }
}
