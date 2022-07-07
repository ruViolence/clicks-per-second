package dev.dejvokep.clickspersecond.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import dev.dejvokep.clickspersecond.ClicksPerSecond;
import dev.dejvokep.clickspersecond.utils.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Function;

public class LeaderboardCommand extends PluginCommand {

    public LeaderboardCommand(ClicksPerSecond plugin, CommandManager<CommandSender> manager) {
        super(plugin);

        manager.command(manager.commandBuilder("cps", "clickspersecond").literal("leaderboard").permission("cps.leaderboard")
                .argument(IntegerArgument.optional("page", 1))
                .flag(manager.flagBuilder("fetch").withAliases("f").withDescription(ArgumentDescription.of("fetch if not available"))).handler(context -> {
                    List<PlayerInfo> leaderboard = plugin.getDataStorage().getLeaderboard();
                    int perPage = plugin.getConfiguration().getInt(MESSAGE_PREFIX + "leaderboard.entries-per-page"), page = context.get("page"), pages = (int) Math.ceil((double) leaderboard.size() / perPage);
                    Function<String, String> pageReplacer = message -> message.replace("{page}", String.valueOf(page)).replace("{pages}", String.valueOf(pages));

                    if (page < 1) {
                        send(context, MESSAGE_PREFIX + "leaderboard.invalid-page", pageReplacer);
                        return;
                    }

                    if (displayBoard(context, leaderboard, perPage, page, pages, pageReplacer))
                        return;

                    if (context.flags().isPresent("fetch")) {
                        if (!context.hasPermission("cps.leaderboard.fetch")) {
                            send(context, MESSAGE_NO_PERMISSION);
                            return;
                        }

                        send(context, MESSAGE_REQUEST_SENT);

                        // Fetch
                        plugin.getDataStorage().fetchBoard(perPage * page).whenComplete((board, exception) -> Bukkit.getScheduler().runTask(plugin, () -> {
                            // If an error
                            if (exception != null) {
                                send(context, MESSAGE_REQUEST_ERROR);
                                return;
                            }

                            int newPages = (int) Math.ceil((double) board.size() / perPage);
                            Function<String, String> newPageReplacer = message -> message.replace("{page}", String.valueOf(page)).replace("{pages}", String.valueOf(pages));

                            if (displayBoard(context, board, perPage, page, newPages, newPageReplacer))
                                return;

                            send(context, MESSAGE_PREFIX + "leaderboard.invalid-page", newPageReplacer);
                        }));
                        return;
                    }

                    send(context, MESSAGE_PREFIX + "leaderboard.invalid-page", pageReplacer);
                }).build());
    }

    private boolean displayBoard(CommandContext<CommandSender> context, List<PlayerInfo> board, int perPage, int page, int pages, Function<String, String> pageReplacer) {
        if (page <= pages) {
            send(context, MESSAGE_PREFIX + "leaderboard.header", pageReplacer);
            for (int i = perPage * (page - 1); i < perPage * page && i < board.size(); i++) {
                int finalIndex = i;
                send(context, MESSAGE_PREFIX + "leaderboard.entry", message -> getPlugin().getPlaceholderReplacer().replace(board.get(finalIndex), message.replace("{place}", String.valueOf(finalIndex + 1))));
            }
            send(context, MESSAGE_PREFIX + "leaderboard.footer", pageReplacer);
            return true;
        }

        return false;
    }

}