package dev.sodiograaz.shardanaLeaderboard.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* @author Sodiograaz
 @since 14/09/2024
*/
@Getter
@AllArgsConstructor
public class Pair<K,V> {
	
	private K dataKey;
	private V dataValue;
	
}