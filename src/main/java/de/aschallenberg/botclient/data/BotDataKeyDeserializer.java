package de.aschallenberg.botclient.data;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class BotDataKeyDeserializer extends KeyDeserializer {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
		return mapper.readValue(key, BotData.class);
	}
}
