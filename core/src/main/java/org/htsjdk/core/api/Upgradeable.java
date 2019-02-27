package org.htsjdk.core.api;

import org.htsjdk.core.utils.Version;

public interface Upgradeable {

    boolean runVersionUpgrade(final Version sourceVersion, final Version targetVersion);

}
