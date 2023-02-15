package Services;

import Enums.*;
import Models.*;
import Models.Vector;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    
    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        Position tujuan = new Position();
        if (ambilMakanan(playerAction, tujuan)){
            System.out.println("Ambil makanan");
        } else {
            System.out.println("Random");
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = new Random().nextInt(360);

        }
        this.playerAction = playerAction;
        System.out.println(playerAction.action);
        System.out.println(playerAction.heading);
        System.out.println();
    }

    private boolean ambilMakanan(PlayerAction playerAction, Position tujuan){
        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            int i = 0;
            // ambil makanan yang tidak menyebabkan player menyentuh batas arena
            while (true && i < foodList.size()){
                double radPlayer = getDistanceBetween(foodList.get(i), new Position(0, 0)) + bot.getSize();
                if (radPlayer <= getGameState().world.radius){
                    playerAction.heading = getHeadingBetween(foodList.get(i));
                    playerAction.action = PlayerActions.FORWARD;
                    tujuan.x = foodList.get(i).position.x;
                    tujuan.y = foodList.get(i).position.y;
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private double getDistanceBetween(GameObject object1, Position p) {
        var triangleX = Math.abs(object1.getPosition().x - p.getX());
        var triangleY = Math.abs(object1.getPosition().y - p.getY());
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private boolean willIntersect(GameObject a, GameObject b, Position dest){
        int B = 1;
        int A = -(dest.y-a.getPosition().y)/(dest.x-a.getPosition().x);
        int C = -(a.getPosition().y*A+a.getPosition().x*B);
        double dist = Math.abs(A*b.getPosition().x + B*b.getPosition().y+C)/Math.sqrt(A*A+B*B);


        return Vector.sudut(a.getPosition(),dest,b.getPosition()) <= 90 && 
                Vector.sudut(dest, a.getPosition(), b.getPosition()) <= 90 &&
                a.size+b.size < dist;
    }

    private boolean isObstacle(GameObject a){
        ObjectTypes objT = a.getGameObjectType();
        return objT == ObjectTypes.ASTEROIDFIELD || objT == ObjectTypes.GASCLOUD || objT == ObjectTypes.WORMHOLE || objT == ObjectTypes.PLAYER || objT == ObjectTypes.PLAYER;
    }

    private boolean isFreeObstacle(GameObject a, GameObject b, GameObject obstacle){
        List<GameObject> gameobj = gameState.getGameObjects();
        for (int i = 0;i < gameobj.size();i++){
            if (isObstacle(gameobj.get(i)) &&
                 willIntersect(bot, gameobj.get(i), b.getPosition())){
                return false;
            }
        }
        return true;
    }
}
