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

import lombok.RequiredArgsConstructor;

import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Gierke
 */
@Component
@RequiredArgsConstructor
public class ProductLinkProcessor implements RepresentationModelProcessor<RepositoryLinksResource> {

	private final RepositoryEntityLinks entityLinks;
	private final LinkRelationProvider relProvider;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 */
	@Override
	public RepositoryLinksResource process(RepositoryLinksResource resource) {

		Link products = entityLinks.linkToCollectionResource(Product.class);
		UriTemplate template = UriTemplate.of(products.getHref().concat("/{id}"));

		resource.add(Link.of(template, relProvider.getItemResourceRelFor(Product.class)));

		return resource;
	}
}
