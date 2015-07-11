package Compiler;

import Compiler.utils.Token;
import Compiler.utils.Arvore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class AnalisadorSintatico {

    private LinkedHashMap<Integer, Arvore<Token>> arvores;
    private AnalisadorLexico aL;

    public AnalisadorSintatico(AnalisadorLexico an) {
        this.aL = an;
    }

    public LinkedHashMap<Integer, Arvore<Token>> getArvores() {
        return arvores;
    }

    private int indexOf(ArrayList array, Object x) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOf(ArrayList array, Object x, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array.get(i).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int rIndexOf(ArrayList array, Object x) {
        for (int i = array.size() - 1; i >= 0; i--) {
            if (array.get(i).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int rIndexOf(ArrayList array, Object x, int start, int end) {
        for (int i = end; i >= start; i--) {
            if (array.get(i).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int rIndexOfParen(ArrayList array, Object x, int start, int end) {
        Stack<Integer> pilha = new Stack<>();
        for (int i = end; i >= start; i--) {
            if (array.get(i).equals(new Token(")", ")"))) {
                pilha.push(1);
            } else if (array.get(i).equals(new Token("(", "("))) {
                if (pilha.empty()) {
                    System.err.println("'(' sem ')' equivalente.");
                }
                pilha.pop();
            }
            if (array.get(i).equals(x)
                    && pilha.empty()) {
                return i;
            }
        }
        if (!pilha.empty()) {
            System.err.println("')' sem '(' equivalente.");
        }
        return -1;
    }

    private void retiraQuebra() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        Integer[] keys = new Integer[linhas.size()];
        keys = linhas.keySet().toArray(keys);
        ArrayList<Token> aux = new ArrayList<>();
        int nLinha = 0;

        for (int i = 0; i < keys.length; i++) {
            ArrayList<Token> linha = linhas.get(keys[i]);
            if (!linha.get(linha.size() - 1).equals(new Token(".endinstr", ".endinstr"))) {
                if (aux.isEmpty()) {
                    nLinha = i;
                }
                for (Token t : linha) {
                    aux.add(t);
                }
            } else if (!aux.isEmpty()) {
                for (Token t : linha) {
                    aux.add(t);
                }

                linhas.replace(keys[nLinha], aux);

                for (int j = nLinha + 1; j <= i; j++) {
                    linhas.remove(keys[j]);
                }
                aux = new ArrayList<>();
            }
        }
        keys = linhas.keySet().toArray(keys);
        for (int i = 0; i < keys.length; i++) {
            aux = linhas.get(keys[i]);
            aux.remove(aux.size() - 1);
            linhas.replace(keys[i], aux);
        }
    }

    private boolean verificaFim() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        Integer[] keys = new Integer[linhas.size()];
        keys = linhas.keySet().toArray(keys);

        for (int i = 0; i < keys.length; i++) {
            if (linhas.get(keys[i]).contains(new Token("fim", "fim"))) {
                if (linhas.get(keys[i]).size() > 2) {
                    System.err.println("Fim deve estar em uma linha separada. Linha: " + keys[i]);
                    System.exit(0);
                }
                if (i < (keys.length - 1)) {
                    System.err.println("Instrucoes apos o "
                            + "termino do programa. Linha: " + keys[i]);
                    System.exit(0);
                }
                return false;
            }
        }
        System.err.println("Fim do programa nao encontrado.");
        return true;
    }

    private boolean estruturacao() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();
        Integer[] keys = new Integer[linhas.size()];
        Stack<String> pilha = new Stack<>();
        Stack<Integer> lStk = new Stack<>();

        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            Integer nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();

            int indiceSe = indexOf(linha, new Token("if", ""));
            if (indiceSe != -1) {
                verificaSe(nLinha, linha);
                pilha.push("se");
                lStk.push(nLinha);
            }
            int indiceSenao = indexOf(linha, new Token("else", ""));
            if (indiceSenao != -1) {
                if (!"se".equals(pilha.peek())) {
                    System.err.println("'senao' sem 'se "
                            + "correspondente.Linha: " + nLinha);
                    System.exit(0);
                } else {
                    pilha.pop();
                    lStk.pop();
                    pilha.push("senao");
                    lStk.push(nLinha);
                }
                if (linha.size() != 1) {
                    System.err.println("Palavra-chave 'senao' "
                            + "deve estar sozinha na linha.Linha: " + nLinha);
                    System.exit(0);
                }
            }
            int indiceFimSe = indexOf(linha, new Token("endif", ""));
            if (indiceFimSe != -1) {
                if (pilha.empty()) {
                    System.err.println("Todos os blocos "
                            + "condicionais ja foram encerrados.Linha: " + nLinha);
                    System.exit(0);
                } else if (!"se".equals(pilha.peek())
                        && !"senao".equals(pilha.peek())) {
                    System.err.println("'fim-se' sem 'se' "
                            + "ou 'senao' correspondente.Linha: " + nLinha);
                    System.exit(0);
                } else {
                    pilha.pop();
                    lStk.pop();
                }
                if (linha.size() != 1) {
                    System.err.println("'fim-se' "
                            + "deve estar em linha a parte.Linha: " + nLinha);
                    System.exit(0);
                }
            }
            int indiceEnquanto = indexOf(linha, new Token("while", ""));
            if (indiceEnquanto != -1) {
                verificaEnquanto(nLinha, linha);
                pilha.push("enquanto");
                lStk.push(nLinha);
            }
            int indiceFimEnquanto = indexOf(linha, new Token("endwhile", ""));
            if (indiceFimEnquanto != -1) {
                if (pilha.empty()) {
                    System.err.println("Todos os blocos "
                            + "'enquanto' ja foram encerrados.Linha: " + nLinha);
                    System.exit(0);
                } else if (!"enquanto".equals(pilha.peek())) {
                    System.err.println("'fim-enquanto' sem "
                            + "'enquanto'.Linha: " + nLinha);
                    System.exit(0);
                } else {
                    pilha.pop();
                    lStk.pop();
                }
                if (linha.size() != 1) {
                    System.err.println("'fim-enquanto' "
                            + "deve estar sozinha na linha." + nLinha);
                    System.exit(0);
                }
            }
            int indicePara = indexOf(linha, new Token("for", ""));
            if (indicePara != -1) {
                verificaPara(nLinha, linha);
                pilha.push("para");
                lStk.push(nLinha);
            }
            int indiceFimPara = indexOf(linha, new Token("endfor", ""));
            if (indiceFimPara != -1) {
                if (pilha.empty()) {
                    System.err.println("Todos os blocos 'para' ja foram encerrados.Linha: " + nLinha);
                    System.exit(0);
                } else if (!"para".equals(pilha.peek())) {
                    System.err.println("'fim-para' sem "
                            + "'para'.Linha: " + nLinha);
                    System.exit(0);
                } else {
                    pilha.pop();
                    lStk.pop();
                }
                if (linha.size() != 1) {
                    System.err.println("'fim-para' "
                            + "deve estar sozinha na linha.Linha: " + nLinha);
                    System.exit(0);
                }
            }
            int indiceDefinicao = indexOf(linha, new Token("def", ""));
            //Se for um def
            if (indiceDefinicao != -1) {
                verificaDefinicao(nLinha, linha);
                pilha.push("funcao");
                lStk.push(nLinha);
            }
            int indiceFimDef = indexOf(linha, new Token("enddef", ""));
            //Se for um enddef
            if (indiceFimDef != -1) {
                if (pilha.empty()) {
                    System.err.println("Todos as definicoes "
                            + "de funcoes ja foram encerrados.Linha: " + nLinha);
                    System.exit(0);
                } else if (!"funcao".equals(pilha.peek())) {
                    System.err.println("'fim-funcao' sem "
                            + "'funcao' correspondente.Linha: " + nLinha);
                    System.exit(0);
                } else {
                    pilha.pop();
                    lStk.pop();
                }
                if (linha.size() != 1) {
                    System.err.println("'fim-funcao' "
                            + "deve estar sozinha na linha.Linha: " + nLinha);
                    System.exit(0);
                }
            }
        }

        //Se houver bloco nao encerrado
        while (!pilha.empty()) {
            String e = pilha.pop();
            int nl = lStk.pop();
            System.err.println("Bloco '" + e
                    + "' nao encerrado.Linha: " + nl);

            System.exit(0);
        }
        return false;
    }

    private boolean atribuicao() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();

        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            Integer nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();
            int indiceAtribuicao = indexOf(linha, new Token("=", ""));

            if (indiceAtribuicao != -1) {
                if (!("id".equals(linha.get(0).getTipo())
                        && indiceAtribuicao == 1)
                        && !("id".equals(linha.get(0).getTipo())
                        && "[".equals(linha.get(1).getTipo())
                        && "]".equals(linha.get(indiceAtribuicao - 1).getTipo()))) {
                    System.err.println("Atribuicoes deve ser a uma "
                            + "variavel.Linha: " + nLinha);

                    System.exit(0);
                }
                Arvore<Token> arvore = new Arvore<>(new Token("=", ""));
                Arvore<Token> termo = termo(linha, 0, indiceAtribuicao - 1);
                Arvore<Token> condicao = condicao(linha, indiceAtribuicao + 1, linha.size() - 1);

                if (termo != null) {
                    arvore.setEsquerda(termo);
                } else {
                    System.err.println("Erro no termo.Linha: " + nLinha);

                    System.exit(0);
                }
                if (condicao != null) {
                    arvore.setDireita(condicao);
                } else {
                    System.err.println("Erro na condicao.Linha: " + nLinha);

                    System.exit(0);
                }

                arvores.put(nLinha, arvore);

            }
        }
        return false;
    }

    private boolean verificaVetor() {
        Map<Integer, ArrayList<Token>> linhas = aL.getTokens();

        for (Map.Entry<Integer, ArrayList<Token>> entrySet : linhas.entrySet()) {
            Integer nLinha = entrySet.getKey();
            ArrayList<Token> linha = entrySet.getValue();

            int indiceVetor = indexOf(linha, new Token("vet", ""));

            if (indiceVetor != -1) {

                if (indiceVetor > 0) {
                    System.err.println("Token antes da palavra chave "
                            + "'vetor'.Linha: " + nLinha);

                    System.exit(0);
                }
                if (indiceVetor == linha.size() - 1) {
                    System.err.println("Vetor a ser declarado nao "
                            + "especificado.Linha: " + nLinha);

                    System.exit(0);
                } else {
                    if (!"id".equals(linha.get(indiceVetor + 1).getTipo())) {
                        System.err.println("Vetor deve possuir um nome de "
                                + "variavel valido.Linha: " + nLinha);

                        System.exit(0);
                    }
                    if (indiceVetor + 4 > linha.size() - 1) {
                        System.err.println("Tamanho do vetor nao "
                                + "especificado corretamente.Linha: " + nLinha);

                        System.exit(0);
                    }
                    if (indiceVetor + 4 == linha.size() - 1) {
                        if (!"[".equals(linha.get(indiceVetor + 2).getTipo())
                                || !"]".equals(linha.get(indiceVetor + 4).getTipo())) {
                            System.err.println("Tamanho do vetor nao "
                                    + "especificado corretamente.Linha: " + nLinha);

                            System.exit(0);
                        }
                        if (!"int".equals(linha.get(indiceVetor + 3).getTipo())) {
                            System.err.println("Tamanho do vetor deve "
                                    + "ser inteiro.Linha: " + nLinha);

                            System.exit(0);
                        }
                    }
                    if (indiceVetor + 8 == linha.size()) {
                        if (!"[".equals(linha.get(indiceVetor + 2).getTipo())
                                || !"]".equals(linha.get(indiceVetor + 4).getTipo())
                                || !"[".equals(linha.get(indiceVetor + 5).getTipo())
                                || !"]".equals(linha.get(indiceVetor + 7).getTipo())) {
                            System.err.println("Tamanho do vetor nao "
                                    + "especificado corretamente.Linha: " + nLinha);

                            System.exit(0);
                        }

                        if (!"int".equals(linha.get(indiceVetor + 3).getTipo())
                                || !"int".equals(linha.get(indiceVetor + 6).getTipo())) {
                            System.err.println("Tamanho do vetor deve "
                                    + "ser inteiro.Linha: " + nLinha);

                            System.exit(0);
                        }
                    } else if (indiceVetor + 8 < linha.size()) {
                        int indexColch = rIndexOf(linha, new Token("]", ""));
                        if (indexColch <= 8) {
                            System.err.println("Tamanho do vetor nao "
                                    + "especificado corretamente.Linha: " + nLinha);
                        } else {
                            System.err.println("Nao deve haver tokens "
                                    + "apos a declaracao de um vetor.Linha: " + nLinha);
                        }

                        System.exit(0);
                    }
                }
            }
        }
        return false;
    }

    private boolean verificaSe(int nLinha, ArrayList<Token> linha) {

        int indiceSe = indexOf(linha, new Token("if", ""));

        if (indiceSe > 0) {
            System.err.println("Token antes da "
                    + "palavra-chave 'se'.Linha: " + nLinha);
            System.exit(0);
        }
        int indexThen = indexOf(linha, new Token("then", ""));
        if (indexThen == -1) {
            System.err.println("Ausencia de "
                    + "'entao' apos palavra-chave 'se'.Linha: " + nLinha);

            System.exit(0);
        }
        if (indexThen + 1 < linha.size()) {
            System.err.println("Token apos a "
                    + "palavra-chave 'entao'.Linha: " + nLinha);

            System.exit(0);
        }
        if (condicao(linha, indiceSe + 1, indexThen - 1) == null) {
            System.err.println("Erro na condicao.Linha: " + nLinha);

            System.exit(0);
        }

        return false;
    }

    private boolean verificaEnquanto(int nLinha, ArrayList<Token> linha) {

        int indiceEnquanto = indexOf(linha, new Token("while", ""));

        if (indiceEnquanto > 0) {
            System.err.println("Token antes da "
                    + "palavra-chave 'enquanto'.Linha: " + nLinha);
            System.exit(0);
        }

        int indexDo = indexOf(linha, new Token("do", ""));
        if (indexDo == -1) {
            System.err.println("Ausencia de "
                    + "'entao' apos palavra-chave 'faca'.Linha: " + nLinha);
            System.exit(0);
        } else {
            if (indexDo + 1 < linha.size()) {
                System.err.println("Token apos a "
                        + "palavra-chave 'faca'.Linha: " + nLinha);
                System.exit(0);
            }
            if (condicao(linha, indiceEnquanto + 1, indexDo - 1) == null) {
                System.err.println("Erro na condicao.Linha: " + nLinha);
                System.exit(0);
            }
        }
        return false;
    }

    private boolean verificaPara(int nLinha, ArrayList<Token> linha) {

        int indicePara = indexOf(linha, new Token("for", ""));
        int indiceDe = indexOf(linha, new Token("from", ""));
        int indiceAte = indexOf(linha, new Token("to", ""));
        int indiceFaca = indexOf(linha, new Token("do", ""));

        if (indicePara > 0) {
            System.err.println("Token antes da "
                    + "palavra-chave 'para'.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceDe == -1) {
            System.err.println("Ausencia de "
                    + "'de' apos palavra-chave 'para'.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceAte == -1) {
            System.err.println("Ausencia de "
                    + "'ate' apos palavra-chave 'de'.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceFaca == -1) {
            System.err.println("Ausencia de "
                    + "'faca' apos palavra-chave 'ate'.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceFaca + 1 < linha.size()) {
            System.err.println("Token apos a "
                    + "palavra-chave 'faca'.Linha: " + nLinha);
            System.exit(0);
        }
        if (indicePara + 1 < linha.size()) {
            if (!"id".equals(linha.get(indicePara + 1).getTipo())) {
                System.err.println("Identificador a ser "
                        + "iterado deve ser uma variavel.Linha: " + nLinha);
                System.exit(0);
            }
            if (!"int".equals(linha.get(indiceDe + 1).getTipo())
                    && !"float".equals(linha.get(indiceDe + 1).getTipo())) {
                System.err.println("Valor inicial deve ser "
                        + "numerico.Linha: " + nLinha);
                System.exit(0);
            }
            if (!"int".equals(linha.get(indiceAte + 1).getTipo())
                    && !"float".equals(linha.get(indiceAte + 1).getTipo())) {
                System.err.println("Valor final deve ser "
                        + "numerico.Linha: " + nLinha);
                System.exit(0);
            }
        }
        if (indiceDe != -1
                && indiceDe - indicePara > 2) {
            System.err.println("Quantidade excessiva "
                    + "de identificadores a serem iterados.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceAte != -1
                && indiceAte - indiceDe > 2) {
            System.err.println("Quantidade excessiva "
                    + "de valores iniciais.Linha: " + nLinha);
            System.exit(0);
        }
        if (indiceFaca != -1
                && indiceFaca - indiceAte > 2) {
            System.err.println("Quantidade excessiva "
                    + "de valores finais.Linha: " + nLinha);
            System.exit(0);
        }

        return false;
    }

    private boolean verificaDefinicao(int nLinha, ArrayList<Token> linha) {

        int indiceDef = indexOf(linha, new Token("def", ""));

        if (indiceDef > 0) {
            System.err.println("Token antes da "
                    + "palavra-chave 'funcao'.Linha: " + nLinha);
            System.exit(0);
        }
        if (funcao(linha, indiceDef + 1, linha.size() - 1) == null) {
            System.err.println("Erro na declaracao de funcao.Linha: " + nLinha);
            System.exit(0);
        }

        return false;
    }

    private Arvore<Token> condicao(ArrayList<Token> linha, int start, int end) {

        Integer[] indexComparativos = new Integer[8];
        indexComparativos[0] = rIndexOfParen(linha, new Token("and", "e"), start, end);
        indexComparativos[1] = rIndexOfParen(linha, new Token("or", "ou"), start, end);
        indexComparativos[2] = rIndexOfParen(linha, new Token("==", "="), start, end);
        indexComparativos[3] = rIndexOfParen(linha, new Token("!=", "!="), start, end);
        indexComparativos[4] = rIndexOfParen(linha, new Token("<", "<"), start, end);
        indexComparativos[5] = rIndexOfParen(linha, new Token("<=", "<="), start, end);
        indexComparativos[6] = rIndexOfParen(linha, new Token(">", ">"), start, end);
        indexComparativos[7] = rIndexOfParen(linha, new Token(">=", ">="), start, end);

        int maior = -1;
        for (int j = 0; j < 8; j++) {
            if (indexComparativos[j] > maior) {
                maior = indexComparativos[j];
            }
        }
        if (maior == -1) {
            return expressao(linha, start, end);
        }

        Arvore<Token> arvore = new Arvore<>(linha.get(maior));
        arvore.setDireita(condicao(linha, start, maior - 1));
        arvore.setEsquerda(expressao(linha, maior + 1, end));

        return arvore;
    }

    private Arvore<Token> expressao(ArrayList<Token> linha, int start, int end) {

        Integer[] indiceOperadores = new Integer[8];
        indiceOperadores[0] = rIndexOfParen(linha, new Token("+", ""), start, end);
        indiceOperadores[1] = rIndexOfParen(linha, new Token("-", ""), start, end);

        int maior = -1;
        for (int j = 0; j < 2; j++) {
            if (indiceOperadores[j] > maior) {
                maior = indiceOperadores[j];
            }
        }

        if (maior == -1) {
            return expressaoPrecedente(linha, start, end);
        }

        Arvore<Token> arvore = new Arvore<>(linha.get(maior));
        arvore.setEsquerda(expressao(linha, start, maior - 1));
        arvore.setDireita(expressaoPrecedente(linha, maior + 1, end));

        return arvore;
    }

    private Arvore<Token> expressaoPrecedente(ArrayList<Token> linha, int start, int end) {

        Integer[] indiceOperadores = new Integer[8];
        indiceOperadores[0] = rIndexOfParen(linha, new Token("*", ""), start, end);
        indiceOperadores[1] = rIndexOfParen(linha, new Token("/", ""), start, end);

        int maior = -1;
        for (int j = 0; j < 2; j++) {
            if (indiceOperadores[j] > maior) {
                maior = indiceOperadores[j];
            }
        }

        if (maior == -1) {
            return termo(linha, start, end);
        }
        Arvore<Token> arvore = new Arvore<>(linha.get(maior));
        arvore.setDireita(termo(linha, maior + 1, end));
        arvore.setEsquerda(expressaoPrecedente(linha, start, maior - 1));

        return arvore;
    }

    private Arvore<Token> termo(ArrayList<Token> linha, int start, int end) {
        if ("(".equals(linha.get(start).getTipo())
                && ")".equals(linha.get(end).getTipo())) {
            return condicao(linha, start + 1, end - 1);
        } else if (start == end
                && ("id".equals(linha.get(start).getTipo())
                || "float".equals(linha.get(start).getTipo())
                || "int".equals(linha.get(start).getTipo())
                || "true".equals(linha.get(start).getTipo())
                || "false".equals(linha.get(start).getTipo())
                || "str".equals(linha.get(start).getTipo()))) {
            return new Arvore<>(linha.get(start));
        } else if ("fun".equals(linha.get(start).getTipo())
                && "(".equals(linha.get(start + 1).getTipo())
                && ")".equals(linha.get(end).getTipo())) {
            return funcao(linha, start, end);
        } else if ("id".equals(linha.get(start).getTipo())
                && "[".equals(linha.get(start + 1).getTipo())
                && "]".equals(linha.get(end).getTipo())) {
            int indexFim1 = indexOf(linha, new Token("]", ""), start + 2, end);
            int indexComeco2 = rIndexOf(linha, new Token("[", ""), start + 1, end - 1);

            Arvore<Token> arvore = new Arvore<>(linha.get(start));
            if (indexFim1 != -1 && indexComeco2 != -1) {
                condicao(linha, start + 2, indexFim1 - 1);
                condicao(linha, indexComeco2 + 1, end - 1);
            } else {
                condicao(linha, start + 2, end - 1);
            }

            String valor = "";

            for (int i = start; i <= end; i++) {
                valor += "".equals(linha.get(i).getValor())
                        ? linha.get(i).getTipo()
                        : linha.get(i).getValor();
            }

            return new Arvore<>(new Token("id", valor));
        } else {
            throw null;
        }
    }

    private Arvore<Token> funcao(ArrayList<Token> linha, int start, int end) {
        if (end - start > 2
                && (!"fun".equals(linha.get(start).getTipo())
                || !"(".equals(linha.get(start + 1).getTipo())
                || !")".equals(linha.get(end).getTipo()))) {
            return null;
        }

        int i = start + 2;
        int virgula;
        while (i != end) {
            virgula = indexOf(linha, new Token(",", ""), i, end - 1);
            if (virgula != -1) {
                condicao(linha, i, virgula - 1);
                i = virgula + 1;
            } else {
                condicao(linha, i, end - 1);
                i = end;
            }
        }

        String valor = "";
        for (int j = start; j <= end; j++) {
            valor += "".equals(linha.get(j).getValor())
                    ? linha.get(j).getTipo()
                    : linha.get(j).getValor();
        }

        return new Arvore<>(new Token("fun", valor));
    }

    public boolean startAnalysis() {
        retiraQuebra();
        verificaFim();
        estruturacao();
        atribuicao();
        verificaVetor();

        System.out.println("\n\nAnalise Sintatica:");
        for (Map.Entry<Integer, Arvore<Token>> entry : arvores.entrySet()) {
            System.out.print(entry.getKey());
            entry.getValue().print();
            System.out.println("\n");
        }
        System.out.println("\n\n");
        return false;
    }
}
