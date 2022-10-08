package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class SharpnessVI extends SuperItem {

    protected SharpnessVI(SiegeGame plugin, String key, WorldGame worldGame) {
        super(plugin, key, worldGame);
    }

    @Override
    protected void display(GamePlayer owner) {
        Player player = owner.getBukkitPlayer();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have obtained a " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SUPER ITEM" + ChatColor.LIGHT_PURPLE +"!" +
                " Enjoy your " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sharpness VI");
    }

    @Override
    protected void removeDisplay(GamePlayer owner) {
        owner.getBukkitPlayer().getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    @Override
    protected ItemStack itemStack() {
        return new ItemBuilder(Material.NETHERITE_SWORD)
                .addEnchantment(Enchantment.DAMAGE_ALL, 6)
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addEnchantment(Enchantment.FIRE_ASPECT, 2)
                .setUnbreakable(true)
                .addLoreLine(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SUPER ITEM")
                .build();
    }

    @Override
    protected void initialize(SiegeGame plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getDisplayName() {
        return "Sharpness VI";
    }
}
