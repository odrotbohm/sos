/*
 * Copyright 2017-2021 the original author or authors.
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

import static org.assertj.core.api.Assertions.*;

import example.sos.monolith.catalog.Catalog;
import example.sos.monolith.catalog.Product;
import example.sos.monolith.inventory.InsufficientStock;
import example.sos.monolith.inventory.InventoryItem;
import example.sos.monolith.inventory.InventoryItemRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@SpringBootTest
@Transactional
public class OrderManagerIntegrationTests {

	@Autowired OrderManager orders;
	@Autowired Catalog catalog;
	@Autowired InventoryItemRepository inventory;

	@Test
	public void completingAnOrderUpdatesInventory() {

		Product product = catalog.findAll().iterator().next();
		long before = inventory.findByProduct(product) //
				.orElseThrow(() -> new IllegalStateException()) //
				.getAmount();

		Order order = orders.createOrder();
		orders.addToOrder(order, product, 5);
		orders.completeOrder(order);

		assertThat(inventory.findByProduct(product)) //
				.map(InventoryItem::getAmount) //
				.hasValue(before - 5);
	}

	@Test
	public void throwsExceptionInCaseProductIsOutOfStock() {

		completingAnOrderUpdatesInventory();
		completingAnOrderUpdatesInventory();

		assertThatExceptionOfType(InsufficientStock.class) //
				.isThrownBy(() -> completingAnOrderUpdatesInventory());
	}
}
