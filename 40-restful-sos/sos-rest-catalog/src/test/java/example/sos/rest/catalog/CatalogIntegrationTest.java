package example.sos.rest.catalog;
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

import static org.assertj.core.api.Assertions.*;

import example.sos.rest.events.EventRepository;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CatalogIntegrationTest {

	@Autowired Catalog catalog;
	@Autowired TestListener listener;
	@Autowired EventRepository events;

	@Test
	public void creatingAProductFiresProductAddedEvent() {

		Product product = Product.of("Description", BigDecimal.valueOf(50.00));

		catalog.save(product);

		assertThat(listener.productAdded.getProduct()).isEqualTo(product);
		assertThat(events.findAll(PageRequest.of(0, 10, Sort.by("publicationDate")))).contains(listener.productAdded);
	}
}
