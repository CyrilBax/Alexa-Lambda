package com.handlers.game;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.api.APICall;

public class StartQuizIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(Predicates.requestType(LaunchRequest.class));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String speechText = "Bienvenue sur une partie de Trivial Pursuit. Combien y a t-il de joueur?";
		speechText = APICall.call("init");
		input.getAttributesManager().getSessionAttributes().put("sessionId", APICall.getSessionId());
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt("Combien y a t-il de joueur?")
				.withSimpleCard("QuizCard", speechText)
				.withShouldEndSession(false)
				.build();
	}
}
