package net.sprakle.homeAutomation.interpretation.module.modules.deleteCurrentSong;

import java.util.ArrayList;
import java.util.Stack;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentre;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DeleteCurrentSong implements InterpretationModule {

	private final Logger logger;
	private final Synthesis synth;
	private final MediaCentre mc;

	public DeleteCurrentSong(Logger logger, ExternalSoftware exs) {
		this.logger = logger;

		this.synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);
		this.mc = (MediaCentre) exs.getSoftware(SoftwareName.MEDIA_CENTRE);
	}

	@Override
	public boolean claim(Phrase phrase) {

		ArrayList<PhraseOutline> outlines = new ArrayList<>();
		PhraseOutline poA = new PhraseOutline(getName());
		poA.addMandatoryTag(new Tag(TagType.FILE_CONTROL, "delete"));
		poA.addMandatoryTag(new Tag(TagType.LANGUAGE, "this"));
		poA.addMandatoryTag(new Tag(TagType.MEDIA, "track"));
		outlines.add(poA);

		PhraseOutline match = phrase.matchOutlines(outlines);

		return outlines.contains(match);
	}

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Track track = mc.getCurrentTrack();

		if (phrases.size() == 1) {
			synth.speak("Are you sure you want to delete " + track + "?");
			return ExecutionResult.ADDITIONAL_INTERACTION_REQUIRED;
		} else {
			Phrase answerPhrase = phrases.lastElement();
			Tag answer = answerPhrase.getTag(new Tag(TagType.DECISION, null));

			if (answer != null && answer.getValue().equals("yes")) {

				if (track.delete())
					synth.speak("the track has been deleted");
				else
					synth.speak("I was unable to delete that track");
			} else {
				synth.speak("Ok, I will not delete the track");
			}

			return ExecutionResult.COMPLETE;
		}
	}
	@Override
	public String getName() {
		return "Delete current song";
	}

}
