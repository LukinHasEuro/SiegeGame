package me.cedric.siegegame;

import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.World;

import java.util.*;

public final class GameManager {

    private final Set<WorldGame> worlds = new HashSet<>();
    private final Queue<WorldGame> worldQueue = new LinkedList<>();
    private final SiegeGame plugin;
    private WorldGame currentMap;
    private WorldGame lastMap = null;

    public GameManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void removeWorld(WorldGame worldGame) {
        worlds.add(worldGame);
    }

    public void addWorld(WorldGame worldGame) {
        worlds.add(worldGame);
        worldQueue.add(worldGame);
    }

    public WorldGame getNextMap() {
        return worldQueue.peek();
    }

    public WorldGame getCurrentMap() {
        return currentMap;
    }

    public void startNextMap() {
        WorldGame worldGame = worldQueue.poll();
        worldGame.getBukkitWorld().setSpawnLocation(worldGame.getDefaultSpawnPoint());
        assignTeams(worldGame);

        for (Team team : worldGame.getTeams()) {
            for (GamePlayer gamePlayer : team.getPlayers()) {
                if (team.getTeamTown().getSpawnOrNull() == null) {
                    gamePlayer.getBukkitPlayer().teleport(worldGame.getDefaultSpawnPoint());
                    System.out.println("TEAM " + team.getTeamTown().getName() + " DOES NOT HAVE A TOWN SPAWN. TELEPORTING TO WORLD SPAWN!");
                    continue;
                }

                gamePlayer.getBukkitPlayer().teleport(team.getTeamTown().getSpawnOrNull());
            }
        }

        this.lastMap = currentMap;
        this.currentMap = worldGame;
        worldQueue.add(lastMap);
    }

    public void assignTeams(WorldGame worldGame, List<GamePlayer> players, PlayerManager manager) {
        Random r = new Random();

        List<GamePlayer> list = new ArrayList<>(players);
        Set<Team> teams = worldGame.getTeams();

        while (list.size() != 0) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(0, list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);

                team.addPlayer(player);
                list.remove(chosenPlayer);
            }
        }
    }

    public void assignTeams(WorldGame worldGame) {
        assignTeams(worldGame, plugin.getPlayerManager().getPlayers(), plugin.getPlayerManager());
    }

    public WorldGame getLastMap() {
        return lastMap;
    }

    public WorldGame getWorldGame(World world) {
        return worlds.stream().filter(worldGame -> worldGame.getBukkitWorld().equals(world)).findFirst().orElse(null);
    }
}
