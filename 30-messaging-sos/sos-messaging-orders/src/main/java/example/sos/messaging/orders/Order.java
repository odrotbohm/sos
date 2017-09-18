/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.sos.messaging.orders;

import example.sos.messaging.orders.ProductInfo.ProductNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.AbstractAggregateRoot;

/**
 * @author Oliver Gierke
 */
@Value
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends AbstractAggregateRoot<Order> {

	UUID id;
	List<LineItem> lineItems;
	@Wither(AccessLevel.PRIVATE) Status status;

	public static Order newOrder() {
		return new Order(UUID.randomUUID(), new ArrayList<>(), Status.PLACED);
	}

	public Order add(ProductInfo product, long quantity) {

		if (status == Status.COMPLETED) {
			throw new IllegalStateException("Order already completed.");
		}

		lineItems.stream() //
				.filter(it -> it.hasProductNumber(product.getProductNumber())) //
				.findFirst() //
				.map(it -> it.addAmount(quantity)) //
				.orElseGet(() -> {

					LineItem item = LineItem.of(product, quantity);
					lineItems.add(item);

					return item;
				});

		return this;
	}

	public Order complete() {

		Order order = withStatus(Status.COMPLETED).andEventsFrom(this);

		return order.andEvent(OrderCompleted.of(order));
	}

	private enum Status {
		PLACED, COMPLETED;
	}

	@Getter
	@AllArgsConstructor
	public static class LineItem {

		private final ProductNumber productNumber;
		private final BigDecimal price;
		private final String description;
		private Long quantity;

		public static LineItem of(ProductInfo info, Long quantity) {
			return new LineItem(info.getProductNumber(), info.getPrice(), info.getDescription(), quantity);
		}

		boolean hasProductNumber(ProductNumber number) {
			return this.productNumber == number;
		}

		LineItem addAmount(Long delta) {

			this.quantity = this.quantity + delta;

			return this;
		}
	}

	@Value
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class OrderCompleted {
		Order order;
	}
}
