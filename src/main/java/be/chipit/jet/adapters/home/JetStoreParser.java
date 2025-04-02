package be.chipit.jet.adapters.home;

import java.io.File;

public interface JetStoreParser {
    JetStore read(File file);

    void write(File file, JetStore jetStore);
}
