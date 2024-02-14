package be.chipit.jet.adapters.home;

import java.io.File;

public interface JetConfigParser {
    JetConfig read(File file);

    void write(File file, JetConfig jetConfig);
}
