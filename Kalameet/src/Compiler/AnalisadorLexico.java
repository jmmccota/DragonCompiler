/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JM
 */
public class AnalisadorLexico {

    private Map<Integer, ArrayList<String>> tokens;
    private Map<String, String> lexemas;
    private String path;

    public AnalisadorLexico(String path) {
        tokens = new LinkedHashMap<Integer, ArrayList<String>>();
        lexemas = new HashMap<String, String>();
        this.path = path;
        preencheLexemas();
    }

    private void preencheLexemas() {
//Aritmeticos
        lexemas.put("+", "sum");
        lexemas.put("-", "sub");
        lexemas.put("*", "mult");
        lexemas.put("x", "mult");
        lexemas.put("/", "div");
        lexemas.put(":", "div");
        lexemas.put(".", ".");
        lexemas.put(",", ".");
//Comparativos
        lexemas.put(">", "gt");
        lexemas.put(">=", "gte");
        lexemas.put("<", "lt");
        lexemas.put("=<", "lte");
        lexemas.put("==", "eq");
        lexemas.put("!=", "eq");
//Gerais
        lexemas.put("=", "atrib");
        lexemas.put("int", "int,");
        lexemas.put("float", "float,");
        lexemas.put("str", "str,");
        lexemas.put("var", "id,");
//Palavras-chave
//Condicionais
        lexemas.put("se", "cond");
        lexemas.put("então", "initcond");
        lexemas.put("senão", "altcond");
        lexemas.put("fim-se", "endcond");
//Loops
        lexemas.put("para", "forloop");
        lexemas.put("de", "rng1forloop");
        lexemas.put("até", "rng2forloop");
        lexemas.put("faça", "initforloop");
        lexemas.put("fim-para", "endforloop");
        lexemas.put("enquanto", "whileloop");
        lexemas.put("fim-enquanto", "endwhileloop");

    }

    public BufferedReader carrega(String path) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "Cp1252"));
        return reader;
    }

    public void serializa() throws IOException {
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream(path + ".lex"), "Cp1252"));
        FileOutputStream fos
                = new FileOutputStream(path + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(lexemas);
        oos.close();
        fos.close();
        //Serializa o linkedhashmap de tokens e salva
    }

    public void deserializa() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(path + ".ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap<String, String> map = (HashMap) ois.readObject();
        ois.close();
        fis.close();
//        for (Map.Entry<String, String> entrySet : map.entrySet()) {
//            String key = entrySet.getKey();
//            String value = entrySet.getValue();
//            System.out.println(key + " - " + value);
//        }
    }

    public static void main(String[] args) {
        try {
            AnalisadorLexico al = new AnalisadorLexico("teste");
            al.deserializa();
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
