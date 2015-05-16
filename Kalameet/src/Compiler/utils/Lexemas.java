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

    private HashMap<String, String> lexemas;

    public Lexemas() {
        lexemas = new HashMap<>();

        //############### LEXEMAS DA LINGUAGEM #################
        //Aritmeticos
        lexemas.put("+", "+");
        lexemas.put("-", "-");
        lexemas.put("*", "*");
        lexemas.put("x", "*");
        lexemas.put("/", "/");
        lexemas.put(":", "/");
        //Comparativos
        lexemas.put(">", ">");
        lexemas.put(">=", ">=");
        lexemas.put("=>", ">=");
        lexemas.put("<", "<");
        lexemas.put("<=", "<=");
        lexemas.put("=<", "<=");
        lexemas.put("==", "==");
        lexemas.put("!=", "!=");
        lexemas.put("e", "and");
        lexemas.put("ou", "or");
        lexemas.put("não", "not");
        //Gerais
        lexemas.put("(", "(");
        lexemas.put(")", ")");
        lexemas.put("[", "[");
        lexemas.put("]", "]");
        lexemas.put("=", "=");
        lexemas.put(".", ".");
        lexemas.put(",", ",");
        //Palavras-chave
        //Gerais
        lexemas.put("endinstr", "endinstr");
        lexemas.put("verdadeiro", "true");
        lexemas.put("falso", "false");
        lexemas.put("funcao", "funinit");
        lexemas.put("fim-funcao", "endfun");
        //Tipos
        lexemas.put("int", "int");
        lexemas.put("float", "float");
        lexemas.put("str", "str,");
        lexemas.put("var", "var");
        lexemas.put("fun", "fun");
        lexemas.put("vetor", "vet");
        //Condicionais
        lexemas.put("se", "if");
        lexemas.put("então", "then");
        lexemas.put("senão", "else");
        lexemas.put("fim-se", "endif");
        //Loops
        lexemas.put("para", "for");
        lexemas.put("de", "from");
        lexemas.put("até", "to");
        lexemas.put("faça", "do");
        lexemas.put("fim-para", "endfor");
        lexemas.put("enquanto", "while");
        lexemas.put("fim-enquanto", "endwhile");
        //#####################################################
    }

    public HashMap<String, String> getLexemas() {
        return lexemas;
    }

}
