package io.github.steveplays28.restartserver;

import io.github.steveplays28.restartserver.commands.RestartCommand;
import net.minecraft.server.MinecraftServer;

import java.time.Instant;

public class RestartScheduler {
	public boolean isRestartScheduled = false;
	public long nextRestart = -1;

	public boolean isRestartIfNoPlayersHaveBeenOnlineScheduled = false;
	public long nextRestartIfNoPlayersHaveBeenOnline = -1;

	public void onTick(MinecraftServer server) {
		restartIntervalCounter(server);
		RestartIfNoPlayersHaveBeenOnlineCounter(server);
	}

	private void restartIntervalCounter(MinecraftServer server) {
		if (RestartServer.config.restartInterval <= 0) {
			return;
		}

		// Get current epoch time
		long now = Instant.now().getEpochSecond();

		if (isRestartScheduled) {
			if (nextRestart <= now) {
				// Restart server
				RestartCommand.execute(server.getCommandSource());
			}
		} else {
			// Schedule restart
			nextRestart = now + RestartServer.config.restartInterval;
			isRestartScheduled = true;
		}
	}

	private void RestartIfNoPlayersHaveBeenOnlineCounter(MinecraftServer server) {
		if (!RestartServer.config.restartIfNoPlayersHaveBeenOnline) {
			return;
		}

		// Get current epoch time
		long now = Instant.now().getEpochSecond();

		if (isRestartIfNoPlayersHaveBeenOnlineScheduled) {
			if (server.getCurrentPlayerCount() > 0) {
				// Unschedule no players restart
				nextRestartIfNoPlayersHaveBeenOnline = -1;
				isRestartIfNoPlayersHaveBeenOnlineScheduled = false;
			}

			if (nextRestartIfNoPlayersHaveBeenOnline <= now) {
				// Restart server
				RestartCommand.execute(server.getCommandSource());
			}
		} else {
			if (server.getCurrentPlayerCount() <= 0) {
				// Schedule no players restart
				nextRestartIfNoPlayersHaveBeenOnline = now + RestartServer.config.noPlayersWaitTime;
				isRestartIfNoPlayersHaveBeenOnlineScheduled = true;
			}
		}
	}
}
