package domain;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

public class Problem {
	
	final String id;
	final int size;
	final Operator[] operators;
	final String solution;

	public Problem(String id, int size, Operator[] operators, String challenge) {
		this.id = id;
		this.size = size;
		this.operators = operators;
		this.solution = challenge;
	}
	
	public int getSize() {
		return size;
	}
	
	public Operator[] getOperators() {
		return operators;
	}
	
	public String getChallenge() {
		return solution;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static Problem fromJson(String json) throws JsonProcessingException, IOException {
		ObjectMapper m = new ObjectMapper();
		JsonNode rootNode = m.readTree(json);
		String id = rootNode.get("id").asText();
		int size = rootNode.get("size").asInt();
		ArrayNode opNode = (ArrayNode) rootNode.get("operators");
		Operator[] operators = new Operator[opNode.size()];
		for (int i = 0; i < opNode.size(); i++) {
			operators[i] = Operator.valueOf(opNode.get(i).asText());
		}
		String challenge = rootNode.get("challenge").asText();
		return new Problem(id, size, operators, challenge);
	}
}
