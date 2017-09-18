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

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.web.JsonPath;

/**
 * @author Oliver Gierke
 */
class Payloads {

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

	interface ProductAdded {

		@JsonPath("$.product.id")
		UUID getProductId();

		@JsonPath("$.product.name")
		String getName();
	}
}
