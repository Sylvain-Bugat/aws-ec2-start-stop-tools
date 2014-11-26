package com.github.sbugat.ec2tools;

public class InstanceOrder {

	public final String instanceId;

	public final OrderType orderType;

	public InstanceOrder( final String instanceIdArg, final String orderTypeArg ){

		instanceId = instanceIdArg;
		orderType = OrderType.valueOf( orderTypeArg );
	}
}
