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
            AnalisadorLexico al = new AnalisadorLexico("lprime.l");
            al.startAnalysis();
            al.salva(false);
            //al.deserializa();
//        } catch (IOException ex) {
//            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
//        }
        } catch (IOException ex) {
            Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
