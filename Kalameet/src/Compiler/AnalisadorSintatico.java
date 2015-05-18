/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import Compiler.utils.Lexemas;
import Compiler.utils.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author JM
 */
public class AnalisadorSintatico {

    private AnalisadorLexico aL;
    private HashMap<String,String> lexemas;

    public AnalisadorSintatico(AnalisadorLexico an) {
        this.aL = an;
    }

    public void verificaParametro(ArrayList<Token> token) {

    }

    public void verificaCondicao(ArrayList<Token> token) {

    }

    public void verificaComandos(ArrayList<Token> tokens) {
        ArrayList<Token> tokenCondicao = new ArrayList<Token>();
        ArrayList<Token> tokenComandos = new ArrayList<Token>();
        ArrayList<Token> tokenEnquanto = new ArrayList<Token>();
        Stack<Token> pilha = new Stack<>();

        for (Iterator<Token> it = tokens.iterator(); it.hasNext();) {
            Token token = it.next();
            if (token != null) {
                if (token.getTipo().equals("cond")) {
                    //le ate achar um entao
                    pilha.clear();
                    pilha.push(token);
                    token = it.next();
                    while (it.hasNext() && (!token.getTipo().equals("initcond"))) {
                        if (!token.getTipo().equals("|n")) {
                            tokenCondicao.add(token);
                        }
                        token = it.next();
                    }
                    verificaCondicao(tokenCondicao);
                    tokenCondicao.clear();
                    token = it.next();
                    //le ate axar o fim-se
                    while (it.hasNext()) {
                        if (!token.getTipo().equals("|n")) {
                            if (token.getTipo().equals("cond")) {
                                pilha.push(token);
                            } else if (token.getTipo().equals("endcond")) {
                                pilha.pop();
                                if (pilha.isEmpty()) {
                                    break;
                                }
                            }
                            tokenComandos.add(token);
                        }
                        token = it.next();
                    }
                    if (!pilha.isEmpty()) {
                        System.out.println("Erro");
                    }
                    verificaComandos(tokenComandos);
                    tokenComandos.clear();
                } else if (token.getTipo().equals("whileloop")) { //le ate achar um enquanto
                    pilha.clear();
                    pilha.push(token);
                    token = it.next();
                    //le ate axar um faça
                    while (it.hasNext() && (!token.getTipo().equals("initforloop"))) {
                        if (!token.getTipo().equals("|n")) {
                            tokenEnquanto.add(token);
                        }
                        token = it.next();
                    }
                    verificaCondicao(tokenEnquanto);
                    tokenEnquanto.clear();
                    token = it.next();
                    //le ate axar o fim-enquanto
                    while (it.hasNext()) {
                        if (!token.getTipo().equals("|n")) {
                            if (token.getTipo().equals("whileloop")) {
                                pilha.push(token);
                            } else if (token.getTipo().equals("endwhileloop")) {
                                pilha.pop();
                                if (pilha.isEmpty()) {
                                    break;
                                }
                            }
                            tokenComandos.add(token);
                        }
                        token = it.next();
                    }
                    if (!pilha.isEmpty()) {
                        System.out.println("Erro");
                    }
                    verificaComandos(tokenComandos);
                    tokenComandos.clear();
                } //le ate achar um para
                else if (token.getTipo().equals("forloop")) {
                    pilha.clear();
                    pilha.push(token);
                    token = it.next();
                    //le se proximo elemento é um id
                    if (token == null || !token.getTipo().equals("id")) {
                        System.out.println("Erro");
                    } else {
                        token = it.next();
                        //le se proximo elemento é um "de"
                        if (token == null || !token.getTipo().equals("rng1forloop")) {
                            System.out.println("Erro");
                        } else {
                            token = it.next();
                            //le se proximo elemento é um inteiro
                            if (token == null || !token.getTipo().equals("Int")) {
                                System.out.println("Erro");
                            } else {
                                token = it.next();
                                //le se proximo elemento é "até"
                                if (token == null || !token.getTipo().equals("rng2forloop")) {
                                    System.out.println("Erro");
                                } else {
                                    token = it.next();
                                    //le se o próximo é um inteiro
                                    if (token == null || !token.getTipo().equals("Int")) {
                                        System.out.println("Erro");
                                    } else {
                                        token = it.next();
                                        //le se o próximo é um faça
                                        if (token == null || !token.getTipo().equals("initforloop")) {
                                            System.out.println("Erro");
                                        } else {
                                            token = it.next();
                                            //le ate axar um fim-para
                                            while (it.hasNext()) {
                                                if (!token.getTipo().equals("|n")) {
                                                    if (token.getTipo().equals("forloop")) {
                                                        pilha.push(token);
                                                    } else if (token.getTipo().equals("endforloop")) {
                                                        pilha.pop();
                                                        if (pilha.isEmpty()) {
                                                            break;
                                                        }
                                                    }
                                                    tokenEnquanto.add(token);
                                                }
                                                token = it.next();
                                            }
                                            if (!pilha.isEmpty()) {
                                                System.out.println("Erro");
                                            }
                                            verificaComandos(tokenEnquanto);
                                            tokenEnquanto.clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (token.getTipo().equals("id")) { //se ler um id
                    token = it.next();
                    //le se proximo elemento é um =
                    if (token == null || !token.getTipo().equals("atrib")) {
                        System.out.println("Erro");
                    } else {
                        token = it.next();
                        //le ate axar um \n
                        while (it.hasNext() && (!token.getTipo().equals("|n"))) {
                            tokenEnquanto.add(token);
                            token = it.next();
                        }
                        verificaCondicao(tokenEnquanto);
                        tokenEnquanto.clear();
                    }
                } //Analisa se é uma função
                else if (token.getTipo().equals("function")) {
                    pilha.clear();
                    token = it.next();
                    //le se o proximo é um fun
                    if (token == null || !token.getTipo().equals("fun")) {
                        System.out.println("Erro");
                    } else {
                        token = it.next();
                        if (token == null || !token.getTipo().equals("(")) {
                            System.out.println("Erro");
                        } else {
                            pilha.push(token);
                            token = it.next();
                            while (!token.getTipo().equals("|n")) {
                                if (token.getTipo().equals("(")) {
                                    pilha.push(token);
                                } else if (token.getTipo().equals(")")) {
                                    pilha.pop();
                                    if (pilha.isEmpty()) {
                                        break;
                                    }
                                }
                                tokenEnquanto.add(token);
                                token = it.next();
                            }
                            if (!pilha.isEmpty()) {
                                System.out.println("Erro");
                            }
                            pilha.clear();
                            if (it.hasNext()) {
                                pilha.push(token);
                            }
                            token = it.next();
                            verificaParametro(tokenEnquanto);
                            tokenEnquanto.clear();

                            while (it.hasNext()) {
                                if (!token.getTipo().equals("|n")) {
                                    if (token.getTipo().equals("function")) {
                                        pilha.push(token);
                                    } else if (token.getTipo().equals("endfunction")) {
                                        pilha.pop();
                                        if (pilha.isEmpty()) {
                                            break;
                                        }
                                    }
                                    tokenEnquanto.add(token);
                                }
                                token = it.next();
                            }
                            if (!pilha.isEmpty()) {
                                System.out.println("Erro");
                            }
                            verificaComandos(tokenEnquanto);
                            tokenEnquanto.clear();
                        }
                    }

                } else if (token.getTipo().equals("vet")) { //Analisa se eh declaração de vetor
                    token = it.next();
                    if (it.hasNext() && token.getTipo().equals("id")) {
                        token = it.next();
                        //analisa se é uma declaração de vetor
                        if (it.hasNext() && token.getTipo().equals("[")) {
                            token = it.next();
                            while (it.hasNext() && !("|n").equals(token.getTipo()) && !("]").equals(token.getTipo())) {
                                tokenEnquanto.add(token);
                                token = it.next();
                            }
                            if (token == null || token.getTipo().equals("|n")) {
                                System.out.println("Erro: token esperado ']'");
                            }
                            verificaCondicao(tokenEnquanto);
                            tokenEnquanto.clear();

                            token = it.next();
                            //analisa se é declaração de matriz
                            if (it.hasNext() && token.getTipo().equals("[")) {
                                while (it.hasNext() && !token.getTipo().equals("|n") && !token.getTipo().equals("]")) {
                                    tokenEnquanto.add(token);
                                    token = it.next();
                                }
                                if (token == null || token.getTipo().equals("|n")) {
                                    System.out.println("Erro: token esperado ']'");
                                }
                                verificaCondicao(tokenEnquanto);
                                tokenEnquanto.clear();

                            }
                        } else {
                            System.out.println("Erro: token [ esperado");
                        }
                    } else {
                        System.out.println("Erro: declaração de vetor sem id");
                    }
                } else if (!token.getTipo().equals("|n")) {
                    System.out.println("Erro");
                }

            }
        }
    }

    public void startAnalysis() {
        boolean erro = false;
        Map<Integer, ArrayList<Token>> tokens = aL.getTokens();
        lexemas = Lexemas.getLexemas();
        ArrayList<Token> listao = new ArrayList<Token>();
        for (Map.Entry<Integer, ArrayList<Token>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Token> value = entrySet.getValue();
            for (Token value1 : value) {
                if (value1.getValor().equals("fim")) {
                    // System.out.println("entrou aqui");
                    verificaComandos(listao);
                    erro = true;
                } else {
                    listao.add(value1);
                }

            }
            listao.add(new Token(lexemas.get("|n"), "|n"));
        }
        if (!erro) {
            System.err.println("Não foi achado FIM");
        }
    }
}
