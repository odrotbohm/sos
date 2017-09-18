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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.util.StringUtils;

/**
 * @author Oliver Gierke
 */
@RepositoryRestResource(exported = false)
public interface EventRepository
		extends Repository<AbstractEvent<?>, UUID>, QuerydslPredicateExecutor<AbstractEvent<?>> {

	Optional<AbstractEvent<?>> findById(UUID id);

	Page<AbstractEvent<?>> findAll(Pageable pageable);

	Page<AbstractEvent<?>> findByAggregateType(Class<?> type, Pageable pageable);

	@Query("select e from AbstractEvent e where type(e) = ?1")
	Page<AbstractEvent<?>> findByType(Class<?> type, Pageable pageable);

	@Query("select distinct(e.aggregateType) from AbstractEvent e")
	List<Class<?>> findAggregateTypes();

	@Query("select distinct(type(e)) from AbstractEvent e")
	List<Class<? extends AbstractEvent<?>>> findEventTypes();

	default <T extends AbstractEvent<?>> Optional<Class<? extends AbstractEvent<?>>> findEventTypeByName(String name) {

		return findEventTypes().stream() //
				.filter(it -> StringUtils.uncapitalize(it.getSimpleName()).equals(name)) //
				.findFirst();
	}

	<T extends AbstractEvent<?>> T save(T event);

	long count();
}
