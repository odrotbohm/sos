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
package example.sos.rest.catalog;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.data.domain.AbstractAggregateRoot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Oliver Gierke
 */
@Data
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(creatorVisibility = NON_PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Product extends AbstractAggregateRoot<Product> {

	private final @Id @GeneratedValue Long id;
	private String description;
	private BigDecimal price;

	@JsonCreator
	public static Product of(String description, BigDecimal price) {

		Product product = new Product(null, description, price);
		product.registerEvent(ProductAdded.of(product));

		return product;
	}

	Product withPrice(BigDecimal price) {

		Product product = new Product(id, description, price);
		product.registerEvent(PriceChanged.of(id, this.price, price));

		return product;
	}
}
