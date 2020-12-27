package com.yuhtin.minecraft.lyces.tempoonline.command;

import com.yuhtin.minecraft.lyces.tempoonline.configuration.values.MessageValue;
import com.yuhtin.minecraft.lyces.tempoonline.inventory.OnlineTimeInventory;
import com.yuhtin.minecraft.lyces.tempoonline.utils.TimeUtils;
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

        if (target != null) {

            context.sendMessage(MessageValue.get(MessageValue::timeOfTarget)
                    .replace("%target%", target.getName())
                    .replace("%time%", "1 dia e 1 hora")
            );

            return;

        }

        OnlineTimeInventory onlineTimeInventory = new OnlineTimeInventory().init();
        onlineTimeInventory.openInventory(context.getSender());

    }

    @Command(
            name = "tempo.enviar",
            target = CommandTarget.PLAYER
    )
    public void sendTimeCommand(Context<Player> context,
                                Player target,
                                String time) {

        long timeInMillis = TimeUtils.getTime(time);
        // TODO

    }
}
