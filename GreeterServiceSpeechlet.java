package com.kmit.WordBuildingGame;

import org.slf4j.Logger;
import com.amazon.speech.slu.Slot;
import org.slf4j.LoggerFactory;

import java.util.*;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import java.io.*;

public class GreeterServiceSpeechlet implements SpeechletV2 {
	List<String> words;
	List<String> check;
	String previous = null;
	int count = 0, userScore = 0;
	private static Random rand = new Random();
	int word = 0;
	private static final Logger log = LoggerFactory.getLogger(GreeterServiceSpeechlet.class);

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
		log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
		log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());

		String speechOutput = "Welcome to Alexa WordBuilding game. " + "Start by saying a meaningful word.";

		String fileName = "words.txt";
		File wordList = new File(fileName);
		previous = null;
		count = 0;
		userScore = 0;
		words = new ArrayList<>();
		check = new ArrayList<>();
		Scanner reader = null;

		try {
			reader = new Scanner(wordList);
		} catch (FileNotFoundException e) {
			System.exit(0);
		}

		while (reader.hasNextLine()) {
			String word = reader.nextLine();
			words.add(word);
		}
		word = words.size();

		String repromptText = "For instructions on what you can say, please say help me.";

		return newAskResponse(speechOutput, repromptText);

	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		IntentRequest request = requestEnvelope.getRequest();
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				requestEnvelope.getSession().getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("wordintent".equals(intentName)) {
			return wordBuildIntent(intent);
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelp();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			String errorSpeech = "This is unsupported.  Please try something else.";
			return newAskResponse(errorSpeech, errorSpeech);
		}
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
		log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());

	}

	private SpeechletResponse wordBuildIntent(Intent intent) {
		String resString = null;
		String responseText = null;
		Slot ss = intent.getSlot("variable");
		String s1 = ss.getValue();
		if (check.contains(s1)) {
			userScore--;
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Incorrect word, game has ended since you have repeated a word. " + " Your score is "
					+ userScore + ". Have a Nice Day!");

			return SpeechletResponse.newTellResponse(outputSpeech);

		}

		if (s1.length() >= 3) {

			if (count == 0) {
				userScore++;
				check.add(s1);
				char ch = s1.charAt(s1.length() - 1);
				String temp = "";
				temp = temp + ch;
				String s = null;
				while (true) {
					int place = rand.nextInt(word);
					s = words.get(place);
					if (s.startsWith(temp)) {

						break;
					}

				}

				words.remove(s);
				word = words.size();
				previous = s;
				resString = s;

				count++;
			} else {

				char ch1 = previous.charAt(previous.length() - 1);
				String t = "";
				t += ch1;
				if (s1.startsWith(t)) {
					userScore++;
					check.add(s1);
					char ch = s1.charAt(s1.length() - 1);
					String temp = "";
					temp += ch;
					String s = null;
					while (true) {
						int place = rand.nextInt(word);
						s = words.get(place);
						if (s.startsWith(temp)) {

							break;
						}

					}
					words.remove(s);

					word = words.size();
					previous = s;
					resString = s;
					responseText = resString;

				} else {

					userScore--;
					PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
					outputSpeech.setText("Incorrect word, game has ended since you have "
							+ "not started the word with correct letter. " + "Your score is " + userScore + " "
							+ ". Have a Nice Day!");

					return SpeechletResponse.newTellResponse(outputSpeech);

				}

			}

		} else {

			userScore--;
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Incorrect word, game has ended due to length anomaly." + " Your score is " + userScore
					+ ". Have a Nice Day!");

			return SpeechletResponse.newTellResponse(outputSpeech);
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(responseText);

		SimpleCard card = new SimpleCard();
		card.setTitle("Intent Values  ");
		card.setContent(responseText);

		return newAskResponse(resString, resString);

	}

	private SpeechletResponse getHelp() {
		String speechOutput = "You can say a meaningful word";
		String repromptText = "You can say a meaningful word";
		return newAskResponse(speechOutput, repromptText);
	}

	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {

		SimpleCard card = new SimpleCard();
		card.setTitle(stringOutput);
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(stringOutput);

		PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
		repromptOutputSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
	}

}
