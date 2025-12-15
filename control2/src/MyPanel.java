import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MyPanel extends JPanel {

    private JList<File> fileList;
    private JTextArea textArea;
    private FileSystemLogic model;
    private JButton backButton;
    private JTextField pathField;
    private JButton goButton;
    private JButton createFileButton;


    public MyPanel(FileSystemLogic model) {
        this.model = model;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("<");
        topPanel.add(backButton, BorderLayout.WEST);

        pathField = new JTextField(model.getCurrentDirectory().getAbsolutePath());
        goButton = new JButton("Перейти");
        createFileButton = new JButton("Создать файл");
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(goButton, BorderLayout.EAST);
        pathPanel.add(createFileButton, BorderLayout.WEST);

        topPanel.add(pathPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        fileList = new JList<>();
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(fileList);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, textScrollPane);

        add(splitPane);

        loadFiles();

        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    String fileInfo = model.getFileInfo(selectedFile);
                    textArea.setText(fileInfo);
                }
            }
        });

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File selectedFile = fileList.getSelectedValue();
                    if (selectedFile != null && selectedFile.isDirectory()) {
                        if (model.changeDirectory(selectedFile)) {
                            pathField.setText(model.getCurrentDirectory().getAbsolutePath());
                            loadFiles();
                        }
                    }
                }
            }
        });

        backButton.addActionListener(e -> {
            if (model.goToParentDirectory()) {
                pathField.setText(model.getCurrentDirectory().getAbsolutePath());
                loadFiles();
            }
        });

        goButton.addActionListener(e -> {
            String path = pathField.getText();
            if (model.changeDirectory(path)) {
                loadFiles();
            } else {
                JOptionPane.showMessageDialog(this, "Ай-ай-ай. Не существует такого пути", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        createFileButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(this, "Введите имя файла:", "Создание файла", JOptionPane.PLAIN_MESSAGE);
            if (fileName != null && !fileName.trim().isEmpty()) {
                if (model.createFile(fileName)) {
                    loadFiles();
                } else {
                    JOptionPane.showMessageDialog(this, "Не удалось создать файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadFiles() {
        File[] files = model.getFilesInCurrentDirectory();
        fileList.setListData(files);
    }
}