/*
 * Tags individual words (or small groups) based on a tags file. The tags file should
 * be formatted like this:
 * "turn on" {COMMAND/Activate}
 * "turn off" {COMMAND/Deactivate}
 * 
 * If a tag has a value of 'i', it means a value should be filled in at runtime based on other words given in the phrase.
 * 		EX: phrase.rawText: "set the lights to 50 percent" tagged: {SETTER/50} {UNIT/percent} {OD_OBJECT/light}
 */

package speech.interpretation.utilities.tagger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import speech.interpretation.utilities.tagger.tags.Tag;
import speech.interpretation.utilities.tagger.tags.TagType;
import speech.interpretation.utilities.tagger.tags.TagUtilities;
import speech.synthesis.Synthesis;
import utilities.fileAccess.read.LineByLine;
import utilities.logger.Logger;
import events.EventManager;
import events.EventType;

public class Tagger {

	Logger logger;
	Synthesis synth;

	Path tagFile;
	private List<String> lines = null;

	public Tagger(Logger logger, Synthesis synth, Path tagFile) {
		this.logger = logger;
		this.synth = synth;
		this.tagFile = tagFile;

		lines = LineByLine.read(logger, tagFile); // read lines from file
	}

	public ArrayList<Tag> tagText(String text) {
		// will hold the tags
		ArrayList<Tag> tags = new ArrayList<Tag>();

		/* 
		 * TAG STANDARD TAGS
		 * (located in tagFile)
		 */

		// loop though each line, searching for matches in the phrase. Note: this must not simply device words by whitespace and match them, as some tags are multi-worded
		for (String s : lines) {
			String trigger = TagFileParser.getTrigger(logger, s);

			// check if it contains the trigger, but only if there are no characters surrounding it
			if (shouldTag(text, trigger)) {

				// standard tags
				Tag tag = TagFactory.getTag(logger, s, text);
				tags.add(tag);
			}
		}

		/*
		 * TAG NUMBERS
		 */

		// holds each word that was separated by whitespace
		String[] words = text.split(" ");

		// tag any numbers
		for (String word : words) {
			word = word.trim(); // just in case there were multiple spaces

			// is it a number?
			if (word.matches("-?\\d+(\\.\\d+)?")) { // woo! my first regex!

				// make a temporary tagFile line based off that number, as the TagFactory needs it to give a tag it's position on the line
				String tagFileLine = "\"" + word + "\" {NUMBER/" + word + "}";
				Tag tag = TagFactory.getTag(logger, tagFileLine, text);
				tags.add(tag);
			}
		}

		tags = TagUtilities.orderTags(tags);

		/*
		 * REPLACE {SETTER/i} + {NUMBER/x} with {SETTER/x}
		 * EX: {SETTER/i} + {NUMBER/20} + {OD_OBJECT/light} = {SETTER/20} {OD_OBJECT/light}
		 * 
		 * Tag order is important here
		 */

		// to avoid C.M.E, we need to add add and remove things *after* the for loop is complete.
		// addAfter contains Tags to add, addAt has the corresponding index it should be added at
		ArrayList<Tag> addAfter = new ArrayList<Tag>();
		ArrayList<Integer> addAt = new ArrayList<Integer>();

		// removeAfter contains Tags to remove
		ArrayList<Tag> removeAfter = new ArrayList<Tag>();

		for (Tag t : tags) {
			if (t.getType() == TagType.SETTER) {
				// we found a setter! now look for the next number

				int currentIndex = tags.indexOf(t) + 1; // look for the number

				Boolean searching = true;
				while (searching) {

					// quit when reaching the end of the arrayList in case there is no NUMBER
					if (currentIndex >= tags.size()) {
						break;
					}

					if (tags.get(currentIndex).getType() == TagType.NUMBER) {

						Tag oldSetter = t;
						Tag oldNumber = tags.get(currentIndex);

						// create the new SETTER tag with the value of the NUMBER tag
						Tag newSetter = new Tag(TagType.SETTER, oldNumber.getValue(), oldSetter.getPosition());

						// remove the old NUMBER tag
						//tags.remove(oldNumber);
						removeAfter.add(oldNumber);

						// replace the old SETTER with the new one
						//tags.add(tags.indexOf(oldSetter), newSetter);
						//tags.remove(oldSetter);
						addAfter.add(newSetter);
						addAt.add(tags.indexOf(oldSetter));

						removeAfter.add(oldSetter);
					}

					currentIndex++;
				}
			}
		}

		for (Tag t : removeAfter) {
			tags.remove(t);
		}

		for (Tag t : addAfter) {
			int index = addAt.get(addAfter.indexOf(t));
			tags.add(index, t);
		}

		// sort tags back into order in rawText
		tags = TagUtilities.orderTags(tags);

		return tags;
	}

	private Boolean shouldTag(String text, String trigger) {
		Boolean result = true;

		// first see if the trigger is even there
		if (text.contains(trigger)) {
			// now check to see if any characters besides whitespace surround the trigger (nothing is okay too, as that means it is the beginning or end of a line)

			int before = text.indexOf(trigger) - 1;
			int after = text.indexOf(trigger) + trigger.length();

			char charBefore = Character.UNASSIGNED;
			char charAfter = Character.UNASSIGNED;

			// check to see if either is a blank space (beginning or end of line)
			if (before != -1) {
				charBefore = text.charAt(before);
			}

			if (after != text.length()) {
				charAfter = text.charAt(after);
			}

			// not check if either whitespace or null
			if (charBefore != Character.UNASSIGNED && charBefore != ' ') {
				result = false;
			}

			if (charAfter != Character.UNASSIGNED && charAfter != ' ') {
				result = false;
			}

		} else {
			result = false;
		}

		return result;
	}

	private void loadTaglist() {
		lines = LineByLine.read(logger, tagFile); // read lines from file
	}

	public void reloadTaglist() {
		synth.speak("Re-loading tag list");

		loadTaglist();

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.TAGLIST_FILE_UPDATED, null);
	}
}