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
import example.sos.messaging.inventory.integration.Payloads.OrderCompleted;
import example.sos.messaging.inventory.integration.Payloads.ProductAdded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
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
	private final ProjectionFactory projectionFactory;

	/**
	 * Creates a new {@link InventoryItem} for the product that was added.
	 * 
	 * @param message
	 */
	@KafkaListener(topics = "products")
	public void onProductAdded(Message<String> message) throws IOException {

		ProductAdded event = readFromMessage(message, ProductAdded.class);

		Optional<InventoryItem> item = inventory.findByProductId(event.getProductId());

		if (item.isPresent()) {
			log.info("Inventory item for product {} already available!", event.getProductId());
			return;
		}

		log.info("Creating inventory item for product {}.", event.getProductId());

		item.orElseGet(() -> inventory.save(InventoryItem.of(event.getProductId(), event.getName(), 0L)));
	}

	/**
	 * Updates the current stock by reducing the inventory items by the amount of the individual line items in the order.
	 * 
	 * @param event
	 */
	@Transactional
	@KafkaListener(topics = "orders")
	void onOrderCompleted(Message<String> message) {

		OrderCompleted event = readFromMessage(message, OrderCompleted.class);

		log.info("Order completed: {}", event.getOrderId().toString());

		event.getLineItems().stream() //
				.peek(it -> log.info("Decreasing quantity of product {} by {}.", it.getProductNumber(), it.getQuantity()))
				.forEach(it -> inventory.updateInventoryItem(it.getProductNumber(), it.getQuantity()));
	}

	private <T> T readFromMessage(Message<String> message, Class<T> type) {

		try (InputStream stream = new ByteArrayInputStream(message.getPayload().getBytes())) {
			return projectionFactory.createProjection(type, stream);
		} catch (IOException o_O) {
			throw new RuntimeException(o_O);
		}
	}
}
