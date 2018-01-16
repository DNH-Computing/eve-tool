/*
 * Copyright (c) Orchestral Developments Ltd and the Orion Health group of companies (2001 - 2018).
 *
 * This document is copyright. Except for the purpose of fair reviewing, no part
 * of this publication may be reproduced or transmitted in any form or by any
 * means, electronic or mechanical, including photocopying, recording, or any
 * information storage and retrieval system, without permission in writing from
 * the publisher. Infringers of copyright render themselves liable for
 * prosecution.
 */
package nz.net.dnh.eve.market.evemarketer;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

import nz.net.dnh.eve.market.eve_central.EveCentralMarketStatResponse;
import nz.net.dnh.eve.web.view.VersionReader;

@Service
public class EveMarketerMarketRequester {
	@Autowired
	private VersionReader versionReader;

	private static final Logger logger = LoggerFactory.getLogger(EveMarketerMarketRequester.class);

	private Client client;

	public EveMarketerMarketRequester() {
		this.client = Client.create();
		this.client.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(final ClientRequest arg0)
					throws ClientHandlerException {
				arg0.getHeaders().add("User-Agent", "dnh.eve-blueprint-tool/" + EveMarketerMarketRequester.this.versionReader.getVersion());
				return getNext().handle(arg0);
			}
		});
	}

	public EveCentralMarketStatResponse getDataForType(
			final Collection<Integer> typeIds) {
		logger.debug("Requesting market data for types: {}", typeIds);

		WebResource resource = this.client.resource("https://api.evemarketer.com/ec/marketstat");
		resource = resource.queryParam("usesystem", "30000142"); // FIXME Limit to Jita in The Forge for now

		for (final Integer typeId : typeIds) {
			resource = resource.queryParam("typeid", typeId.toString());
		}

		logger.debug("URI is: {}", resource.getURI());

		return resource.get(EveCentralMarketStatResponse.class);
	}
}

