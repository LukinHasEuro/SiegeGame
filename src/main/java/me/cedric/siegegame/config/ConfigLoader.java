package me.cedric.siegegame.config;

import com.palmergames.bukkit.towny.TownyAPI;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.teams.TeamImpl;
import me.cedric.siegegame.world.WorldGame;
import me.cedric.siegegame.world.WorldGameImpl;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.common.config.ConfigSection;
import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.InvalidConfigurationException;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigLoader {

    private final SiegeGame plugin;

    private static final String YML_PATH_DIVIDER = ".";

    private FileConfig mapsYml;
    private static final String MAPS_SECTION_KEY = "maps";
    private static final String MAPS_SECTION_WORLD_NAME_KEY = "worldname";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_KEY = "defaultspawn";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_X = "x";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Y = "y";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Z = "z";
    private static final String MAPS_SECTION_MAP_MAPBORDER_KEY = "worldborder";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X1_KEY = "x1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y1_KEY = "y1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z1_KEY = "z1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X2_KEY = "x2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y2_KEY = "y2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z2_KEY = "z2";
    private static final String MAPS_SECTION_WORLD_TEAMS_KEY = "teams";
    private static final String MAPS_SECTION_WORLD_TEAMS_NAME = "name";
    private static final String MAPS_SECTION_WORLD_TEAMS_COLOR = "color";
    private static final String MAPS_SECTION_WORLD_TEAMS_TOWN = "town";

    private FileConfig shopYml;
    private static final String SHOP_SECTION_KEY = "shop";
    private static final String SHOP_SECTION_SHOP_NAME_KEY = "shop-name";
    private static final String SHOP_SECTION_MATERIAL_KEY = "material";
    private static final String SHOP_SECTION_SLOT_KEY = "slot";
    private static final String SHOP_SECTION_PRICE_KEY = "price";
    private static final String SHOP_SECTION_DISPLAY_NAME_KEY = "display-name";
    private static final String SHOP_SECTION_LORE_KEY = "lore";
    private static final String SHOP_SECTION_ENCHANTMENTS_KEY = "enchantments";
    private static final String SHOP_SECTION_HIDE_ITEM_FLAGS_KEY = "item-flags";

    private HashMap<String, String> lateLoadWorlds = new HashMap<>();

    public ConfigLoader(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void initializeAndLoad() {

        try {
            setupConfig();
        } catch (Exception x) {
            x.printStackTrace();
            return;
        }

        if (!mapsYml.isConfigurationSection(MAPS_SECTION_KEY) || !shopYml.isConfigurationSection(SHOP_SECTION_KEY)) {
            cryAndDisable();
            return;
        }

        loadMaps();
        loadShop();
        plugin.getLogger().info("Config loaded.");
    }

    private void loadMaps() {
        for (String mapKey : mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false)) {
            String worldName = mapsYml.getString(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_NAME_KEY);

            if (worldName == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world name for map key " + mapKey + " - Skipping!");
                continue;
            }

            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world object for map key " + mapKey + " - Skipping!");
                lateLoadWorlds.put(worldName, mapKey);
                // Iris loads its worlds late. Let's add them to a list and listen to WorldLoadEvent - Iris fires it when loading a world
                continue;
            }

            loadWorld(world, mapKey);
        }

        plugin.getLogger().info("Maps loaded.");
    }

    public void loadWorld(World world, String mapKey) {
        int x = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_X);
        int y = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Y);
        int z = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Z);

        int x1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_X1_KEY);
        int y1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Y1_KEY);
        int z1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Z1_KEY);

        int x2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_X2_KEY);
        int y2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Y2_KEY);
        int z2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Z2_KEY);

        Vector corner1Vector = new Vector(x1, y1, z1);
        Vector corner2Vector = new Vector(x2, y2, z2);

        Border border = new Border(new BoundingBox(world, corner1Vector, corner2Vector));

        WorldGameImpl worldGame = new WorldGameImpl(mapKey, world, border, new Location(world, x, y, z));
        ConfigSection teamsSection = mapsYml.getConfigurationSection(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_KEY);
        Set<Team> teams = loadTeams(worldGame, teamsSection);
        worldGame.addTeams(teams);

        plugin.getGameManager().addWorld(worldGame);

        if (lateLoadWorlds.containsKey(world.getName()))
            removeLateWorld(world.getName());
    }

    private void loadShop() {
        for (String key : shopYml.getConfigurationSection(SHOP_SECTION_KEY).getKeys(false)) {
            String material = shopYml.getString(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_MATERIAL_KEY);
            int slot = shopYml.getInt(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_SLOT_KEY);
            int price = shopYml.getInt(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_PRICE_KEY);
            String displayName = shopYml.getString(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_DISPLAY_NAME_KEY);
            List<String> lore = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_LORE_KEY);
            List<String> itemFlags = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_HIDE_ITEM_FLAGS_KEY);
            List<String> enchantments = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_ENCHANTMENTS_KEY);

            lore.forEach(s -> s = ChatColor.translateAlternateColorCodes('&', s));

            ItemBuilder itemBuilder = new ItemBuilder(EMaterial.valueOf(material.toUpperCase()))
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
                    .setLore(lore);

            for (String flag : itemFlags) {
                itemBuilder.addFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }

            for (String enchantment : enchantments) {
                String[] s = enchantment.split(";");
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s[0]));
                int level = Integer.parseInt(s[1]);
                itemBuilder.addEnchantment(ench, level);
            }
            itemBuilder.transformNBT(nbtItem -> nbtItem.addCompound("siege-game").setInteger("price", price));

            ShopItem button = new ShopItem(itemBuilder.build(), price, slot);
            plugin.getShopGUI().addItem(button);
        }

        String shopName = shopYml.getString(SHOP_SECTION_SHOP_NAME_KEY);
        plugin.getShopGUI().setGUIName(ChatColor.translateAlternateColorCodes('&', shopName));

        plugin.getLogger().info("Shop loaded.");
    }

    private Set<Team> loadTeams(WorldGame worldGame, ConfigSection section) {
        Set<Team> teams = new HashSet<>();
        String currentPath = section.getCurrentPath();
        for (String key : section.getKeys(false)) {
            String name = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_NAME);
            String hexColor = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_COLOR);
            String townName = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_TOWN);

            TeamImpl team = new TeamImpl(worldGame, color(hexColor), TownyAPI.getInstance().getTown(townName), name, key);
            teams.add(team);
        }

        return teams;
    }

    private Color color(String hexColor) {
        int r = Integer.valueOf(hexColor.substring(1, 3), 16);
        int g = Integer.valueOf(hexColor.substring(3, 5), 16);
        int b = Integer.valueOf(hexColor.substring(5, 7), 16);

        return Color.fromRGB(r, g, b);
    }

    private void cryAndDisable() {
        plugin.getLogger().severe("There was a problem loading the config. Disabling...");
        plugin.disablePlugin();
    }

    private void setupConfig() throws IOException, InvalidConfigurationException {
        File mapFile = new File(plugin.getDataFolder(), "maps.yml");
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");

        if (!mapFile.exists())
            plugin.saveResource("maps.yml", false);
        if (!shopFile.exists())
            plugin.saveResource("shop.yml", false);

        mapsYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "maps.yml"));
        shopYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "shop.yml"));

        mapsYml.mergeDefaults();
        shopYml.mergeDefaults();
    }

    public Set<String> getLateLoadWorldNames() {
        return lateLoadWorlds.keySet();
    }

    public String getLateWorldMapKey(String s) {
        return lateLoadWorlds.get(s);
    }

    public void removeLateWorld(String worldName) {
        lateLoadWorlds.remove(worldName);
    }
}














