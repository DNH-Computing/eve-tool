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

import javax.xml.bind.annotation.XmlElement;

import nz.net.dnh.eve.market.eve_central.EveCentralMarketStatResponse;

public final class EveMarketerWrapper {
	@XmlElement(name = "marketstat")
	EveCentralMarketStatResponse marketstat;
}
