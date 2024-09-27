package dev.sodiograaz.shardanaLeaderboard.storage;

import dev.sodiograaz.shardanaLeaderboard.ShardanaLeaderboard;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* @author Sodiograaz
 @since 13/09/2024
*/
public class LeaderboardStorage
{
	
	private Connection connection;
	
	private final String host,port,username,password,database,params;
	
	public LeaderboardStorage(String host, String port, String username, String password, String database) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		this.params = "?autoReconnect=true";
	}
	
	public void createConnection()
	{
		var logger = Bukkit.getLogger();
		try
		{
			if(isInvalidConnection())
			{
				logger.severe("Connection to database is invalid. stopping plugin.");
				Bukkit.getServer().getPluginManager().disablePlugin(ShardanaLeaderboard.getInstance());
				return;
			}
			
			this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s%s",this.host,this.port,this.database,this.params),
					this.username,this.password);
			
			ScriptRunner scriptRunner = new ScriptRunner(this.connection);
			
			var reader = new InputStreamReader(this.getClass()
					.getClassLoader()
					.getResourceAsStream("schema.sql"));
			
			scriptRunner.runScript(reader);
			
		} catch (SQLException exception)
		{
			logger.severe(exception.getLocalizedMessage());
		}
	}
	
	private boolean isInvalidConnection() throws SQLException
	{
		return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s%s",this.host,this.port,this.database,this.params),
				this.username,this.password).isClosed();
	}
	
	public PlayerManager getPlayerManager()
	{
		return new PlayerManager(this.connection);
	}
	
	public KillsManager getKillsManager()
	{
		return new KillsManager(this.connection, this.getPlayerManager());
	}
	
}