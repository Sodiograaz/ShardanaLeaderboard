package dev.sodiograaz.shardanaLeaderboard;

import dev.sodiograaz.shardanaLeaderboard.storage.LeaderboardStorage;
import dev.sodiograaz.shardanaLeaderboard.tasks.KillsHologramTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShardanaLeaderboard extends JavaPlugin
{

	private static @Getter ShardanaLeaderboard instance;
	private static @Getter LeaderboardStorage leaderboardStorage;
	
	private static final @Getter String KILL_HOLOGRAM_NAME = "kill-hologram";
	
	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		
		instance = this;
		leaderboardStorage = new LeaderboardStorage(getConfig().getString("database.host"),
				getConfig().getString("database.port"),
				getConfig().getString("database.username"),
				getConfig().getString("database.password"),
				getConfig().getString("database.database"));
		
		Bukkit.getScheduler()
				.runTaskTimer(this, new KillsHologramTask(), getConfig()
						.getLong("time-scale.delay"), getConfig()
						.getLong("time-scale.period"));
	}
	
}