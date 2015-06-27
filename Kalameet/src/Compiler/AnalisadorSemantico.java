package Compiler;

import Compiler.utils.Escopo;
import java.util.HashMap;
import Compiler.utils.Simbolo;
import Compiler.utils.Token;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalisadorSemantico {

    //----------------------------------------------------------------------------------------
    private HashMap<String, Simbolo> tabelaSimbolos;
    //chave para o hash: (simbolo.nome + simbolo.escopo)

    public AnalisadorSemantico() {
        tabelaSimbolos = new HashMap<>();
    }

    public void addTabelaSimbolo(Simbolo s) {
        //Se ja existe
        if (tabelaSimbolos.containsKey(s.hash)) {

            //Se tipo incompativel
            if (!tabelaSimbolos.get(s.hash).tipo.equals(s.tipo)) {
                System.err.println("Variavel '" + s.nome + "' e do tipo '"
                        + tabelaSimbolos.get(s.hash).tipo + "' e foi atribuida valor do tipo '"
                        + s.tipo + "'.Linha: " + s.ultimoUso);
                System.exit(0);
            }

            //Se foi utilizado mais a frente
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
        //Se foi utilizado mais a frente
        if (linha > tabelaSimbolos.get(hash).ultimoUso) {
            tabelaSimbolos.get(hash).ultimoUso = linha;
        }

        return tabelaSimbolos.get(hash);
    }

    public void printTabelaSimbolos() {
        for (String key : tabelaSimbolos.keySet()) {
            System.out.println(key + " = {\n" + tabelaSimbolos.get(key).toString() + "\n}");
        }
    }
    //----------------------------------------------------------------------------------------
     /* VARIAVEIS */
    private LinkedHashMap<Integer, ArrayList<Token>> linhas;
    private Escopo escopos;

    /* VARIAVEIS AUXILIARES */
    private int nLinha;
    private ArrayList<String> funcoesDeclaradas;

    /* CONSTRUTORES */
    public AnalisadorSemantico(LinkedHashMap<Integer, ArrayList<Token>> tokens) {
        linhas = tokens;
        escopos = new Escopo();
        tabelaSimbolos = new HashMap<>();

        funcoesDeclaradas = new ArrayList<>();
    }

    public Map<String, Simbolo> getTabelaSimbolos() {
        return tabelaSimbolos;
    }

    /* FUNCOES AUXILIARES */
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

    //Procura o elemento mais a direita fora de parenteses
    //  usada devido ao fato de a gramatica derivar a esquerda
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

    //Verifica se contem determinado token
    private boolean contains(ArrayList<Token> linha, Token t, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (t.equals(linha.get(i))) {
                return true;
            }
        }
        return false;
    }

    //Verifica se tem determinado tipo de variavel
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

    /* FUNCOES DE ANALISE SEMANTICA */
    //Retorna o tipo de um expressao/condicao
    private String tipoExpressao(ArrayList<Token> linha, int start, int end) {

        //Se nao existe expressao
        if (start > end) {
            return "null";
        }

        //Checa se e expressao entre parenteses
        //Encontra o operador mais a direita fora de parenteses
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
        //Se nao tiver nenhum operador fora de parenteses e tiver parenteses
        if (maior == -1
                && linha.get(start) == new Token("(", "")
                && linha.get(end) == new Token(")", "")) {
            return tipoExpressao(linha, start + 1, end - 1);
        } //Se e boleano
        else if (contains(linha, new Token("==", ""), start, end)
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
        } //Se e string
        else if (contains(linha, "str", start, end)) {
            return "str";
        } //Se e float
        else if (contains(linha, "float", start, end)
                || contains(linha, new Token("/", ""), start, end)) {
            return "float";
        } //Se e int
        else {
            return "int";
        }

    }

    

}
