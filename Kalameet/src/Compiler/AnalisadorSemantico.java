package Compiler;

import Compiler.utils.Escopo;
import java.util.HashMap;
import Compiler.utils.Simbolo;
import Compiler.utils.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalisadorSemantico {

    //----------------------------------------------------------------------------------------
    private HashMap<String, Simbolo> tabelaSimbolos;
    private LinkedHashMap<Integer, ArrayList<Token>> linhas;
    private Escopo escopos;
    private int nLinha;

    public AnalisadorSemantico() {
        tabelaSimbolos = new HashMap<>();
    }

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

    public String[] getFuncoes() {
        ArrayList<String> funcoes = new ArrayList<>();
        for (Map.Entry<String, Simbolo> entrySet : tabelaSimbolos.entrySet()) {
            if (entrySet.getValue().isFuncao) {
                funcoes.add(entrySet.getKey());
            }
        }

        Object[] aux = funcoes.toArray();
        String[] ret = new String[aux.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = aux[i].toString();
        }
        return ret;
    }

    public void printTabelaSimbolos() {
        for (String key : tabelaSimbolos.keySet()) {
            System.out.println(key + " = {\n" + tabelaSimbolos.get(key).toString() + "\n}");
        }
    }
    //----------------------------------------------------------------------------------------

    public AnalisadorSemantico(LinkedHashMap<Integer, ArrayList<Token>> tokens) {
        linhas = tokens;
        escopos = new Escopo();
        tabelaSimbolos = new HashMap<>();
    }

    public HashMap<String, Simbolo> getTabelaSimbolos() {
        return tabelaSimbolos;
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

    private int rIndexOfParen(ArrayList array, Object x, int start, int end) {
        int pilha = 0;
        for (int i = end; i >= start; i--) {
            if (array.get(i).equals(new Token(")", ""))) {
                pilha++;
            } else if (array.get(i).equals(new Token("(", ""))) {
                pilha--;
            }
            if (array.get(i).equals(x)
                    && pilha == 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean contains(ArrayList<Token> linha, Token t, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (t.equals(linha.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<Token> linha, String tipo, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (tipo.equals(linha.get(i).getTipo())) {
                return true;
            } else if ("id".equals(linha.get(i).getTipo())
                    || "fun".equals(linha.get(i).getTipo())) {
                Simbolo s = getSimbolo(escopos.getVariavel(linha.get(i).getValor()), nLinha);
                if (s.tipo.equals(tipo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String tipoExpressao(ArrayList<Token> linha, int start, int end) {
        if (start > end) {
            return "null";
        }

        Token[] operador = new Token[14];
        operador[0] = new Token("==", "");
        operador[1] = new Token("!=", "");
        operador[2] = new Token(">=", "");
        operador[3] = new Token("<=", "");
        operador[4] = new Token(">", "");
        operador[5] = new Token("<", "");
        operador[6] = new Token("and", "");
        operador[7] = new Token("or", "");
        operador[8] = new Token("not", "");
        operador[9] = new Token("+", "");
        operador[10] = new Token("-", "");
        operador[11] = new Token("*", "");
        operador[12] = new Token("/", "");
        operador[13] = new Token("=", "");
        int maior = -1;
        for (int j = 0; j < 14; j++) {
            if (rIndexOfParen(linha, operador[j], start, end) > maior) {
                maior = rIndexOfParen(linha, operador[j], start, end);
                break;
            }
        }

        if (maior == -1
                && linha.get(start) == new Token("(", "")
                && linha.get(end) == new Token(")", "")) {
            return tipoExpressao(linha, start + 1, end - 1);

        } else if (contains(linha, new Token("==", ""), start, end)
                || contains(linha, new Token("!=", ""), start, end)
                || contains(linha, new Token(">=", ""), start, end)
                || contains(linha, new Token("<=", ""), start, end)
                || contains(linha, new Token(">", ""), start, end)
                || contains(linha, new Token("<", ""), start, end)
                || contains(linha, new Token("and", ""), start, end)
                || contains(linha, new Token("or", ""), start, end)
                || contains(linha, new Token("not", ""), start, end)
                || contains(linha, new Token("true", ""), start, end)
                || contains(linha, new Token("false", ""), start, end)
                || contains(linha, "bool", start, end)) {
            return "bool";

        } else if (contains(linha, "str", start, end)) {
            return "str";

        } else if (contains(linha, "float", start, end)
                || contains(linha, new Token("/", ""), start, end)) {
            return "float";

        } else {
            return "int";
        }

    }

    private void consistenciaTipo(ArrayList<Token> linha, int start, int end) {
        if (start > end) {
            return;
        }
        if (linha.get(start) == new Token("(", "")
                && linha.get(end) == new Token(")", "")) {
            consistenciaTipo(linha, start + 1, end - 1);
        }

        Token[] operador = new Token[14];
        operador[0] = new Token("==", "");
        operador[1] = new Token("!=", "");
        operador[2] = new Token(">=", "");
        operador[3] = new Token("<=", "");
        operador[4] = new Token(">", "");
        operador[5] = new Token("<", "");
        operador[6] = new Token("and", "");
        operador[7] = new Token("or", "");
        operador[8] = new Token("not", "");
        operador[9] = new Token("+", "");
        operador[10] = new Token("-", "");
        operador[11] = new Token("*", "");
        operador[12] = new Token("/", "");
        operador[13] = new Token("=", "");
        int maior = -1;
        int op = -1;
        for (int j = 0; j < 9; j++) {
            if (rIndexOfParen(linha, operador[j], start, end) > maior) {
                maior = rIndexOfParen(linha, operador[j], start, end);
                op = j;
            }
        }

        if (maior != -1) {
            String tipoEsqPuro = tipoExpressao(linha, start, maior - 1);
            String tipoDirPuro = tipoExpressao(linha, maior + 1, end);
            String tipoEsq = ("int".equals(tipoEsqPuro) || "float".equals(tipoEsqPuro)) ? "num" : tipoEsqPuro;
            String tipoDir = ("int".equals(tipoDirPuro) || "float".equals(tipoDirPuro)) ? "num" : tipoDirPuro;

            if (op < 6 && tipoDir.equals(tipoEsq) && !"null".equals(tipoDir)
                    && !"bool".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op < 8 && tipoDir.equals(tipoEsq) && "bool".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op == 8 && "null".equals(tipoEsq) && "bool".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op == 9 && tipoDir.equals(tipoEsq) && "num".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op == 10 && ((tipoDir.equals(tipoEsq) && "num".equals(tipoDir))
                    || ("null".equals(tipoEsq) && "num".equals(tipoDir)))) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op < 13 && tipoDir.equals(tipoEsq) && "num".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else if (op == 13 && tipoDirPuro.equals(tipoEsqPuro) && !"null".equals(tipoDir)) {
                consistenciaTipo(linha, start, maior - 1);
                consistenciaTipo(linha, maior + 1, end);
            } else {
                System.err.println("Tipos invalidos para operador '"
                        + operador[op].getTipo() + "'.");
                System.exit(0);
            }
        }
    }

    private void analisaCondicao(ArrayList<Token> linha, String bloco) {
        if ("bool".equals(tipoExpressao(linha, 1, linha.size() - 2))) {
            consistenciaTipo(linha, 1, linha.size() - 2);
        } else {
            System.err.println("Condicao do '" + bloco + "' deve ser booleana");
        }

    }

    private void analisaAtribuicao(ArrayList<Token> linha) {
        String var = linha.get(0).getValor();
        String nomeVar;
        boolean isVetor = false;
        if (var.contains("[")) {
            nomeVar = var.substring(0, var.indexOf('[')).trim();
            isVetor = true;
        } else {
            nomeVar = var;
        }

        escopos.adicionaVariavel(nomeVar);
        String s = escopos.getVariavel(nomeVar);
        addTabelaSimbolo(new Simbolo(nomeVar, tipoExpressao(linha, 2, linha.size() - 1),
                escopos.getVariavel(nomeVar), nLinha, false, isVetor));
        consistenciaTipo(linha, 2, linha.size() - 1);
    }

    private void analisaParametros(ArrayList<Token> linhaChamada, int start) {
        int nivel = 0, fim = start + 2;
        for (int i = start + 1; i < linhaChamada.size(); i++) {
            Token t = linhaChamada.get(i);
            if (null != t.getTipo()) {
                switch (t.getTipo()) {
                    case "(":
                        nivel++;
                        break;
                    case ")":
                        nivel--;
                        break;
                }
            }
            if (nivel == 0) {
                fim = i;
                break;
            }
        }
        ArrayList<String> params = getSimbolo(
                linhaChamada.get(start).getValor(), 0).parametros;
        int cont = 0;

        for (int i = start + 2; i < fim; i++) {
            Token t = linhaChamada.get(i);
            int limite = indexOf(linhaChamada, new Token(",", ""), start, linhaChamada.size() - 1) - 1;
            if (limite == -2) {
                limite = fim;
            }
            try {
                if (!tipoExpressao(linhaChamada, i, limite).equals(params.get(cont))) {
                    System.err.println("Tipo invalido de parametro para '"
                            + linhaChamada.get(start).getValor() + "' (Parametro " + cont + " e "
                            + tipoExpressao(linhaChamada, i, limite) + " e era esperado"
                            + params.get(cont) + ").");
                    System.exit(0);
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Quantidade invalida de parametros para '"
                        + linhaChamada.get(start).getValor() + "' (" + params.size() + " parametros).");
            }
            i = limite + 1;
            cont++;
        }
        if (cont != params.size()) {
            System.err.println("Quantidade invalida de parametros para '"
                    + linhaChamada.get(start).getValor() + "' (" + params.size() + " parametros).");
        }
    }

    public void analisaLinha(ArrayList<Token> linha) {
        if ("if".equals(linha.get(0).getTipo())) {
            escopos.adicionaEscopo();
            analisaCondicao(linha, "se");
        } else if ("while".equals(linha.get(0).getTipo())) {
            escopos.adicionaEscopo();
            analisaCondicao(linha, "enquanto");
        } else if ("for".equals(linha.get(0).getTipo())) {
            escopos.adicionaEscopo();
            escopos.adicionaVariavel(linha.get(1).getValor());
            addTabelaSimbolo(new Simbolo(linha.get(1).getValor(), "int",
                    escopos.getVariavel(linha.get(1).getValor()), nLinha, false, false));
        } else if (indexOf(linha, new Token("=", ""), 0, linha.size() - 1) != -1) {
            analisaAtribuicao(linha);
        } else if ("else".equals(linha.get(0).getTipo())) {
            escopos.removeEscopo();
            escopos.adicionaEscopo();
        } else if ("endif".equals(linha.get(0).getTipo())) {
            escopos.removeEscopo();
        } else if ("endwhile".equals(linha.get(0).getTipo())) {
            escopos.removeEscopo();
        } else if ("endfor".equals(linha.get(0).getTipo())) {
            escopos.removeEscopo();
        }
    }

    public void startAnalysis() {

        boolean isFuncao = false;
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();
            if (contains(linha, new Token("def", ""), 0, linha.size() - 1)) {
                isFuncao = true;
            } else if (contains(linha, new Token("enddef", ""), 0, linha.size() - 1)) {
                isFuncao = false;
            }

            int indexFun = indexOfTipo(linha, "fun", 0, linha.size() - 1);
            while (indexFun != -1) {

                escopos.getVariavel(linha.get(indexFun).getValor());
                analisaParametros(linha, indexFun);

                indexFun = indexOfTipo(linha, "fun", indexFun + 1, linha.size() - 1);
            }

            analisaLinha(linha);

        }
    }

}
