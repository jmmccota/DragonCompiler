package Compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class compilador {

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
            }
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
