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
        if (hindariMusuh(playerAction, tujuan)){
            System.out.println("Menghindari musuh");
        }else 
        if (kejarMusuh(playerAction)){
            System.out.println("Kejar musuh");
        }
        else
        if (ambilSuperFood(playerAction, tujuan)){
            System.out.println("Ambil superfood");
        } else
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

    private boolean hindariMusuh(PlayerAction playerAction, Position tujuan){
        var listPlayer = gameState.getPlayerGameObjects();
        List<GameObject> listLawanDihindari = new ArrayList<GameObject>();
        int maxSpeed = -1;
        for (int i = 0;i < listPlayer.size();i++){
            var lawan = listPlayer.get(i);
            if (lawan.getId() != bot.getId()
                && isInRadius(lawan, bot.getPosition().getX(), bot.getPosition().getY(), Math.max(50,bot.getSize()+20))
                && lawan.getSize() > bot.getSize()){
                    listLawanDihindari.add(lawan);
                    maxSpeed = Math.max(maxSpeed,lawan.getSpeed());
                }
        }
        if (!listLawanDihindari.isEmpty()){
            if (bot.getSpeed() < maxSpeed && bot.getSize() > 10 && ((bot.effects & Effects.AFTERBURNER.value)==0)){
                playerAction.action = PlayerActions.STARTAFTERBURNER;
            } else {
                playerAction.action = PlayerActions.FORWARD;
            }
            int meanArahLawan = 0;
            for (int i = 0;i < listLawanDihindari.size();i++){
                meanArahLawan += getHeadingBetween(listLawanDihindari.get(i));
            }
            meanArahLawan /= listLawanDihindari.size();
            playerAction.heading = (meanArahLawan+90)%360;
            return true;
        } else {
            return false;
        }
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
            while (i < foodList.size()){
                double radPlayer = getDistanceBetween(foodList.get(i), 0, 0) + bot.getSize();
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

    private boolean ambilSuperFood(PlayerAction playerAction, Position tujuan){
        if (!gameState.getGameObjects().isEmpty()) {
            var superFoodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERFOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            int i = 0;
            // ambil superfood yang tidak menyebabkan player menyentuh batas arena dan di dalam radius player
            while (i < superFoodList.size()){
                if (!isInRadius(superFoodList.get(i), bot.getPosition().x, bot.getPosition().y, bot.getSize()*3)) break;
                double radPlayer = getDistanceBetween(superFoodList.get(i), 0, 0) + bot.getSize();
                if (radPlayer <= getGameState().world.radius) {
                    playerAction.heading = getHeadingBetween(superFoodList.get(i));
                    playerAction.action = PlayerActions.FORWARD;
                    tujuan.x = superFoodList.get(i).position.x;
                    tujuan.y = superFoodList.get(i).position.y;
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

    private double getDistanceBetween(GameObject object1, int x, int y) {
        var triangleX = Math.abs(object1.getPosition().x - x);
        var triangleY = Math.abs(object1.getPosition().y - y);
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

    private boolean isInRadius(GameObject a, int x, int y, double r){
        double length = getDistanceBetween(a, x, y);
        return (length < a.getSize()+r);
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

    private boolean isPathFreeOfObstacle(GameObject a, GameObject b, GameObject obstacle){
        List<GameObject> gameobj = gameState.getGameObjects();
        for (int i = 0;i < gameobj.size();i++){
            if (isObstacle(gameobj.get(i)) &&
                 willIntersect(bot, gameobj.get(i), b.getPosition())){
                return false;
            }
        }
        return true;
    }

    private boolean kejarMusuh(PlayerAction aksi)
    {
        var musuhList = gameState.getPlayerGameObjects()
            .stream().filter(item->item.getId()!=bot.getId())
            .sorted(Comparator.comparing(item->getDistanceBetween(bot, item)))
            .collect(Collectors.toList());
        
        int i = 0;
        while(i<musuhList.size())
        {
            double sizemusuh = musuhList.get(i).getSize();
            if(isInRadius(musuhList.get(i), bot.getPosition().x, bot.getPosition().y, bot.getSize()*3)&&bot.getSize()+6>sizemusuh)
            {
                aksi.heading = getHeadingBetween(musuhList.get(i));
                aksi.action = PlayerActions.FORWARD;
                return true;
            }
            i++;
        }
        return false;
        
    }
}
