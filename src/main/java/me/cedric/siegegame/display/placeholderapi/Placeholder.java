package me.cedric.siegegame.display.placeholderapi;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;

public enum Placeholder {

    TEAM_NAME("team_name", (siegeGame, gamePlayer, s) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getName();
    }),

    TEAM_COLOR("team_color", (siegeGame, gamePlayer, s) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getColor().asRGB() + "";
    }),

    TEAM_POINTS("team_points", (siegeGame, gamePlayer, s) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getPoints() + "";
    }),

    TEAM_YOU_OR_EMPTY("team_you_or_empty", (siegeGame, gamePlayer, s) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        if (team != null && gamePlayer.hasTeam() &&
                team.getConfigKey().equalsIgnoreCase(gamePlayer.getTeam().getConfigKey()))
            return "YOU";
        return "";
    });

    private final String param;
    private final PlaceholderAction<SiegeGame, GamePlayer, String, String> action;

    Placeholder(String param, PlaceholderAction<SiegeGame, GamePlayer, String, String> action) {
        this.param = param;
        this.action = action;
    }

    public String getParameter() {
        return param;
    }

    public PlaceholderAction<SiegeGame, GamePlayer, String, String> getAction() {
        return action;
    }

    private static Team getTeam(SiegeGame plugin, GamePlayer gamePlayer, String configKey) {
        if (configKey == null || configKey.isEmpty())
            return gamePlayer.hasTeam() ? gamePlayer.getTeam() : null;

        WorldGame worldGame = plugin.getGameManager().getWorldGame(gamePlayer.getBukkitPlayer().getWorld());

        if (worldGame == null)
            return null;

        return worldGame.getTeam(configKey);
    }
}
