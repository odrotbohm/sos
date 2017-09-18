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
package example.sos.monolith.orders;

import example.sos.monolith.catalog.Product;
import example.sos.monolith.inventory.Inventory;
import example.sos.monolith.orders.Order.Status;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@Component
@Transactional
@RequiredArgsConstructor
public class OrderManager {

	private final Inventory inventory;
	private final OrderRepository orders;

	public Order createOrder() {
		return orders.save(new Order());
	}

	public void addToOrder(Order order, Product product, long amount) {

		Optional<LineItem> lineItem = order.getLineItems().stream()//
				.filter(it -> it.getProduct().equals(product))//
				.findFirst();

		if (lineItem.isPresent()) {
			lineItem.ifPresent(it -> it.increaseQuantityBy(amount));
		} else {
			order.getLineItems().add(new LineItem(product, amount));
		}
	}

	public void completeOrder(Order order) {

		order.setStatus(Status.COMPLETED);

		inventory.updateStockFor(order);

		orders.save(order);
	}
}
