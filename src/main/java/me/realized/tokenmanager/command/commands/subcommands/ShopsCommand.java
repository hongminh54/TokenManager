package me.realized.tokenmanager.command.commands.subcommands;

import me.realized.tokenmanager.Permissions;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.command.BaseCommand;
import me.realized.tokenmanager.shop.Shop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShopsCommand extends BaseCommand {

    public ShopsCommand(final TokenManagerPlugin plugin) {
        super(plugin, "shops", "shops", Permissions.CMD_SHOP, 1, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final List<String> shops = new ArrayList<>();

        for (final Shop shop : shopConfig.getShops()) {
            shops.add(shop.getName());
        }

        sendMessage(sender, true, "COMMAND.token.shops", "shops", StringUtils.join(shops, ", "));
    }
}
