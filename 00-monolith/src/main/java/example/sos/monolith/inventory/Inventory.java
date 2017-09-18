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
package example.sos.monolith.inventory;

import example.sos.monolith.catalog.Product;
import example.sos.monolith.orders.Order;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@Transactional
@Component
@RequiredArgsConstructor
public class Inventory {

	private final InventoryItemRepository items;

	public InventoryItem addItemFor(Product product, long amount) {
		return items.save(new InventoryItem(product, amount));
	}

	public void updateStockFor(Order order) {

		order.getLineItems().stream().forEach(it -> {

			Product product = it.getProduct();

			InventoryItem item = items.findByProduct(product)//
					.orElseThrow(
							() -> new IllegalStateException(String.format("No InventoryItem found for product %s!", product)));

			item.reduceStockBy(it.getAmount());
			items.save(item);
		});
	}
}
