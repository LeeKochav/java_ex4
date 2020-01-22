package gameClient;

import algorithms.Graph_Algo;
import dataStructure.node_data;
import sun.applet.Main;
import utils.Point3D;

import java.util.Hashtable;
import java.util.List;

public class Algo_Game {


    private Game my_game;
    private Graph_Algo algo_g;
    private Hashtable<Integer,List<node_data>> robots_paths;
    private Hashtable<Integer,Fruit> fruits_status;
    private boolean isOnSrc = false;

    /**
     * Constructor initialize Algo_Game attributes
     * @param game
     */
    public Algo_Game(Game game) {
        my_game = game;
        robots_paths = new Hashtable<>();
        fruits_status=new Hashtable<>();
        algo_g = new Graph_Algo(my_game.getGraph());
        initRobotPath();
    }

    /**
     * This method allocates next robot's destination
     * If the robot next node equals fruit node destination, invoke the use of the equation that making sure the robot will collect the fruit.
     * Equation : dt=(n*w)/s
     * n= (distance fruit source node && fruit actual location)/(distance fruit source node && fruit destination node)
     * w= fruit's edge weight
     * s= robot's speed
     */
    public void MoveRobots()  {
        int dest = -1;
        for (int i = 0; i < my_game.getRobot_size(); i++) {
            Robot robot = my_game.getRobots().get(i);
            if (robot.getDest() == -1) {
                dest = nextNode(i);
                Fruit fruit_dest=fruits_status.get(robot.getId());

                 if(robot.getSrc() == fruit_dest.getEdge().getSrc()&&dest==fruit_dest.getEdge().getDest()) //invoke formula
                {
                    node_data src = my_game.getGraph().getNode(fruit_dest.getEdge().getSrc());
                    node_data dst = my_game.getGraph().getNode(fruit_dest.getEdge().getDest());
                    double dist_src_fruit= src.getLocation().distance2D(fruit_dest.getLocation());
                    double dist_src_dest = src.getLocation().distance2D(dst.getLocation());
                    double n = dist_src_fruit/dist_src_dest;
                    double w = fruit_dest.getEdge().getWeight();
                    double s = robot.getSpeed();

                    int new_dt = (int)((w*n/s)*100);
                    Main_Thread.dt= new_dt;
                } else {
                    Main_Thread.dt = 100;
                }
                my_game.getMy_game().chooseNextEdge(i, dest);

            }
        }
    }

    /**
     * Set the next node of the robot according to the list that holds the path to the targeted fruit.
     * When the robot moves to the next destination in the list, this destination is removed.
     * When the path list is empty it indicated that the robot reached the fruit and need to reallocate a new fruit to the robot.
     * @param rid
     * @return
     */
    private int nextNode(int rid) {
        if (!my_game.getMy_game().isRunning()) {
            return -1;
        }
        Robot robot = my_game.getRobots().get(rid);
        List<node_data> tmp = robots_paths.get(rid);
        if (tmp.isEmpty()) {
            fruits_status.remove(rid);
            synchronized (my_game.getFruits()) {
                if (my_game.getFruits().size() > 0) {
                    Fruit fruit = findClosestFruit(robot);
                    tmp = algo_g.shortestPath(robot.getSrc(),fruit.getEdge().getSrc());
                    node_data dest = my_game.getGraph().getNode(fruit.getEdge().getDest());
                    tmp.add(dest);
                    robots_paths.put(robot.getId(),tmp);
                    fruits_status.put(rid,fruit);
                }
            }
        }

        node_data n = tmp.get(0);
        tmp.remove(0);
        return n.getKey();

    }

    /**
     * Find the nearest fruit for each robot.
     * Fruit_status indicates if the fruit is available to be the target of the robot.
     * @param robot
     * @return
     */
    private Fruit findClosestFruit(Robot robot) {
        double min_dist = Double.MAX_VALUE;
        Fruit ans = null;
        for (int i = 0; i < my_game.getFruits().size(); i++) {
            Fruit fruit = my_game.getFruits().get(i);
            if(fruits_status.values().contains(fruit)) {
                continue;
            }
                double dist = algo_g.shortestPathDist(robot.getSrc(), fruit.getEdge().getSrc());
                if (dist < min_dist) {
                    min_dist = dist;
                    ans = fruit;
                }

            }

            return ans;
        }
    /**
     * First init of the hashtable that hold each robot it's path to the targeted fruit.
     */
    public void initRobotPath() {
        for (int i = 0; i < my_game.getRobot_size(); i++) {
            Robot robot = my_game.getRobots().get(i);
            Fruit fruit = my_game.getFruits().get(i);
            List<node_data> tmp = algo_g.shortestPath(robot.getSrc(), fruit.getEdge().getDest());
            robots_paths.put(robot.getId(), tmp);
            fruits_status.put(robot.getId(),fruit);
        }
    }
}