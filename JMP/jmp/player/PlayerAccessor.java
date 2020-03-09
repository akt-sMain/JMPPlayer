package jmp.player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAccessor {

    private List<Player> registerPlayer = new ArrayList<Player>();
    private Player currentPlayer = null;

    private static PlayerAccessor instance = new PlayerAccessor();

    private PlayerAccessor() {
    }

    public static PlayerAccessor getInstance() {
        return instance;
    }

    public boolean open() {
        boolean ret = true;
        for (Player p : registerPlayer) {
            ret = p.open();
            if (ret == false) {
                break;
            }
        }
        return ret;
    }

    public boolean close() {
        boolean ret = true;
        stopAllPlayer();
        for (Player p : registerPlayer) {
            boolean tmp = ret;
            ret = p.close();
            if (tmp == false) {
                ret = false;
            }
        }
        return ret;
    }

    public List<Player> getPlayers() {
        return registerPlayer;
    }

    public void stopAllPlayer() {
        for (Player p : registerPlayer) {
            p.stop();
        }
    }

    public void register(Player player) {
        if (registerPlayer.contains(player) == false) {
            registerPlayer.add(player);
        }
    }

    public void remove(Player player) {
        if (registerPlayer.contains(player) == true) {
            registerPlayer.remove(player);
        }
    }

    public boolean change(String extension) {
        for (Player player : registerPlayer) {
            if (player.isSupportedExtension(extension) == true) {
                change(player);
                return true;
            }
        }
        return false;
    }

    public void change(Player player) {
        currentPlayer = player;
    }

    public Player getCurrent() {
        return currentPlayer;
    }

    public boolean isSupportedExtension(String extension) {
        for (Player player : registerPlayer) {
            if (player.isSupportedExtension(extension) == true) {
                return true;
            }
        }
        return false;
    }

    public boolean isValid() {
        Player player = getCurrent();
        if (player == null) {
            return false;
        }
        return player.isValid();
    }

}
