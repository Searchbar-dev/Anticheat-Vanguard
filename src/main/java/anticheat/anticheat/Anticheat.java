package anticheat.anticheat;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler;

import javax.swing.*;

public final class Anticheat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void PlayerClickEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickType = event.getClickedBlock();
        if (ClickType.DOUBLE_CLICK){

        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
