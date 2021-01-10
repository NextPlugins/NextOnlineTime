package com.nextplugins.onlinetime.command;

import com.google.inject.Inject;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.inventory.OnlineTimeInventory;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.TimeUtils;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimeCommand {

    @Inject private RewardManager rewardManager;
    @Inject private TimedPlayerManager timedPlayerManager;

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
            permission = "onlinetime.sendtime"
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
        timedPlayer.setTimeInServer(timedPlayer.getTimeInServer() - timeInMillis);

        context.sendMessage(MessageValue.get(MessageValue::sendedTime)
                .replace("%time%", TimeUtils.formatTime(timeInMillis))
                .replace("%target%", target.getName())
        );

        target.sendMessage(MessageValue.get(MessageValue::receivedTime)
                .replace("%time%", TimeUtils.formatTime(timeInMillis))
                .replace("%sender%", context.getSender().getName())
        );

    }
}
