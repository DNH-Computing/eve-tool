package nz.net.dnh.eve.business.impl;

import static nz.net.dnh.eve.HelpingMatchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nz.net.dnh.eve.business.BlueprintIdReference;
import nz.net.dnh.eve.business.BlueprintNotFoundException;
import nz.net.dnh.eve.business.BlueprintSummary;
import nz.net.dnh.eve.business.CandidateBlueprint;
import nz.net.dnh.eve.business.impl.dto.blueprint.BlueprintSummaryImpl;
import nz.net.dnh.eve.business.impl.dto.blueprint.CandidateBlueprintImpl;
import nz.net.dnh.eve.model.domain.Blueprint;
import nz.net.dnh.eve.model.raw.InventoryBlueprintType;
import nz.net.dnh.eve.model.repository.BlueprintRepository;
import nz.net.dnh.eve.model.repository.InventoryBlueprintTypeRepository;
import nz.net.dnh.eve.model.repository.TypeRepository;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@RunWith(MockitoJUnitRunner.class)
public class BlueprintServiceTest {
	// private static final MockBlueprint BLUEPRINT_1 = new MockBlueprint(3, 4, 5, 20, 101, new BigDecimal(6), 7, "Blueprint 1",
	// new BigDecimal(8), new BigDecimal(9), new BigDecimal(10), new BigDecimal(11), new BigDecimal(12), 99, false);
	// private static final MockBlueprint BLUEPRINT_2 = new MockBlueprint(13, 14, 15, 210, 201, new BigDecimal(16), 17, "Blueprint 2",
	// new BigDecimal(18), new BigDecimal(19), new BigDecimal(20), new BigDecimal(21), new BigDecimal(22), 98, false);
	// private static final InventoryBlueprintType INVENTORY_BLUEPRINT_1 = mock(InventoryBlueprintType.class);
	// private static final InventoryBlueprintType INVENTORY_BLUEPRINT_2 = mock(InventoryBlueprintType.class);

	// static {
	// final InventoryType productType = mock(InventoryType.class);
	// when(productType.getTypeName()).thenReturn("Inventory blueprint type");
	// when(INVENTORY_BLUEPRINT_1.getBlueprintTypeID()).thenReturn(985);
	// when(INVENTORY_BLUEPRINT_1.getProductType()).thenReturn(productType);
	// when(INVENTORY_BLUEPRINT_1.getProductTypeID()).thenReturn(17);
	//
	// final InventoryType productType2 = mock(InventoryType.class);
	// when(productType2.getTypeName()).thenReturn("Inventory blueprint type 2");
	// when(INVENTORY_BLUEPRINT_2.getBlueprintTypeID()).thenReturn(985);
	// when(INVENTORY_BLUEPRINT_2.getProductType()).thenReturn(productType2);
	// when(INVENTORY_BLUEPRINT_2.getProductTypeID()).thenReturn(18);
	// }

	@InjectMocks
	private final BlueprintServiceImpl service = new BlueprintServiceImpl();

	@Mock
	private BlueprintRepository blueprintRepository;

	@Mock
	private TypeRepository typeRepository;

	@Mock
	private InventoryBlueprintTypeRepository inventoryBlueprintTypeRepository;

	@Mock
	private BlueprintResolverService blueprintResolverService;

	@Mock
	private BlueprintRequiredTypesService blueprintRequiredTypesService;

	@Test
	public void getBlueprintWithSummary() {
		final BlueprintSummary summary = mock(BlueprintSummary.class);

		final BlueprintSummary out = this.service.getBlueprint(summary);

		assertSame(summary, out);
		verifyNoMoreInteractions(this.blueprintResolverService);
	}

	@Test
	public void getBlueprintWithIdReference() {
		final Blueprint blueprint = mock(Blueprint.class);
		final BlueprintIdReference ref = new BlueprintIdReference(5);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(blueprint);

		final BlueprintSummary out = this.service.getBlueprint(ref);

		assertThat(out, is(blueprintSummaryFor(blueprint)));
	}

	@Test
	public void getBlueprintWithCandidate() {
		final Blueprint blueprint = mock(Blueprint.class);
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		final CandidateBlueprint ref = new CandidateBlueprintImpl(inventoryBlueprint);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(blueprint);

		final BlueprintSummary out = this.service.getBlueprint(ref);

		assertThat(out, is(blueprintSummaryFor(blueprint)));
	}

	@Test(expected = BlueprintNotFoundException.class)
	public void getBlueprintWithUnknownIdReference() {
		final BlueprintIdReference ref = new BlueprintIdReference(5);
		final BlueprintNotFoundException ex = new BlueprintNotFoundException(ref);
		when(this.blueprintResolverService.toBlueprint(ref)).thenThrow(ex);

		this.service.getBlueprint(ref);
	}

	@Test(expected = BlueprintNotFoundException.class)
	public void getBlueprintWithUnknownCandidate() {
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		when(inventoryBlueprint.getBlueprintTypeID()).thenReturn(7);
		final CandidateBlueprint ref = new CandidateBlueprintImpl(inventoryBlueprint);
		final BlueprintNotFoundException ex = new BlueprintNotFoundException(ref);
		when(this.blueprintResolverService.toBlueprint(ref)).thenThrow(ex);

		this.service.getBlueprint(ref);
	}

	@Test
	public void listNoSummaries() {
		when(this.blueprintRepository.findAll(any(Sort.class))).thenReturn(Collections.<Blueprint> emptyList());

		final List<BlueprintSummary> out = this.service.listSummaries();

		assertTrue(out.isEmpty());
	}

	@Test
	public void listSingleSummary() {
		final Blueprint blueprint = mock(Blueprint.class);
		when(this.blueprintRepository.findAll(new Sort("blueprintType.productType.typeName"))).thenReturn(
				Collections.<Blueprint> singletonList(blueprint));

		final List<BlueprintSummary> out = this.service.listSummaries();

		assertEquals(1, out.size());
		assertThat(out.get(0), is(blueprintSummaryFor(blueprint)));
	}

	@Test
	public void listSummaries() {
		final Blueprint blueprint1 = mock(Blueprint.class);
		final Blueprint blueprint2 = mock(Blueprint.class);
		when(this.blueprintRepository.findAll(new Sort("blueprintType.productType.typeName"))).thenReturn(
				Arrays.<Blueprint> asList(blueprint1, blueprint2));

		final List<BlueprintSummary> out = this.service.listSummaries();

		assertEquals(2, out.size());
		assertThat(out, contains(blueprintSummaryFor(blueprint1), blueprintSummaryFor(blueprint2)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void listCandidateBlueprintsSortNotAllowed() {
		this.service.listCandidateBlueprints(new PageRequest(0, 5, Direction.ASC, "totalCost"));
	}

	@Test
	public void listNoCandidateBlueprints() {
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprints(page)).thenReturn(
				new PageImpl<>(Collections.<InventoryBlueprintType> emptyList()));

		final Page<CandidateBlueprint> out = this.service.listCandidateBlueprints(page);
		assertTrue(out.getContent().isEmpty());
	}

	@Test
	public void listSingleCandidateBlueprint() {
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprints(page)).thenReturn(
				new PageImpl<>(Collections.singletonList(inventoryBlueprint)));

		final Page<CandidateBlueprint> out = this.service.listCandidateBlueprints(page);
		assertEquals(1, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(1, out.getTotalPages());
		assertEquals(1, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint)));
	}

	@Test
	public void listCandidateBlueprints() {
		final InventoryBlueprintType inventoryBlueprint1 = mock(InventoryBlueprintType.class);
		final InventoryBlueprintType inventoryBlueprint2 = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprints(page)).thenReturn(
				new PageImpl<>(Arrays.asList(inventoryBlueprint1, inventoryBlueprint2)));

		final Page<CandidateBlueprint> out = this.service.listCandidateBlueprints(page);
		assertEquals(2, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(1, out.getTotalPages());
		assertEquals(2, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint1), candidateBlueprintFor(inventoryBlueprint2)));
	}

	@Test
	public void listCandidateBlueprintsPaged() {
		final InventoryBlueprintType inventoryBlueprint1 = mock(InventoryBlueprintType.class);
		final InventoryBlueprintType inventoryBlueprint2 = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprints(page)).thenReturn(
				new PageImpl<>(Arrays.asList(inventoryBlueprint1, inventoryBlueprint2), page, 34));

		final Page<CandidateBlueprint> out = this.service.listCandidateBlueprints(page);
		assertEquals(2, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(17, out.getTotalPages());
		assertEquals(34, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint1), candidateBlueprintFor(inventoryBlueprint2)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findCandidateBlueprintsSortNotAllowed() {
		this.service.findCandidateBlueprints("a", new PageRequest(0, 5, Direction.ASC, "totalCost"));
	}

	@Test
	public void findNoCandidateBlueprints() {
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprintsBySearch("s", page)).thenReturn(
				new PageImpl<>(Collections.<InventoryBlueprintType> emptyList()));

		final Page<CandidateBlueprint> out = this.service.findCandidateBlueprints("s", page);
		assertTrue(out.getContent().isEmpty());
	}

	@Test
	public void findSingleCandidateBlueprint() {
		final InventoryBlueprintType inventoryBlueprint = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprintsBySearch("q", page)).thenReturn(
				new PageImpl<>(Collections.singletonList(inventoryBlueprint)));

		final Page<CandidateBlueprint> out = this.service.findCandidateBlueprints("q", page);
		assertEquals(1, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(1, out.getTotalPages());
		assertEquals(1, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint)));
	}

	@Test
	public void findCandidateBlueprints() {
		final InventoryBlueprintType inventoryBlueprint1 = mock(InventoryBlueprintType.class);
		final InventoryBlueprintType inventoryBlueprint2 = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprintsBySearch("z", page)).thenReturn(
				new PageImpl<>(Arrays.asList(inventoryBlueprint1, inventoryBlueprint2)));

		final Page<CandidateBlueprint> out = this.service.findCandidateBlueprints("z", page);
		assertEquals(2, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(1, out.getTotalPages());
		assertEquals(2, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint1), candidateBlueprintFor(inventoryBlueprint2)));
	}

	@Test
	public void findCandidateBlueprintsPaged() {
		final InventoryBlueprintType inventoryBlueprint1 = mock(InventoryBlueprintType.class);
		final InventoryBlueprintType inventoryBlueprint2 = mock(InventoryBlueprintType.class);
		final PageRequest page = new PageRequest(0, 2);
		when(this.inventoryBlueprintTypeRepository.findUnknownBlueprintsBySearch("p", page)).thenReturn(
				new PageImpl<>(Arrays.asList(inventoryBlueprint1, inventoryBlueprint2), page, 34));

		final Page<CandidateBlueprint> out = this.service.findCandidateBlueprints("p", page);
		assertEquals(2, out.getNumberOfElements());
		assertEquals(0, out.getNumber());
		assertEquals(17, out.getTotalPages());
		assertEquals(34, out.getTotalElements());
		assertThat(out, contains(candidateBlueprintFor(inventoryBlueprint1), candidateBlueprintFor(inventoryBlueprint2)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createBlueprintAlreadyExists() {
		when(this.blueprintRepository.exists(1)).thenReturn(true);
		this.service.createBlueprint(new BlueprintIdReference(1), new BigDecimal(2), 3, 4, 5, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createBlueprintReferencedDoesntExist() {
		when(this.blueprintRepository.exists(1)).thenReturn(false);
		when(this.inventoryBlueprintTypeRepository.exists(1)).thenReturn(false);
		this.service.createBlueprint(new BlueprintIdReference(1), new BigDecimal(2), 3, 4, 5, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createBlueprintNullSaleValue() {
		when(this.blueprintRepository.exists(1)).thenReturn(false);
		when(this.inventoryBlueprintTypeRepository.exists(1)).thenReturn(true);
		this.service.createBlueprint(new BlueprintIdReference(1), null, 3, 4, 5, false);
	}

	@Test
	public void createBlueprint() {
		final Blueprint blueprint = mock(Blueprint.class);
		when(this.blueprintRepository.exists(1)).thenReturn(false);
		when(this.inventoryBlueprintTypeRepository.exists(1)).thenReturn(true);
		when(this.blueprintRepository.save(any(Blueprint.class))).thenReturn(blueprint);

		final BlueprintSummary created = this.service.createBlueprint(new BlueprintIdReference(1), new BigDecimal(2), 3, 4, 5, false);

		final ArgumentCaptor<Blueprint> captor = ArgumentCaptor.forClass(Blueprint.class);
		verify(this.blueprintRepository).save(captor.capture());
		final Blueprint savedBlueprint = captor.getValue();
		assertEquals(1, savedBlueprint.getBlueprintTypeID());
		assertEquals(new BigDecimal(2), savedBlueprint.getSaleValue());
		assertEquals(3, savedBlueprint.getNumberPerRun());
		assertEquals(4, savedBlueprint.getProductionEfficiency());
		assertEquals(5, savedBlueprint.getMaterialEfficiency());

		assertThat(created, is(blueprintSummaryFor(blueprint)));
	}

	@Test(expected=BlueprintNotFoundException.class)
	public void editNonExistingBlueprint() {
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		final BlueprintNotFoundException ex = new BlueprintNotFoundException(ref);
		when(this.blueprintResolverService.toBlueprint(ref)).thenThrow(ex);
		this.service.editBlueprint(ref, new BigDecimal(2), 3, 4, 5, null);
	}

	@Test
	public void editBlueprintAllFields() {
		final Blueprint b = mock(Blueprint.class);
		when(b.getSaleValue()).thenReturn(new BigDecimal(1));
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, new BigDecimal(2), 3, 4, 5, null);

		verify(b).setSaleValue(new BigDecimal(2));
		verify(b).touchLastUpdated();
		verify(b).setNumberPerRun(3);
		verify(b).setProductionEfficiency(4);
		verify(b).setMaterialEfficiency(5);
	}

	@Test
	public void editBlueprintAllFieldsIdenticalSaleValue() {
		final Blueprint b = mock(Blueprint.class);
		when(b.getSaleValue()).thenReturn(new BigDecimal(2));
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, new BigDecimal(2), 3, 4, 5, null);

		verify(b).setSaleValue(new BigDecimal(2));
		verify(b, never()).touchLastUpdated();
		verify(b).setNumberPerRun(3);
		verify(b).setProductionEfficiency(4);
		verify(b).setMaterialEfficiency(5);
	}

	@Test
	public void editBlueprintSaleValue() {
		final Blueprint b = mock(Blueprint.class);
		when(b.getSaleValue()).thenReturn(new BigDecimal(1));
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, new BigDecimal(2), null, null, null, null);

		verify(b).setSaleValue(new BigDecimal(2));
		verify(b).touchLastUpdated();
		verify(b, never()).setNumberPerRun(anyInt());
		verify(b, never()).setProductionEfficiency(anyInt());
		verify(b, never()).setMaterialEfficiency(anyInt());
	}

	@Test
	public void editBlueprintIdenticalSaleValue() {
		final Blueprint b = mock(Blueprint.class);
		when(b.getSaleValue()).thenReturn(new BigDecimal(2));
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, new BigDecimal(2), null, null, null, null);

		verify(b).setSaleValue(new BigDecimal(2));
		verify(b, never()).touchLastUpdated();
		verify(b, never()).setNumberPerRun(anyInt());
		verify(b, never()).setProductionEfficiency(anyInt());
		verify(b, never()).setMaterialEfficiency(anyInt());
	}

	@Test
	public void editBlueprintNumberPerRun() {
		final Blueprint b = mock(Blueprint.class);
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, null, 3, null, null, null);

		verify(b, never()).setSaleValue(any(BigDecimal.class));
		verify(b, never()).touchLastUpdated();
		verify(b).setNumberPerRun(3);
		verify(b, never()).setProductionEfficiency(anyInt());
		verify(b, never()).setMaterialEfficiency(anyInt());
	}

	@Test
	public void editBlueprintHours() {
		final Blueprint b = mock(Blueprint.class);
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, null, null, 4, null, null);

		verify(b, never()).setSaleValue(any(BigDecimal.class));
		verify(b, never()).touchLastUpdated();
		verify(b, never()).setNumberPerRun(anyInt());
		verify(b).setProductionEfficiency(4);
		verify(b, never()).setMaterialEfficiency(anyInt());
	}

	@Test
	public void editBlueprintMaterialEfficiency() {
		final Blueprint b = mock(Blueprint.class);
		final BlueprintIdReference ref = new BlueprintIdReference(1);
		when(this.blueprintResolverService.toBlueprint(ref)).thenReturn(b);
		this.service.editBlueprint(ref, null, null, null, 5, null);

		verify(b, never()).setSaleValue(any(BigDecimal.class));
		verify(b, never()).touchLastUpdated();
		verify(b, never()).setNumberPerRun(anyInt());
		verify(b, never()).setProductionEfficiency(anyInt());
		verify(b).setMaterialEfficiency(5);
	}

	private static Matcher<BlueprintSummary> blueprintSummaryFor(final Blueprint blueprint) {
		return new TypeSafeMatcher<BlueprintSummary>(BlueprintSummaryImpl.class) {

			@Override
			public void describeTo(final Description description) {
				description.appendText("Blueprint Summary for ").appendValue(blueprint);
			}

			@Override
			protected boolean matchesSafely(final BlueprintSummary item) {
				return blueprint.equals(((BlueprintSummaryImpl) item).toBlueprint());
			}
		};
	}

	private static Matcher<CandidateBlueprint> candidateBlueprintFor(final InventoryBlueprintType inventoryBlueprint) {
		final CandidateBlueprintImpl expected = new CandidateBlueprintImpl(inventoryBlueprint);
		return new TypeSafeMatcher<CandidateBlueprint>() {

			@Override
			public void describeTo(final Description description) {
				description.appendText("Candidate Blueprint for ").appendValue(inventoryBlueprint);
			}

			@Override
			protected boolean matchesSafely(final CandidateBlueprint item) {
				return expected.equals(item);
			}
		};
	}
}
