package com.github.sbugat.ec2tools;

/**
 * Loaded instance order.
 * 
 * @author Sylvain Bugat
 */
public class InstanceOrder {

	public final String instanceId;

	public final OrderType orderType;

	public InstanceOrder(final String instanceIdArg, final String orderTypeArg) {

		instanceId = instanceIdArg;
		orderType = OrderType.valueOf(orderTypeArg);
	}

	@Override
	public String toString() {
		return instanceId + ':' + orderType;
	}

	@Override
	public boolean equals(final Object object) {

		if (this == object) {
			return true;
		}

		if (object == null || !InstanceOrder.class.isInstance(object)) {
			return false;
		}

		final InstanceOrder instanceOrder = (InstanceOrder) object;

		if (instanceId != null && !instanceId.equals(instanceOrder.instanceId) || orderType != instanceOrder.orderType) {
			return false;
		}

		return true;
	}
}
