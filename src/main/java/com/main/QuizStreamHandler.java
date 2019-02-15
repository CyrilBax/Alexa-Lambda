package com.main;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.handlers.CancelandStopIntentHandler;
import com.handlers.HelpIntentHandler;
import com.handlers.SessionEndedRequestHandler;
import com.handlers.game.ResponseIntentHandler;
import com.handlers.game.StartQuizIntentHandler;

public class QuizStreamHandler extends SkillStreamHandler {

	private static Skill getSkill() {
		return Skills.standard()
				.addRequestHandlers(
					new CancelandStopIntentHandler(),
					new HelpIntentHandler(),
					new SessionEndedRequestHandler(),
					new StartQuizIntentHandler(),
					new ResponseIntentHandler())
				.withSkillId("amzn1.ask.skill.f90cb312-8a7f-44c1-929e-ca16a9504ae5")
				.build();
	}

	public QuizStreamHandler() {
		super(getSkill());
	}

}
