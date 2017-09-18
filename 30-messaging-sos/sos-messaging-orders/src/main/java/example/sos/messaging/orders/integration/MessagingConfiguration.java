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
package example.sos.messaging.orders.integration;

import example.sos.messaging.orders.Order.OrderCompleted;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.web.JsonProjectingMethodInterceptorFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

/**
 * @author Oliver Gierke
 */
@Configuration
class MessagingConfiguration {

	@Bean
	SpelAwareProxyProjectionFactory projectionFactory(ObjectMapper mapper) {

		JacksonMappingProvider provider = new JacksonMappingProvider(mapper);
		JsonProjectingMethodInterceptorFactory interceptorFactory = new JsonProjectingMethodInterceptorFactory(provider);

		SpelAwareProxyProjectionFactory factory = new SpelAwareProxyProjectionFactory();
		factory.registerMethodInvokerFactory(interceptorFactory);

		return factory;
	}

	@Component
	@RequiredArgsConstructor
	static class KafkaIntegration {

		private final @NonNull KafkaOperations<Object, Object> jms;
		private final @NonNull ObjectMapper mapper;

		@EventListener
		void on(OrderCompleted event) throws Exception {
			jms.send("orders", mapper.writeValueAsString(event));
		}
	}
}
