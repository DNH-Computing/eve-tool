package nz.net.dnh.eve.market.eve_central;

import java.util.Collection;

import nz.net.dnh.eve.web.view.VersionReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

@Service
public class EveCentralMarketStatRequester {
	@Autowired
	private VersionReader versionReader;

	private Client client;

	public EveCentralMarketStatRequester() {
		this.client = Client.create();
		this.client.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(final ClientRequest arg0)
					throws ClientHandlerException {
				arg0.getHeaders().add("User-Agent", "dnh.eve-blueprint-tool/"+EveCentralMarketStatRequester.this.versionReader.getVersion());
				return getNext().handle(arg0);
			}
		});
	}

	public EveCentralMarketStatResponse getDataForType(
			final Collection<Integer> typeIds) {
		final WebResource resource = this.client.resource("http://api.eve-central.com/api/marketstat");
		resource.queryParam("usesystem", "30000142"); // FIXME Limit to Jita in
														// The Forge for now

		for (final Integer typeId : typeIds) {
			resource.queryParam("typeid", typeId.toString());
		}

		return resource.get(EveCentralMarketStatResponse.class);
	}
}