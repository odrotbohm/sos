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
package example.sos.messaging.catalog.integration;

import example.sos.messaging.catalog.Product.ProductAdded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Oliver Gierke
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Component
class KafkaIntegration {

	private final KafkaOperations<Object, Object> kafka;
	private final ObjectMapper mapper;

	@EventListener
	void on(ProductAdded event) throws Exception {

		String payload = mapper.writeValueAsString(event);

		log.info("Publishing {} to Kafka…", payload);

		kafka.send("products", payload);
	}
}
