package com.victor.minerador.commands;

import com.victor.minerador.data.PlayerManager;
import com.victor.minerador.utils.BarraDeProgressoUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ComandoMinerador implements CommandExecutor {

    private final PlayerManager playerManager;
    private final BarraDeProgressoUtil barraDeProgressoUtil;

    public ComandoMinerador(PlayerManager p, BarraDeProgressoUtil barraDeProgressoUtil) {
        this.playerManager = p;
        this.barraDeProgressoUtil = barraDeProgressoUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("minerador")) {
            UUID id = p.getUniqueId();
            int blocos = playerManager.carregarProgresso(id);
            barraDeProgressoUtil.mostrarBarra(p, blocos);
            return true;
        }

        return false;
    }
}
