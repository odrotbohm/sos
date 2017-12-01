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
package example.sos.messaging.inventory.integration;

import example.sos.messaging.inventory.Inventory;
import example.sos.messaging.inventory.InventoryItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.web.JsonPath;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@Slf4j
@Component
@RequiredArgsConstructor
class KafkaIntegration {

	private final Inventory inventory;

	/**
	 * Creates a new {@link InventoryItem} for the product that was added.
	 * 
	 * @param message will never be {@literal null}.
	 */
	@KafkaListener(topics = "products")
	public void onProductAdded(ProductAdded message) throws IOException {

		Optional<InventoryItem> item = inventory.findByProductId(message.getProductId());

		if (item.isPresent()) {
			log.info("Inventory item for product {} already available!", message.getProductId());
			return;
		}

		log.info("Creating inventory item for product {}.", message.getProductId());

		item.orElseGet(() -> inventory.save(InventoryItem.of(message.getProductId(), message.getName(), 0L)));
	}

	interface ProductAdded {

		@JsonPath("$.product.id")
		UUID getProductId();

		@JsonPath("$.product.name")
		String getName();
	}

	/**
	 * Updates the current stock by reducing the inventory items by the amount of the individual line items in the order.
	 * 
	 * @param message will never be {@literal null}.
	 */
	@Transactional
	@KafkaListener(topics = "orders")
	void on(OrderCompleted message) {

		log.info("Order completed: {}", message.getOrderId().toString());

		message.getLineItems().stream() //
				.peek(it -> log.info("Decreasing quantity of product {} by {}.", it.getProductNumber(), it.getQuantity()))
				.forEach(it -> inventory.updateInventoryItem(it.getProductNumber(), it.getQuantity()));
	}

	interface OrderCompleted {

		@JsonPath("$.order.id")
		UUID getOrderId();

		@JsonPath("$.order.lineItems")
		Collection<LineItem> getLineItems();

		interface LineItem {

			UUID getProductNumber();

			Long getQuantity();
		}
	}
}
