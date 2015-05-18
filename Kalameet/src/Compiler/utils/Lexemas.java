/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler.utils;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;

public class Lexemas {

    private static HashMap<String, String> instancia;

    private Lexemas() {
        instancia = new HashMap<>();
    }

    private HashMap<String, String> preenche(){
        //############### LEXEMAS DA LINGUAGEM #################
          //Aritmeticos
        instancia.put("+", "+");
        instancia.put("-", "-");
        instancia.put("*", "*");
        instancia.put("x", "*");
        instancia.put("/", "/");
        instancia.put(":", "/");
        //Comparativos
        instancia.put(">", ">");
        instancia.put(">=", ">=");
        instancia.put("=>", ">=");
        instancia.put("<", "<");
        instancia.put("<=", "<=");
        instancia.put("=<", "<=");
        instancia.put("==", "==");
        instancia.put("!=", "!=");
        instancia.put("e", "and");
        instancia.put("ou", "or");
        instancia.put("não", "not");
        //Gerais
        instancia.put("(", "(");
        instancia.put(")", ")");
        instancia.put("[", "[");
        instancia.put("]", "]");
        instancia.put("=", "=");
        instancia.put(".", ".");
        instancia.put(",", ",");
        //Palavras-chave
        //Gerais
        instancia.put("endinstr", "endinstr");
        instancia.put("verdadeiro", "true");
        instancia.put("falso", "false");
        instancia.put("funcao", "funinit");
        instancia.put("fim-funcao", "endfun");
        //Tipos
        instancia.put("int", "int");
        instancia.put("float", "float");
        instancia.put("str", "str,");
        instancia.put("var", "var");
        instancia.put("fun", "fun");
        instancia.put("vetor", "vet");
        //Condicionais
        instancia.put("se", "if");
        instancia.put("então", "then");
        instancia.put("senão", "else");
        instancia.put("fim-se", "endif");
        //Loops
        instancia.put("para", "for");
        instancia.put("de", "from");
        instancia.put("até", "to");
        instancia.put("faça", "do");
        instancia.put("fim-para", "endfor");
        instancia.put("enquanto", "while");
        instancia.put("fim-enquanto", "endwhile");
        instancia.put("|n","|n");
        return instancia;
    }
    public static synchronized HashMap<String, String> getLexemas() {
        if(instancia == null){
            instancia = new Lexemas().preenche();
        } 
        return instancia;
    }

}
