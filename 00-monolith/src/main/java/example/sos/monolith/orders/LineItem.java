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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Oliver Gierke
 */
@Data
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class LineItem {

	private @Id @GeneratedValue Long id;
	private @ManyToOne Product product;
	private long amount;

	LineItem(Product product, long amount) {

		this.product = product;
		this.amount = amount;
	}

	public LineItem increaseQuantityBy(long amount) {

		this.amount = this.amount + amount;

		return this;
	}
}
