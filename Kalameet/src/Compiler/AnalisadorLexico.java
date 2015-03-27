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
        tokens = new LinkedHashMap<Integer, ArrayList<Token>>();
        lexemas = new HashMap<String, String>();
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

    public void startAnalysis() {
        try (BufferedReader br = carrega(path)) {
            int count = 0;
            String lin = "";
            boolean comentario = false;
            ArrayList<Token> listaTokens;

            while (br.ready()) {
                listaTokens = new ArrayList<>();
                count++;
                lin = br.readLine();
                // Token token = null;
                String t = "";
                for (int i = 0; i < lin.length(); i++) {
                    if (lin.charAt(i) == '#') {
                        comentario = !comentario;
                    }
                    if (!comentario) {
                        if (lin.charAt(i) == '"') {

                            i++;
                            while (lin.charAt(i) != '"' && i < lin.length()) {
                                t += lin.charAt(i);
                                i++;
                            }
                            listaTokens.add(new Token(lexemas.get("string"), t));
                            t = "";
                        } else if (lin.charAt(i) == '>' || lin.charAt(i) == '<' || lin.charAt(i) == '=' || lin.charAt(i) == '!') {

                            t += lin.charAt(i);

                            if (lin.length() > (i + 1) && (lin.charAt(i + 1) == '>' || lin.charAt(i + 1) == '<' || lin.charAt(i + 1) == '=')) {
                                String proximo = t + lin.charAt(i + 1);
                                if (lexemas.containsKey(proximo)) {
                                    t = proximo;
                                }
                                i++;
                            }
                            listaTokens.add(new Token(lexemas.get(t), t));
                            t = "";
                        } else if (((i + 2) < lin.length()) && ("" + lin.charAt(i) + lin.charAt(i + 1) + lin.charAt(i + 2)).equals("fim")) {
                            t = "" + lin.charAt(i) + lin.charAt(i + 1) + lin.charAt(i + 2);
                            i += 2;
                            String segunda = "";

                            if (lin.charAt(i + 1) == '-') {
                                int aux = i + 2;
                                while (aux < lin.length() && Character.isLetter(lin.charAt(aux))) {
                                    segunda += lin.charAt(aux);
                                    aux++;
                                }
//                                aux--;
                                if (lexemas.containsKey(t + "-" + segunda)) {
                                    listaTokens.add(new Token(lexemas.get(t + "-" + segunda), t + "-" + segunda));
//                                    i = aux - 1;
                                    i = aux;
                                    t = "";
                                } else {
                                    listaTokens.add(new Token(lexemas.get("var"), t));
                                    listaTokens.add(new Token(lexemas.get("-"), "-"));
                                    t = "";
                                }

                            }
                        } else if (lin.charAt(i) == 'e' && i > 0 && (i + 1) < lin.length() && !listaTokens.isEmpty() && listaTokens.get(listaTokens.size() - 1).getTipo() == ")") {
                            //i++;
                            while (lin.charAt(i) == ' ' && i < lin.length()) {
                                i++;

                            }
//                            i--;
//                            if (s.charAt(i + 1) == '(') {
                            if (lin.charAt(i) == '(') {
//                                t = "";
                                listaTokens.add(new Token(lexemas.get("e"), "e"));
//                            } else if (!Character.isLetter(s.charAt(i + 1)) && !Character.isDigit(s.charAt(i + 1))) {
                            } else if (!Character.isLetter(lin.charAt(i)) && !Character.isDigit(lin.charAt(i))) {
                                t += 'e';
                                listaTokens.add(new Token(lexemas.get("var"), t));
                                t = "";
                            } else {
//                                t = "";
                                t += 'e';
                                if (lin.charAt(i) == ' ') {
                                    listaTokens.add(new Token(lexemas.get("var"), t));
                                    t = "";
                                }
                            }

                        } else if (Character.isDigit(lin.charAt(i))) {
                            if (i > 0 && Character.isLetter(lin.charAt(i - 1)) && lin.charAt(i - 1) != 'x') {
                                while (Character.isDigit(lin.charAt(i))) {
                                    t += lin.charAt(i);
                                    i++;
                                }
                                i--;
                                listaTokens.add(new Token(lexemas.get("int"), t));
                                t = "";
                            } else {
                                boolean inteiro = true;
                                do {
                                    t += lin.charAt(i);
                                    i++;
                                    if (i < lin.length() && (lin.charAt(i) == '.' || lin.charAt(i) == ',') && (i + 1) < lin.length() && Character.isDigit(lin.charAt(i + 1)) && inteiro) {
                                        t = t + lin.charAt(i);
                                        i++;
                                        inteiro = false;
                                    }
                                    if (i < lin.length() && !Character.isDigit(lin.charAt(i))) {
                                        i--;
                                        break;
                                    }
                                } while (i < lin.length() && Character.isDigit(lin.charAt(i)));
                                if (inteiro) {
                                    listaTokens.add(new Token(lexemas.get("int"), t));
                                } else {
                                    listaTokens.add(new Token(lexemas.get("float"), t));
                                }
                                t = "";
                            }
                        } else if (lin.charAt(i) == 'x' && i > 0 && (i + 1) < lin.length() && ((lin.charAt(i - 1) == ' ' && lin.charAt(i + 1) == ' '))) {
                            t = " ";
                            t += lin.charAt(i) + " ";
                            listaTokens.add(new Token(lexemas.get(t), t));
                        } else if (lin.length() == 1 && lin.charAt(i) == 'x') {
                            t = " ";
                            t += lin.charAt(i) + " ";
                            //System.err.println('"' + t + '"');
                            listaTokens.add(new Token(lexemas.get("var"), t));
                        } else if (lin.charAt(i) == '.') {
                            t = "";
                            listaTokens.add(new Token(lexemas.get("."), "."));
//------------------------------------------------------------------------------------------------                            
                        } 
                    }
                }
                tokens.put(count, listaTokens);
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, "Arquivo nao encontrado", ex);
        } finally {

        }
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
