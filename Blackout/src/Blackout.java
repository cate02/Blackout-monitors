/*
 * boxpmenu pack into exe with icon that can be start menu shortcut
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

public class Blackout {
    
    private static JFrame frame;
    private static JButton pop = new JButton("Pop");
    private JPopupMenu pmenu = new JPopupMenu();
    private JMenuItem pclose = new JMenuItem("Close");
    private JMenuItem ptheme = new JMenuItem("Theme");
    private JMenuItem restart = new JMenuItem("Restart");
    private JMenuItem onTop = new JMenuItem("On top✅");
    private Point initialClick;
    private static List<BlackBox> blkList = new ArrayList<BlackBox>();
    private static List<JButton> btnList = new ArrayList<JButton>();
    private static Color black = new Color(0, 0, 0);
    private Color grayer = new Color(23, 23, 23);
    private static Color grayerer = new Color(11, 11, 11);
    private Color lightBlack = new Color(15, 15, 15);
    private Color lightGrayer = new Color(69, 69, 69);
    private static Color lightGrayerer = new Color(33, 33, 33);
    private static boolean[] isBtnOn;
    private boolean isTop = true;
    private static boolean isHovering;
    private JPopupMenu boxPMenu = new JPopupMenu();
    private JMenuItem fade5s = new JMenuItem("Fade for 5 seconds");
    private JMenuItem fade20s = new JMenuItem("Fade for 20 seconds");
    private JMenuItem fade60s = new JMenuItem("Fade for 60 seconds");
    
    private static float secondaryColorMult = 1.3f;
    private static float hoverOppacityAdd = 20;
    private static float mainOpacity = 10;
    private static float textOpacity = 10;
    private static Color backColor = new Color(100, 50, 200);
    private static Color mainColor;
    private static Color secondaryColor;
    private static Color hoverColor;
    private static Color hoverSecondaryColor;
    
    private static Preferences preferences;
    private int[] screenBounds = { 0, 0 };
    private static String activatedButtons;
    
    private float contrastPercent = 20f;
    
    public Blackout() {
        preferences = Preferences.userNodeForPackage(Blackout.class);
        screenBounds[0] = preferences.getInt("screenX", 0);
        screenBounds[1] = preferences.getInt("screenY", 0);
        
        frame = new JFrame();
        // if frame moved change preferences
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                preferences.putInt("screenX", frame.getX());
                preferences.putInt("screenY", frame.getY());
            }
        });
        
        pop.addActionListener(listener);
        
    }
    
    static void loadActivatedButtons() {
        activatedButtons = preferences.get("activatedButtons", "");
        String[] parts = activatedButtons.split(",");
        isBtnOn = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            isBtnOn[i] = parts[i].equals("1");
            triggerBlackout(i, isBtnOn[i]);
            System.out.println(isBtnOn[i] + " " + parts[i]);
        }
        System.out.println("Activated buttons loaded: " + activatedButtons);
    }
    
    void saveActivatedButtons() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < isBtnOn.length; i++) {
            sb.append(isBtnOn[i] ? "1" : "0");
            if (i < isBtnOn.length - 1)
                sb.append(",");
        }
        preferences.put("activatedButtons", sb.toString());
        activatedButtons = sb.toString();
        System.out.println("Activated buttons saved: " + activatedButtons);
    }
    
    public void setUpBoxes() {
        GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = gEnvironment.getScreenDevices();
        // Organising buttons appropiately
        while (notSorted(devices)) {
            for (int iii = 0; iii < devices.length - 1; iii++) {
                if (devices[iii].getDefaultConfiguration().getBounds().getX() > devices[iii + 1]
                        .getDefaultConfiguration().getBounds().getX()) {
                    GraphicsDevice temp = devices[iii];
                    devices[iii] = devices[iii + 1];
                    devices[iii + 1] = temp;
                }
            }
        }
        
        for (int i = 0; i < devices.length; i++) {
            String scrnNum = "scrn" + i;
            btnList.add(new JButton(scrnNum));
            btnList.get(i).addActionListener(listener);
            // Add the button to the Frame
            frame.add(btnList.get(i));
            // Get the rectange of monitor
            Rectangle monitor = devices[i].getDefaultConfiguration().getBounds();
            BlackBox blackBox = new BlackBox();
            blackBox.create(monitor);
            blackBox.setColor(Color.BLACK);
            MouseAdapter tempMThing = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        BlackBox sourceBox = (BlackBox) e.getSource();
                        sourceBox.setActive(!sourceBox.isActive);
                    }
                }
            };
            blackBox.addMouseListener(tempMThing);
            
            blkList.add(blackBox);
        }
        
        
        // isbtnon[i]=...
        
    }
    
    static void triggerBlackout(int i, boolean state) {
        isBtnOn[i] = state;
        blkList.get(i).setActive(isBtnOn[i]);
        updateColors();
        /*
        if (isBtnOn[i]) {
            btnList.get(i).setBackground(mainColor);
        } else {
            btnList.get(i).setBackground(secondaryColor);
        }*/
        
    }
    
    public void setUpGUI() {
        boxPMenu.add(fade5s);
        boxPMenu.add(fade20s);
        boxPMenu.add(fade60s);
        fade5s.addActionListener(listener);
        fade20s.addActionListener(listener);
        fade60s.addActionListener(listener);
        
        MouseAdapter mThing = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                initialClick = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON3)
                    pmenu.show(e.getComponent(), x, y);
            }
        };
        frame.addMouseListener(mThing);
        
        MouseAdapter mThing2 = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                // frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2,
                // lightGrayer));
                updateColors();
            }
            
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                // frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2,
                // grayer));
                updateColors();
            }
        };
        
        frame.addMouseListener(mThing2);
        pop.addMouseListener(mThing2);
        
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(e.getXOnScreen() - initialClick.x, e.getYOnScreen() - initialClick.y);
            }
        });
        
        pmenu.add(onTop);
        pmenu.add(ptheme);
        // pmenu.add(restart);
        pmenu.add(pclose);
        onTop.addActionListener(listener);
        restart.addActionListener(listener);
        pclose.addActionListener(listener);
        ptheme.addActionListener(listener);
        frame.setTitle("Screen cover");
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(screenBounds[0], screenBounds[1]); // frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(1 + btnList.size(), 1)); // !! this is where size is defined !!
        isBtnOn = new boolean[btnList.size()];
        int i = 0;
        for (JButton btn : btnList) {
            // isBtnOn[i] = false;
            frame.add(btn);
            btn.addMouseListener(mThing2);
            // btn.setBackground(grayerer);
            // btn.setForeground(grayer);
            btn.setFocusable(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            i++;
        }
        // frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2,
        // grayer)); // Border
        
        // pop.setBackground(black);
        // pop.setForeground(grayer);
        pop.setFocusable(false);
        pop.setBorderPainted(false);
        pop.setContentAreaFilled(false);
        pop.setOpaque(true);
        frame.add(pop);
        frame.setUndecorated(true);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        Blackout box = new Blackout();
        box.setUpBoxes();
        loadActivatedButtons();
        box.setUpGUI();
        updateColors();
        loadActivatedButtons();
        
    }
    
    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comm = e.getActionCommand();
            // System.out.println("|" + comm);
            
            if (comm.contains("scrn")) {
                int btnStrnNum = Integer.parseInt(String.valueOf(comm.charAt(comm.length() - 1)));
                triggerBlackout(btnStrnNum, !isBtnOn[btnStrnNum]);
                saveActivatedButtons();
                
            } else if (comm.contains("On top")) {
                if (!isTop) {
                    frame.setAlwaysOnTop(true);
                    isTop = true;
                    onTop.setText("On top✅");
                } else {
                    frame.setAlwaysOnTop(false);
                    isTop = false;
                    onTop.setText("On top❌");
                }
            } else if (comm.equals("Pop")) {
                for (JFrame frames : blkList) {
                    frames.setAlwaysOnTop(true);
                    frames.setAlwaysOnTop(false);
                }
                
            } else if (comm.equals("Theme")) {
                ThemeWindow themeWindow = new ThemeWindow();
            } else if (comm.equals("Close")) {
                System.exit(0);
            } else if (comm.equals("Restart")) {
                System.out.println("Restarting");
                restartApplication();
            } else if (comm.equals("Fade for 5 seconds")) {
                System.out.println("Fade for 5 seconds");
            } else if (comm.equals("Fade for 20 seconds")) {
                System.out.println("Fade for 20 seconds");
            } else if (comm.equals("Fade for 60 seconds")) {
                System.out.println("Fade for 60 seconds");
            }
            
        }
    };
    
    public boolean notSorted(GraphicsDevice[] arr) {// but, highest->lowest
        int points = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].getDefaultConfiguration().getBounds().getX() <= arr[i + 1].getDefaultConfiguration().getBounds()
                    .getX())
                points++;
            else {
                points = 0;
                break;
            }
        }
        if (points == arr.length - 1)
            return false;
        else
            return true;
    }
    
    public static void printArr(int[] arr) {
        System.out.printf("{%3d\n", arr[0]);
        for (int i = 1; i < arr.length; i++) {
            System.out.printf("%4d\n", arr[i]);
        }
        System.out.println("}");
    }
    
    public void restartApplication() {
        try {
            
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(
                    Blackout.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            
            /* is it a jar file? */
            if (!currentJar.getName().endsWith(".jar"))
                return;
            
            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());
            
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            System.exit(0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Color changeColor(Color color, float opacity) {
        // Clamp opacity between 0 and 100
        opacity = Math.max(0, Math.min(opacity, 100));
        
        float blendFactor = opacity / 100f;
        
        int r = (int) (color.getRed() + (255 - color.getRed()) * blendFactor);
        int g = (int) (color.getGreen() + (255 - color.getGreen()) * blendFactor);
        int b = (int) (color.getBlue() + (255 - color.getBlue()) * blendFactor);
        
        return new Color(r, g, b);
    }
    
    static void setNewColors(Color backColor, float secondaryColorMult, float mainOpacity, float textOpacity,
            float hoverOppacityAdd) {
        System.out.println(secondaryColorMult + " " + mainOpacity + " " + textOpacity + " " + hoverOppacityAdd);
        Blackout.backColor = backColor;
        Blackout.secondaryColorMult = secondaryColorMult;
        Blackout.mainOpacity = mainOpacity;
        Blackout.textOpacity = textOpacity;
        Blackout.hoverOppacityAdd = hoverOppacityAdd;
        
        updateColors();
    }
    
    
    static void updateColors() {
        
        mainColor = changeColor(backColor, mainOpacity);
        secondaryColor = changeColor(mainColor, mainOpacity * secondaryColorMult);
        hoverColor = changeColor(mainColor, mainOpacity + hoverOppacityAdd);
        hoverSecondaryColor = changeColor(secondaryColor, (mainOpacity + hoverOppacityAdd) * secondaryColorMult);
        Color textColor = changeColor(secondaryColor, textOpacity);
        
        
        Color color1 = mainColor; // lighter
        Color color2 = secondaryColor; // darker
        Color color3 = textColor; // lightest, border+text
        if (isHovering) {
            color1 = changeColor(mainColor, hoverOppacityAdd);
            color2 = changeColor(mainColor, hoverOppacityAdd * secondaryColorMult);
            color3 = changeColor(textColor, hoverOppacityAdd);
        }
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2, color1));
        pop.setForeground(color2);
        pop.setBackground(color1);
        
        for (int i = 0; i < btnList.size(); i++) {
            blkList.get(i).setColor(backColor);
            JButton btn = btnList.get(i);
            if (isBtnOn[i]) {
                btn.setForeground(color2);
                btn.setBackground(color1);
            } else {
                btn.setForeground(color1);
                btn.setBackground(color2);
            }
        }
    }
}