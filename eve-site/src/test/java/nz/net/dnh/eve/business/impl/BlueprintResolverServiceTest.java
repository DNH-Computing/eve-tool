package nz.net.dnh.eve.business.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import nz.net.dnh.eve.business.BlueprintIdReference;
import nz.net.dnh.eve.business.BlueprintNotFoundException;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.CandidateBlueprint;
import nz.net.dnh.eve.business.impl.dto.blueprint.BlueprintSummaryImpl;
import nz.net.dnh.eve.business.impl.dto.blueprint.CandidateBlueprintImpl;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.repository.BlueprintRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlueprintResolverServiceTest {

	@InjectMocks
	private final BlueprintResolverServiceImpl service = new BlueprintResolverServiceImpl();

	@Mock
	private BlueprintRepository blueprintRepository;

	@Mock
	private BlueprintRequiredTypesService requiredTypesService;

	@Before
	public void setup() {
		// Just return the value given when asked to refresh any blueprint
		when(this.blueprintRepository.refresh(any(Blueprint.class))).thenAnswer(returnsFirstArg());
	}

	@Test
	public void toBlueprintWithSummary() {
		final Blueprint b = mock(Blueprint.class);
		final BlueprintSummary summary = new BlueprintSummaryImpl(b, this.requiredTypesService);

		final Blueprint out = this.service.toBlueprint(summary);

		verify(this.blueprintRepository).refresh(b);
		assertSame(b, out);
		verifyNoMoreInteractions(this.blueprintRepository);
	}

	@Test
	public void toBlueprintWithIdReference() {
		final Blueprint b = mock(Blueprint.class);
		final BlueprintIdReference ref = new BlueprintIdReference(5);
		when(this.blueprintRepository.findOne(5)).thenReturn(b);

		final Blueprint out = this.service.toBlueprint(ref);

		assertSame(b, out);
	}

	@Test
	public void toBlueprintWithCandidate() {
		final Blueprint b = mock(Blueprint.class);
		when(this.blueprintRepository.findOne(7)).thenReturn(b);
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		when(inventoryBlueprint.getBlueprintTypeID()).thenReturn(7);
		final CandidateBlueprint ref = new CandidateBlueprintImpl(inventoryBlueprint);

		final Blueprint out = this.service.toBlueprint(ref);

		assertSame(b, out);
	}

	@Test(expected = BlueprintNotFoundException.class)
	public void toBlueprintWithUnknownIdReference() {
		final BlueprintIdReference ref = new BlueprintIdReference(5);

		this.service.toBlueprint(ref);
	}

	@Test(expected = BlueprintNotFoundException.class)
	public void toBlueprintWithUnknownCandidate() {
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		when(inventoryBlueprint.getBlueprintTypeID()).thenReturn(7);
		final CandidateBlueprint ref = new CandidateBlueprintImpl(inventoryBlueprint);

		this.service.toBlueprint(ref);
	}
}
