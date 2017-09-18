/*
so  * Copyright 2017 the original author or authors.
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

import example.sos.rest.events.Event;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Oliver Gierke
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EventRelProvider implements RelProvider {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.RelProvider#getCollectionResourceRelFor(java.lang.Class)
	 */
	@Override
	public String getCollectionResourceRelFor(Class<?> type) {
		return StringUtils.uncapitalize(type.getSimpleName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.RelProvider#getItemResourceRelFor(java.lang.Class)
	 */
	@Override
	public String getItemResourceRelFor(Class<?> type) {
		return getCollectionResourceRelFor(type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.Plugin#supports(java.lang.Object)
	 */
	@Override
	public boolean supports(Class<?> delimiter) {
		return Event.class.isAssignableFrom(delimiter);
	}
}
