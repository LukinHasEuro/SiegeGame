package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PlayerManager {

    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final SiegeGame plugin;

    public PlayerManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, new GamePlayer(uuid, plugin));
    }

    public void removePlayer(UUID uuid) {
        GamePlayer gamePlayer = players.get(uuid);
        if (gamePlayer.hasTeam())
            gamePlayer.getTeam().removePlayer(gamePlayer);
        gamePlayer.getBorderHandler().clear();
        players.remove(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public List<GamePlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public void clear() {
        players.clear();
    }

}
