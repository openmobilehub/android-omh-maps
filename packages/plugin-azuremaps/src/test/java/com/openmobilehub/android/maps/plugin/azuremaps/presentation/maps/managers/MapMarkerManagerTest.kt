package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import a.a.a.a.a.m
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.azure.android.maps.control.ImageManager
import com.azure.android.maps.control.LayerManager
import com.azure.android.maps.control.Popup
import com.azure.android.maps.control.PopupManager
import com.azure.android.maps.control.SourceManager
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

class MapMarkerManagerTest {
    private val context = mockk<Context>(relaxed = true)
    private lateinit var mapMarkerManager: MapMarkerManager
    private val uiMock = mockk<m>(relaxed = true)
    private val sourcesMock = mockk<SourceManager>(relaxed = true)
    private val layersMock = mockk<LayerManager>(relaxed = true)
    private val imagesMock = mockk<ImageManager>(relaxed = true)
    private val popupsMock = mockk<PopupManager>(relaxed = true)
    private val mockedPopupViewGroup = mockk<ViewGroup>(relaxed = true)
    private val defaultMarkerIconDrawable = mockk<Drawable>()
    private val convertDrawableToBitmapMock = mockk<Bitmap>()
    private val iwMockFrameLayout = mockk<FrameLayout>(relaxed = true)

    @Before
    fun setUp() {
        every { context.resources } returns mockk<Resources>()
        every { convertDrawableToBitmapMock.width } returns 80
        every { convertDrawableToBitmapMock.height } returns 80
        every { convertDrawableToBitmapMock.density } returns 1
        mockkStatic(ResourcesCompat::class)
        mockkObject(DrawableConverter)
        every {
            ResourcesCompat.getDrawable(
                context.resources,
                any<Int>(),
                any<Resources.Theme>()
            )
        } answers { defaultMarkerIconDrawable }
        every { DrawableConverter.convertDrawableToBitmap(any()) } answers { convertDrawableToBitmapMock }

        every { mockedPopupViewGroup.findViewById<View>(any<Int>()) } returns iwMockFrameLayout

        every { popupsMock.add(any<Popup>()) } answers {
            val popup = firstArg<Popup>()
            popup.setFrame(mockedPopupViewGroup)
        }

        mapMarkerManager = MapMarkerManager(
            context,
            object : AzureMapInterface {
                override val ui: m
                    get() = uiMock
                override val sources: SourceManager
                    get() = sourcesMock
                override val layers: LayerManager
                    get() = layersMock
                override val images: ImageManager
                    get() = imagesMock
                override val popups: PopupManager
                    get() = popupsMock
            }
        )

        mockkStatic(LayoutInflater::class)
        mockkStatic(Executors::class) // for mocking the executor handling View.drawToBitmap(...)
    }

    @Test
    fun `setMarkerClickListener triggers listener on marker click`() {
        // Arrange
        val marker = mapMarkerManager.addMarker(
            OmhMarkerOptions().apply {
                clickable = true
                draggable = true
            }
        )
        val omhOnMarkerClickListener = mockk<OmhOnMarkerClickListener>(relaxed = true)
        every { omhOnMarkerClickListener.onMarkerClick(any()) } returns true
        mapMarkerManager.setMarkerClickListener(omhOnMarkerClickListener)

        // Act
        mapMarkerManager.maybeHandleClick(marker.markerUUID.toString())

        // Assert
        verify { omhOnMarkerClickListener.onMarkerClick(marker) }
    }

    @Test
    fun `setCustomInfoWindowViewFactory sets custom IW view factory`() {
        // Arrange
//        mockkConstructor(OmhInfoWindow::class)
//        var capturedInfoWindowViewFactory: OmhInfoWindowViewFactory? = null
//        every { anyConstructed<OmhInfoWindow>().setCustomInfoWindowViewFactory(any()) } answers {
//            capturedInfoWindowViewFactory = firstArg()
//        }
//        var capturedInfoWindowContentsViewFactory: OmhInfoWindowViewFactory? = null
//        every { anyConstructed<OmhInfoWindow>().setCustomInfoWindowContentsViewFactory(any()) } answers {
//            capturedInfoWindowContentsViewFactory = firstArg()
//        }
        val marker = mapMarkerManager.addMarker(OmhMarkerOptions())

        val mockedIWView = mockk<View>(relaxed = true)
        val capturedIWViewClickListener = slot<View.OnClickListener>()
        every { mockedIWView.setOnClickListener(capture(capturedIWViewClickListener)) } returns Unit
        val capturedIWViewLongClickListener = slot<View.OnLongClickListener>()
        every {
            mockedIWView.setOnLongClickListener(
                capture(
                    capturedIWViewLongClickListener
                )
            )
        } returns Unit

        // Act
        mapMarkerManager.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
            override fun createInfoWindowView(marker: OmhMarker): View {
                return mockedIWView
            }
        })

        // Assert
        Assert.assertSame(mockedIWView, marker.omhInfoWindow.windowView)
        Assert.assertNotNull(capturedIWViewClickListener.captured)
        Assert.assertNotNull(capturedIWViewLongClickListener.captured)
    }

    @Test
    fun `setInfoWindowOpenStatusChangeListener sets a listener invoked on InfoWindow close`() {
        // Arrange
        val capturedClickListener = slot<View.OnClickListener>()
        every { iwMockFrameLayout.setOnClickListener(capture(capturedClickListener)) } returns Unit
        val marker = mapMarkerManager.addMarker(
            OmhMarkerOptions().apply {
                clickable = true
                draggable = true
            }
        )
        val omhOnIWOpenStatusChangeListener =
            mockk<OmhOnInfoWindowOpenStatusChangeListener>(relaxed = true)
        mapMarkerManager.setInfoWindowOpenStatusChangeListener(omhOnIWOpenStatusChangeListener)

        val mockedIWView = mockk<View>(relaxed = true)
        val capturedIWViewClickListener = slot<View.OnClickListener>()
        every { mockedIWView.setOnClickListener(capture(capturedIWViewClickListener)) } returns Unit
        val capturedIWViewLongClickListener = slot<View.OnLongClickListener>()
        every {
            mockedIWView.setOnLongClickListener(
                capture(
                    capturedIWViewLongClickListener
                )
            )
        } returns Unit

        mapMarkerManager.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
            override fun createInfoWindowView(marker: OmhMarker): View {
                return mockedIWView
            }
        })

        // Act
        marker.showInfoWindow()

        // Assert
        Assert.assertNotNull(capturedClickListener.captured)
        verify(exactly = 1) { omhOnIWOpenStatusChangeListener.onInfoWindowOpen(marker) }

        // Act
        marker.hideInfoWindow()

        // Assert
        verify(exactly = 1) { omhOnIWOpenStatusChangeListener.onInfoWindowClose(marker) }
    }

    @Test
    fun `setOnInfoWindowClickListener sets a listener invoked on InfoWindow click`() {
        // Arrange
        val marker = mapMarkerManager.addMarker(
            OmhMarkerOptions().apply {
                clickable = true
                draggable = true
            }
        )
        val omhOnIWClickListener = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
        mapMarkerManager.setOnInfoWindowClickListener(omhOnIWClickListener)
        val omhOnIWLongClickListener = mockk<OmhOnInfoWindowLongClickListener>(relaxed = true)
        mapMarkerManager.setOnInfoWindowLongClickListener(omhOnIWLongClickListener)

        val mockedIWView = mockk<View>(relaxed = true)
        val capturedIWViewClickListener = slot<View.OnClickListener>()
        every { mockedIWView.setOnClickListener(capture(capturedIWViewClickListener)) } returns Unit
        val capturedIWViewLongClickListener = slot<View.OnLongClickListener>()
        every {
            mockedIWView.setOnLongClickListener(
                capture(
                    capturedIWViewLongClickListener
                )
            )
        } returns Unit

        mapMarkerManager.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
            override fun createInfoWindowView(marker: OmhMarker): View {
                return mockedIWView
            }
        })

        // Act
        capturedIWViewClickListener.captured.onClick(mockedIWView)

        // Assert
        verify(exactly = 1) { omhOnIWClickListener.onInfoWindowClick(marker) }

        // Act
        capturedIWViewLongClickListener.captured.onLongClick(mockedIWView)

        // Assert
        verify(exactly = 1) { omhOnIWLongClickListener.onInfoWindowLongClick(marker) }
    }
}
