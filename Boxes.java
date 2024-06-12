/*
boxpmenu
pack into exe with icon that can be start menu shortcut
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

public class Boxes {

    private JFrame frame;
    private JButton pop = new JButton("Pop");
    private JPopupMenu pmenu = new JPopupMenu();
    private JMenuItem pclose = new JMenuItem("Close");
    private JMenuItem restart = new JMenuItem("Restart");
    private JMenuItem onTop = new JMenuItem("On top✅");
    private Point initialClick;
    private List<JFrame> blkList = new ArrayList<JFrame>();
    private List<JButton> btnList = new ArrayList<JButton>();
    private Color black = new Color(0, 0, 0);
    private Color grayer = new Color(23, 23, 23);
    private Color grayerer = new Color(11, 11, 11);
    private Color lightBlack = new Color(15, 15, 15);
    private Color lightGrayer = new Color(69, 69, 69);
    private Color lightGrayerer = new Color(33, 33, 33);
    private boolean[] isBtnOn;
    private boolean isTop = true;
    private boolean isHovering;
    private JPopupMenu boxPMenu = new JPopupMenu();
    private JMenuItem fade5s = new JMenuItem("Fade for 5 seconds");
    private JMenuItem fade20s = new JMenuItem("Fade for 20 seconds");
    private JMenuItem fade60s = new JMenuItem("Fade for 60 seconds");

    public Boxes() {
        frame = new JFrame();
        pop.addActionListener(listener);
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
            JFrame blackOut = new JFrame();
            // make blackOut not appear as a window in alt tab and taskbar
            blackOut.setType(javax.swing.JFrame.Type.UTILITY);

            blackOut.setLocation(monitor.getLocation());
            blackOut.setSize(monitor.getSize());
            blackOut.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            blackOut.getContentPane().setBackground(Color.BLACK);
            blackOut.setUndecorated(true);
            blackOut.setVisible(false);
            blkList.add(blackOut);
        }
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
                frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2, lightGrayer));
                int i = 0;
                for (JButton btn : btnList) {
                    btn.setForeground(lightGrayer);
                    if (isBtnOn[i]) {
                        btn.setBackground(lightBlack);
                    } else {
                        btn.setBackground(lightGrayerer);
                    }
                    i++;
                }
                pop.setForeground(lightGrayer);
                pop.setBackground(lightBlack);
            }

            public void mouseExited(MouseEvent e) {
                isHovering = false;
                frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2, grayer));
                int i = 0;
                for (JButton btn : btnList) {
                    btn.setForeground(grayer);
                    if (isBtnOn[i]) {
                        btn.setBackground(black);
                    } else {
                        btn.setBackground(grayerer);
                    }
                    i++;
                }
                pop.setForeground(grayer);
                pop.setBackground(black);
            }
        };

        for (int i = 0; i < blkList.size(); i++) {
            MouseAdapter tempMThing = new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    System.out.println(((JFrame) e.getSource()).getContentPane().getSize());
                }

                // if middle clicked, go away
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    initialClick = e.getPoint();
                    // if (e.getButton() == MouseEvent.BUTTON3)
                    // boxPMenu.show(e.getComponent(), x, y);

                    if (e.getButton() == MouseEvent.BUTTON2) {
                        ((JFrame) e.getSource()).setVisible(false);
                        for (int i = 0; i < blkList.size(); i++) {
                            if (blkList.get(i).equals((JFrame) e.getSource())) {
                                int monitorId = i;
                                btnList.get(monitorId).setBackground(grayerer);
                                isBtnOn[monitorId] = false;
                            }

                        }
                    }
                }
            };
            blkList.get(i).addMouseListener(tempMThing);
            // blkList.get(i).add(boxPMenu);
        }

        frame.addMouseListener(mThing2);
        pop.addMouseListener(mThing2);

        frame.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e) {
                frame.setLocation(e.getXOnScreen() - initialClick.x, e.getYOnScreen() - initialClick.y);
            }
        });

        pmenu.add(onTop);
        pmenu.add(restart);
        pmenu.add(pclose);
        onTop.addActionListener(listener);
        restart.addActionListener(listener);
        pclose.addActionListener(listener);
        frame.setTitle("Screen cover");
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0); // frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(1 + btnList.size(), 1)); // !! this is where size is defined !!
        isBtnOn = new boolean[btnList.size()];
        int i = 0;
        for (JButton btn : btnList) {
            isBtnOn[i] = false;
            frame.add(btn);
            btn.addMouseListener(mThing2);
            btn.setBackground(grayerer);
            btn.setForeground(grayer);
            btn.setFocusable(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            i++;
        }
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(15, 2, 2, 2, grayer)); // Border

        pop.setBackground(black);
        pop.setForeground(grayer);
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
        Boxes box = new Boxes();
        box.setUpBoxes();
        box.setUpGUI();
    }

    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comm = e.getActionCommand();
            System.out.println("|" + comm);

            if (comm.contains("scrn")) {
                int btnStrnNum = Integer.parseInt(String.valueOf(comm.charAt(comm.length() - 1)));
                JButton btn = btnList.get(btnStrnNum);

                if (!isBtnOn[btnStrnNum]) {
                    if (isHovering) {
                        btn.setBackground(lightBlack);
                    } else {
                        btn.setBackground(black);
                    }
                    blkList.get(btnStrnNum).setVisible(true);
                    isBtnOn[btnStrnNum] = true;
                } else {
                    if (isHovering) {
                        btn.setBackground(lightGrayerer);
                    } else {
                        btn.setBackground(grayerer);
                    }
                    blkList.get(btnStrnNum).setVisible(false);
                    isBtnOn[btnStrnNum] = false;
                }
                // if comm contains ontop
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
            final File currentJar = new File(Boxes.class.getProtectionDomain().getCodeSource().getLocation().toURI());

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
}