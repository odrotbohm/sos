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
package example.sos.messaging.orders.api;

import example.sos.messaging.orders.Order;
import example.sos.messaging.orders.OrderRepository;
import example.sos.messaging.orders.ProductInfo;
import example.sos.messaging.orders.ProductInfoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Oliver Gierke
 */
@RestController
@RequiredArgsConstructor
class OrderController {

	private final @NonNull ProductInfoRepository productInfos;
	private final @NonNull OrderRepository orders;
	private final @NonNull RepositoryEntityLinks links;

	@PostMapping("/orders/new")
	HttpEntity<?> createOrder() {

		Iterable<ProductInfo> infos = productInfos.findAll(Sort.by("createdDate").descending());

		ProductInfo info = Streamable.of(infos).stream() //
				.findFirst() //
				.orElseThrow(() -> new IllegalStateException("No ProductInfo found!"));

		Order order = Order.newOrder();
		order.add(info, 2);

		orders.save(order.complete());

		return ResponseEntity //
				.created(links.linkForSingleResource(Order.class, order.getId()).toUri()) //
				.build();
	}
}
