import org.apache.maven.cli.logging.BaseSlf4jConfiguration
import org.apache.maven.cli.logging.Slf4jConfiguration.Level

public class DummyLogbackConfiguration extends BaseSlf4jConfiguration {
    @Override
    public void setRootLoggerLevel(Level level) {
        // do not change Gradle's log level
    }

    @Override
    public void activate()
    {
        // no op
    }
}
