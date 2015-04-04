package Compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author JM
 */
public class AnalisadorLexico {

    private Map<Integer, ArrayList<Token>> tokens;
    private Map<String, String> lexemas;
    private String path;
    private Stack<String> pilha;
    private boolean funcao;

    public AnalisadorLexico(String path) {
        tokens = new LinkedHashMap<>();
        lexemas = new HashMap<>();
        pilha = new Stack<>();
        this.path = path;
        preencheLexemas();
    }

    private void preencheLexemas() {
//Aritmeticos
        lexemas.put("+", "+");
        lexemas.put("-", "-");
        lexemas.put("*", "*");
        lexemas.put(" x ", "*");
        lexemas.put("/", "/");
        lexemas.put(":", "/");
        lexemas.put(".", ".");
        lexemas.put(",", ".");
        lexemas.put("[", "[");
        lexemas.put("]", "]");
        lexemas.put("(", "(");
        lexemas.put(")", ")");
//        lexemas.put("", "");
//Comparativos
        lexemas.put(">", "gt");
        lexemas.put(">=", "gte");
        lexemas.put("<", "lt");
        lexemas.put("=<", "lte");
        lexemas.put("==", "eq");
        lexemas.put("!=", "neq");
//Gerais
        lexemas.put("=", "=");
        lexemas.put("int", "int");
        lexemas.put("float", "float");
        lexemas.put("string", "string");
        lexemas.put("var", "id");
        lexemas.put("fun", "fun");
        lexemas.put("vet", "vet");
//Palavras-chave
//Condicionais
        lexemas.put("se", "cond");
        lexemas.put("então", "initcond");
        lexemas.put("senão", "altcond");
        lexemas.put("fim-se", "endcond");
        lexemas.put("e", "&&");
        lexemas.put("ou", "||");
//Loops
        lexemas.put("para", "forloop");
        lexemas.put("de", "rng1forloop");
        lexemas.put("até", "rng2forloop");
        lexemas.put("faça", "initloop");
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
        //Serializa o linkedhashmap de tokens e salva
        FileOutputStream fos
                = new FileOutputStream(path + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tokens);
        oos.close();
        fos.close();

    }

    public void deserializa() throws ClassNotFoundException, IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path + ".ser");
            ois = new ObjectInputStream(fis);
            tokens = (HashMap) ois.readObject();
            ois.close();
            fis.close();
//        teste pra ver se serializacao funciona
//        for (Map.Entry<String, String> entrySet : map.entrySet()) {
//            String key = entrySet.getKey();
//            String value = entrySet.getValue();
//            System.out.println(key + " - " + value);
//        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, "Arquivo não encontrado", ex);
        }
    }

    public BufferedReader carrega() throws FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(file, "Cp1252");
        BufferedReader br = new BufferedReader(isr);
//        file.close();
//        isr.close();
        return br;
    }

    public void salva() throws FileNotFoundException, IOException {
        File arquivo = new File(path + ".lex");
        FileWriter fw = new FileWriter(arquivo, true);
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Token> value = entrySet.getValue();
            //fw.write("\n" + key + "\t");
            for (Token value1 : value) {
                fw.write(value1.getValor() + "\n");
            }
        }
        fw.close();
    }

    public void salva(boolean substituir) throws FileNotFoundException, IOException {
        File arquivo = new File(path + ".lex");
        FileWriter fw = new FileWriter(arquivo, substituir);
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Token> value = entrySet.getValue();
            //fw.write("\n" + key + "\t");
            System.out.print(key + "\t- ");
            for (Token value1 : value) {
                System.out.print(value1.toString() + " ");
                fw.write(value1.getValor() + "\n");
            }
            System.out.println("");
        }
        fw.close();
    }

    public void imprime() {
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Token> value = entrySet.getValue();
            System.out.print(key + "\t- ");
            for (Token value1 : value) {
                System.out.print(value1.toString() + " ");
            }
            System.out.println("");
        }
    }

    public void startAnalysis() {
        // avisei pra não copiar...
    }

    public static void main(String[] args) {
        try {
            AnalisadorLexico al = new AnalisadorLexico("lprime.l");
            al.startAnalysis();
            al.salva(false);
            //al.deserializa();

        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
