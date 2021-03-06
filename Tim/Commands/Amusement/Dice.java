package Tim.Commands.Amusement;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tim.Commands.ICommandHandler;
import Tim.Data.ChannelInfo;
import Tim.Data.CommandData;
import Tim.Tim;
import com.bernardomg.tabletop.dice.parser.DefaultDiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.parser.DiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.roller.DefaultRoller;
import org.pircbotx.hooks.types.GenericUserEvent;

public class Dice implements ICommandHandler {
	private final DiceNotationExpressionParser diceParser       = new DefaultDiceNotationExpressionParser(new DefaultRoller());
	private       HashSet<String>              handledCommands  = new HashSet<>();
	private       Pattern                      basicDicePattern = Pattern.compile("^d(\\d+)$");

	public Dice() {
		handledCommands.add("dice");
		handledCommands.add("roll");
	}

	public static void helpSection(GenericUserEvent event) {
		String[] strs = {
			"Dice Command:", "    !roll <dice string> - Generates a random number as if rolling specified dice.",
			};

		for (String str : strs) {
			event.getUser()
				 .send()
				 .message(str);
		}
	}

	@Override
	public boolean handleCommand(CommandData commandData) {
		ChannelInfo cdata = Tim.db.channel_data.get(commandData.getChannelEvent()
															   .getChannel()
															   .getName()
															   .toLowerCase());

		Matcher matches = basicDicePattern.matcher(commandData.command);
		if (matches.matches()) {
			commandData.command = "dice";
			commandData.argString = "1d" + matches.group(1);
		}

		if (handledCommands.contains(commandData.command)) {
			if (cdata.commands_enabled.get("dice")) {
				try {
					int result = diceParser.parse(commandData.argString)
										   .roll();

					if (result < 9000) {
						commandData.getMessageEvent()
								   .respond("Your result is " + result);
					} else {
						commandData.getMessageEvent()
								   .respond("OVER 9000!!! (" + result + ")");
					}
				} catch (Exception exception) {
					commandData.getMessageEvent()
							   .respond("I don't understand that dice string.");
				}
			} else {
				commandData.getMessageEvent()
						   .respond("I'm sorry. I don't do that here.");
			}

			return true;
		} else {
			return false;
		}
	}
}
