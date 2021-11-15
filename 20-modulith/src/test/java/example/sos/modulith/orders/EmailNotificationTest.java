/*
 * Copyright 2017-2021 the original author or authors.
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
package example.sos.modulith.orders;

import static org.assertj.core.api.Assertions.*;

import example.sos.modulith.catalog.Catalog;
import example.sos.modulith.catalog.Product;
import example.sos.modulith.orders.Order.OrderCompleted;

import org.junit.jupiter.api.Test;
import org.moduliths.events.EventPublication;
import org.moduliths.events.EventPublicationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Oliver Gierke
 */
@SpringBootTest
@DirtiesContext
public class EmailNotificationTest {

	@Autowired OrderManager orders;
	@Autowired Catalog catalog;
	@Autowired EventPublicationRegistry registry;
	@Autowired EmailSender emails;

	@Test
	public void completingAnOrderUpdatesInventory() throws Exception {

		Product product = catalog.findAll().iterator().next();

		Order order = orders.createOrder();
		order.add(product, 5);
		orders.complete(order);

		// Publication registration left
		Iterable<EventPublication> publications = registry.findIncompletePublications();

		assertThat(publications).hasSize(1);
		assertThat(publications).allMatch(it -> it.getEvent() instanceof OrderCompleted);

		Thread.sleep(1200);

		// Publication registration left
		assertThat(registry.findIncompletePublications()).isEmpty();
	}

	@Test
	public void systemCrashDuringTransactionalListenerExecutionKeepsPublicationRegistration() throws Exception {

		emails.setFail(true);

		Product product = catalog.findAll().iterator().next();

		Order order = orders.createOrder();
		order.add(product, 5);
		orders.complete(order);

		Thread.sleep(1200);

		// Publication registration left
		Iterable<EventPublication> publications = registry.findIncompletePublications();

		assertThat(publications).hasSize(1);
		assertThat(publications).allMatch(it -> it.getEvent() instanceof OrderCompleted);
	}

}
