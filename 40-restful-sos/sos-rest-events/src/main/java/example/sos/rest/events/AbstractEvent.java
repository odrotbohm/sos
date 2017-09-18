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
package example.sos.rest.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.core.ResolvableType;

/**
 * @author Oliver Gierke
 */
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EqualsAndHashCode(of = "id")
public abstract class AbstractEvent<T> implements Event {

	private final @Id UUID id = UUID.randomUUID();
	private final LocalDateTime publicationDate = LocalDateTime.now();
	private final Class<?> aggregateType;

	protected AbstractEvent() {

		this.aggregateType = ResolvableType //
				.forClass(AbstractEvent.class, this.getClass()) //
				.resolveGeneric(0);
	}

	/*
	 * (non-Javadoc)
	 * @see example.sos.rest.events.Event#getAggregateType()
	 */
	@Override
	public Class<?> getAggregateType() {
		return aggregateType;
	}
}
