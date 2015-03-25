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
    public static void main(String[] args) {
        try {
            //        try {
            if (args.length > 0) {
                if (args[0].equals("-help")) {
                    System.out.println("###################################");
                    System.out.println("####      Primeira Etapa        ###");
                    System.out.println("####  Analisado lexico para     ###");
                    System.out.println("####   Para Linguagem L         ###");
                    System.out.println("#### notasdeaula.lacerda.eti.br ###");
                    System.out.println("####----------------------------###");
                    System.out.println("####   Sintaxe:                 ###");
                    //   System.out.println("#### -2f = salva em arquivo #######");
                    System.out.println("#### java -jar lprime.l         ###");
                    System.out.println("###################################");
                } else {
                    AnalisadorLexico al = new AnalisadorLexico(args[0]);
                    al.startAnalysis();
                    al.salva(false);
                }

            } else {
                //Thread.sleep(40000);
                AnalisadorLexico al = new AnalisadorLexico("lprime.l");

                al.startAnalysis();
                al.salva(false);
                //al.serializa();
            }
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        catch (InterruptedException ex) {
//            Logger.getLogger(compilador.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
