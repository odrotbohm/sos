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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.data.domain.AbstractAggregateRoot;

/**
 * @author Oliver Gierke
 */
@Data
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
class InventoryItem extends AbstractAggregateRoot<InventoryItem> {

	private @Id @GeneratedValue Long id;
	private @OneToOne Product product;
	private long amount;

	InventoryItem(Product product, long amount) {

		this.product = product;
		this.amount = amount;
	}

	InventoryItem refillBy(long amount) {

		this.amount = this.amount + amount;

		return this;
	}

	InventoryItem reduceStockBy(long amount) {

		if (this.amount < amount) {
			registerEvent(OutOfStock.of(product));
		}

		this.amount = this.amount - amount;

		return this;
	}

	@Value
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class OutOfStock {
		Product product;
	}
}
