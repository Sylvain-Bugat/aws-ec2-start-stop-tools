package com.github.sbugat.ec2tools.service.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.model.instance.InstanceOrder;
import com.google.common.collect.ImmutableList;

/**
 * Configuration loading class.
 * 
 * @author Sylvain Bugat
 * 
 */
@Singleton
public class ConfigurationService {

	/** SLF4J Xlogger. */
	private static final XLogger LOG = XLoggerFactory.getXLogger(ConfigurationService.class);

	/** Configuration file name. */
	public static final String CONFIGURATION_FILE_NAME = "ec2tools.ini";

	/** INI file loading configuration. */
	@Inject
	private HierarchicalINIConfiguration hierarchicalINIConfiguration;

	/** Loaded configuration. */
	private final Map<String, List<InstanceOrder>> configuredSections = new HashMap<String, List<InstanceOrder>>();

	/**
	 * Load the configuration from the configuration file.
	 * 
	 * @throws ConfigurationException configuration loading exception
	 */
	public void loadConfiguration() throws ConfigurationException {

		LOG.entry();
		configuredSections.clear();

		hierarchicalINIConfiguration.clear();
		hierarchicalINIConfiguration.load(CONFIGURATION_FILE_NAME);

		// Sections loading
		for (final String section : hierarchicalINIConfiguration.getSections()) {

			LOG.info("Loading section: {}", section);

			final List<InstanceOrder> sectionList = new ArrayList<InstanceOrder>();

			final SubnodeConfiguration subnodeConfiguration = hierarchicalINIConfiguration.getSection(section);

			final Iterator<String> iterator = subnodeConfiguration.getKeys();
			while (iterator.hasNext()) {

				final String instanceId = iterator.next();
				LOG.trace("Loading configuration for instanceId: {} -> order: {}", instanceId, subnodeConfiguration.getString(instanceId));
				sectionList.add(new InstanceOrder(instanceId, subnodeConfiguration.getString(instanceId)));
			}

			configuredSections.put(section, ImmutableList.copyOf(sectionList));
			LOG.info("section {} loaded with {} orders", section, sectionList.size());
		}

		LOG.exit();
	}

	/**
	 * Get all loaded sections.
	 * 
	 * @return all loaded sections.
	 */
	public Map<String, List<InstanceOrder>> getConfiguredSections() {

		return configuredSections;
	}

	/**
	 * Get a loaded section if it exists.
	 * 
	 * @param section section to return
	 * @return loaded sections or null
	 */
	public List<InstanceOrder> getConfiguredSections(final String section) {

		return configuredSections.get(section);
	}

	@Override
	public String toString() {

		final StringBuilder stringBuilder = new StringBuilder();

		for (final Entry<String, List<InstanceOrder>> section : configuredSections.entrySet()) {

			stringBuilder.append("Section ");
			stringBuilder.append(section.getKey());
			stringBuilder.append(System.lineSeparator());

			if (section.getValue().isEmpty()) {
				stringBuilder.append("	EMPTY");
			}
			else {
				for (final InstanceOrder instanceOrder : section.getValue()) {
					stringBuilder.append('	');
					stringBuilder.append(instanceOrder);
				}
			}

			stringBuilder.append(System.lineSeparator());
		}

		return stringBuilder.toString();
	}

	/**
	 * Get a string representation of one section.
	 * 
	 * @param sectionsToExecute section to
	 * @return the string of the section or empty if the section don't exists
	 */
	public String toString(final List<String> sectionsToExecute) {

		final StringBuilder stringBuilder = new StringBuilder();

		for (final Entry<String, List<InstanceOrder>> section : configuredSections.entrySet()) {

			if (sectionsToExecute.contains(section.getKey())) {
				stringBuilder.append("Section ");
				stringBuilder.append(section.getKey());
				stringBuilder.append(System.lineSeparator());

				if (section.getValue().isEmpty()) {
					stringBuilder.append("	EMPTY");
				}
				else {
					for (final InstanceOrder instanceOrder : section.getValue()) {
						stringBuilder.append('	');
						stringBuilder.append(instanceOrder);
					}
				}

				stringBuilder.append(System.lineSeparator());
			}
		}

		return stringBuilder.toString();
	}
}
