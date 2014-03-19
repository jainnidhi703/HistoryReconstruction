import javax.swing.*;

public class Main {
    public static void main(String args[]) {

        UserInterface ui = new UserInterface();
        ui.setTitle("Auto Summarization");
        ui.setSize(800, 500);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setVisible(true);

    }
}
