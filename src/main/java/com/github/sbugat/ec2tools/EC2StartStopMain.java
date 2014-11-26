package com.github.sbugat.ec2tools;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.github.sbugat.ec2tools.service.AmazonEC2Service;

public class EC2StartStopMain {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger( EC2StartStopMain.class );

	public static void main( final String args[] ) {

		log.info( "Starting EC2Tools" );

		//Configuration loading
		final Configuration configuration;
		try {
			configuration = new Configuration();
		}
		catch (final ConfigurationException e) {

			log.error( "Error during configuration loading ", e );
			System.exit( 1 );
			//Just for the final: dead code
			return;
		}

		final AmazonEC2Service amazonEC2Service = new AmazonEC2Service();

		for( final String section : args ){

			final List<InstanceOrder> orderList = configuration.getSectionOrders( section );

			if( null != orderList ) {

				for( final InstanceOrder order : orderList ){

					if(OrderType.START == order.orderType ) {

						amazonEC2Service.startInstance( order.instanceId );
					}
					else if(OrderType.STOP == order.orderType ) {

						amazonEC2Service.stopInstance( order.instanceId );
					}
				}
			}
		}

		System.exit( 0 );
	}
}
