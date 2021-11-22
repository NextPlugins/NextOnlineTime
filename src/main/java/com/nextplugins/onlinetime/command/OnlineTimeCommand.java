package com.nextplugins.onlinetime.command;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.conversion.Conversor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.configuration.ConfigurationManager;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.npc.manager.NPCManager;
import com.nextplugins.onlinetime.npc.runnable.NPCRunnable;
import com.nextplugins.onlinetime.registry.InventoryRegistry;
import com.nextplugins.onlinetime.utils.ColorUtils;
import com.nextplugins.onlinetime.utils.LocationUtils;
import com.nextplugins.onlinetime.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class OnlineTimeCommand implements CommandExecutor {

    private final TimedPlayerManager timedPlayerManager = NextOnlineTime.getInstance().getTimedPlayerManager();
    private final ConversorManager conversorManager = NextOnlineTime.getInstance().getConversorManager();
    private final InventoryRegistry inventoryRegistry = NextOnlineTime.getInstance().getInventoryRegistry();
    private final NPCManager npcManager = NextOnlineTime.getInstance().getNpcManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando apenas pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            List<String> messages = sender.hasPermission("nextonlinetime.admin")
                ? MessageValue.get(MessageValue::helpMessageAdmin)
                : MessageValue.get(MessageValue::helpMessage);

            for (String message : messages) {
                player.sendMessage(message.replace("%label%", "tempo"));
            }

            return true;
        }

        String subCommand = args[0];

        // see

        if (subCommand.equalsIgnoreCase("ver")) {
            Player target = null;

            try {
                target = Bukkit.getPlayer(args[1]);
            } catch (Throwable ignored) {
            }

            String name = target == null ? player.getName() : target.getName();

            TimedPlayer timedPlayer = timedPlayerManager.getPlayers().getOrDefault(name, null);
            if (timedPlayer == null) {
                player.sendMessage(MessageValue.get(MessageValue::offlinePlayer));
                return true;
            }

            player.sendMessage(MessageValue.get(MessageValue::timeOfTarget)
                .replace("%target%", name)
                .replace("%time%", TimeUtils.format(timedPlayer.getTimeInServer()))
            );

            return true;
        }

        // menu

        if (subCommand.equalsIgnoreCase("menu")) {
            inventoryRegistry.getMainInventory().openInventory(player);
            return true;
        }

        // send

        if (subCommand.equalsIgnoreCase("enviar")) {
            if (!player.hasPermission("nextonlinetime.sendtime")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Você deve especificar um jogador alvo.");
                return true;
            }

            Player target;

            try {
                target = Bukkit.getPlayer(args[1]);
            } catch (Throwable ignored) {
                player.sendMessage(ChatColor.RED + "O jogador alvo é invalido.");
                return true;
            }

            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Você deve especificar um jogador alvo.");
                return true;
            }

            String time;

            try {
                time = args[2];
            } catch (Throwable ignored) {
                player.sendMessage(ChatColor.RED + "Você deve especificar uma quantia de tempo válida.");
                return true;
            }

            if (player == target) {
                player.sendMessage(MessageValue.get(MessageValue::cantSendForYou));
                return true;
            }

            long timeInMillis = TimeUtils.unformat(time);
            if (timeInMillis < 1) {
                player.sendMessage(MessageValue.get(MessageValue::invalidTime));
                return true;
            }

            TimedPlayer timedPlayer = timedPlayerManager.getByName(player.getName());
            if (timedPlayer.getTimeInServer() < timeInMillis) {
                player.sendMessage(MessageValue.get(MessageValue::noTime));
                return true;
            }

            TimedPlayer timedTarget = timedPlayerManager.getByName(target.getName());

            timedTarget.addTime(timeInMillis);
            timedPlayer.removeTime(timeInMillis);

            player.sendMessage(MessageValue.get(MessageValue::sendedTime)
                .replace("%time%", TimeUtils.format(timeInMillis))
                .replace("%target%", target.getName())
            );

            target.sendMessage(MessageValue.get(MessageValue::receivedTime)
                .replace("%time%", TimeUtils.format(timeInMillis))
                .replace("%sender%", player.getName())
            );

            return true;
        }

        // set npc

        if (subCommand.equalsIgnoreCase("setnpc")) {
            if (!player.hasPermission("nextonlinetime.admin")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando");
                return true;
            }

            Location location = player.getLocation();
            ConfigurationManager configManager = ConfigurationManager.of("npc.yml");

            FileConfiguration config = configManager.load();
            config.set("position", LocationUtils.serialize(location));

            try {

                config.save(configManager.getFile());

                NPCRunnable runnable = (NPCRunnable) npcManager.getRunnable();
                runnable.spawnDefault(location);

                player.sendMessage(ColorUtils.colored("&aNPC setado com sucesso."));

            } catch (Exception exception) {
                player.sendMessage(ColorUtils.colored("&cNão foi possível setar o npc, o sistema está desabilitado por falta de dependência."));
            }

            return true;
        }

        // delete npc

        if (subCommand.equalsIgnoreCase("delnpc")) {
            if (!player.hasPermission("nextonlinetime.admin")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando");
                return true;
            }

            ConfigurationManager configManager = ConfigurationManager.of("npc.yml");

            FileConfiguration config = configManager.load();
            config.set("position", "");

            try {

                config.save(configManager.getFile());

                NPCRunnable runnable = (NPCRunnable) npcManager.getRunnable();
                runnable.clear();

                player.sendMessage(ColorUtils.colored("&aNPC deletado com sucesso."));

            } catch (Exception exception) {
                player.sendMessage(ColorUtils.colored("&cNão foi possível deletar o npc."));
            }

            return true;
        }

        // conversor

        if (subCommand.equalsIgnoreCase("conversor")) {
            if (!player.hasPermission("nextonlinetime.admin")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando");
                return true;
            }

            final String conversor = args[1];

            Conversor pluginConversor = checkConversor(sender, conversor);

            if (pluginConversor == null) return true;

            player.sendMessage(ColorUtils.colored(
                "&aIniciando conversão de dados do plugin " + pluginConversor.getConversorName() + "."
            ));

            long initial = System.currentTimeMillis();
            conversorManager.setConverting(true);

            Set<TimedPlayer> timedPlayers = pluginConversor.lookupPlayers();
            if (timedPlayers == null) {

                player.sendMessage(ColorUtils.colored(
                    "&cOcorreu um erro, veja se configurou corretamente o conversor."
                ));
                return true;

            }

            conversorManager.startConversion(
                player,
                timedPlayers,
                pluginConversor.getConversorName(),
                initial
            );
        }

        return false;
    }

    private Conversor checkConversor(CommandSender sender, String conversor) {
        if (conversorManager.isConverting()) {
            sender.sendMessage(ColorUtils.colored(
                "&cVocê já está convertendo uma tabela, aguarde a finalização da mesma."
            ));
            return null;
        }

        final int maxPlayers = sender instanceof Player ? 1 : 0;
        if (Bukkit.getOnlinePlayers().size() > maxPlayers) {
            sender.sendMessage(ColorUtils.colored(
                "&cEsta função só pode ser usada com apenas você online."
            ));
            return null;
        }

        Conversor pluginConversor = conversorManager.getByName(conversor);
        if (pluginConversor == null) {
            sender.sendMessage(ColorUtils.colored(
                "&cEste conversor é inválido, conversores válidos: " + conversorManager.availableConversors()
            ));
        }

        return pluginConversor;
    }

}
