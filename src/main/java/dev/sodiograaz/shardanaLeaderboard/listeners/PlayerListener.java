package dev.sodiograaz.shardanaLeaderboard.listeners;

import dev.sodiograaz.shardanaLeaderboard.ShardanaLeaderboard;
import dev.sodiograaz.shardanaLeaderboard.storage.KillsManager;
import dev.sodiograaz.shardanaLeaderboard.storage.LeaderboardStorage;
import dev.sodiograaz.shardanaLeaderboard.storage.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/* @author Sodiograaz
 @since 14/09/2024
*/
public class PlayerListener implements Listener {
	
	private final LeaderboardStorage leaderboardStorage = ShardanaLeaderboard.getLeaderboardStorage();
	private final PlayerManager playerManager = leaderboardStorage.getPlayerManager();
	private final KillsManager killsManager = leaderboardStorage.getKillsManager();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		playerManager.createPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKillSomeone(PlayerDeathEvent event)
	{
		Player killed = event.getPlayer();
		var war_world_name = ShardanaLeaderboard.getInstance().getConfig().getString("war-world-name", "war");
		// Check if the world is war-world
		var damage_entity = event.getDamageSource().getCausingEntity();
		// Check if the entity causing the death is a player
		if(damage_entity instanceof Player killer)
		{
			// Check if both players are in the same world
			if(!killed.getWorld().getName().equals(war_world_name) && !damage_entity.getWorld().getName().equals(war_world_name))
				return;
			if(!killsManager.registeredPlayerAlreadyExists(killed.getUniqueId().toString()))
			{
				// Register the kill
				killsManager.registerKill(killer, 1);
				return;
			}
			
			killsManager.updateKillRegister(killer, 1);
		}
	}
	
}