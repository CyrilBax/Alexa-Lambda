package com.handlers;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.api.APICall;

public class CancelandStopIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(
				Predicates.intentName("AMAZON.StopIntent")
					.or(Predicates.intentName("AMAZON.CancelIntent"))
		);
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String speechText = "Vous avez arrêté la partie. Le joueur qui a le plus de points est "
				+ APICall.call("mostValuablePlayer")
				+ ". Merci d'avoir jouer à notre jeu du camembert. "
				+ "Si vous avez aimé notre jeu n'hésiter pas à mettre 20 sur 20 "
				+ "à notre application sur Amazon Alexa. "
				+ "Bonne vacance à vous.";
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withSimpleCard("HelloWorld", speechText)
				.build();
	}

}
