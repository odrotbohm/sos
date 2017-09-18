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
package example.sos.messaging.catalog;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.AbstractAggregateRoot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Oliver Gierke
 */
@Value
@EqualsAndHashCode(of = "id", callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(creatorVisibility = NON_PRIVATE)
public class Product extends AbstractAggregateRoot<Product> {

	UUID id = UUID.randomUUID();
	String name, manufacturer;
	BigDecimal price;

	@JsonCreator
	public static Product of(String name, String manufacturer, BigDecimal price) {

		Product product = new Product(name, manufacturer, price);
		product.registerEvent(ProductAdded.of(product));

		return product;
	}

	interface Typed {

		default String getType() {
			return getClass().getSimpleName();
		}
	}

	@Value(staticConstructor = "of")
	public static class ProductAdded implements Typed {

		Product product;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "ProductAdded";
		}
	}
}
