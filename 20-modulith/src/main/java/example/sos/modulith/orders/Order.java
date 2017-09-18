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
package example.sos.modulith.orders;

import example.sos.modulith.catalog.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.domain.AbstractAggregateRoot;

/**
 * @author Oliver Gierke
 */
@Data
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@Entity
@Table(name = "ORDERS")
public class Order extends AbstractAggregateRoot<Order> {

	private @Id @GeneratedValue Long id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) //
	private List<LineItem> lineItems;

	private @Wither Status status;

	Order() {
		this.lineItems = new ArrayList<>();
		this.status = Status.SUBMITTED;
	}

	public Order add(Product product, long amount) {

		Optional<LineItem> lineItem = getLineItems().stream()//
				.filter(it -> it.getProduct().equals(product))//
				.findFirst();

		if (lineItem.isPresent()) {
			lineItem.ifPresent(it -> it.increaseQuantityBy(amount));
		} else {
			getLineItems().add(new LineItem(product, amount));
		}

		return this;
	}

	Order complete() {

		return withStatus(Status.COMPLETED) //
				.andEventsFrom(this) //
				.andEvent(OrderCompleted.of(this));
	}

	public enum Status {
		SUBMITTED, COMPLETED;
	}

	@Value
	@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
	public static class OrderCompleted {
		Order order;
	}
}
