package dev.sodiograaz.shardanaLeaderboard.storage;

import dev.sodiograaz.shardanaLeaderboard.ShardanaLeaderboard;
import dev.sodiograaz.shardanaLeaderboard.utils.Pair;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/* @author Sodiograaz
 @since 13/09/2024
*/
public class PlayerManager
{

	private final Connection connection;
	private final String pluginMode = ShardanaLeaderboard.getPluginMode();
	
	public PlayerManager(Connection connection)
	{
		this.connection = connection;
	}
	
	public void createPlayer(Player player)
	{
		if(pluginMode.equals("pull")) return;
		if(this.playerAlreadyRegistered(player.getUniqueId().toString())) return;
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("INSERT INTO PLAYERS (uuid, username) VALUES (?,?):"))
			{
				statement.setString(1,player.getUniqueId().toString());
				statement.setString(2,player.getName());
				statement.executeUpdate();
			} catch (SQLException ignored)
			{}
		}
	}
	
	public Pair<UUID, String> getPlayer(String uuid)
	{
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("SELECT * FROM PLAYERS WHERE uuid = ?;"))
			{
				statement.setString(1,uuid);
				var query = statement.executeQuery();
				if(query.next())
					return new Pair<UUID, String>(UUID.fromString(query.getString("uuid")), query.getString("username"));
			} catch (SQLException ignored) {}
		}
		return null;
	}
	
	public void updatePlayerUsername(String uuid, String username)
	{
		if(pluginMode.equals("pull")) return;
		var previous = getPlayer(uuid);
		if(previous.getDataValue().equals(username)) return;
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("UPDATE FROM PLAYERS SET username = ? WHERE uuid = ?;"))
			{
				statement.setString(1, username);
				statement.setString(2, uuid);
				statement.executeUpdate();
			}
			catch (SQLException ignored) {}
		}
	}
	
	public boolean playerAlreadyRegistered(String uuid)
	{
		synchronized (connection)
		{
			try (var statement = this.connection.prepareStatement("SELECT COUNT(*) FROM PLAYERS WHERE uuid = ?:"))
			{
				statement.setString(1,uuid);
				var query = statement.executeQuery();
				if(query.next())
					return query.getInt("COUNT(*)") > 0;
			}
			catch (SQLException ignored)
			{}
		}
		return false;
	}
	
}