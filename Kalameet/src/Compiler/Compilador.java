package Compiler;

import Compiler.utils.Arvore;
import Compiler.utils.Escopo;
import Compiler.utils.Simbolo;
import Compiler.utils.Token;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.SysexMessage;

public class Compilador {

    private LinkedHashMap<Integer, ArrayList<Token>> linhas;
    private HashMap<Integer, Arvore<Token>> arvores;
    private HashMap<String, Simbolo> tabelaSimbolos;
    private BufferedWriter file;
    private Escopo escopos;
    private String[] variaveis;
    int nLinha;

    public void addTabelaSimbolo(Simbolo s) {
        if (tabelaSimbolos.containsKey(s.hash)) {

            if (!tabelaSimbolos.get(s.hash).tipo.equals(s.tipo)) {
                System.err.println("Variavel '" + s.nome + "' e do tipo '"
                        + tabelaSimbolos.get(s.hash).tipo + "' e foi atribuida valor do tipo '"
                        + s.tipo + "'.Linha: " + s.ultimoUso);
                System.exit(0);
            }
            if (s.ultimoUso > tabelaSimbolos.get(s.hash).ultimoUso) {
                tabelaSimbolos.get(s.hash).ultimoUso = s.ultimoUso;
            }

            if (s.isFuncao) {
                tabelaSimbolos.replace(s.hash, s);
            }

        } else {
            tabelaSimbolos.put(s.hash, s);
        }
    }

    public Simbolo getSimbolo(String hash, int linha) {
        if (linha > tabelaSimbolos.get(hash).ultimoUso) {
            tabelaSimbolos.get(hash).ultimoUso = linha;
        }

        return tabelaSimbolos.get(hash);
    }

    public Compilador(String out, String pathTo, String pathA, String pathTa)
            throws IOException, ClassNotFoundException {
        carregar(pathTo, pathA, pathTa);
        this.init(out);
    }

    public Compilador(String out,
            LinkedHashMap<Integer, ArrayList<Token>> linhas,
            HashMap<Integer, Arvore<Token>> arvores,
            HashMap<String, Simbolo> tabelaSimbolos)
            throws FileNotFoundException, IOException {
        this.linhas = linhas;
        this.arvores = arvores;
        this.tabelaSimbolos = tabelaSimbolos;
        this.init(out);
    }

    private void init(String out) throws IOException {
        File f = new File(out);
        if (!f.exists()) {
            f.createNewFile();
        }
        file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
        variaveis = new String[10];
        escopos = new Escopo();
        for (int i = 0; i < variaveis.length; i++) {
            variaveis[i] = "";
        }
    }

    private void carregar(String pathTo, String pathA, String pathTa)
            throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(pathTo);
        ObjectInputStream ois = new ObjectInputStream(fis);
        linhas = (LinkedHashMap<Integer, ArrayList<Token>>) ois.readObject();
        fis = new FileInputStream(pathA);
        ois = new ObjectInputStream(fis);
        arvores = (HashMap<Integer, Arvore<Token>>) ois.readObject();
        fis = new FileInputStream(pathTa);
        ois = new ObjectInputStream(fis);
        tabelaSimbolos = (HashMap<String, Simbolo>) ois.readObject();
        fis.close();
        ois.close();
    }

    private int getRegistrador() {
        for (int i = 0; i < variaveis.length; i++) {
            String x = "";
            x = variaveis[i];
            try {
                if (getSimbolo(x, 0).ultimoUso < nLinha) {
                    return i;
                }
            } catch (Exception e) {
                return i;
            }
        }
        return -1;
    }

    private void addVariavel(String nome) {
        if (getVarivavel(nome) != -1) {
            return;
        }
        int x = getRegistrador();
        if (x == -1) {
            System.err.println("Quantidade de registradores estourada. Linha:" + nLinha);
            System.exit(0);
        }
        variaveis[x] = escopos.getVariavel(nome);

    }

    private int getVarivavel(String nome) {
        nome = escopos.getVariavel(nome);
        for (int i = 0; i < variaveis.length; i++) {
            if (variaveis[i].equals(nome)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOf(ArrayList array, Object x, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (array.get(i).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfTipo(ArrayList<Token> array, String tipo, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (array.get(i).getTipo().equals(tipo)) {
                return i;
            }
        }
        return -1;
    }

    private String token(Token t) {
        if (t.getTipo().equals("str")) {
            return "\"" + t.getValor() + "\"";
        } else if (t.getTipo().equals("int") || t.getTipo().equals("float") || t.getTipo().equals("array")) {
            return t.getValor();
        } else if (t.getTipo().equals("fun")) {
            return chamadaFuncao(t).getValor();
        } else if (t.getTipo().equals("id")) {
            escopos.adicionaVariavel(t.getValor());
            addVariavel(t.getValor());
            return "n" + getVarivavel(t.getValor());
        } else if (t.getTipo().equals("and")) {
            return "&&";
        } else if (t.getTipo().equals("or")) {
            return "||";
        } else if (t.getTipo().equals("not")) {
            return "!";
        } else if (t.getTipo().equals("def")) {
            return "function ";
        } else if (t.getTipo().equals("endfor") || t.getTipo().equals("endif")
                || t.getTipo().equals("endwhile") || t.getTipo().equals("enddef")) {
            return "}\n";
        } else if (t.getTipo().equals("if") || t.getTipo().equals("while") || t.getTipo().equals("for")) {
            return t.getTipo() + "(";
        } else if (t.getTipo().equals("else")) {
            return "}else{\n";
        } else if (t.getTipo().equals("then") || t.getTipo().equals("do")) {
            return "){\n";
        } else if (t.getTipo().equals("from")) {
            return "=";
        } else if (t.getTipo().equals("end")) {
            return "";
        } else {
            return t.getTipo();
        }
    }

    private ArrayList<ArrayList<Token>> loadArvore() {
        Arvore<Token> arvore = arvores.get(nLinha);
        ArrayList<ArrayList<Token>> lista = new ArrayList<>();
        if (arvore == null) {
            return null;
        }
        int cont = 0;
        while (arvore.altura() != 1) {
            ArrayList<Token> x = arvore.subarvoreReplace(new Token("id", ".aux" + cont)).inOrdem();
            lista.add(x);
            if (!x.contains(new Token("=", ""))) {
                lista.get(lista.size() - 1).add(0, new Token("=", ""));
                lista.get(lista.size() - 1).add(0, new Token("id", ".aux" + cont));
                escopos.adicionaVariavel(".aux" + cont);

                addTabelaSimbolo(new Simbolo(".aux" + cont, "",
                        escopos.getVariavel(".aux" + cont), nLinha, false, false));
            }
            cont++;
        }
        return lista;
    }

    private void declaraVariaveis() {
        String s = "var ";
        for (int i = 0; i < 10; i++) {
            s += "a" + i + ",";
        }
        try {
            file.append(s.substring(0, s.length() - 1) + ";\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    private Token chamadaFuncao(Token funcao) {
        String[] aux = funcao.getValor().split("[(]");
        String nome = aux[0];
        String param = aux[1].substring(0, aux[1].length() - 1);
        String[] parametros = param.split(",");
        String s = nome += "(";
        for (String p : parametros) {
            if ("".equals(p)) {
                continue;
            }
            String v = "";
            try {
                Integer.parseInt(p);
                v = p;
            } catch (NumberFormatException e) {
            }
            try {
                Double.parseDouble(p);
                v = p;
            } catch (NumberFormatException e) {
            }
            if (!"".equals(v)) {
            } else if (p.contains("\"")
                    || p.contains("[")) {
                v = p;
            } else if (p.contains("(")) {
                v = chamadaFuncao(new Token("fun", p)).getValor();
            } else {
                v = token(new Token("id", p));
            }
            s += v + ",";
        }
        s = ("".equals(parametros[0]) ? s : s.substring(0, s.length() - 1)) + ")";
        return new Token("fun", s);
    }

    private void atribuicao() {
        ArrayList<ArrayList<Token>> listas = loadArvore();
        for (ArrayList<Token> lista : listas) {
            String s = "";
            for (Token t : lista) {
                s += token(t);
            }
            try {
                file.append(s + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(3);
            }
        }
    }

    private void executarLinha(ArrayList<Token> linha) throws IOException {
        if (linha.contains(new Token("=", ""))) {

            if (linha.get(0).getValor().startsWith("tela")) {
                String s = "alert(";
                for (int i = 2; i < linha.size(); i++) {
                    s += token(linha.get(i));
                }
                file.append(s + ");\n");
                return;
            }

            atribuicao();
        } else if (linha.contains(new Token("for", ""))) {
            int idx = linha.indexOf(new Token("to", ""));
            String op;
            if (Integer.parseInt(linha.get(idx - 1).getValor())
                    < Integer.parseInt(linha.get(idx + 1).getValor())) {
                op = "<=";
            } else {
                op = ">=";
            }
            String s = "";
            for (int i = 0; i < linha.size(); i++) {
                Token t = linha.get(i);
                if ("to".equals(t.getTipo())) {
                    s += ";" + token(linha.get(1)) + op;
                } else if ("do".equals(t.getTipo())) {
                    s += ";" + token(linha.get(1)) + ("<=".equals(op) ? "++" : "--") + token(t);
                } else {
                    s += token(t);
                }
            }
            file.append(s);
        } else if (linha.contains(new Token("vet", ""))) {
            int idx2 = indexOf(linha, new Token("[", ""), 3, linha.size() - 1);
            String s = linha.get(1).getValor() + " = [";
            for (int i = 0; i < Integer.parseInt(linha.get(3).getValor()); i++) {
                if (idx2 == -1) {
                    s += "0,";
                } else {
                    s += "[";
                    for (int j = 0; j < Integer.parseInt(linha.get(6).getValor()); j++) {
                        s += "0,";
                    }
                    s = s.substring(0, s.length() - 1) + "],";
                }
            }
            s = s.substring(0, s.length() - 1) + "];\n";
            file.append(s);
        } else {
            for (Token t : linha) {
                file.append(token(t));
            }
        }

    }

    private void executarFuncao(String nomeFun)
            throws IOException {
        String[] backupVariaveis = new String[10];
        for (int i = 0; i < backupVariaveis.length; i++) {
            backupVariaveis[i] = variaveis[i];
            variaveis[i] = "";
        }
        Escopo backup = escopos;
        escopos = new Escopo();
        escopos.idEscopo = backup.idEscopo;
        int backupLinha = nLinha;

        boolean naFuncao = false;

        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();
            if (linha.contains(new Token("fun", nomeFun))
                    && linha.contains(new Token("def", ""))) {

                naFuncao = true;
                escopos.adicionaEscopo();
                String chamada = "";
                for (Token t : linha) {
                    if ("fun".equals(t.getTipo())) {
                        chamada += t.getValor();
                    } else {
                        chamada += token(t);
                    }
                }
                file.append(chamada + "{\n");
                declaraVariaveis();

                continue;
            }
            if (!naFuncao) {
                continue;
            }
            if (linha.contains(new Token("enddef", ""))) {
                file.append(token(new Token("enddef", "")));
                break;
            }
            if ("=".equals(linha.get(1).getTipo())
                    && nomeFun.equals(linha.get(0).getValor())) {
                String ret = "return ";
                for (int i = 2; i < linha.size(); i++) {
                    ret += token(linha.get(i));
                }
                file.append(ret + ";\n");
                continue;
            }

            executarLinha(linha);
        }
        backup.idEscopo = escopos.idEscopo;
        escopos = backup;
        nLinha = backupLinha;
    }

    public void startAnalysis() {
        boolean isFuncao = false;

        declaraVariaveis();
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();

            if (linha.contains(new Token("def", ""))) {
                isFuncao = true;
            } else if (linha.contains(new Token("enddef", ""))) {
                isFuncao = false;
                continue;
            }

            if (isFuncao) {
                continue;
            }

            if (linha.contains(new Token("if", ""))
                    || linha.contains(new Token("while", ""))
                    || linha.contains(new Token("for", ""))) {
                escopos.adicionaEscopo();
            } else if (linha.contains(new Token("endif", ""))
                    || linha.contains(new Token("endwhile", ""))
                    || linha.contains(new Token("endfor", ""))) {
                escopos.removeEscopo();
            }
            int indexFun = indexOfTipo(linha, "fun", 0, linha.size() - 1);
            while (indexFun != -1) {

                if (escopos.getVariavel("." + linha.get(indexFun).getValor()) == null) {
                    escopos.adicionaVariavel("." + linha.get(indexFun).getValor());
                    try {
                        executarFuncao(linha.get(indexFun).getValor());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.exit(3);
                    }
                }
                indexFun = indexOfTipo(linha, "fun", indexFun + 1, linha.size() - 1);
            }

            try {
                executarLinha(linha);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(3);
            }

        }
        try {
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(3);
        }
    }

    /**
     * @param args the command line arguments
     */
    private static void help() {
        System.out.println("Historia:");
        System.out.println("\tKalameet é o dragão negro do jogo Dark Souls");
        System.out.println("\tEm homenagem ao livro texto da disciplina que tem um dragão na capa");
        System.out.println("\tCompiladores| 2015/01");
        System.out.println("\tProf: Leonardo Lacerda");
        System.out.println("\tAluno: João Marcos");
        System.out.println("uso:");
        System.out.println("\tInvocar maquina virtual java:");
        System.out.println("\t\tjava -jar");
        System.out.println("\tchamada do programa:");
        System.out.println("\tPassar arquivo de entrada, padrão lprime.l");
        System.out.println("\t\tjava -jar Kalameet.jar arquivo.txt");
        System.out.println("\tSalvar para arquivo: -2f, padrão ativado");
        System.out.println("\t\tjava -jar Kalameet.jar arquivo.txt -2f");
        System.out.println("");
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                if (args[0].equals("-help") || args[0].equals("-h")) {
                    help();
                } else {
                    AnalisadorLexico al = new AnalisadorLexico(args[0]);
                    al.startAnalysis();
                    if (args.length > 1 && args[1].equals("-2f")) {
                        al.salva(false);
                    } else {
                        al.imprime();
                    }
                }

            } else {
                help();
                AnalisadorLexico al = new AnalisadorLexico("lprime.l");
                al.startAnalysis();
                al.salva(false);
                AnalisadorSintatico aS = new AnalisadorSintatico(al);
                aS.startAnalysis();
                AnalisadorSemantico aSem = new AnalisadorSemantico((LinkedHashMap<Integer, ArrayList<Token>>) al.getTokens());
                aSem.startAnalysis();
            }
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
