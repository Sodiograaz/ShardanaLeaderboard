package dev.sodiograaz.shardanaLeaderboard.tasks;

import dev.sodiograaz.shardanaLeaderboard.ShardanaLeaderboard;
import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramManager;
import eu.decentsoftware.holograms.api.holograms.objects.HologramObject;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/* @author Sodiograaz
 @since 27/09/2024
*/
public class KillsHologramTask implements Runnable
{
	
	private final FileConfiguration configuration = ShardanaLeaderboard.getInstance().getConfig();;
	// Config
	
	// Section
	private final ConfigurationSection kill_hologram = configuration.getConfigurationSection("kill-hologram");
	
	private final String hologram_order = kill_hologram.getString("order", "ASCENDING");
	private final long limit = kill_hologram.getLong("limit");
	private final String format_per_player = kill_hologram.getString("format-per-player");
	private final String header = kill_hologram.getString("header");
	private final String footer = kill_hologram.getString("footer");
	
	// Hologram-details
	private final ConfigurationSection hologram_details = kill_hologram.getConfigurationSection("hologram-details");
	private final String world = hologram_details.getString("world");
	private final double x = hologram_details.getDouble("x");
	private final double y = hologram_details.getDouble("y");
	private final double z = hologram_details.getDouble("z");
	private final double offset_respect_to_y = hologram_details.getDouble("offset-in-respect-to-y");
	
	private Map<String, Long> data;
	
	private final String hologram_name = ShardanaLeaderboard.getKILL_HOLOGRAM_NAME();
	
	private final Location location = new Location(Bukkit.getWorld(world), x, y + offset_respect_to_y, z);
	
	private final DecentHolograms decentHolograms = DecentHologramsAPI.get();
	private final HologramManager hologramManager = decentHolograms.getHologramManager();
	
	@Override
	public void run()
	{
		data = ShardanaLeaderboard.getLeaderboardStorage().getKillsManager()
				.getAllPlayerKills();
		if(hologramManager.getHologram(hologram_name).getPage(0).getLines().size() > 2)
		{
			hologramManager.getHologram(hologram_name).getPage(0).getLines().forEach(HologramObject::delete);
		}
		if(!hologramManager.containsHologram(hologram_name))
		{
			DecentHologramsAPI.get()
					.getHologramManager()
					.registerHologram(new Hologram(hologram_name, location));
		}
	
		var hologram = hologramManager.getHologram(hologram_name);
		var page = hologram.getPage(0);
		
		// Header
		page.addLine(new HologramLine(page, location, MiniMessage.miniMessage().escapeTags(header.replaceAll("%total_war_kills%", String.valueOf(ShardanaLeaderboard.getLeaderboardStorage()
				.getKillsManager().getTotalKillsOfAllPlayers())))));

		// Data Handling
		List<String> usernames = new ArrayList<>(data.keySet());
		List<Long> counts = new ArrayList<>(data.values());
		
		for(var i = 0; i < limit; i++)
		{
			for(var j = 0; j < limit; j++)
			{
				var username = usernames.get(i);
				var count = counts.get(j);
				
				var content = format_per_player.replaceAll("%username%", username)
								.replaceAll("%kill_count%", String.valueOf(count));
				
				page.addLine(new HologramLine(page, location, MiniMessage.miniMessage().escapeTags(content)));
			}
		}
		
		// Footer
		page.addLine(new HologramLine(page, location, MiniMessage.miniMessage().escapeTags(footer.replaceAll("%total_war_kills%", String.valueOf(ShardanaLeaderboard.getLeaderboardStorage()
				.getKillsManager().getTotalKillsOfAllPlayers())))));
	}
}