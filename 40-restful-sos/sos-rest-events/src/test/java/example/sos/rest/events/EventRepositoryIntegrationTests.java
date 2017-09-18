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

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Gierke
 */
@Transactional
@DataJpaTest
@RunWith(SpringRunner.class)
public class EventRepositoryIntegrationTests {

	@SpringBootApplication
	static class TestConfiguration {}

	@Autowired EventRepository events;

	@Test
	public void persistsEvents() {

		FirstEvent firstEvent = events.save(new FirstEvent());
		SecondEvent secondEvent = events.save(new SecondEvent());

		assertThat(events.count()).isEqualTo(2L);

		Iterable<AbstractEvent<?>> result = events.findAll();

		assertThat(result).hasSize(2);
		assertThat(result).contains(firstEvent, secondEvent);
	}

	@Test
	public void findsEventsByAggregateType() {

		FirstEvent firstEvent = events.save(new FirstEvent());
		events.save(new SecondEvent());

		Page<AbstractEvent<?>> result = events.findByAggregateType(String.class, PageRequest.of(0, 10));

		assertThat(result).hasSize(1);
		assertThat(result).contains(firstEvent);
	}

	@Test
	public void findsEventsByEventType() {

		events.save(new FirstEvent());
		SecondEvent secondEvent = events.save(new SecondEvent());

		Page<AbstractEvent<?>> result = events.findByType(SecondEvent.class, PageRequest.of(0, 10));

		assertThat(result).hasSize(1);
		assertThat(result).contains(secondEvent);
	}
}
