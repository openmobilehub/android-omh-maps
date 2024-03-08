import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class DefaultUUIDGeneratorTest {

    private val generator = DefaultUUIDGenerator()

    @Test
    fun `generate returns unique UUID each time`() {
        val uuid1 = generator.generate()
        val uuid2 = generator.generate()

        Assert.assertNotEquals(uuid1, uuid2)
    }

    @Test
    fun `generate returns valid UUID`() {
        val uuid = generator.generate()

        val uuidFromString = UUID.fromString(uuid.toString())

        Assert.assertEquals(uuid, uuidFromString)
    }
}
