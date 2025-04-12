package de.aschallenberg.botclient.data;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BotData implements Serializable {

	@EqualsAndHashCode.Include
	private String name;
	private String ownerName;
}
