package anticheat.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import sun.management.counter.perf.PerfLongArrayCounter;

import java.util.HashMap;

public final class Anticheat extends JavaPlugin implements Listener {

    HashMap<Player, Integer> playerFlags = new HashMap<>();
    HashMap<Player, Boolean> acSpeedBypass = new HashMap<>();

    String kickMessage = "§cYou are permanently banned from Test.\n§cYou were banned for: §7Cheating\n§7If you feel this ban is unjustified, fill out a Support Ticket at\nhttps://test.com/appeal.\n§6You may also purchase an unban at https://store.test.com.";

    public void kickPlayer(Player player){
        player.kickPlayer(kickMessage);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        for (Player player : Bukkit.getOnlinePlayers()){
            playerFlags.put(player, 0);
            acSpeedBypass.put(player, false);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (playerFlags.get(player) >= 3){
                        kickPlayer(player);
                        Bukkit.broadcastMessage("§ex §6" + player.getName() + "§e was banned by §6Vanguard §efor use of §6Unfair Advantage§e.");
                    }
                }
                int amount = 0;
                for (Player player : Bukkit.getOnlinePlayers()){
                    amount = amount + playerFlags.get(player);
                    playerFlags.put(player, 0);
                }
            }
        }, 0L, 150L);
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerFlags.put(player, 0);
        acSpeedBypass.put(player, false);
        event.setJoinMessage("");
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerFlags.put(player, 0);
        acSpeedBypass.put(player, false);
        event.setQuitMessage("");
    }

    @EventHandler
    public void TPEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        acSpeedBypass.put(player, true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                acSpeedBypass.put(player, false);
            }
        }, 20L);
    }
    @EventHandler
    public void AntiKB(EntityDamageByEntityEvent e) {
        Player victim = (Player) e.getEntity();
        double velocity = victim.getVelocity().getY();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                double velocity1 = victim.getVelocity().getY();
                if (velocity == velocity1){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        if (player.hasPermission("anticheat.log")){
                            playerFlags.put((Player) victim, playerFlags.get(victim) + 1);
                            player.sendMessage("§7[§6Vanguard§7] §6" + victim.getName() + " §eMight be using §6Velocity §c[Type A] §7[§c" + (double) velocity1 + " §7/ §c" + (double) velocity1 + " §6Velocity§7] §7[§a" + ((CraftPlayer) player).getHandle().ping + " §ams§7]");
                        }
                    }
                }
            }
        }, 5L);
    }

    @EventHandler
    public void PlayerHitEntity(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        float values = (float) damager.getLocation().distanceSquared(victim.getLocation());
        if (damager instanceof Player){
            if ((!((Player) damager).getGameMode().equals(GameMode.CREATIVE))){
                float distance = (float) damager.getLocation().distance(victim.getLocation());
                double rounded = (double) Math.round(distance * 100) / 100;
                if (distance >= 4.10){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        if (player.hasPermission("anticheat.log")){
                            playerFlags.put((Player) damager, playerFlags.get(damager) + 1);
                            player.sendMessage("§7[§6Vanguard§7] §6" + damager.getName() + " §eMight be using §6Reach §c[Type A] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player).getHandle().ping + " §ams§7]");
                        }
                    }
                }
                if (values >= 20){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        if (player.hasPermission("anticheat.log")){
                            playerFlags.put((Player) damager, playerFlags.get(damager) + 1);
                            player.sendMessage("§7[§6Vanguard§7] §6" + damager.getName() + " §eMight be using §6Reach §c[Type B] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player).getHandle().ping + " §ams§7]");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        Player player1 = event.getPlayer();
        double beforez = player1.getLocation().getZ();
        double beforex = player1.getLocation().getX();
        float value = 9;
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                double afterz = player1.getLocation().getZ();
                if (beforez - afterz >= value){
                    if (!(player1.getGameMode().equals(GameMode.CREATIVE))){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            if (player.hasPermission("anticheat.log")){
                                if (!(acSpeedBypass.get(player1))){
                                    double rounded = (double) Math.round(beforez - afterz);
                                    playerFlags.put((Player) player1, playerFlags.get(player1) + 1);
                                    player.sendMessage("§7[§6Vanguard§7] §6" + player1.getName() + " §eMight be using §6Speed §c[Type A] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player1).getHandle().ping + " §ams§7]");
                                }
                            }
                        }
                    }
                }
                double afterx = player1.getLocation().getX();
                if (beforex - afterx >= value){
                    if (!(player1.getGameMode().equals(GameMode.CREATIVE))){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            if (player.hasPermission("anticheat.log")){
                                if (!(acSpeedBypass.get(player1))){
                                    double rounded = (double) Math.round(beforez - afterz);
                                    playerFlags.put((Player) player1, playerFlags.get(player1) + 1);
                                    player.sendMessage("§7[§6Vanguard§7] §6" + player1.getName() + " §eMight be using §6Speed §c[Type A] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player1).getHandle().ping + " §ams§7]");
                                }
                            }
                    }
                }
                if (beforex - afterx <= -value){
                    if (!(player1.getGameMode().equals(GameMode.CREATIVE))){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            if (player.hasPermission("anticheat.log")){
                                double rounded = (double) Math.round(beforex - afterx);
                                playerFlags.put((Player) player1, playerFlags.get(player1) + 1);
                                player.sendMessage("§7[§6Vanguard§7] §6" + player1.getName() + " §eMight be using §6Speed §c[Type C] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player1).getHandle().ping + " §ams§7]");
                            }
                        }
                    }
                }
                if (beforez - afterz <= -value){
                    if (!(player1.getGameMode().equals(GameMode.CREATIVE))){
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.hasPermission("anticheat.log")) {
                                double rounded = (double) Math.round(beforez - afterz);
                                playerFlags.put((Player) player1, playerFlags.get(player1) + 1);
                                player.sendMessage("§7[§6Vanguard§7] §6" + player1.getName() + " §eMight be using §6Speed §c[Type D] §7[§e" + rounded + " §6Blocks§7] §7[§a" + ((CraftPlayer) player1).getHandle().ping + " §ams§7]");
                            }
                        }
                        }
                    }
                }
            }
        }, 20L);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
