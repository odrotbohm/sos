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
package example.sos.modulith.orders;

import static org.assertj.core.api.Assertions.*;

import example.sos.modulith.catalog.Catalog;
import example.sos.modulith.catalog.Product;
import example.sos.modulith.inventory.InsufficientStock;
import example.sos.modulith.inventory.Inventory;

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
	@Autowired Inventory inventory;

	@Test
	public void completingAnOrderUpdatesInventory() {

		Product product = catalog.findAll().iterator().next();
		long before = inventory.getStockFor(product);

		Order order = orders.createOrder();
		order.add(product, 5);
		orders.complete(order);

		assertThat(inventory.getStockFor(product)).isEqualTo(before - 5);
	}

	@Test
	public void throwsExceptionInCaseProductIsOutOfStock() throws Exception {

		completingAnOrderUpdatesInventory();
		completingAnOrderUpdatesInventory();

		assertThatExceptionOfType(InsufficientStock.class) //
				.isThrownBy(() -> completingAnOrderUpdatesInventory());
	}
}
