package tetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class MainFrame extends JFrame {

    private MainFrame() {
        super("Tetris");
        setSize(600, 750);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(ColorManager.DEEP);

        add(Field.getInstance());
        add(Controller.getInstance());

        setVisible(true);
    }


    public static void main(String... args) {
        UIManager.put("Button.select", ColorManager.DARK);
        SwingUtilities.invokeLater(MainFrame::new);
    }
}