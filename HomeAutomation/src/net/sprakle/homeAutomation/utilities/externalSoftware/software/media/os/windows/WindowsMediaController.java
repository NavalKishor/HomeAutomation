package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.windows;

import net.sprakle.homeAutomation.utilities.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os.MediaController;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class WindowsMediaController extends MediaController {

	Logger logger;
	CommandLineInterface cli;

	public WindowsMediaController(Logger logger, CommandLineInterface cli) {
		super(logger);

		this.logger = logger;
		this.cli = cli;
	}

	@Override
	public void playbackCommand(PlaybackCommand pc) {
		freakOut();
	}

	@Override
	public void playTrack(Track track) {
		freakOut();
	}

	@Override
	public void enqueueTrack(Track track) {
		freakOut();
	}

	@Override
	public void loadTracks() {
		freakOut();
	}

	@Override
	public void setVolume(double vol) {
		freakOut();
	}

	@Override
	public void changeVolume(double change) {
		freakOut();
	}

	private void freakOut() {
		logger.log("Windows Media Controller not yet implemented!", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
	}

}
