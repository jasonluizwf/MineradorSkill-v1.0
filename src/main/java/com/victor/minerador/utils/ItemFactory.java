package com.victor.minerador.utils;

import com.victor.minerador.Minerador;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    private final Minerador plugin;

    public ItemFactory(Minerador plugin) {
        this.plugin = plugin;
    }

    /**
     * Função para criar uma picareta personalizada de diamante.
     * @return retorna um Obj do tipo ItemStack já personalizado.
     */
    public ItemStack criarPicaretaPersonalizada() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        if(meta != null) {
            //nome estilizado
            meta.setDisplayName("§b§lPicareta do Minerador");

            //descrição
            List<String> lore = new ArrayList<>();
            lore.add("§7Exclusiva para mineradores experientes.");
            meta.setLore(lore);

            //brilho falso sem mostrar encantamento
            meta.addEnchant(Enchantment.EFFICIENCY, 5, true);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);

            //tag personalizada para identificar dps
            NamespacedKey key = new NamespacedKey(plugin, "minerador_item");

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "sim");
            item.setItemMeta(meta);
        }
        return item;
    }
}
