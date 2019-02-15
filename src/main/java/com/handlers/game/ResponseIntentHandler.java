package com.handlers.game;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;
import com.api.APICall;

public class ResponseIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(
			Predicates.intentName("AnswerIntent")
				.or(Predicates.intentName("StartGameIntent"))
				.or(Predicates.intentName("AnswerCategoryIntent"))
		);
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		APICall.setSessionId(
			(String) input.getAttributesManager().getSessionAttributes().get("sessionId")
		);

		switch (APICall.call("gameStep")) {
			case "PLAYER_COUNT":
				return playerCountAfterResponse(input);

			case "PLAYER_NAME":
				return playerNameAfterResponse(input);

			case "PLAYER_AGE":
				return playerAgeAfterResponse(input);

			case "WAIT_TO_START":
				return startQuestionAfterResponse(input);
				
			case "SELECT_CATEGORY":
				return selectCategoryAfterResponse(input);

			case "QUESTION":
				return answerQuestionAfterResponse(input);

			default:
				throw new IllegalStateException();
		}
	}

	private Optional<Response> playerCountAfterResponse(HandlerInput input) {
		IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
		Map<String, Slot> slotMap = request.getIntent().getSlots();

		if (slotMap.containsKey("AnswerNumber")) {
			try {
				Integer playerCount = Integer.parseInt(request.getIntent().getSlots().get("AnswerNumber").getValue());

				if ((playerCount > 0) && (playerCount <= 6)) {
					String repromptText = APICall.call("setPlayerCount?count=" + playerCount);
					String speechText = "Pour " + playerCount + " joueur. " + repromptText;

					return input.getResponseBuilder()
							.withSpeech(speechText)
							.withReprompt(repromptText)
							.withSimpleCard("QuizCard", speechText)
							.withShouldEndSession(false)
							.build();
				}
			} catch (NumberFormatException e) {}
			
			String speechText = "Donnez un nombre de joueur entre 1 et 6.";
			return input.getResponseBuilder()
					.withSpeech(speechText)
					.withReprompt("Combien y a t-il de joueur?")
					.withSimpleCard("QuizCard", speechText)
					.withShouldEndSession(false)
					.build(); 
		}
	
		String speechText = "Avant de commencer la partie veuillez indiquer le nombre de joueur.";
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt("Combien y a t-il de joueur?")
				.withSimpleCard("QuizCard", speechText)
				.withShouldEndSession(false)
				.build();
	}

	private Optional<Response> playerNameAfterResponse(HandlerInput input) {
		IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
		Map<String, Slot> slotMap = request.getIntent().getSlots();
		Slot answerName = slotMap.get("AnswerName");

		if (answerName.getValue() != null) {
			String speechText = APICall.call("setPlayerName?name=" + answerName.getValue());
			return input.getResponseBuilder()
					.withSpeech(speechText)
					.withReprompt(speechText)
					.withSimpleCard("QuizCard", speechText)
					.build();
		}

		String speechText = "Dites un nom de joueur correct.";
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(speechText)
				.withSimpleCard("QuizCard", speechText)
				.build();
	}

	private Optional<Response> playerAgeAfterResponse(HandlerInput input) {
		IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
		Map<String, Slot> slotMap = request.getIntent().getSlots();
		Slot answerNumberSlot = slotMap.get("AnswerNumber");

		if (answerNumberSlot.getValue() != null) {
			try {
				String speechText = APICall.call("setPlayerAge?age=" + answerNumberSlot.getValue());
				return input.getResponseBuilder()
						.withSpeech(speechText)
						.withReprompt(speechText)
						.withSimpleCard("QuizCard", speechText)
						.withShouldEndSession(false)
						.build();
			} catch (NumberFormatException e) {}
		}

		int currentPlayerId = Integer.parseInt(APICall.call("currentPlayerId"));
		String speechText = "Âge incorrect. Quel âge à le joueur " + (currentPlayerId + 1) + ".";
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt("Quel âge à le joueur" + (currentPlayerId + 1) + ".")
				.withSimpleCard("QuizCard", speechText)
				.build();
	}

	private Optional<Response> startQuestionAfterResponse(HandlerInput input) {
		if (input.matches(Predicates.intentName("StartGameIntent"))) {
			String speechText = APICall.call("de");
			return input.getResponseBuilder()
					.withSpeech(speechText)
					.withReprompt(speechText)
					.withSimpleCard("QuizCard", speechText)
					.withShouldEndSession(false)
					.build();
		}

		String speechText = "Voulez-vous commencer la partie?";
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(speechText)
				.withSimpleCard("QuizCard", speechText)
				.withShouldEndSession(false)
				.build();
	}

	private Optional<Response> selectCategoryAfterResponse(HandlerInput input) {
		if (input.matches(Predicates.intentName("AnswerCategoryIntent"))) {
			IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
			Map<String, Slot> slotMap = request.getIntent().getSlots();
			Slot answerCategorySlot = slotMap.get("AnswerCategory");

			if (answerCategorySlot.getValue() != null) {
				String speechText;
				try {
					String category = URLEncoder.encode(answerCategorySlot.getValue(), "UTF-8");
					speechText = APICall.call("setCategory?category=" + category);
				} catch (UnsupportedEncodingException e) {
					speechText = "Erreur d'encoding.";
				}

				return input.getResponseBuilder()
						.withSpeech(speechText)
						.withReprompt(speechText)
						.withSimpleCard("QuizCard", speechText)
						.withShouldEndSession(false)
						.build();
			}
		}

		String speechText = "Je n'ai pas compris votre demande. " + APICall.call("getCategories");
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(speechText)
				.withSimpleCard("QuizCard", speechText)
				.withShouldEndSession(false)
				.build();
	}

	private Optional<Response> answerQuestionAfterResponse(HandlerInput input) {
		if (input.matches(Predicates.intentName("AnswerIntent"))) {
			IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
			Map<String, Slot> slotMap = request.getIntent().getSlots();
			
			String slotName;
			if (
					slotMap.containsKey("AnswerLitteral") &&
					(slotMap.get("AnswerLitteral").getValue() != null)
			) {
				slotName = "AnswerLitteral";
			} else if (
					slotMap.containsKey("AnswerNumber") &&
					(slotMap.get("AnswerNumber").getValue() != null)
			) {
				slotName = "AnswerNumber";
			} else {
				return wrongAnswerAfterResponse(input);
			}

			String speechText;
			try {
				String playerResponse = URLEncoder.encode(slotMap.get(slotName).getValue(), "UTF-8");
				speechText = APICall.call("check?reponse=" + playerResponse);
			} catch (UnsupportedEncodingException e) {
				speechText = "Erreur d'encoding.";
			}

			speechText += APICall.call("de");
			return input.getResponseBuilder()
					.withSpeech(speechText)
					.withReprompt(speechText)
					.withSimpleCard("QuizCard", speechText)
					.withShouldEndSession(false)
					.build();
		}

		return wrongAnswerAfterResponse(input);
	}

	private Optional<Response> wrongAnswerAfterResponse(HandlerInput input) {
		String response = APICall.call("reponse");
		String repromptText = APICall.call("de");
		String speechText = "Mauvaise réponse. La réponse était: \"" + response  + "\". " + repromptText;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("QuizCard", speechText)
				.withShouldEndSession(false)
				.build();
	}
}
