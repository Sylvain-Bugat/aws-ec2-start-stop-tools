package com.github.sbugat.ec2tools;

/**
 * Loaded instance order
 *
 * @author Sylvain Bugat
 */
public class InstanceOrder {

	public final String instanceId;

	public final OrderType orderType;

	public InstanceOrder( final String instanceIdArg, final String orderTypeArg ){

		instanceId = instanceIdArg;
		orderType = OrderType.valueOf( orderTypeArg );
	}

	public String toString() {
		return instanceId + ':' + orderType;
	}
}
