package com.github.sbugat.ec2tools.model.instance;

/**
 * Loaded instance order.
 * 
 * @author Sylvain Bugat
 */
public final class InstanceOrder {

	/** Instande identifier of the order. */
	private final String instanceId;

	/** Type of the order. */
	private final OrderType orderType;

	/**
	 * Copy arguments constructor.
	 * 
	 * @param instanceIdArg instance identifier to copy
	 * @param orderTypeArg order type to copy
	 */
	public InstanceOrder(final String instanceIdArg, final String orderTypeArg) {

		instanceId = instanceIdArg;
		orderType = OrderType.valueOf(orderTypeArg);
	}

	/**
	 * Return the instance identifier.
	 * 
	 * @return instance identifier
	 */
	public String getInstanceId() {
		return instanceId;
	}

	/**
	 * return the order type.
	 * 
	 * @return order type
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	@Override
	public String toString() {
		return instanceId + ':' + orderType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = prime;
		if (null != instanceId) {
			result += instanceId.hashCode();
		}

		result *= prime;
		if (null != orderType) {
			result += orderType.hashCode();
		}

		return result;
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
