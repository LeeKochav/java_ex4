package gameClient;

import dataStructure.edge_data;
import dataStructure.node_data;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class represents a Graphical User Interface - GUI of the game.
 * MyGameGui attributes:
 * 1. Game .
 * 2. mode- manual /automate .
 */
public class MyGameGUI extends JFrame implements ActionListener, MouseListener {

    private Game my_game;
    public int mode;
    private static int HEIGHT = 1000;
    private static int WIDTH = 1000;
    public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
    public static final String jdbcUser = "student";
    public static final String jdbcUserPassword = "OOP2020student";

    /**
     * Default constructor
     */
    public MyGameGUI() {
    }

    /**
     * Constructor of MyGameGui initialize the game by a given stage input, mode of the game and the GUI frame.
     *
     * @param g
     * @param mode
     */
    public MyGameGUI(Game g, int mode) {
        this.my_game = g;
        this.mode = mode;
        init();
    }

    /**
     * Initialize the GUI frame.
     * If the mode is manual , mouse listener is available.
     */
    private void init() {
        this.setBounds(200, 0, WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Game information");
        menu.setFont(new Font("deafult", Font.BOLD, 12));
        MenuItem UserscoreForStage = new MenuItem("User rank for this stage");
        MenuItem Userscore = new MenuItem("User rank for all stages");
        MenuItem GameScore = new MenuItem("Game rank for all players");
        UserscoreForStage.setFont(new Font("deafult", Font.BOLD, 12));
        Userscore.setFont(new Font("deafult", Font.BOLD, 12));
        GameScore.setFont(new Font("deafult", Font.BOLD, 12));
        Userscore.addActionListener(this);
        GameScore.addActionListener(this);
        UserscoreForStage.addActionListener(this);
        menu.add(UserscoreForStage);
        menu.add(Userscore);
        menu.add(GameScore);
        menuBar.add(menu);

        this.setMenuBar(menuBar);
        this.setTitle("My Game GUI");

        if (this.mode == 0) {
            addMouseListener(this);
        }

        this.setVisible(true);
    }

    /**
     * This function scale game elements given location into the GUI frame.
     *
     * @param data   - x/y coordinate data
     * @param r_min  - minimum x/y original range
     * @param r_max- maximum x/y original range
     * @param t_min  -minimum x/y target range- GUI window
     * @param t_max- maximum x/y target range- GUI window
     * @return
     */
    private double scale(double data, double r_min, double r_max, double t_min, double t_max) {
        double res = ((data - r_min) / (r_max - r_min)) * (t_max - t_min) + t_min;
        return res;
    }

    /**
     * Paint GUI .
     * BufferedImage, Graphics2D- these variables are used for double buffering that disable frame flickering.
     *
     * @param g
     */
    public void paint(Graphics g) {
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setBackground(new Color(240, 240, 240));
        g2d.clearRect(0, 0, WIDTH, HEIGHT);
        paintGraph(g2d);
        paintFruits(g2d);
        paintRobots(g2d);
        Graphics2D g2dComponent = (Graphics2D) g;
        g2dComponent.drawImage(bufferedImage, null, 0, 0);
    }

    /**
     * This function draws the graph elements- nodes, edges, and the direction of the edges.
     *
     * @param g
     */
    private void paintGraph(Graphics2D g) {
        double[] x_toScale = my_game.getScale_x();
        double[] y_toScale = my_game.getScale_y();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Time left: " + (my_game.getMy_game().timeToEnd() / 1000), 800, 80);
        for (node_data node : this.my_game.getGraph().getV()) {

            int node_x = (int) scale(node.getLocation().x(), x_toScale[0], x_toScale[1], 50, WIDTH - 50);
            int node_y = (int) scale(node.getLocation().y(), y_toScale[0], y_toScale[1], 200, HEIGHT - 200);

            g.setColor(Color.BLACK);
            g.fillOval(node_x - 5, node_y - 5, 10, 10);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString(node.getKey() + "", node_x + 5, node_y + 5);

            if (my_game.getGraph().getE(node.getKey()) != null) {
                for (edge_data edge : my_game.getGraph().getE(node.getKey())) {

                    node_data src = my_game.getGraph().getNode(edge.getSrc());
                    node_data dest = my_game.getGraph().getNode(edge.getDest());

                    int src_x = (int) scale(src.getLocation().x(), x_toScale[0], x_toScale[1], 50, WIDTH - 50);
                    int src_y = (int) scale(src.getLocation().y(), y_toScale[0], y_toScale[1], 200, HEIGHT - 200);
                    int dest_x = (int) scale(dest.getLocation().x(), x_toScale[0], x_toScale[1], 50, WIDTH - 50);
                    int dest_y = (int) scale(dest.getLocation().y(), y_toScale[0], y_toScale[1], 200, HEIGHT - 200);

                    g.setColor(Color.BLACK);
                    g.drawLine(src_x, src_y, dest_x, dest_y);


                    g.setColor(Color.RED);
                    int dir_x = (((((((src_x + dest_x) / 2) + dest_x) / 2) + dest_x) / 2) + dest_x) / 2;
                    int dir_y = (((((((src_y + dest_y) / 2) + dest_y) / 2) + dest_y) / 2) + dest_y) / 2;

                    g.fillOval(dir_x - 5, dir_y - 5, 10, 10);

                }
            }

        }
    }

    /**
     * This function draws the fruits
     *
     * @param g
     */
    private void paintFruits(Graphics2D g) {
        synchronized (my_game.getFruits()) {
            double[] x_toScale = my_game.getScale_x();
            double[] y_toScale = my_game.getScale_y();
            for (Fruit fruit : my_game.getFruits()) {
                g.setColor(Color.ORANGE);
                if (fruit.getType() == 1) {
                    g.setColor(Color.GREEN);
                }
                int fruit_x = (int) scale(fruit.getLocation().x(), x_toScale[0], x_toScale[1], 50, WIDTH - 50);
                int fruit_y = (int) scale(fruit.getLocation().y(), y_toScale[0], y_toScale[1], 200, HEIGHT - 200);

                g.fillOval(fruit_x - 7, fruit_y - 7, 15, 15);
                g.setColor(Color.BLACK);
                g.drawString(fruit.getValue() + "", fruit_x + 10, fruit_y + 10);
            }
        }
    }

    /**
     * This function draws the robots
     *
     * @param g
     */
    private void paintRobots(Graphics2D g) {
        double[] x_toScale = my_game.getScale_x();
        double[] y_toScale = my_game.getScale_y();
        List<String> rob = my_game.getMy_game().getRobots();
        for (int i = 1; i <= rob.size(); i++) {
            g.drawString(rob.get(i - 1), 150, 100 + (20 * i));
        }
        for (int i = 0; i < my_game.getRobot_size(); i++) {
            Robot robot = my_game.getRobots().get(i);
            int robot_x = (int) scale(robot.getLocation().x(), x_toScale[0], x_toScale[1], 50, WIDTH - 50);
            int robot_y = (int) scale(robot.getLocation().y(), y_toScale[0], y_toScale[1], 200, HEIGHT - 200);
            g.setColor(Color.GRAY);
            g.drawOval(robot_x - 15, robot_y - 15, 30, 30);
            g.setFont(new Font("Arial", Font.BOLD, 15));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String str=e.getActionCommand();

        switch (str) {
            case "User rank for this stage":
                showStageBestResults(Main_Thread.stage);
                break;

            case "User rank for all stages":
                int id = 999;

                try {
                    String id_str = JOptionPane.showInputDialog(this, "Please insert id");
                    id = Integer.parseInt(id_str);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                showUserResults(id);
                break;

            case "Game rank for all players":
                showGameResults();
                break;
            default:
        }
    }

    /**
     * This function is relevant for manual mode, when the user click on the screen a MouseEven is caught.
     * The user insert for each robot the next destination node.
     * Robot next node destination allocation is allowed if and only if the robot destination is -1,
     * Robot destination equal to -1 in two options:
     * 1. The robot reached the previous destination allocation
     * 2. In the start of the game all robots destinations are set to -1.
     * Only neighbors nodes, source node and destination share an edge, are valid for robot movement.
     * New destination is set by the chooseNextEdge function from the game server.
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < my_game.getRobot_size(); i++) {
            Robot robot = my_game.getRobots().get(i);
            if (robot.getDest() == -1) {
                String dst_str = JOptionPane.showInputDialog(this, "Please insert robot " + robot.getId() + " next node destination");
                try {
                    int dest = Integer.parseInt(dst_str);
                    this.my_game.getMy_game().chooseNextEdge(robot.getId(), dest);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void showStageBestResults(int stage) {
        String[] columnNames = { "UserID", "LevelID", "score", "moves", "time" };
        JFrame frame1 = new JFrame("Stage "+stage+" Results  " );
        frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame1.setLayout(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel();
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }
        LinkedHashMap<String, String> tp = DataBase.stageBestResults(stage);
        for (Map.Entry<String, String> entry : tp.entrySet()) {
            tableModel.addRow(entry.getValue().split(","));
        }

        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame1.add(scroll);
        frame1.setSize(400, 300);
        frame1.setVisible(true);
    }

    private void showUserResults(int id) {
        String[] columnNames = { "UserID", "LevelID", "score", "moves", "time" };
        JFrame frame1 = new JFrame("My Game Results, Games Played: " + DataBase.gamesPlayed(id));
        frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame1.setLayout(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel();
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }
        TreeMap<Integer, String> tp = DataBase.myBestResults(id);
        for (Map.Entry<Integer, String> entry : tp.entrySet()) {
            tableModel.addRow(entry.getValue().split(","));
        }

        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame1.add(scroll);
        frame1.setSize(400, 300);
        frame1.setVisible(true);
    }

    private void showGameResults() {
        String[] columnNames = { "UserID", "LevelID", "score", "moves", "time" };
        JFrame frame1 = new JFrame("Game Results");
        frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame1.setLayout(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel();
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }
        TreeMap<String, String> tp = DataBase.gameBestResults();
        for (Map.Entry<String, String> entry : tp.entrySet()) {
            tableModel.addRow(entry.getValue().split(","));
        }

        JTable table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame1.add(scroll);
        frame1.setSize(400, 300);
        frame1.setVisible(true);
    }

}
