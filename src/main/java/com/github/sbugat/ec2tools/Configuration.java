package com.github.sbugat.ec2tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

public class Configuration {

	public static final String CONFIGURATION_FILE_NAME ="ec2tools.ini";

	private final Map<String,List<InstanceOrder>> configuredSections = new HashMap<String, List<InstanceOrder>>();

	public Configuration() throws ConfigurationException {

		final HierarchicalINIConfiguration hierarchicalINIConfiguration = new HierarchicalINIConfiguration( CONFIGURATION_FILE_NAME );

		//Sections loading
		for( final String section : hierarchicalINIConfiguration.getSections() ) {

			final List<InstanceOrder> sectionList = new ArrayList<InstanceOrder>();
			configuredSections.put( section, sectionList );

			final SubnodeConfiguration subnodeConfiguration = hierarchicalINIConfiguration.getSection( section );

			final Iterator<String> iterator = subnodeConfiguration.getKeys();
			while( iterator.hasNext() ) {

				final String instanceId = iterator.next();
				sectionList.add( new InstanceOrder( instanceId, subnodeConfiguration.getString( instanceId ) ) );
			}
		}
	}

	public List<InstanceOrder> getSectionOrders( final String section ){

		return configuredSections.get( section );
	}
}
