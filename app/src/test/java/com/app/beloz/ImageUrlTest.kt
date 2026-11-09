package com.app.beloz

import com.app.beloz.data.remote.ImageUrlResolver
import org.junit.Assert.*
import org.junit.Test

class ImageUrlTest {
    @Test fun rejectsCredentialsUnsafeSchemesAndTraversal() {
        assertNull(ImageUrlResolver.resolve("https://user:password@example.com/photo.png"))
        assertNull(ImageUrlResolver.resolve("file:///private/photo.png"))
        assertNull(ImageUrlResolver.resolve("../private/photo.png"))
        assertNull(ImageUrlResolver.resolve(null))
    }
    @Test fun resolvesCatalogAssetsWithoutDuplicatingImagesPrefix() {
        assertTrue(ImageUrlResolver.resolve("/images/poke_vegano.jpg")!!.endsWith("/images/poke_vegano.jpg.png"))
        assertTrue(ImageUrlResolver.resolve("poke_camaron.jpg")!!.endsWith("/images/poke_camaron.jpg.png"))
        assertEquals("https://example.com/photo.png", ImageUrlResolver.resolve("https://example.com/photo.png"))
    }
}
