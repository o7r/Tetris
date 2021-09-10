package tetris;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class Controller extends JPanel {

    //シングルトンパターンを使用。
    private static final Controller thisPanel = new Controller();
    public static Controller getInstance() {
        return thisPanel;
    }

    private Controller() {
        setSize(180, 300);
        setLocation(360, 20);
        setOpaque(false);
        setLayout(new GridLayout(0, 2, 3, 3));

        add(new CustomButton("Start", e -> {
            Field.getInstance().gameStart();
        }));
        add(new CustomButton("Cancel", e -> {
            Field.getInstance().gameEnd();
        }));

        add(new InvisiblePanel());
        add(new InvisiblePanel());

        add(new CustomButton("R Left", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().rotateLeft();
            }
        }));
        add(new CustomButton("R Right", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().rotateRight();
            }
        }));
        add(new CustomButton("Left", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().moveLeft();
            }
        }));
        add(new CustomButton("Right", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().moveRight();
            }
        }));
        add(new CustomButton("Hard Drop", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().hardDrop();
            }
        }));
        add(new CustomButton("Soft Drop", e -> {
            if(tetrominoIsExist()) {
                Field.getInstance().getTetromino().oneDrop();
            }
        }));
    }


    private boolean tetrominoIsExist() {
        return Field.getInstance().getTetromino() != null;
    }


    private static final class CustomButton extends JButton {

        private CustomButton(String text, ActionListener listener) {
            super(text);
            setFocusPainted(false);
            addActionListener(listener);
            setBackground(ColorManager.THIN);
            setForeground(ColorManager.WHITE);
            setBorder(new LineBorder(ColorManager.BLACK));
        }
    }


    private static final class InvisiblePanel extends Component {

        private InvisiblePanel() {
            setFocusable(false);
            setVisible(false);
        }
    }
}