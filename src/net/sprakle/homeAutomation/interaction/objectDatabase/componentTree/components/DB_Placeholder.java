package net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.components;

import net.sprakle.homeAutomation.interaction.objectDatabase.ComponentType;
import net.sprakle.homeAutomation.interaction.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DB_Placeholder extends Component {

	public DB_Placeholder(Logger logger, Component parent, String identifier) {
		super(logger, parent, identifier);

		this.componentType = ComponentType.DB_PLACEHOLDER;
	}

}
