package com.kmit.WordBuildingGame;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class GreeterServiceSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	private static final Set<String> supportedApplicationIds;
	static {

		supportedApplicationIds = new HashSet<String>();
		supportedApplicationIds.add("amzn1.ask.skill.7040facd-26d4-42a7-8c20-356d8040c38d");
	}

	public GreeterServiceSpeechletRequestStreamHandler() {
		super(new GreeterServiceSpeechlet(), supportedApplicationIds);
	}
}
