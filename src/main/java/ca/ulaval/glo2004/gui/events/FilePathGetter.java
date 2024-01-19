package ca.ulaval.glo2004.gui.events;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

/**
 * Opens a file explorer to select a file path.
 */
public class FilePathGetter {
    /**
     * Opens a file explorer to select a dir path.
     * @param extension : The extension of the files to show.
     * @return The selected dir path.
     */
    static public Optional<String> getDirPath(String approveButtonText, String extension) {
        JFileChooser fileChooser = new JFileChooser(new java.io.File("."));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                extension.toUpperCase() + " files", extension);
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = fileChooser.showDialog(null, approveButtonText);

        if (res == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            return Optional.of(path);
        }

        return Optional.empty();
    }

    /**
     * Opens a file explorer to select a file path.
     * @param extension : The extension of the file to select.
     * @return The selected file path.
     */
    static public Optional<String> getFilePath(String approveButtonText, String extension) {
        JFileChooser fileChooser = new JFileChooser(new java.io.File("."));
        fileChooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return extension + " files";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(extension);
                }
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = fileChooser.showDialog(null, approveButtonText);

        if (res == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            return Optional.of(path);
        }

        return Optional.empty();
    }

    /**
     * Opens a file explorer to select a .stl file path.
     * @return The selected .stl file path.
     */
    static public Optional<String> getSTLDirPath(String approveButtonText) {
        return getDirPath(approveButtonText, "stl");
    }
}
