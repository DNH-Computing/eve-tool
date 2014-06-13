package nz.net.dnh.eve.business.impl.dto.type;

import static nz.net.dnh.eve.HelpingMatchers.greaterThan;
import static nz.net.dnh.eve.HelpingMatchers.lessThan;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import nz.net.dnh.eve.business.Component;
import nz.net.dnh.eve.business.Mineral;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractSortableTypeImplTest<WrappedClass, T extends AbstractSortableTypeImpl> {
	private static final int ID = 720;
	private static final String NAME = "Test Type Name";

	@Parameters
	public static List<Object[]> params() {
		return Arrays.asList(new Object[] { true }, new Object[] { false });
	}

	protected WrappedClass wrappedObject;
	protected T impl;

	private final boolean shouldBeMineral;
	private final boolean shouldBeMissing;


	public AbstractSortableTypeImplTest(final boolean shouldBeMineral, final boolean shouldBeMissing) {
		this.shouldBeMineral = shouldBeMineral;
		this.shouldBeMissing = shouldBeMissing;
	}

	protected abstract WrappedClass constructWrappedClass(int id, String name);

	protected abstract T constructMineral(WrappedClass wrapped);

	protected abstract T constructComponent(WrappedClass wrapped);

	private T constructInstance(final WrappedClass wrapped) {
		if (this.shouldBeMineral)
			return constructMineral(wrapped);
		else
			return constructComponent(wrapped);
	}

	@Before
	public void setup() throws Exception {
		this.wrappedObject = constructWrappedClass(ID, NAME);
		this.impl = constructInstance(this.wrappedObject);
	}

	@Test
	public void isMineralReturnsCorrectValue() {
		assertEquals(this.shouldBeMineral, this.impl.isMineral());
	}

	@Test
	public void instanceofMineralAndComponentReturnCorrectValues() {
		assertEquals(this.shouldBeMineral, this.impl instanceof Mineral);
		assertEquals(!this.shouldBeMineral, this.impl instanceof Component);
	}

	@Test
	public void isMissingReturnsCorrectValue() {
		assertEquals(this.shouldBeMissing, this.impl.isMissing());
	}

	@Test
	public void getIdPassesThrough() {
		assertEquals(ID, this.impl.getId());
	}

	@Test
	public void getNamePassesThrough() {
		assertEquals(NAME, this.impl.getName());
	}

	@Test
	public void hashCodePassesThrough() {
		assertEquals(this.wrappedObject.hashCode(), this.impl.hashCode());
	}

	@Test
	public void compareToUsesName() {
		final T lesser = constructInstance(constructWrappedClass(ID, "AAA"));
		final T greater = constructInstance(constructWrappedClass(ID, "ZZZ"));
		assertThat(this.impl, is(greaterThan(lesser)));
		assertThat(this.impl, is(lessThan(greater)));
	}

	@Test
	public void compareToUsesIdIfNameEqual() {
		final T lesser = constructInstance(constructWrappedClass(ID + 1, "AAA"));
		final T greaterWithSameName = constructInstance(constructWrappedClass(ID + 1, NAME));
		final T lesserWithSameName = constructInstance(constructWrappedClass(ID - 1, NAME));
		assertThat(this.impl, is(greaterThan(lesser)));
		assertThat(this.impl, is(lessThan(greaterWithSameName)));
		assertThat(this.impl, is(greaterThan(lesserWithSameName)));
	}

	@Test
	public void equalsChecksType() {
		final T mineral = constructMineral(this.wrappedObject);
		final T component = constructComponent(this.wrappedObject);
		assertThat(mineral, is(not(equalTo(component))));
		
		final T expectedEqual = this.shouldBeMineral ? mineral : component;
		final T expectedUnEqual = this.shouldBeMineral ? component : mineral;
		assertThat(this.impl, is(equalTo(expectedEqual)));
		assertThat(this.impl, is(not(equalTo(expectedUnEqual))));
	}

	@Test
	public void equalsChecksId() {
		final WrappedClass unEqualWrapped = constructWrappedClass(ID + 1, NAME);
		final T mineral = constructMineral(unEqualWrapped);
		final T component = constructComponent(unEqualWrapped);

		assertThat(this.impl, is(not(equalTo(mineral))));
		assertThat(this.impl, is(not(equalTo(component))));
	}

}
