package com.victor.minerador.data;

import com.victor.minerador.Minerador;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class PlayerManager {

    private final Map<UUID, Boolean> modoMineradorAtivo = new HashMap<>();

    private final Minerador minerador;
    FileConfiguration data;
    File dataFile;

    public PlayerManager(Minerador minerador) {
        this.minerador = minerador;
        salvarPlayer(); // ⬅️ Adicionado aqui
    }

    private void salvarPlayer() {

        dataFile = new File(minerador.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                minerador.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
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

    public boolean isModoMinerador(Player jogador) {
        return modoMineradorAtivo.getOrDefault(jogador.getUniqueId(), false);
    }

    public void setModoMinerador(Player jogador, boolean ativo) {
        modoMineradorAtivo.put(jogador.getUniqueId(), ativo);
    }
}
