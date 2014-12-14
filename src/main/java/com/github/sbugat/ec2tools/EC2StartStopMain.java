package com.github.sbugat.ec2tools;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.github.sbugat.ec2tools.service.AmazonEC2Service;

/**
 * Main class of start-stop EC2 tools
 *
 * @author Sylvain Bugat
 *
 */
public class EC2StartStopMain {

	private static final XLogger log = XLoggerFactory.getXLogger( EC2StartStopMain.class );

	public static void main( final String args[] ) {

		log.entry( (Object[]) args );
		log.info( "Start of EC2StartStop tools" );

		//Configuration loading
		final Configuration configuration;
		try {
			configuration = new Configuration();
		}
		catch (final ConfigurationException e) {

			log.error( "Error during configuration loading ", e );
			log.exit( 1 );
			System.exit( 1 );
			//Just for the final: dead code
			return;
		}

		//Amazon EC2 service initialization
		final AmazonEC2Service amazonEC2Service;
		try {
			amazonEC2Service = new AmazonEC2Service();
		}
		catch (final Exception e) {

			log.error( "Error during Amazon initialization ", e );
			log.exit( 1 );
			System.exit( 1 );
			//Just for the final: dead code
			return;
		}

		//Process all arguments sections
		boolean error = false;
		for( final String section : args ){

			//Get all orders of a section
			final List<InstanceOrder> orderList = configuration.getSectionOrders( section );

			if( null != orderList ) {

				//Process each order
				for( final InstanceOrder instanceOrder : orderList ){

					if( ! processOrder( amazonEC2Service, instanceOrder ) ) {

						error = true;
					}
				}
			}
			//If a section is unknown (and not empty)
			else {
				log.error( "Error unknown section {}", section );
				error = true;
			}
		}

		//Return in error if a section is unknown or if an order cannot be done
		if( error ) {
			log.error( "End of EC2StartStop tools with errors" );
			log.exit( 1 );
			System.exit( 1 );
		}

		log.info( "End of EC2StartStop tools with success" );
		log.exit( 0 );
		System.exit( 0 );
	}

	/**
	 * Process an order to start or stop an Amazon EC2 instance
	 *
	 * @param amazonEC2Service service to use
	 * @param instanceOrder order to process
	 * @return true is the order has been processed with success, false otherwise
	 */
	private static boolean processOrder( final AmazonEC2Service amazonEC2Service, final InstanceOrder instanceOrder ){

		log.entry( amazonEC2Service, instanceOrder );
		//Starting instance order
		if(OrderType.START == instanceOrder.orderType ) {

			try {
				amazonEC2Service.startInstance( instanceOrder.instanceId );
			}
			//Starting error
			catch( final Exception e ){
				log.error( "Error starting instance {}", instanceOrder.instanceId, e );
				return false;
			}
		}
		//Stoping instance order
		else if(OrderType.STOP == instanceOrder.orderType ) {

			try {
				amazonEC2Service.stopInstance( instanceOrder.instanceId );
			}
			//Stoping error
			catch( final Exception e ){
				log.error( "Error stoping instance {}", instanceOrder.instanceId, e );
				log.exit( false );
				return false;
			}
		}

		log.exit( true );
		return true;
	}
}
