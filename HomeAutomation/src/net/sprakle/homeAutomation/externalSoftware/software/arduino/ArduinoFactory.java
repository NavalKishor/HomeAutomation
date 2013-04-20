package net.sprakle.homeAutomation.externalSoftware.software.arduino;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ArduinoFactory implements SoftwareInterfaceFactory {

	private Logger logger;
	public ArduinoFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new ArduinoActive(logger);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new ArduinoInactive();
	}
}