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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
        lexemas.put("+", "<sum>");
        lexemas.put("-", "<sub>");
        lexemas.put("*", "<mult>");
        lexemas.put("x", "<mult>");
        lexemas.put("/", "<div>");
        lexemas.put(":", "<div>");
        lexemas.put(".", "<.>");
        lexemas.put(",", "<.>");
//Comparativos
        lexemas.put(">", "<gt>");
        lexemas.put(">=", "<gte>");
        lexemas.put("<", "<lt>");
        lexemas.put("=<", "<lte>");
        lexemas.put("==", "<eq>");
        lexemas.put("!=", "<neq>");
//Gerais
        lexemas.put("=", "<atrib>");
        lexemas.put("int", "<int>");
        lexemas.put("float", "<float>");
        lexemas.put("string", "<string>");
        lexemas.put("var", "<id>");
//Palavras-chave
//Condicionais
        lexemas.put("se", "<cond>");
        lexemas.put("então", "<initcond>");
        lexemas.put("senão", "<altcond>");
        lexemas.put("fim-se", "<endcond>");
//Loops
        lexemas.put("para", "<forloop>");
        lexemas.put("de", "<rng1forloop>>");
        lexemas.put("até", "<rng2forloop>>");
        lexemas.put("faça", "<initforloop>>");
        lexemas.put("fim-para", "<endforloop>>");
        lexemas.put("enquanto", "<whileloop>>");
        lexemas.put("fim-enquanto", "<endwhileloop>>");

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
        for (Map.Entry<Integer, ArrayList<String>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<String> value = entrySet.getValue();
            fw.write("\n" + key + "\t");
            for (String value1 : value) {
                fw.write(value1);
            }
        }
        fw.close();
    }

    public void salva(boolean substituir) throws FileNotFoundException, IOException {
        File arquivo = new File(path + ".lex");
        FileWriter fw = new FileWriter(arquivo, substituir);
        for (Map.Entry<Integer, ArrayList<String>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<String> value = entrySet.getValue();
            fw.write("\n" + key + "\t");
            for (String value1 : value) {
                fw.write(value1);
            }
        }
        fw.close();
    }

    public void startAnalysis() {
        try {
            BufferedReader br = carrega(path);
            int count = 0;
            while (br.ready()) {
                count++;
                tokens.put(count, analisa(br.readLine()));
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, "Arquivo nao encontrado", ex);
        }
        for (Map.Entry<Integer, ArrayList<String>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<String> value = entrySet.getValue();
            System.out.print(key);
            for (String value1 : value) {
                System.out.print(" - " + value1);
            }
            System.out.println("");
        }
    }

    private ArrayList<String> analisa(String s) {
        ArrayList<String> listaTokens = new ArrayList<String>();
        String token = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '#') {
                return listaTokens;
            }
            if (s.charAt(i) == '"') {
                i++;
                token = lexemas.get("string")+"<";
                while (s.charAt(i) != '"') {
                    token += s.charAt(i);
                    i++;
                }
                token+=">";
                listaTokens.add(token);
            } else if (ValidaLetra(s.charAt(i))) {
                token = "";
                token = token + s.charAt(i);
                if (lexemas.containsKey(token) && !ValidaLetra(s.charAt(i + 1))) {
                    token = lexemas.get(token) + "<" + token+">";
                    listaTokens.add(token);
                }
//            else if(true){
//                
//            }
            }
        }
        return listaTokens;
    }

    private boolean ValidaLetra(char letra) {
        return (letra >= 65 && letra <= 90) || (letra >= 97 && letra <= 122);
    }

//    public boolean ValidaHifen(char simbolo, int indice) {
//        return simbolo.charAt(indice) == '-' && ValidaLetra(simbolo.charAt(indice - 1)) && ValidaLetra(simbolo.charAt(indice + 1));
//    }
    public static void main(String[] args) {
        try {
            AnalisadorLexico al = new AnalisadorLexico("lprime.l");
            al.startAnalysis();
            al.salva(false);
            //al.deserializa();
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
