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
package example.sos.rest.events.api;

import example.sos.rest.events.AggregateReference;
import example.sos.rest.events.Event;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Gierke
 */
@Component
@RequiredArgsConstructor
public class EventResourceProcessor implements ResourceProcessor<Resource<Event>> {

	private final PersistentEntities entities;
	private final EntityLinks links;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 */
	@Override
	public Resource<Event> process(Resource<Event> resource) {

		PersistentEntity<?, ? extends PersistentProperty<?>> entity = entities
				.getRequiredPersistentEntity(resource.getContent().getClass());
		PersistentPropertyAccessor accessor = entity.getPropertyAccessor(resource.getContent());

		for (PersistentProperty<?> property : entity.getPersistentProperties(AggregateReference.class)) {

			Object propertyValue = accessor.getProperty(property);
			AggregateReference annotation = property.getRequiredAnnotation(AggregateReference.class);

			if (annotation.type().equals(Class.class)) {

				Class<?> type = property.getType();
				PersistentEntity<?, ? extends PersistentProperty<?>> propertyEntity = entities
						.getRequiredPersistentEntity(type);
				IdentifierAccessor identifierAccessor = propertyEntity.getIdentifierAccessor(propertyValue);

				resource.add(links.linkToSingleResource(type, identifierAccessor.getIdentifier()));
			} else {
				resource.add(links.linkToSingleResource(annotation.type(), propertyValue));
			}
		}

		return resource;
	}
}
