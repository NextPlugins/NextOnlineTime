package com.nextplugins.onlinetime.command;

import com.google.inject.Inject;
import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.conversion.Conversor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.inventory.OnlineTimeInventory;
import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.ActionBarUtils;
import com.nextplugins.onlinetime.utils.ColorUtils;
import com.nextplugins.onlinetime.utils.TimeUtils;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimeCommand {

    @Inject private RewardManager rewardManager;
    @Inject private TimedPlayerManager timedPlayerManager;
    @Inject private ConversorManager conversorManager;

    @Command(
            name = "tempo",
            aliases = {"tempoonline", "time"},
            target = CommandTarget.PLAYER
    )
    public void timeCommand(Context<Player> context) {
        context.getSender().sendMessage(
                MessageValue.get(MessageValue::helpMessage)
                        .stream()
                        .map(line -> line.replace("%label%", context.getLabel()))
                        .collect(Collectors.toList())
                        .toArray(new String[]{})
        );
    }

    @Command(
            name = "tempo.ver",
            target = CommandTarget.PLAYER
    )
    public void viewTimeCommand(Context<Player> context,
                                @Optional Player target) {

        if (target == null) target = context.getSender();

        context.sendMessage(MessageValue.get(MessageValue::timeOfTarget)
                .replace("%target%", target.getName())
                .replace("%time%", TimeUtils.formatTime(this.timedPlayerManager.getByName(target.getName()).getTimeInServer()))
        );

    }

    @Command(
            name = "tempo.menu",
            target = CommandTarget.PLAYER
    )
    public void openInventoryCommand(Context<Player> context) {

        OnlineTimeInventory onlineTimeInventory = new OnlineTimeInventory().init();
        onlineTimeInventory.openInventory(context.getSender());

    }

    @Command(
            name = "tempo.enviar",
            target = CommandTarget.PLAYER,
            permission = "nextonlinetime.sendtime"
    )
    public void sendTimeCommand(Context<Player> context,
                                Player target,
                                String time) {

        if (context.getSender() == target) {

            context.sendMessage(MessageValue.get(MessageValue::cantSendForYou));
            return;

        }

        long timeInMillis = TimeUtils.getTime(time);
        if (timeInMillis < 1) {

            context.sendMessage(MessageValue.get(MessageValue::invalidTime));
            return;

        }

        TimedPlayer timedPlayer = this.timedPlayerManager.getByName(context.getSender().getName());
        if (timedPlayer.getTimeInServer() < timeInMillis) {

            context.sendMessage(MessageValue.get(MessageValue::noTime));
            return;

        }

        TimedPlayer timedTarget = this.timedPlayerManager.getByName(target.getName());

        timedTarget.addTime(timeInMillis);
        timedPlayer.removeTime(timeInMillis);

        context.sendMessage(MessageValue.get(MessageValue::sendedTime)
                .replace("%time%", TimeUtils.formatTime(timeInMillis))
                .replace("%target%", target.getName())
        );

        target.sendMessage(MessageValue.get(MessageValue::receivedTime)
                .replace("%time%", TimeUtils.formatTime(timeInMillis))
                .replace("%sender%", context.getSender().getName())
        );

    }

    @Command(
            name = "conversor",
            permission = "onlinetime.admin",
            target = CommandTarget.ALL
    )
    public void onConversorCommand(Context<CommandSender> context,
                                   String conversor) {

        if (this.conversorManager.isConverting()) {

            context.sendMessage(ColorUtils.colored(
                    "&cVocê já está convertendo uma tabela, aguarde a finalização da mesma."
            ));
            return;

        }

        int maxPlayers = context.getSender() instanceof Player ? 1 : 0;
        if (Bukkit.getOnlinePlayers().size() > maxPlayers) {

            context.sendMessage(ColorUtils.colored(
                    "&cEsta função só pode ser usada com apenas você online."
            ));
            return;

        }

        Conversor pluginConversor = this.conversorManager.getByName(conversor);
        if (pluginConversor == null) {

            context.sendMessage(ColorUtils.colored(
                    "&cEste conversor é inválido, conversores válidos: " + this.conversorManager.avaliableConversors()
            ));
            return;

        }

        context.sendMessage(ColorUtils.colored(
                "&aIniciando conversão de dados do plugin " + pluginConversor.getConversorName() + "."
        ));

        long initial = System.currentTimeMillis();
        this.conversorManager.setConverting(true);

        Set<TimedPlayer> timedPlayers = pluginConversor.lookupPlayers();
        if (timedPlayers == null) {

            context.sendMessage(ColorUtils.colored(
                    "&cOcorreu um erro, veja se configurou corretamente o conversor."
            ));
            return;

        }

        AtomicInteger converted = new AtomicInteger();

        Bukkit.getScheduler().runTaskAsynchronously(
                NextOnlineTime.getInstance(),
                () -> {

                    for (TimedPlayer timedPlayer : timedPlayers) {

                        this.timedPlayerManager.getTimedPlayerDAO().insertOne(timedPlayer);
                        converted.incrementAndGet();

                    }

                    context.sendMessage(ColorUtils.colored(
                            "&aConversão terminada em &2" + TimeUtils.formatTime(System.currentTimeMillis() - initial) + "&a.",
                            "&aVocê &lnão &aprecisa reiniciar o servidor para salvar as alterações."
                    ));

                    this.conversorManager.setConverting(false);

                    int actionBarTaskID = this.conversorManager.getActionBarTaskID();
                    if (actionBarTaskID != 0) Bukkit.getScheduler().cancelTask(actionBarTaskID);

                }
        );

        if (context.getSender() instanceof ConsoleCommandSender) return;

        Player sender = (Player) context.getSender();
        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(NextOnlineTime.getInstance(), () -> {

            if (!sender.isOnline()) return;
            if (!this.conversorManager.isConverting()) {

                return;

            }

            String format = ColorUtils.colored(String.format(
                    "&b&LNextOnlineTime &a> &eConvertido &a%s &ede &a%s &edados em &6%s",
                    converted,
                    timedPlayers.size(),
                    TimeUtils.formatTime(System.currentTimeMillis() - initial)
            ));

            ActionBarUtils.sendActionBar(
                    sender,
                    format
            );


        }, 0L, 20L).getTaskId();

        this.conversorManager.setActionBarTaskID(taskID);

    }

}
