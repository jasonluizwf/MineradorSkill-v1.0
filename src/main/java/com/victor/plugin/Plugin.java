package com.victor.plugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Plugin extends JavaPlugin  implements Listener {

    private final Map<UUID, Boolean> modoMineradorAtivo = new HashMap<>();
    private final Map<UUID, BlockFace> ultimaFace = new HashMap<>();
    private final Map<UUID, Long> ultimoClique = new HashMap<>();
    private final Map<UUID, Integer> blocosMinerados = new HashMap<>();

    private static final int NIVEL_I = 500;
    private static final int NIVEL_II = 2000;
    private static final int NIVEL_III = 5000;


    FileConfiguration data;
    File dataFile;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @EventHandler
    public void quandoMinerar(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        Material tipo = e.getBlock().getType();

        if (tipo != Material.STONE && tipo != Material.DEEPSLATE) return;

        int atual = carregarProgresso(id);
        int novo = atual + 1;
        salvarProgresso(id, novo);

        if (novo == NIVEL_I || novo == NIVEL_II || novo == NIVEL_III) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3f, 1.0f);
            p.sendMessage("§6Parabéns! Você desbloqueou a habilidade " +
                    switch (novo) {
                        case NIVEL_I -> "§a2x1 para " + Material.IRON_PICKAXE.name();
                        case NIVEL_II -> "§b3x3 " + Material.IRON_PICKAXE.name();
                        case NIVEL_III -> "§e5x5 " + Material.IRON_PICKAXE.name();
                        default -> "§f!";
                    });
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("minerador")) {
            UUID id = p.getUniqueId();
            int blocos = carregarProgresso(id);
            mostrarBarra(p, blocos);
            return true;
        }

        return false;
    }

    public void mostrarBarra(Player p, int blocos) {
        int proximo = blocos < NIVEL_I ? NIVEL_I : blocos < NIVEL_II ? NIVEL_II : blocos < NIVEL_III ? NIVEL_III : NIVEL_III;
        int nivel = 0;

        UUID id = p.getUniqueId();
        int atual = carregarProgresso(id);

        int barra = (int) (((double) blocos / proximo) * 20);
        StringBuilder barraVisual = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            barraVisual.append(i < barra ? "§a|" : "§7|");
        }

        if(atual >= NIVEL_III) {
            nivel = 3;
        } else if(atual >= NIVEL_II) {
            nivel = 2;
        }else if(atual >= NIVEL_I) {
            nivel = 1;
        }

        p.sendMessage("§eMinerador nivel " + nivel + " [" + barraVisual + "§e] §f" + blocos + "/" + proximo);
    }

    @EventHandler
    public void aoClicar(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if (!item.getType().name().endsWith("_PICKAXE")) return;

        Material tipo = e.getPlayer().getInventory().getItemInMainHand().getType();
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        // Cooldown de 30 ticks (1.5 segundos)
        long agora = System.currentTimeMillis();
        long ultimo = ultimoClique.getOrDefault(id, 0L);

        int atual = carregarProgresso(id);

        if (agora - ultimo < 1500) {
            if (atual >= NIVEL_I) {
                p.sendMessage("§7Aguarde um pouco antes de ativar novamente.");
            } else p.sendMessage("§7Você não quebrou blocos suficientes. " + atual);
            return;
        }

        boolean novoEstado = !modoMineradorAtivo.getOrDefault(id, false);

        ultimoClique.put(id, agora); // atualiza o tempo do clique

        switch (tipo) {
            case IRON_PICKAXE -> {
                if (atual >= NIVEL_I) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 2x1.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                modoMineradorAtivo.put(id, novoEstado);
            }
            case DIAMOND_PICKAXE -> {
                if (atual >= NIVEL_II) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 3x3.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                modoMineradorAtivo.put(id, novoEstado);
            }
            case NETHERITE_PICKAXE -> {
                if (atual >= NIVEL_III) {
                    if (novoEstado) {
                        p.sendMessage("§aModo Minerador §lATIVADO§a! Quebre blocos em 5x5.");
                    } else {
                        p.sendMessage("§cModo Minerador §lDESATIVADO§c.");
                    }
                }
                modoMineradorAtivo.put(id, novoEstado);
            }
            default -> p.sendMessage("§7Você não tem nível suficiente.");
        }
    }

    @EventHandler
    public void quandoQuebrarOBloco(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Material tipoDoBloco = blockBreakEvent.getBlock().getType();

        if(tipoDoBloco != Material.STONE && tipoDoBloco != Material.DEEPSLATE) return;
        if(modoMinerador(player)) {
            minerarTresPorTres(blockBreakEvent);
        }

    }

    @EventHandler
    public void quandoQuebrarBloco(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Material tipo = e.getBlock().getType();

        if (tipo != Material.STONE && tipo != Material.DEEPSLATE) return;

        UUID id = p.getUniqueId();
        blocosMinerados.put(id, blocosMinerados.getOrDefault(id, 0) + 1);

        int minerados = blocosMinerados.get(id);

        // feedback a cada NIVEL_I blocos
        if (minerados % NIVEL_I == 0) {
            p.sendMessage("§7Você já minerou §e" + minerados + "§7 blocos!");
        }
    }

    @EventHandler
    public void saberFaceDoBloco(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        ultimaFace.put(e.getPlayer().getUniqueId(), e.getBlockFace());
    }

    private void minerarTresPorTres(BlockBreakEvent blockBreakEvent) {
        Player p = blockBreakEvent.getPlayer();
        Material tipo = p.getInventory().getItemInMainHand().getType();
        UUID id = p.getUniqueId();
        int atual = carregarProgresso(id);

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
                    if (atual >= NIVEL_I) {
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
                    if (atual >= NIVEL_II) {
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
                    if (atual >= NIVEL_III) {
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

    public void salvarProgresso(UUID id, int blocos) {
        data.set("jogadores." + id + ".blocos", blocos);
        salvarArquivo();
    }

    public int carregarProgresso(UUID id) {
        return data.getInt("jogadores." + id + ".blocos", 0);
    }

    public void salvarArquivo() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean modoMinerador(Player jogador) {
        return modoMineradorAtivo.getOrDefault(jogador.getUniqueId(), false);
    }
}
