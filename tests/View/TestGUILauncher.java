package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Launcher for testing different Monopoly GUI implementations.
 * This class provides a simple interface to launch the different GUI prototypes.
 */
public class TestGUILauncher {
    private JFrame launcherFrame;
    
    /**
     * Constructor that initializes and displays the launcher GUI.
     */
    public TestGUILauncher() {
        createLauncherFrame();
    }
    
    /**
     * Create and set up the launcher frame.
     */
    private void createLauncherFrame() {
        launcherFrame = new JFrame("Monopoly GUI Launcher");
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.setSize(400, 200);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("Select Monopoly GUI Version", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        
        JButton basicButton = new JButton("Basic GUI Version");
        basicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchBasicGUI();
            }
        });
        
        JButton advancedButton = new JButton("Advanced GUI Version");
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchAdvancedGUI();
            }
        });
        
        buttonsPanel.add(basicButton);
        buttonsPanel.add(advancedButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        launcherFrame.add(mainPanel);
        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setVisible(true);
    }
    
    /**
     * Launch the basic GUI version.
     */
    private void launchBasicGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestMonopolyGUI();
            }
        });
    }
    
    /**
     * Launch the advanced GUI version.
     */
    private void launchAdvancedGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestMonopolyAdvancedGUI();
            }
        });
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing applications
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestGUILauncher();
            }
        });
    }
}