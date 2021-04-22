package com.nextplugins.onlinetime.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.conversion.Conversor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import com.nextplugins.onlinetime.utils.ActionBarUtils;
import com.nextplugins.onlinetime.utils.ColorUtils;
import com.nextplugins.onlinetime.utils.TimeUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Singleton
public class ConversorManager {

    @Inject private TimedPlayerDAO timedPlayerDAO;

    private static final String CONVERSION_FORMAT = "&b&L%s &a> &eConvertido &a%s &ede &a%s &edados em &6%s";

    protected final List<Conversor> conversors = new ArrayList<>();
    private boolean converting;
    private int actionBarTaskID;

    public Conversor getByName(String name) {

        return conversors.stream()
                .filter(conversor -> conversor.getConversorName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);

    }

    public List<String> avaliableConversors() {

        return conversors.stream()
                .map(Conversor::getConversorName)
                .collect(Collectors.toList());

    }

    public void registerConversor(Conversor conversor) {
        conversors.add(conversor);
    }

    /**
     * Save to database a {@link Set} of {@link TimedPlayer}
     *
     * @param sender requested the conversion (can be null)
     * @param timedPlayers to save
     * @param conversorName name of conversor (can be null)
     * @param initial time in milliseconds that the conversion was requested
     */
    public void startConversion(@Nullable CommandSender sender,
                                @NotNull Set<TimedPlayer> timedPlayers,
                                @Nullable String conversorName,
                                long initial) {

        AtomicInteger converted = new AtomicInteger();

        Bukkit.getScheduler().runTaskAsynchronously(
                NextOnlineTime.getInstance(),
                () -> {

                    for (TimedPlayer timedPlayer : timedPlayers) {

                        this.timedPlayerDAO.saveOne(timedPlayer);
                        converted.incrementAndGet();

                    }

                    if (sender != null) sender.sendMessage(ColorUtils.colored(
                            "&aConversão terminada em &2" + TimeUtils.format(System.currentTimeMillis() - initial) + "&a.",
                            "&aVocê &lnão &aprecisa reiniciar o servidor para salvar as alterações."
                    ));

                    this.converting = false;
                    Bukkit.getScheduler().cancelTask(this.actionBarTaskID);

                }
        );

        if (sender == null || sender instanceof ConsoleCommandSender || conversorName == null) return;

        Player player = (Player) sender;

        this.actionBarTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(NextOnlineTime.getInstance(), () -> {

            if (!player.isOnline()) return;

            String format = ColorUtils.colored(String.format(CONVERSION_FORMAT,
                    conversorName,
                    converted,
                    timedPlayers.size(),
                    TimeUtils.format(System.currentTimeMillis() - initial)
            ));

            ActionBarUtils.sendActionBar(
                    player,
                    ColorUtils.colored(format)
            );


        }, 0L, 20L).getTaskId();

    }

}
