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
   
    private Map<Integer, ArrayList<Token>> tokens;
    public AnalisadorSintatico(Map<Integer, ArrayList<Token>> listaTokens){
        this.tokens = listaTokens;
    }
}
