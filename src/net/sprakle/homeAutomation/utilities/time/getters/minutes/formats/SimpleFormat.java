package net.sprakle.homeAutomation.utilities.time.getters.minutes.formats;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;

public class SimpleFormat implements TimeFormat {

	private final Logger logger;

	public SimpleFormat(Logger logger) {
		this.logger = logger;
	}

	@Override
	public PhraseOutline getOutline() {
		PhraseOutline po = new PhraseOutline("simple minute format");
		po.addMandatoryTag(new Tag(TagType.NUMBER, null));
		po.addMandatoryTag(new Tag(TagType.TIME, "minute"));
		po.negateUnxepectedTagPenalty();
		return po;
	}

	@Override
	public int getTime(Phrase phrase) {
		Tag minuteTag = phrase.getTag(new Tag(TagType.TIME, "minute"));
		Tag numberTag = phrase.getRelativeTag(minuteTag, new Tag(TagType.NUMBER, null), -1, 1);

		String numberString = numberTag.getValue();
		return Integer.parseInt(numberString);
	}

}
