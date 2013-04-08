package com.openttd.robot.rule;

import com.openttd.admin.OpenttdAdmin;
import com.openttd.admin.event.ChatEvent;
import com.openttd.admin.event.ChatEventListener;
import com.openttd.admin.event.ClientEvent;
import com.openttd.admin.event.ClientEventListener;
import com.openttd.network.admin.NetworkClient.Send;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Show announce on client join and every 6 months.
 * Answer to basic clients call.
 * Rule #1: !rules, !r
 * Rule #2: !help, !h
 * Rule #3: $help, $h
 * Rule #4: $pause, $p
 * Rule #5: $unpause, $u
 */
public class ServerAnnouncer extends AbstractRule implements ClientEventListener, ChatEventListener {

	private final ExternalUsers externalUsers;

	public ServerAnnouncer(OpenttdAdmin openttdAdmin, ExternalUsers externalUsers) {
		super(openttdAdmin);
		this.externalUsers = externalUsers;
	}

	@Override
	public void onClientEvent(ClientEvent clientEvent) {
		switch(clientEvent.getAction()) {
		case CREATE: {
			int clientId = clientEvent.getClientId();
			showWelcome(clientId);
			break;
		}
		}
	}

	@Override
	public void onChatEvent(ChatEvent chatEvent) {
		Integer clientId = chatEvent.getClientId();
		String message = chatEvent.getMessage();
		if(clientId != null && message != null) {
			message = message.trim();
			if(message.startsWith("!rules") || message.equals("!r")) {
				//Rule #1
				showRules(clientId);
			} else if(message.startsWith("!help") || message.equals("!h")) {
				//Rule #2
				showHelp(clientId);
			} else if(message.startsWith("$")
				&& externalUsers.getExternalUser(clientId) != null
				&& externalUsers.getExternalUser(clientId).isAdmin()) {
				if(message.startsWith("$help") || message.equals("$h")) {
					//Rule #3
					showHelpAdmin(clientId);
				} else if(message.startsWith("$pause") || message.equals("$p")) {
					//Rule #4
					pause();
				} else if(message.startsWith("$unpause") || message.equals("$u")) {
					//Rule #5
					unpause();
				}
			}
		}
	}

	@Override
	public Collection<Class> listEventTypes() {
		Collection<Class> listEventTypes = new ArrayList<Class>(2);
		listEventTypes.add(ChatEvent.class);
		listEventTypes.add(ClientEvent.class);
		return listEventTypes;
	}

	private void pause() {
		Send send = super.getSend();
		send.rcon("pause");
	}
	
	private void unpause() {
		Send send = super.getSend();
		send.rcon("unpause");
	}
	
	private void showWelcome(int clientId) {
		Send send = super.getSend();
		send.chatClient(clientId, "Welcome to www.strategyboard.net goal server.");
		send.chatClient(clientId, "Type: !howto or !help.");
	}

	private void showRules(int clientId) {
		Send send = super.getSend();
		send.chatClient(clientId, "Any violation of these rules will make you banned.");
		send.chatClient(clientId, "# 0: Retaliation doesn't allow you to break any of these rules.");
		send.chatClient(clientId, "# 1: Stay friendly and respect each other! Any racism/rude language will be sanctioned.");
		send.chatClient(clientId, "# 2: Play fair! No reserving! No rating exploit! No blockage! Don't build useless structures!");
		send.chatClient(clientId, "# 3: No heavy terraforming/Town Destroying!");
		send.chatClient(clientId, "# 4: No 'Ghost companies' for extra money (e.g. to prepare your future build).");
		send.chatClient(clientId, "# 5: No multiple companies for one person! (One person => One company).");
		send.chatClient(clientId, "# 6: No one way trains!");
		send.chatClient(clientId, "# 7: Don't buy other companies!");
		send.chatClient(clientId, "# 8: Nobody can claim an industry(or goods)!");
		send.chatClient(clientId, "# 9: No 'Next,Next,Turn'!");
		send.chatClient(clientId, "#10: No catchment area exploit!");
		send.chatClient(clientId, "http://www.strategyboard.net/ for more info");
	}

	private void showHelp(int clientId) {
		Send send = super.getSend();
		send.chatClient(clientId, "Valid commands are !goal, !rules, !login, !howto, !score, !rename and !resetme.");
		send.chatClient(clientId, "Short commands are !g, !r, !cv or !cp (equ. to !score)");
	}
	
	private void showHelpAdmin(int clientId) {
		Send send = super.getSend();
		send.chatClient(clientId, "Valid commands are $clients, $companies, $warn, $kick, $ban, $reset, $pause, $unpause");
		send.chatClient(clientId, "Short commands are $cls, $cps, $w, $k, $b, $r, $p, $u");
	}
	
}
