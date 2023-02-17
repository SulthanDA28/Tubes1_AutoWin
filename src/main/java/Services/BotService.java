package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import javax.swing.UIDefaults.ProxyLazyValue;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private GameObject firedTeleporter;
    private int wait = 0;

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
        if (gameState.getWorld().radius == null) {
            playerAction.action = PlayerActions.STOP;
            System.out.println("World is null\n");
            return;
        }

        if (firedTeleporter == null){
            var listTp = gameState.getGameObjects()
                        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                        .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                        .collect(Collectors.toList());
            for (int i = 0;i < listTp.size();i++){
                GameObject obj = listTp.get(i);
                if (obj.getGameObjectType() == ObjectTypes.TELEPORTER && 
                    isInRadius(obj, bot.position, bot.size+50)){
                        firedTeleporter = obj;
                        break;
                    }
            }
        } else {
            System.out.println(String.format("TELEPORTETET %d %d",firedTeleporter.position.x,firedTeleporter.position.y));
        }

        boolean isChased = false, isKejarSuper = false;
        Position tujuan = new Position();

        if (ledakkanSupernova(playerAction)){
            System.out.println("Ledakkan supernova");
        } else
        if (hindariTorpedo(playerAction)){
            System.out.println("Menghindari torpedo");
            isChased = true;
        } else
        if (hindariMusuh(playerAction, tujuan)){
            System.out.println("Menghindari musuh");
            isChased = true;
        
        } else
        if (tembakSupernova(playerAction)){
            System.out.println("Tembak supernova");
        } else 
        if (kejarMusuh(playerAction)){
            System.out.println("Kejar musuh");
        } else
        if (ambilSupernova(playerAction)){
            System.out.println("Kejar supernova");
            isKejarSuper = true;
        } else
        if (ambilSuperFood(playerAction, tujuan)){
           System.out.println("Ambil superfood");
        } else
        if (ambilMakanan(playerAction, tujuan)){
            System.out.println("Ambil makanan");
        } else
        if (tembakTorpedo(playerAction)){
            System.out.println("Serang lawan");
        } else {
            System.out.println("Random");
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = new Random().nextInt(360);

        }
        
        double radPlayer = getDistanceBetween(bot, 0, 0) + bot.getSize();
        if (isInEffect(bot, Effects.AFTERBURNER) && (bot.getSize() < 10 || !isChased || !isKejarSuper)){
            System.out.println("Mematikan afterburner");
            playerAction.action = PlayerActions.STOPAFTERBURNER;
        } else
        if (radPlayer >= getGameState().world.radius){
            playerAction.heading = getHeadingBetween(0, 0);
            playerAction.action = PlayerActions.FORWARD;
            System.out.println("Melewati batas peta, kembali ke pusat...");
        } else
        if (!isChased){
            Position newPosition = nextPosition(playerAction);
            var listObstacle = gameState.getGameObjects()
                .stream().filter(item -> isObstacle(item))
                .collect(Collectors.toList());
            boolean sudah = false;
            for (int i = 0;i < listObstacle.size();i++){
                int headingToObs = getHeadingBetween(listObstacle.get(i));
                if (isInRadius(listObstacle.get(i), bot.getPosition(), bot.getSize())){
                    System.out.println("Keluar dari obstacle");
                    playerAction.heading = (headingToObs+180)%360;
                    sudah = true;
                    break;
                }
            }
            for (int i = 0;i < listObstacle.size() && !sudah;i++){
                int headingToObs = getHeadingBetween(listObstacle.get(i));
                if (isInRadius(listObstacle.get(i), newPosition, bot.getSize())){
                    System.out.println(headingToObs);
                    System.out.println("CCW");
                    playerAction.heading = (headingToObs+90)%360;
                    System.out.println("Menghindari obstacle");
                    break;
                }
            }
        }

        this.playerAction = playerAction;
        if (playerAction.action == PlayerActions.TELEPORT || true){
            System.out.println(this.playerAction.action);
            System.out.println(this.playerAction.heading);
            System.out.println();
        }
        
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
        updateTeleporter();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private void updateTeleporter() {
        if (firedTeleporter != null){
            Optional<GameObject> optionalTeleporter = gameState.getGameObjects().stream().filter(gameObject -> gameObject.id.equals(firedTeleporter.id)).findAny();
            optionalTeleporter.ifPresentOrElse(tp -> this.firedTeleporter = tp,() -> this.firedTeleporter = null);
        }  
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

    private int getHeadingBetween(int x, int y) {
        var direction = toDegrees(Math.atan2(y - bot.getPosition().y, x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private boolean isInRadius(GameObject a, Position p, double r){
        double length = getDistanceBetween(a, p.x, p.y);
        return (length <= a.getSize()+r);
    }

    private Position nextPosition(PlayerAction playerAction){
        int x = bot.getPosition().x+(int)(bot.getSpeed()*Math.cos(playerAction.heading));
        int y = bot.getPosition().y+(int)(bot.getSpeed()*Math.sin(playerAction.heading));
        return new Position(x,y);
    }

    private boolean isInEffect(GameObject obj, Effects effect){
        return (obj.effects & effect.value) > 0;
    }

    private boolean isObstacle(GameObject a){
        ObjectTypes objT = a.getGameObjectType();
        return objT == ObjectTypes.ASTEROIDFIELD || objT == ObjectTypes.GASCLOUD || objT == ObjectTypes.WORMHOLE;
    }

    private boolean hindariMusuh(PlayerAction playerAction, Position tujuan){
        var listPlayer = gameState.getPlayerGameObjects();
        List<GameObject> listLawanDihindari = new ArrayList<GameObject>();
        int maxSpeed = -1;
        for (int i = 0;i < listPlayer.size();i++){
            var lawan = listPlayer.get(i);
            // Ambil kandidat musuh yang akan dihindari, yaitu berada pada range tertentu
            if (lawan.getId() != bot.getId()
                && isInRadius(lawan, bot.getPosition(), Math.max(50,bot.getSize()*3))
                && lawan.getSize() > bot.getSize()){
                    listLawanDihindari.add(lawan);
                    maxSpeed = Math.max(maxSpeed,lawan.getSpeed());
                }
        }
        if (!listLawanDihindari.isEmpty()){
            var sorted = listLawanDihindari.stream().sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
            // tembakkan torpedo pada lawan terdekat yang tidak memiliki shield
            for (int i = 0;i < sorted.size();i++){
                if (bot.getSize()>20 && bot.torpedoSalvoCount > 0 && !isInEffect(sorted.get(i), Effects.SHIELD)){
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                    playerAction.heading = getHeadingBetween(sorted.get(i));
                    return true;
                }
            }
            // lari dari musuh, arahnya berupa berlawanan dengan rata-rata heading musuh ke pemain
            int meanArahLawan = 0;
            for (int i = 0;i < sorted.size();i++){
                meanArahLawan += getHeadingBetween(sorted.get(i));
            }
            if (bot.getSpeed() < maxSpeed && bot.getSize() > 10 && (!isInEffect(bot,Effects.AFTERBURNER))){
                // hidupkan afterburner jika speed kurang dan size mencukupi
                playerAction.action = PlayerActions.STARTAFTERBURNER;
            } else {
                playerAction.action = PlayerActions.FORWARD;
            }
            meanArahLawan /= listLawanDihindari.size();
            playerAction.heading = (meanArahLawan+135)%360;
            return true;
        } else {
            return false;
        }
    }

    private boolean hindariTorpedo(PlayerAction playerAction){
        if (!gameState.getGameObjects().isEmpty()) {
            var listTorp = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            if (!listTorp.isEmpty()){
                int i = 0;
                while (i < listTorp.size()){
                    // hindari torpedo terdekat dan mengarah ke badan pemain
                    double range = Math.abs(Math.atan(bot.getSize()/getDistanceBetween(bot, listTorp.get(i))));
                    int headingTorpToPlayer = (getHeadingBetween(listTorp.get(i))+180)%360;
                    double anglediff = (headingTorpToPlayer-listTorp.get(i).currentHeading+180+360)%360-180;
                    if (anglediff <= range && anglediff >= -range){
                        if (bot.getSize() > 40 && isInRadius(listTorp.get(i), bot.getPosition(), bot.getSize()*2)){
                            // jika mencukupi, gunakan shield
                            playerAction.action = PlayerActions.ACTIVATESHIELD;
                        } else {
                            // jika masih jauh, gunakan afterburner dan lari searah 90 derajat ccw dr heading ke torpedo
                            if (bot.getSize() >= 10 && !isInEffect(bot, Effects.AFTERBURNER)) 
                                playerAction.action = PlayerActions.STARTAFTERBURNER;
                            else 
                                playerAction.action = PlayerActions.FORWARD;
                            playerAction.heading = (getHeadingBetween(listTorp.get(i))+90)%360;
                        }
                        return true;
                    }
                    i++;
                }
            }
        }
        return false;
    }

    private boolean ambilMakanan(PlayerAction playerAction, Position tujuan){
        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            int i = 0;
            // ambil makanan terdekat yang tidak menyebabkan player menyentuh batas arena
            while (i < foodList.size()){
                double radPlayer = getDistanceBetween(foodList.get(i), 0, 0) + bot.getSize();
                if (radPlayer <= getGameState().world.radius){
                    playerAction.heading = getHeadingBetween(foodList.get(i));
                    System.out.println(playerAction.heading);
                    playerAction.action = PlayerActions.FORWARD;
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
            // ambil superfood terdekat yang tidak menyebabkan player menyentuh batas arena dan di dalam radius player
            while (i < superFoodList.size()){
                if (!isInRadius(superFoodList.get(i), bot.getPosition(), bot.getSize()*2)) break;
                double radPlayer = getDistanceBetween(superFoodList.get(i), 0, 0) + bot.getSize();
                if (radPlayer <= getGameState().world.radius) {
                    playerAction.heading = getHeadingBetween(superFoodList.get(i));
                    playerAction.action = PlayerActions.FORWARD;
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private boolean tembakTorpedo(PlayerAction player){
        var listMusuh = gameState.getPlayerGameObjects().stream()
                    .filter(item -> item.getId() != bot.getId()).sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
        for (int i = 0;i < listMusuh.size();i++){
            // tembak torpedo ke musuh terdekat yang tidak menggunakan shield
            if (!isInEffect(listMusuh.get(i),Effects.SHIELD)){
                playerAction.action = PlayerActions.FIRETORPEDOES;
                playerAction.heading = getHeadingBetween(listMusuh.get(i));
                return true;
            }
        }
        return false;
    }
    
    private boolean kejarMusuh(PlayerAction aksi)
    {
        // kejar kandidat musuh terdekat
        var musuhList = gameState.getPlayerGameObjects()
            .stream().filter(item->item.getId()!=bot.getId())
            .sorted(Comparator.comparing(item->getDistanceBetween(bot, item)))
            .collect(Collectors.toList());
        
        int i = 0;
        while(i<musuhList.size())
        {
            double sizemusuh = musuhList.get(i).getSize();
            if (bot.getSize()>sizemusuh && firedTeleporter != null && isInRadius(musuhList.get(i), firedTeleporter.getPosition(), bot.getSize())){
                // jika teleport milik pemain mengenai musuh yang lebih kecil, langsung teleport
                aksi.action = PlayerActions.TELEPORT;
                return true;
            } else
            if (bot.getSize()-15>sizemusuh && isInRadius(musuhList.get(i), bot.getPosition(), bot.getSize()*2)){
                // jika dekat dengan pemain, kejar secara manual
                    aksi.heading = getHeadingBetween(musuhList.get(i));
                    aksi.action = PlayerActions.FORWARD;
                    return true;
            } else if (firedTeleporter == null && bot.size-40-15 > sizemusuh){
                // jika jauh dan perbedaan size cukup besar serta size pemain mencukupi, tembakkan teleporter ke musuh tersebut
                aksi.action = PlayerActions.FIRETELEPORT;
                aksi.heading = getHeadingBetween(musuhList.get(i));
                return true;
            }
            i++;
        }
        return false;
        
    }

    private boolean ambilSupernova(PlayerAction aksi)
    {
        if (!gameState.getGameObjects().isEmpty()){
            var supernova = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP)
            .collect(Collectors.toList());

            if(supernova.size()>0){
                for (int i = 0;i < gameState.getGameObjects().size();i++){
                    //jika supernova berada didalam obstacle, jangan ambil
                    var obj = gameState.getGameObjects().get(i);
                    if (isObstacle(obj) && isInRadius(supernova.get(0), obj.position, obj.size)){
                        return false;
                    }
                }
                if (firedTeleporter != null && isInRadius(supernova.get(0), firedTeleporter.position, bot.getSize()*2)){
                    // gunakan teleporter jika sudah ditembakkan dan teleporter mengenai supernova
                    aksi.action = PlayerActions.TELEPORT;
                }
                if (isInRadius(bot, supernova.get(0).getPosition(), 250)){ 
                    // jika pada range tertentu, gunakan afterburner
                    if(!isInEffect(bot, Effects.AFTERBURNER)&& bot.getSize()>10){
                        aksi.action = PlayerActions.STARTAFTERBURNER;
                    } 
                    else{
                        aksi.action = PlayerActions.FORWARD;
                    }
                    
                } else {
                    // jika diluar range, gunakan teleporter jika mencukupi, jalan jika tidak
                    if (firedTeleporter == null && bot.getSize() >= 40){
                        aksi.action = PlayerActions.FIRETELEPORT;
                    } else {
                        aksi.action = PlayerActions.FORWARD;
                    }
                }
                aksi.heading = getHeadingBetween(supernova.get(0));
                return true;
            }
        }
        return false;
    }

    private boolean tembakSupernova(PlayerAction aksi){
        if (bot.superNovaAvailable > 0){
            if (wait < 5) wait++;
            else {
                // tembakkan supernova ke musuh terjauh yang ledakannya diperkirakan tidak mengenai pemain
                var listMusuh = gameState.getPlayerGameObjects().stream().filter(item -> item.getId() != bot.getId()).
                            sorted(Comparator.comparing(item->-getDistanceBetween(bot, item))).collect(Collectors.toList());
                if (!isInRadius(bot, listMusuh.get(0).position, 150)){
                    playerAction.action = PlayerActions.FIRESUPERNOVA;
                    playerAction.heading = getHeadingBetween(listMusuh.get(0));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ledakkanSupernova(PlayerAction aksi){
        var listSupernovaBomb = gameState.getGameObjects().stream().filter(item->item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
                                .collect(Collectors.toList());
        if (!listSupernovaBomb.isEmpty()){
            var bom = listSupernovaBomb.get(0);
            if (getDistanceBetween(bom, 0,0) >= 0.90*gameState.world.radius){
                // ledakkan paksa supernova jika akan keluar dari arena (untuk menghindari bug)
                aksi.action = PlayerActions.DETONATESUPERNOVA;
                return true;
            }
            var listMusuh = gameState.getPlayerGameObjects().stream().filter(item -> item.getId() != bot.getId()).
                            collect(Collectors.toList());
            for (int i = 0;i < listMusuh.size();i++){
                // ledakkan jika ledakan supernova akan mengenai musuh dan ledakan tersebut tidak mengenai pemain
                if (isInRadius(listMusuh.get(i), bom.getPosition(), 150) && !isInRadius(bot, bom.getPosition(), 150)){
                    aksi.action = PlayerActions.DETONATESUPERNOVA;
                    return true;
                }
            }

        }
        return false;
    }
}
