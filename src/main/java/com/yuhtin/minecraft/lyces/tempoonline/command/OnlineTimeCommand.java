package com.yuhtin.minecraft.lyces.tempoonline.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimeCommand {

    @Command(
            name = "tempo",
            aliases = {"tempoonline"},
            target = CommandTarget.PLAYER
    )
    public void timeCommand(Context<Player> context) {

        Player sender = context.getSender();
        //TODO

    }

}
