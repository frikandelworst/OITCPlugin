package com.frikandelworst.oneinthechamber.Listeners;


import com.frikandelworst.oneinthechamber.OneIntheChamber;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BowListener implements Listener {
    public void startCooldown(Player p){
        BukkitRunnable runnable = new BukkitRunnable() {
            private int cooldown = OneIntheChamber.getPlugin().getConfig().getInt("ArrowCooldown");
            @Override
            public void run() {
                p.setLevel(cooldown);
                p.setExp(cooldown / 15F);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                if (cooldown == 0) {
                    p.sendMessage(ChatColor.GREEN + "Je kan weer schieten!");
                    p.getInventory().addItem(new ItemStack(Material.ARROW));
                    cancel();
                    return;
                }
                cooldown--;
            }
        };
        runnable.runTaskTimer(OneIntheChamber.getPlugin(), 0, 20);
    }
     @EventHandler
        public void onArrowLand(ProjectileHitEvent e) {
            e.getEntity().remove();
            if (e.getEntity().getType() != EntityType.ARROW || !(e.getEntity().getShooter() instanceof Player)) {
                return;
            }
            if(e.getHitEntity() == null){
                startCooldown((Player) e.getEntity().getShooter());
                return;
            }
            Player p = (Player) e.getEntity().getShooter();
            if (e.getHitEntity().getType() == EntityType.PLAYER) {
                p.getInventory().addItem(new ItemStack(Material.ARROW));
            } else {
                startCooldown(p);
        }
    }
}