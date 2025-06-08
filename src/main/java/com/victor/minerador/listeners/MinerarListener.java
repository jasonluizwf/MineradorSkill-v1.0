package com.victor.minerador.listeners;

import com.victor.minerador.Minerador;
import com.victor.minerador.data.PlayerManager;
import com.victor.minerador.model.NivelMineracao;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinerarListener implements Listener {

    private final Map<UUID, BlockFace> ultimaFace = new HashMap<>();
    private final Map<UUID, Long> ultimoClique = new HashMap<>();
    private final PlayerManager playerManager;

    public MinerarListener(Minerador minerador) {
        this.playerManager = new PlayerManager(minerador);
    }

    @EventHandler
    public void contadorDeBlocoMinerado(BlockBreakEvent e) {
        Material tipo = e.getBlock().getType();
        if (tipo != Material.STONE && tipo != Material.DEEPSLATE) return;

        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        int contador = playerManager.carregarProgresso(id);
        int contadorSalvo = contador + 1;

        playerManager.salvarProgresso(id, contadorSalvo);

        NivelMineracao nivel = NivelMineracao.porBlocos(contadorSalvo);

        if (nivel != null && contadorSalvo == nivel.getBlocosNecessarios()) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1.0f);
            p.sendMessage("§6Parabéns! Você desbloqueou a habilidade " +
                    switch (nivel) {
                        case INICIANTE -> "§a2x1 para " + Material.IRON_PICKAXE.name();
                        case AVANCADO -> "§b3x3 " + Material.IRON_PICKAXE.name();
                        case MESTRE -> "§e5x5 " + Material.IRON_PICKAXE.name();
                    });
        }
    }

    @EventHandler
    public void ativarDesativarMineracao(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if (!item.getType().name().endsWith("_PICKAXE")) return;

        Material tipo = e.getPlayer().getInventory().getItemInMainHand().getType();
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        // Cooldown de 30 ticks (1.5 segundos)
        long agora = System.currentTimeMillis();
        long ultimo = ultimoClique.getOrDefault(id, 0L);

        int atual = playerManager.carregarProgresso(id);

        if (agora - ultimo < 1500) {
            if (atual >= NivelMineracao.INICIANTE.getBlocosNecessarios()) {
                p.sendMessage("§7Aguarde um pouco antes de ativar novamente.");
            } else p.sendMessage("§7Você não quebrou blocos suficientes. " + atual);
            return;
        }

        boolean novoEstado = !playerManager.isModoMinerador(p);

        ultimoClique.put(id, agora); // atualiza o tempo do clique

        switch (tipo) {
            case IRON_PICKAXE -> {
                if (atual >= NivelMineracao.INICIANTE.getBlocosNecessarios()) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 2x1.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                playerManager.setModoMinerador(p, novoEstado);
            }
            case DIAMOND_PICKAXE -> {
                if (atual >= NivelMineracao.AVANCADO.getBlocosNecessarios()) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 3x3.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                playerManager.setModoMinerador(p, novoEstado);
            }
            case NETHERITE_PICKAXE -> {
                if (atual >= NivelMineracao.MESTRE.getBlocosNecessarios()) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 5x5.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                playerManager.setModoMinerador(p, novoEstado);
            }
            default -> p.sendMessage("§7Você não tem nível suficiente.");
        }
    }

    @EventHandler
    public void executarHabilidade(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Material tipoDoBloco = blockBreakEvent.getBlock().getType();

        if(tipoDoBloco != Material.STONE && tipoDoBloco != Material.DEEPSLATE) return;
        if(playerManager.isModoMinerador(player)) {
            logicaQuebrarBlocos(blockBreakEvent);
        }
    }

    @EventHandler
    public void saberFaceDoBloco(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        ultimaFace.put(e.getPlayer().getUniqueId(), e.getBlockFace());
    }

    private void logicaQuebrarBlocos(BlockBreakEvent blockBreakEvent) {
        Player p = blockBreakEvent.getPlayer();
        Material tipo = p.getInventory().getItemInMainHand().getType();
        UUID id = p.getUniqueId();
        int atual = playerManager.carregarProgresso(id);

        if(tipo.name().endsWith("_PICKAXE")){

            Block blocoCentral = blockBreakEvent.getBlock();
            World mundo = blocoCentral.getWorld();

            Location loc = blocoCentral.getLocation();
            ItemStack item = p.getInventory().getItemInMainHand();

            BlockFace face = ultimaFace.getOrDefault(p.getUniqueId(), BlockFace.UP); // padrão caso não tenha clicado antes

            int dx = 0, dy = 0, dz = 0;

            switch (face) {
                case UP, DOWN -> { dx = 1; dz = 1; } // plano horizontal
                case EAST, WEST -> { dy = 1; dz = 1; } // plano parede YZ
                case NORTH, SOUTH -> { dx = 1; dy = 1; } // plano parede XY
            }

            switch (p.getInventory().getItemInMainHand().getType()) {
                case IRON_PICKAXE -> {
                    if (atual >= NivelMineracao.INICIANTE.getBlocosNecessarios()) {
                        // Quebrar blocos ao redor do bloco 2X1
                        int x = 0, z = 0;
                        for (int y = -1; y <= 0; y++) {
                            // Só ignora o bloco central
                            if (y == 0) continue;

                            int px = loc.getBlockX() + (dx != 0 ? x : 0);
                            int py = loc.getBlockY() + (dy != 0 ? y : 0);
                            int pz = loc.getBlockZ() + (dz != 0 ? z : 0);

                            Block b = mundo.getBlockAt(px, py, pz);
                            if (b.getType() != Material.AIR && b.getType().isSolid() && b.getType() == Material.STONE || b.getType() == Material.DEEPSLATE) {
                                b.breakNaturally(item);
                            }
                        }
                    }
                }
                case DIAMOND_PICKAXE -> {
                    if (atual >= NivelMineracao.AVANCADO.getBlocosNecessarios()) {
                        // Quebrar blocos ao redor do bloco 3X3
                        for (int x = -1; x <= 1; x++) {
                            for (int y = -1; y <= 1; y++) {
                                for (int z = -1; z <= 1; z++) {
                                    // Só ignora o bloco central
                                    if (x == 0 && y == 0 && z == 0) continue;

                                    int px = loc.getBlockX() + (dx != 0 ? x : 0);
                                    int py = loc.getBlockY() + (dy != 0 ? y : 0);
                                    int pz = loc.getBlockZ() + (dz != 0 ? z : 0);

                                    Block b = mundo.getBlockAt(px, py, pz);
                                    if (b.getType() != Material.AIR && b.getType().isSolid() && b.getType() == Material.STONE || b.getType() == Material.DEEPSLATE) {
                                        b.breakNaturally(item);
                                    }
                                }
                            }
                        }
                    }
                }
                case NETHERITE_PICKAXE -> {
                    if (atual >= NivelMineracao.MESTRE.getBlocosNecessarios()) {
                        // Quebrar blocos ao redor do bloco 5X5
                        for (int x = -2; x <= 2; x++) {
                            for (int y = -2; y <= 2; y++) {
                                for (int z = -2; z <= 2; z++) {
                                    // Só ignora o bloco central
                                    if (x == 0 && y == 0 && z == 0) continue;

                                    int px = loc.getBlockX() + (dx != 0 ? x : 0);
                                    int py = loc.getBlockY() + (dy != 0 ? y : 0);
                                    int pz = loc.getBlockZ() + (dz != 0 ? z : 0);

                                    Block b = mundo.getBlockAt(px, py, pz);
                                    if (b.getType() != Material.AIR && b.getType().isSolid() && b.getType() == Material.STONE || b.getType() == Material.DEEPSLATE) {
                                        b.breakNaturally(item);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
