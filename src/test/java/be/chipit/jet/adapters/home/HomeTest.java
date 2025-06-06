package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.usecases.DecryptSnippet;
import be.chipit.jet.domain.usecases.EncryptSnippet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeTest {

    @Mock
    private JetStoreParser jetStoreParser;

    @Test
    void initializeNewConfig() {
        var tempDir = System.getProperty("java.io.tmpdir");
        Path path = Path.of(tempDir, "/jet-test-" + RandomGenerator.getDefault().nextInt() + ".yml");
        File tempFile = path.toFile();
        tempFile.deleteOnExit();

        Home home = new Home(jetStoreParser, path, Mockito.mock(DecryptSnippet.class), Mockito.mock(EncryptSnippet.class));
        home.readOrCreateNew();

        verify(jetStoreParser, never()).write(tempFile, JetStore.createNew());
    }

    @Test
    void readExisting() throws IOException {
        var tempDir = System.getProperty("java.io.tmpdir");
        Path path = Path.of(tempDir, "/jet-test-" + RandomGenerator.getDefault().nextInt() + ".yml");
        File tempFile = path.toFile();

        assertThat(tempFile.createNewFile()).isTrue();
        tempFile.deleteOnExit();

        when(jetStoreParser.read(tempFile)).thenReturn(JetStore.createNew());

        Home home = new Home(jetStoreParser, path, Mockito.mock(DecryptSnippet.class), Mockito.mock(EncryptSnippet.class));
        home.readOrCreateNew();

        verify(jetStoreParser, times(1)).read(tempFile);

        assertThat(home.listAll()).hasSize(1);
    }
}
