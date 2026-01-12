package me.realized.tokenmanager.command.commands.subcommands;

import me.realized.tokenmanager.Permissions;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.command.BaseCommand;
import me.realized.tokenmanager.shop.Shop;
import me.realized.tokenmanager.shop.gui.guis.ShopGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenCommand extends BaseCommand {

    public OpenCommand(final TokenManagerPlugin plugin) {
        super(plugin, "open", "open <username> <shop>", null, 3, false, "show");
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final Player target;

        if ((target = Bukkit.getPlayerExact(args[1])) == null) {
            sendMessage(sender, true, "ERROR.player-not-found", "input", args[1]);
            return;
        }

        final String name = args[2].toLowerCase();
        final Optional<Shop> shop = shopConfig.getShop(name);

        if (!shop.isPresent()) {
            sendMessage(sender, true, "ERROR.shop-not-found", "input", name);
            return;
        }

        shopManager.open(target, new ShopGui(plugin, shop.get()));
        sendMessage(sender, true, "COMMAND.tokenmanager.open", "name", name, "player", target.getName());
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return tabCompleteOnlinePlayers(args[1]);
        }

        if (args.length == 3) {
            final String prefix = args[2] != null ? args[2].toLowerCase() : "";
            final Player player = sender instanceof Player ? (Player) sender : null;
            final List<String> result = new ArrayList<>();

            for (final Shop shop : shopConfig.getShops()) {
                final String name = shop.getName();

                if (!prefix.isEmpty() && !name.toLowerCase().startsWith(prefix)) {
                    continue;
                }

                if (player != null && shop.isUsePermission() && !player.hasPermission(Permissions.SHOP + name)) {
                    continue;
                }

                result.add(name);
            }

            return result;
        }

        return null;
    }
}
