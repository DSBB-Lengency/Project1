package src;

import edu.princeton.cs.algs4.Picture;
import sc.SeamCarver;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.List;


public class MainFrame extends JFrame {

    public String path;
    public String inputStr;
    private final JLabel TargetImage;
    private boolean isHorizontal;
    public static final int ICON_SIZE = 30;
    public static String resource_folder = "resource";
    public SeamCarver seamCarver;
    private static final JFrame mainframe = new JFrame("Sustech Seam Carver");

    public MainFrame() {
        isHorizontal = true;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel menu = new JPanel();
        menu.setSize(40, 50);
        this.TargetImage = new JLabel(icon("dragdrop.png", ICON_SIZE / 4), JLabel.CENTER);
        panel.add(this.TargetImage);
        addPanel(menu);
        addHint(menu);
        addRelativeSize(menu);
        addProtect(menu);
        addEliminate(menu);
        addDirection(menu);
        addDropPicture(mainframe,menu);

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        panel.add(menu);

        mainframe.add(panel);
        mainframe.pack();

        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setLocationRelativeTo(null);
        mainframe.setResizable(false);
        mainframe.setVisible(true);
    }

    //加上主题Seam Carver
    private void addPanel(JPanel menu) {
        JPanel sustech = new JPanel();
        JLabel icon = new JLabel(icon("icon.png", ICON_SIZE / 10), JLabel.CENTER);
        sustech.add(icon);
        menu.add(sustech);
    }

    //放入图片的窗口
    private void addDropPicture(JFrame frame, JPanel menu) {
        this.TargetImage.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File image = droppedFiles.get(0);
                    path=image.getPath();
                    Picture picture = new Picture(path);
                    seamCarver=new SeamCarver(picture);
                    evt.dropComplete(true);
                } catch (Exception ignored) {
                    evt.dropComplete(false);
                }
            }
        });
    }

    private void addHint(JPanel menu) {
        JLabel jLabel = new JLabel("Enter a multiple(between 0.5 and 1.5) to zoom in or out");
        jLabel.setSize(3, 20);
        menu.add(jLabel);
    }

    //读入相对大小
    private void addRelativeSize(JPanel menu) {
        JTextField jTextField = new JTextField("");
        jTextField.setPreferredSize(new Dimension(10, 20));
        menu.add(jTextField);
        jTextField.setColumns(0);
        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String key = "0123456789.";
                if (key.indexOf(e.getKeyChar()) < 0) {
                    e.consume();//如果不是数字则取消
                }
            }
        });
        //设置回车监听器
        jTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputStr = jTextField.getText();
                double multiple = Double.parseDouble(inputStr);
                Picture picture = seamCarver.resizeTo(isHorizontal, multiple);
                picture.show();
            }
        });
    }

    //弹出数字大小不符的窗口
    private void invalidNumber() {
        JFrame jFrame = new JFrame();
        JLabel label = new JLabel("Invalid number!");
        jFrame.add(label);
        jFrame.setSize(200, 100);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    private ImageIcon icon(String filename, int... dims) {
        URL url = getClass().getResource(resource_folder + "/" + filename);
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
        int width, height;
        if (dims.length == 0) {
            width = height = ICON_SIZE;
        } else {
            width = icon.getIconWidth() / dims[0];
            height = icon.getIconHeight() / dims[0];
        }
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
    }
    //设置鼠标监听器


    //设置保护或消除按钮
    private void addProtect(JPanel menu) {
        JButton protect = new JButton("Protect Mode");
        protect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        menu.add(protect);
    }

    private void addEliminate(JPanel menu) {
        JButton eliminate = new JButton("Protect Mode");
        eliminate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        menu.add(eliminate);
    }

    //设置操作方向
    private void addDirection(JPanel menu) {
        JRadioButton ho = new JRadioButton("Horizontal");
        ho.setSelected(true);
        ho.setBounds(20, 30, 75, 22);
        ho.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ho.isSelected()) {
                    isHorizontal = true;
                }
            }
        });
        JRadioButton ver = new JRadioButton("Vertical");
        ho.setBounds(20, 30, 75, 22);
        ho.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ver.isSelected()) {
                    isHorizontal = false;
                }
            }
        });
        menu.add(ho);
        menu.add(ver);

        ButtonGroup group = new ButtonGroup();
        group.add(ho);
        group.add(ver);
    }
}
