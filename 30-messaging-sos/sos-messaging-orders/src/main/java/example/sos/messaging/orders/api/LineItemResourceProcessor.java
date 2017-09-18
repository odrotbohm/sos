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

import example.sos.messaging.orders.Order.LineItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.cloud.client.hypermedia.RemoteResource;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Gierke
 */
@Component
@RequiredArgsConstructor
class LineItemResourceProcessor implements ResourceProcessor<Resource<LineItem>> {

	private final @NonNull RemoteResource productResource;

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 */
	@Override
	public Resource<LineItem> process(Resource<LineItem> resource) {

		LineItem content = resource.getContent();

		Optional.ofNullable(productResource.getLink())//
				.map(it -> it.expand(content.getProductNumber().getValue())) //
				.map(it -> it.withRel("product")) //
				.ifPresent(it -> resource.add(it));

		return resource;
	}
}
