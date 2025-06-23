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

public class PlayerManager {

    private final Map<UUID, Boolean> modoMineradorAtivo = new HashMap<>();
    private final Minerador minerador;
    private FileConfiguration data;
    private File dataFile;

    public PlayerManager(Minerador minerador) {
        this.minerador = minerador;
        salvarPlayer(); // ⬅️ Adicionado aqui
    }

    // Salva progresso do jogador no arquivo data.yml.
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

    /**
     * Salva pregresso do player.
     * @param id ID do player.
     * @param blocos Quantidade de blocos que o player quebrou;
     */
    public void salvarProgresso(UUID id, int blocos) {
        data.set("jogadores." + id + ".blocos", blocos);
        salvarArquivo();
    }

    /**
     * Carrega o progresso do player.
     * @param id ID do player.
     * @return Retorna um inteiro para consultar dados quantidade de blocos quebrados do player salvos no arquivo data.yml.
     */
    public int carregarProgresso(UUID id) {
        return data.getInt("jogadores." + id + ".blocos", 0);
    }

    // Metodo para salvar dados no arquivo data.yml
    private void salvarArquivo() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo usado para verificar se o modo minerador esta ativo ou desativado.
     * @param jogador Deve passar um objeto do tipo player.
     * @return Retorna um boolean se esta ativo ou desativado o modo minerador.
     */
    public boolean isModoMinerador(Player jogador) {
        return modoMineradorAtivo.getOrDefault(jogador.getUniqueId(), false);
    }

    /**
     * Metodo para definir se o modo minerador está ativado ou desativado.
     * @param jogador Deve passar um Obj tipo Player.
     * @param flag Deve passar tipo boolean para ativar ou desativar o modo minerador.
     */
    public void setModoMinerador(Player jogador, boolean flag) {
        modoMineradorAtivo.put(jogador.getUniqueId(), flag);
    }
}
