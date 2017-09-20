package Tim.AdminCommands;

import Tim.ChannelInfo;
import Tim.Tim;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Shout {
	public static void parseCommand(String[] args, MessageEvent event) {
		if (args.length < 1) {
			event.respond("You have to include a message.");
			return;
		}

		List<String> argList = Arrays.asList(args);

		String destination = "all";
		if (args[0].startsWith("@")) {
			if (args.length < 2) {
				event.respond("You have to include a message.");
				return;
			}

			destination = args[0].substring(1).toLowerCase();
			argList = argList.subList(1, argList.size());
		}

		Collection<ChannelInfo> destinations;

		if (destination.equals("all")) {
			destinations = Tim.db.channel_data.values();
		} else if (Tim.db.channel_groups.containsKey(destination)) {
			destinations = Tim.db.channel_groups.get(destination);
		} else {
			event.respond("Specified channel group not found.");
			return;
		}

		for (ChannelInfo cdata : destinations) {
			if (event.getUser() != null) {
				Tim.bot.sendIRC().message(cdata.channel, event.getUser().getNick() + " shouts @" + destination + ": " + StringUtils.join(argList, " "));
			}
		}
	}
}