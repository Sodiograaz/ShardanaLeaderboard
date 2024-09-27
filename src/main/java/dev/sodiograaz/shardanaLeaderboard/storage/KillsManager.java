package dev.sodiograaz.shardanaLeaderboard.storage;

import dev.sodiograaz.shardanaLeaderboard.ShardanaLeaderboard;
import dev.sodiograaz.shardanaLeaderboard.utils.Pair;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/* @author Sodiograaz
 @since 14/09/2024
*/
public class KillsManager
{
	
	private final Connection connection;
	private final PlayerManager playerManager;
	
	public KillsManager(Connection connection, PlayerManager playerManager)
	{
		this.connection = connection;
		this.playerManager = playerManager;
	}
	
	public void registerKill(Player killer, long kill_count)
	{
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("INSERT INTO KILL_LEADERBOARD (killer, kill_count) VALUES (?,?):"))
			{
				statement.setString(1, killer.getUniqueId().toString());
				statement.setLong(2, kill_count);
				statement.executeUpdate();
			} catch (SQLException ignored)
			{}
		}
	}
	
	public void updateKillRegister(Player killer, long kill_count)
	{
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("UPDATE KILL_LEADERBOARD SET (kill_count) = (?) WHERE uuid = ?;"))
			{
				statement.setLong(1, kill_count);
				statement.setString(2, killer.getUniqueId().toString());
				statement.executeUpdate();
			}
			catch (SQLException ignored) {}
		}
	}
	
	public Map<String, Long> getAllPlayerKills()
	{
		final Map<String, Long> result = new LinkedHashMap<>();
		
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("SELECT * FROM KILL_LEADERBOARD;"))
			{
				ResultSet query = statement.executeQuery();
				while(query.next())
				{
					var username = this.playerManager.getPlayer(query.getString("uuid"));
					result.put(username.getDataValue(), query.getLong("kill_count"));
				}
			}
			catch (SQLException ignored) {}
		}
		
		return result;
	}
	
	
	public Map<String, Long> getOrderedAllPlayerKills(String mode)
	{
		var data = getAllPlayerKills();
		
		List<Map.Entry<String, Long>> temp = new ArrayList<>(data.entrySet());
		
		switch (mode.toLowerCase())
		{
			case "ascending" -> temp.sort(Comparator.comparingLong(Map.Entry::getValue));
			case "descending" -> temp.sort((x,y) -> Long.compare(y.getValue(), x.getValue()));
			default -> temp.sort(Comparator.comparingLong(Map.Entry::getValue));
		}
		
		Map<String, Long> sort = new HashMap<>();
		
		for(var x : temp)
		{
			sort.put(x.getKey(), x.getValue());
		}
		
		
		return sort;
	}

	public boolean registeredPlayerAlreadyExists(String uuid)
	{
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("SELECT COUNT(*) FROM KILL_LEADERBOARD WHERE uuid = ?;"))
			{
				statement.setString(1, uuid);
				ResultSet query = statement.executeQuery();
				if(query.next())
					return query.getInt("COUNT(*)") > 0;
			}
			catch (SQLException ignored) {}
		}
		return false;
	}
	
	public long getTotalKillsOfAllPlayers()
	{
		long q = 0;
		synchronized (connection)
		{
			var data = getAllPlayerKills();
			for(var x : data.entrySet())
			{
				q = q + x.getValue();
			}
		}
		return q;
	}
	

}