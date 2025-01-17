import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.stream.ImageOutputStream;

public class GifCreator {
    private JFrame frame;
    private JPanel panel;
    private JButton addButton, combineButton;
    private JSpinner delaySpinner;
    private java.util.List<File> images;
    
    public GifCreator() {
        frame = new JFrame("Animated GIF Creator");
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        addButton = new JButton("Add Image");
        combineButton = new JButton("Create GIF");
        
        images = new ArrayList<>();
        
        addButton.addActionListener(e -> openFileChooser());
        combineButton.addActionListener(e -> createGif());
        
        delaySpinner = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 10)); // Set default to 100ms, min 10ms, max 10s, step 10ms
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Frame Delay (ms):"));
        controlPanel.add(delaySpinner);
        
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(addButton, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.WEST);
        frame.add(combineButton, BorderLayout.SOUTH);
        
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            
            for (File file : selectedFiles) {
                images.add(file);
                addThumbnail(file);
            }
        }
    }
    
    private void addThumbnail(File file) {
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(file).getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            JLabel label = new JLabel(file.getName(), icon, JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            label.setHorizontalTextPosition(JLabel.CENTER);
            panel.add(label);
            panel.revalidate();
            panel.repaint();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void createGif() {
        if (images.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No images selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int frameDelay = (int) delaySpinner.getValue();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save GIF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("GIF Image", "gif"));
        
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File outputGif = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".gif");
            try (ImageOutputStream output = ImageIO.createImageOutputStream(outputGif)) {
                BufferedImage firstImage = ImageIO.read(images.get(0));
                ImageIO.write(firstImage, "gif", output);
                output.flush();
                
                for (int i = 1; i < images.size(); i++) {
                    BufferedImage nextImage = ImageIO.read(images.get(i));
                    ImageIO.write(nextImage, "gif", output);
                    output.flush();
                }
                
                JOptionPane.showMessageDialog(frame, "GIF created successfully: " + outputGif.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to create GIF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GifCreator::new);
    }
}
