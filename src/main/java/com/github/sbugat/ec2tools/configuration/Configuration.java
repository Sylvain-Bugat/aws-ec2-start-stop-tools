package com.github.sbugat.ec2tools.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.InstanceOrder;

/**
 * Configuration loading class
 * 
 * @author Sylvain Bugat
 * 
 */
public class Configuration {

	/**
	 * SLF4J Xlogger
	 */
	private static final XLogger log = XLoggerFactory.getXLogger(Configuration.class);

	/**
	 * Configuration file name
	 */
	public static final String CONFIGURATION_FILE_NAME = "ec2tools.ini";

	/**
	 * Loaded configuration
	 */
	private final Map<String, List<InstanceOrder>> configuredSections = new HashMap<String, List<InstanceOrder>>();

	public Configuration() throws ConfigurationException {

		log.entry();

		final HierarchicalINIConfiguration hierarchicalINIConfiguration = new HierarchicalINIConfiguration(CONFIGURATION_FILE_NAME);

		// Sections loading
		for (final String section : hierarchicalINIConfiguration.getSections()) {

			log.info("Loading section: {}", section);

			final List<InstanceOrder> sectionList = new ArrayList<InstanceOrder>();
			configuredSections.put(section, sectionList);

			final SubnodeConfiguration subnodeConfiguration = hierarchicalINIConfiguration.getSection(section);

			final Iterator<String> iterator = subnodeConfiguration.getKeys();
			while (iterator.hasNext()) {

				final String instanceId = iterator.next();
				log.trace("Loading configuration for instanceId: {} -> order: {}", instanceId, subnodeConfiguration.getString(instanceId));
				sectionList.add(new InstanceOrder(instanceId, subnodeConfiguration.getString(instanceId)));
			}

			log.info("section {} loaded with {} orders", section, sectionList.size());
		}

		log.exit();
	}

	public List<InstanceOrder> getSectionOrders(final String section) {

		return configuredSections.get(section);
	}

	@Override
	public String toString() {

		final StringBuilder stringBuilder = new StringBuilder();

		for (final Entry<String, List<InstanceOrder>> section : configuredSections.entrySet()) {

			stringBuilder.append("Section {}");
			stringBuilder.append(section.getKey());
			stringBuilder.append(System.lineSeparator());

			if (section.getValue().isEmpty()) {
				stringBuilder.append("	EMPTY");
				stringBuilder.append(System.lineSeparator());
			}
			else {
				for (final InstanceOrder instanceOrder : section.getValue()) {
					stringBuilder.append('	');
					stringBuilder.append(instanceOrder);
					stringBuilder.append(System.lineSeparator());
				}
			}

			stringBuilder.append(System.lineSeparator());
		}

		return stringBuilder.toString();
	}

	public String toString(final List<String> sectionsToExecute) {

		final StringBuilder stringBuilder = new StringBuilder();

		for (final Entry<String, List<InstanceOrder>> section : configuredSections.entrySet()) {

			if (sectionsToExecute.contains(section.getKey())) {
				stringBuilder.append("Section {}");
				stringBuilder.append(section.getKey());
				stringBuilder.append(System.lineSeparator());

				if (section.getValue().isEmpty()) {
					stringBuilder.append("	EMPTY");
					stringBuilder.append(System.lineSeparator());
				}
				else {
					for (final InstanceOrder instanceOrder : section.getValue()) {
						stringBuilder.append('	');
						stringBuilder.append(instanceOrder);
						stringBuilder.append(System.lineSeparator());
					}
				}

				stringBuilder.append(System.lineSeparator());
			}
		}

		return stringBuilder.toString();
	}
}
