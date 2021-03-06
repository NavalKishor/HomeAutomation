package net.sprakle.homeAutomation.externalSoftware;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.ArduinoFactory;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentreFactory;
import net.sprakle.homeAutomation.externalSoftware.software.speechRecognition.SpeechRecognitionFactory;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.SynthesisFactory;
import net.sprakle.homeAutomation.externalSoftware.software.weather.InternetWeatherFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

class SoftwareFactory {
	static SoftwareInterface getSoftware(Logger logger, CommandLineInterface cli, SoftwareName name, boolean active) {

		SoftwareInterfaceFactory factory = null;

		switch (name) {
			case MEDIA_CENTRE:
				factory = new MediaCentreFactory(logger, cli);
				break;

			case SYNTHESIS:
				factory = new SynthesisFactory(logger, cli);
				break;

			case ARDUINO:
				factory = new ArduinoFactory(logger);
				break;

			case INTERNET_WEATHER:
				factory = new InternetWeatherFactory(logger);
				break;

			case SPEECH_RECOGNITION:
				factory = new SpeechRecognitionFactory(logger);
				break;

			default:
				break;
		}

		return getFromFactory(factory, active);
	}

	static SoftwareInterface getFromFactory(SoftwareInterfaceFactory factory, boolean active) {
		if (active)
			return factory.getActiveSoftware();
		else
			return factory.getInactiveSoftware();
	}
}
