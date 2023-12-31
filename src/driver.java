/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fhmrz
 */
public class driver {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Quiz().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
