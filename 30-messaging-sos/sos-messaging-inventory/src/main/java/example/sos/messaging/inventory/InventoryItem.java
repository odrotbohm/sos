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
package example.sos.messaging.inventory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Oliver Gierke
 */
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Setter(value = AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class InventoryItem extends AbstractAggregateRoot<InventoryItem> {

	private final UUID id = UUID.randomUUID();

	private @Indexed(unique = true) UUID productId;
	private String name;
	private long amount;

	public static InventoryItem of(UUID productId, String name, long amount) {

		InventoryItem item = new InventoryItem(productId, name, amount);
		item.registerEvent(ProductOutOfStock.of(productId));

		return item;
	}

	InventoryItem decrease(long amount) {

		this.amount = this.amount - amount;

		log.info("Decreasing amount of inventory for product {} by {} to {}.", productId, amount, this.amount);

		if (this.amount < 1) {
			registerEvent(ProductOutOfStock.of(productId));
		}

		return this;
	}

	InventoryItem increase(long amount) {

		this.amount = this.amount + amount;

		return this;
	}

	@Value(staticConstructor = "of")
	public static class ProductOutOfStock {
		UUID productId;
	}
}
