import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileSystemLogic {

    private File currentDirectory;

    public FileSystemLogic() {
        this.currentDirectory = new File("").getAbsoluteFile();
    }

    public String getFileInfo(File file) {
        StringBuilder information = new StringBuilder();
        information.append("Имя: ").append(file.getName()).append("\n");
        information.append("Путь: ").append(file.getAbsolutePath()).append("\n");
        if (file.isDirectory()) {
            information.append("Тип: ").append("Папка").append("\n");
        } else {information.append("Тип: ").append("Файл").append("\n");}
        if (file.isFile()) {
            information.append("Размер: ").append(file.length() + " байт").append("\n");
        } else {information.append("Размер: ").append("—").append("\n");}
        information.append("Последнее изменение: ").append(new Date(file.lastModified())).append("\n");
        return information.toString();
    }

    public File[] getFilesInCurrentDirectory() {
        return currentDirectory.listFiles();
    }

    public boolean changeDirectory(File newDirectory) {
        if (newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
            return true;
        }
        return false;
    }

    public boolean changeDirectory(String path) {
        File newDirectory = new File(path);
        if (newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
            return true;
        }
        return false;
    }

    public boolean goToParentDirectory() {
        File parentDirectory = currentDirectory.getParentFile();
        if (parentDirectory != null) {
            currentDirectory = parentDirectory;
            return true;
        }
        return false;
    }

    public File getCurrentDirectory() {
        return currentDirectory.getAbsoluteFile();
    }

    public boolean createFile(String fileName) {
        File newFile = new File(currentDirectory, fileName);
        try {
            return newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}