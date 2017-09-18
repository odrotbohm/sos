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
package example.sos.rest.events.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import example.sos.rest.events.AbstractEvent;
import example.sos.rest.events.EventRepository;
import example.sos.rest.events.QAbstractEvent;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.querydsl.core.BooleanBuilder;

/**
 * @author Oliver Gierke
 */
@BasePathAwareController
@RequiredArgsConstructor
class EventController {

	private final EventRepository events;
	private final ControllerLinkBuilderFactory links;

	@GetMapping("events")
	HttpEntity<Resources<?>> events(PagedResourcesAssembler<AbstractEvent<?>> assembler,
			@SortDefault("publicationDate") Pageable pageable,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime since,
			@RequestParam(required = false) String type) {

		QAbstractEvent $ = QAbstractEvent.abstractEvent;

		BooleanBuilder builder = new BooleanBuilder();

		// Apply date
		Optional.ofNullable(since).ifPresent(it -> builder.and($.publicationDate.after(it)));

		// Apply type
		Optional.ofNullable(type) //
				.flatMap(events::findEventTypeByName) //
				.ifPresent(it -> builder.and($.instanceOf(it)));

		Page<AbstractEvent<?>> result = events.findAll(builder, pageable);

		PagedResources<Resource<AbstractEvent<?>>> resource = assembler.toResource(result, event -> toResource(event));
		resource
				.add(links.linkTo(methodOn(EventController.class).events(assembler, pageable, since, type)).withRel("events"));

		return ResponseEntity.ok(resource);
	}

	@GetMapping("events/{id}")
	ResponseEntity<Resource<AbstractEvent<?>>> event(@PathVariable UUID id) {

		return events.findById(id) //
				.map(this::toResource)//
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}

	private Resource<AbstractEvent<?>> toResource(AbstractEvent<?> event) {

		Resource<AbstractEvent<?>> resource = new Resource<>(event);

		resource.add(links.linkTo(methodOn(EventController.class).event(event.getId())).withSelfRel());

		return resource;
	}
}
