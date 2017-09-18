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
package example.sos.modulith.inventory;

import example.sos.modulith.catalog.Product;
import example.sos.modulith.catalog.Product.ProductAdded;
import example.sos.modulith.inventory.InventoryItem.OutOfStock;
import example.sos.modulith.orders.Order.OrderCompleted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
class InventoryListener {

	private final Inventory inventory;

	@EventListener
	void on(ProductAdded event) {

		if (!inventory.hasItemFor(event.getProduct())) {

			inventory.registerShipment(event.getProduct(), 0);

			log.info("Created inventory item for product {} and zero stock!", event.getProduct());
		}
	}

	@EventListener
	void on(OrderCompleted event) {

		log.info("Received completed order {}. Triggering stock update for line items.", event.getOrder());

		inventory.updateStockFor(event.getOrder());
	}

	@EventListener
	void on(OutOfStock event) {

		Product product = event.getProduct();
		long stock = inventory.getStockFor(product);

		log.info("Product {} out of stock! Current overdraw: {}.", product, stock);

		throw new InsufficientStock(product, stock);
	}
}
