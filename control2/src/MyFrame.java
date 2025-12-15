import javax.swing.*;

public class MyFrame extends JFrame {

    public MyFrame() {
        setTitle("Проводник");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FileSystemLogic model = new FileSystemLogic();
        MyPanel panel = new MyPanel(model);
        add(panel);
        setVisible(true);
    }
}