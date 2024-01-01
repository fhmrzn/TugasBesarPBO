package user;

import javax.swing.UIManager;
import user.dashboard.AdminDashboardFrame;
import user.dashboard.DosenDashboardFrame;

public class Driver {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//          DosenDashboardFrame dd = new DosenDashboardFrame("Fahmi Rozan");
//          dd.setVisible(true);
        AdminDashboardFrame ad = new AdminDashboardFrame("Aunill");
        ad.setVisible(true);
//        RegisterFrame rf = new RegisterFrame();
//        rf.setVisible(true);
    }
}
