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
import example.sos.modulith.orders.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author Oliver Gierke
 */
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class Inventory {

	private final InventoryItemRepository items;

	/**
	 * Returns the amount of items currently available for the given {@link Product}.
	 * 
	 * @param product must not be {@literal null}.
	 * @return
	 */
	public long getStockFor(Product product) {

		Assert.notNull(product, "Product must not be null!");

		return items.findByProduct(product) //
				.map(InventoryItem::getAmount) //
				.orElseThrow(() -> new IllegalArgumentException());
	}

	/**
	 * Returns whether we have an {@link InventoryItem} for the given {@link Product}.
	 * 
	 * @param product must not be {@literal null}.
	 * @return
	 */
	public boolean hasItemFor(Product product) {

		Assert.notNull(product, "Product must not be null!");

		return items.findByProduct(product).isPresent();
	}

	/**
	 * Registers a shipment of the given given amount of the given {@link Product}.
	 * 
	 * @param product must not be {@literal null}.
	 * @param amount
	 * @return
	 */
	public InventoryItem registerShipment(Product product, long amount) {

		Assert.notNull(product, "Product must not be null!");

		log.info("Registering shipment of {} {}.", amount, product.getName());

		InventoryItem item = items.findByProduct(product) //
				.map(it -> it.refillBy(amount)) //
				.orElseGet(() -> new InventoryItem(product, amount));

		return items.save(item);
	}

	/**
	 * Updates the stock for all line items contained in the given {@link Order}.
	 * 
	 * @param order must not be {@literal null}.
	 */
	public void updateStockFor(Order order) {

		order.getLineItems().stream().forEach(it -> {

			Product product = it.getProduct();

			InventoryItem item = items.findByProduct(product)//
					.orElseThrow(
							() -> new IllegalStateException(String.format("No InventoryItem found for product %s!", product)));

			log.info("About to reduce stock for {} by {}.", product, item.getAmount());

			item.reduceStockBy(it.getAmount());

			items.save(item);
		});
	}
}
