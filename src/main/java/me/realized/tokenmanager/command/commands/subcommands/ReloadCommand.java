package me.realized.tokenmanager.command.commands.subcommands;

import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.command.BaseCommand;
import me.realized.tokenmanager.util.Loadable;
import me.realized.tokenmanager.util.Reloadable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand(final TokenManagerPlugin plugin) {
        super(plugin, "reload", "reload", null, 1, false, "rl");
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        if (args.length > getLength()) {
            final Optional<Loadable> target = plugin.find(args[1]);
            final Loadable loadable;

            if (!target.isPresent() || !((loadable = target.get()) instanceof Reloadable)) {
                sendMessage(sender, false, "&cInvalid module. Available: " + plugin.getReloadables());
                return;
            }

            final String name = loadable.getClass().getSimpleName();

            if (plugin.reload(loadable)) {
                sendMessage(sender, false, "&a[" + plugin.getDescription().getFullName() + "] Successfully reloaded " + name + ".");
            } else {
                sendMessage(sender, false, "&cAn error occured while reloading " + name + "! Please check the console for more information.");
            }

            return;
        }

        if (plugin.reload()) {
            sendMessage(sender, false, "&a[" + plugin.getDescription().getFullName() + "] Reload complete.");
        } else {
            sendMessage(sender, false, "&cAn error occured while reloading the plugin! The plugin will be disabled, please check the console for more information.");
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            final String prefix = args[1] != null ? args[1].toLowerCase() : "";
            final List<String> result = new ArrayList<>();

            for (final String name : plugin.getReloadables()) {
                if (prefix.isEmpty() || name.toLowerCase().startsWith(prefix)) {
                    result.add(name);
                }
            }

            return result;
        }

        return null;
    }
}
