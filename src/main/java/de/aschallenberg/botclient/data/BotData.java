package de.aschallenberg.botclient.data;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotData implements Serializable {

	private String name;
	private String ownerName;

	public static BotData fromMap(final Map<String, String> map) {
		return new BotData(map.get("name"), map.get("ownerName"));
	}
}
