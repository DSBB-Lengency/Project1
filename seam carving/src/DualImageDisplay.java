package sc;

import edu.princeton.cs.algs4.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class DualImageDisplay {

    private static SeamCarver seamCarver;

    private static boolean isHorizontal;
    private static BufferedImage leftImage; // 保存左侧图片

    public static void main(String[] args) {
        // 创建顶层容器
        JFrame frame = new JFrame("Dual Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 创建主面板用于包含左右展示框
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // 左侧展示框
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel leftLabel = new JLabel("Drag an image here", SwingConstants.CENTER);
        leftLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftLabel.setVerticalAlignment(SwingConstants.CENTER);

        // 设置左侧展示框为可拖放目标
        new DropTarget(leftLabel, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    // 接收拖放的文件
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    // 读取并显示第一个图片文件
                    if (!droppedFiles.isEmpty()) {
                        File file = droppedFiles.get(0);
                        BufferedImage image = ImageIO.read(file);
                        if (image != null) {
                            leftImage = image; // 保存原始图像
                            seamCarver = new SeamCarver(new Picture(image.getWidth(), image.getHeight()));
                            // 对图片进行缩放
                            int maxWidth = 400;
                            int maxHeight = 300;
                            Image scaledImage = getScaledImage(image, maxWidth, maxHeight);
                            leftLabel.setIcon(new ImageIcon(scaledImage));
                            leftLabel.setText(null); // 清除提示文字
                        } else {
                            leftLabel.setText("Not a valid image file");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    leftLabel.setText("Error: " + ex.getMessage());
                }
            }
        });

        leftPanel.add(leftLabel, BorderLayout.CENTER);

        // 右侧展示框
        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel rightLabel = new JLabel("Image from path", SwingConstants.CENTER);
        rightLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightLabel.setVerticalAlignment(SwingConstants.CENTER);

        // 指定图片路径
        String imagePath = "C:\\Users\\86137\\Documents\\WPSDrive\\897125949\\WPS云盘\\sustech\\大二下\\dsaab\\seam-carving-master\\docs\\images\\small.jpg"; // 替换为你的图片路径
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                if (image != null) {
                    // 对图片进行缩放
                    int maxWidth = 400;
                    int maxHeight = 300;
                    Image scaledImage = getScaledImage(image, maxWidth, maxHeight);
                    rightLabel.setIcon(new ImageIcon(scaledImage));
                    rightLabel.setText(null); // 清除提示文字
                } else {
                    rightLabel.setText("Not a valid image file");
                }
            } else {
                rightLabel.setText("Image file not found");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            rightLabel.setText("Error: " + ex.getMessage());
        }

        rightPanel.add(rightLabel, BorderLayout.CENTER);

        // 将左右展示框添加到主面板
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();

        JButton protectButton = new JButton("Protect");

        // 创建单选按钮和按钮组
        JRadioButton horizontalButton = new JRadioButton("Horizontal");
        JRadioButton verticalButton = new JRadioButton("Vertical");
        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(horizontalButton);
        orientationGroup.add(verticalButton);

        // 默认选项
        horizontalButton.setSelected(true);

        // 创建输入框
        JTextField multipleField = new JTextField(10);

        // 单选按钮监听器
        ActionListener radioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (horizontalButton.isSelected()) {
                    isHorizontal = true;
                    System.out.println(isHorizontal);

                } else if (verticalButton.isSelected()) {
                    isHorizontal = false;
                    System.out.println(isHorizontal);
                }
            }
        };

        horizontalButton.addActionListener(radioListener);
        verticalButton.addActionListener(radioListener);

        // 输入框回车监听器
        multipleField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    double multiple = Double.parseDouble(multipleField.getText());
                    System.out.println(isHorizontal);
                    Picture picture = seamCarver.resizeTo(isHorizontal, multiple);

                    JLabel jLabel = picture.getJLabel();
                    rightLabel.setIcon(jLabel.getIcon());

                    System.out.println("Multiple: " + multipleField.getText());

                }
            }
        });

        // 将按钮和单选按钮及输入框添加到按钮面板
        buttonPanel.add(protectButton);
        buttonPanel.add(horizontalButton);
        buttonPanel.add(verticalButton);
        buttonPanel.add(new JLabel("Multiple:"));
        buttonPanel.add(multipleField);

        // 保护按钮点击事件
        protectButton.addActionListener(e -> {
            if (leftImage != null) {
                showProtectWindow(leftImage);
            } else {
                JOptionPane.showMessageDialog(frame, "Please drag an image into the left box first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 将主面板和按钮面板添加到框架
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 显示保护窗口的方法
    private static void showProtectWindow(BufferedImage image) {
        // 创建新窗口
        JFrame protectFrame = new JFrame("Protect Mode");
        protectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        protectFrame.setLayout(new BorderLayout());

        // 计算新窗口的尺寸
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();
        int frameWidth = imageWidth + 100; // 比图片宽度略宽

        protectFrame.setSize(frameWidth, imageHeight + 50); // 添加一些高度来避免遮挡

        // 创建图片展示面板
        ImagePanel imagePanel = new ImagePanel(image);
        protectFrame.add(imagePanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton withdrawButton = new JButton("Withdraw");
        JButton okButton = new JButton("OK");

        buttonPanel.add(withdrawButton);
        buttonPanel.add(okButton);

        // 按钮点击事件
        withdrawButton.addActionListener(e -> imagePanel.clearPoints());
        okButton.addActionListener(e -> {
            protectFrame.dispose();
            System.out.println("finish");
            System.out.println("Tracked points: " + imagePanel.getPoints());
        });

        // 将图片展示面板和按钮面板添加到新窗口
        protectFrame.add(buttonPanel, BorderLayout.EAST);

        protectFrame.setVisible(true);
    }

    // 自定义面板类用于绘制图片和鼠标拖动轨迹
    static class ImagePanel extends JPanel {
        private final BufferedImage image;
        private final List<Point> points = new ArrayList<>();

        public ImagePanel(BufferedImage image) {
            this.image = image;

            // 添加鼠标监听器来捕捉点击和拖动事件
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    addPoint(e);
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    addPoint(e);
                }
            });
        }

        private void addPoint(MouseEvent e) {
            Point imagePoint = getImageRelativePoint(e.getPoint());
            if (imagePoint != null) {
                points.add(imagePoint);
                System.out.println("Mouse dragged at image coordinates: " + imagePoint);
                repaint();
            }
        }

        private Point getImageRelativePoint(Point mousePoint) {
            int iconWidth = image.getWidth();
            int iconHeight = image.getHeight();

            // 计算鼠标点击位置相对于图片的位置
            int relativeX = mousePoint.x;
            int relativeY = mousePoint.y;

            // 确保点击在图片区域内
            if (relativeX >= 0 && relativeX < iconWidth && relativeY >= 0 && relativeY < iconHeight) {
                return new Point(relativeX, relativeY);
            }
            return null;
        }

        public void clearPoints() {
            points.clear();
            repaint();
        }

        public List<Point> getPoints() {
            return points;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);

            // 绘制半透明红色轨迹
            g.setColor(new Color(255, 0, 0, 128));
            for (Point point : points) {
                g.fillOval(point.x - 2, point.y - 2, 4, 4);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(image.getWidth(), image.getHeight());
        }
    }

    // 缩放图片的方法
    private static Image getScaledImage(BufferedImage srcImg, int maxWidth, int maxHeight) {
        int width = srcImg.getWidth();
        int height = srcImg.getHeight();

        if (width > maxWidth) {
            height = (height * maxWidth) / width;
            width = maxWidth;
        }

        if (height > maxHeight) {
            width = (width * maxHeight) / height;
            height = maxHeight;
        }

        return srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}
